package org.sbaresearch.socialsnapshot.crawl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.sbaresearch.socialsnapshot.Config;
import org.sbaresearch.socialsnapshot.crawl.entity.FbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.IFbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.connections.FbConnection;

/**
 * @author Maurice Wohlkönig
 */
public class DownloadQueue {
	
	private static DownloadQueue downloadQueueInstance;
	private static Logger log = Logger.getLogger(DownloadQueue.class);
	
	final private DownloadThreadPoolExecutor es;
	private ArrayList<DownloadQueueFinishListener> downloadQueueFinishListeners = new ArrayList<>();
	private ArrayList<ImageDownloadListener> imageDownloadListeners = new ArrayList<>();
	
	BlockingQueue<Runnable> queue;
	public Object synchObj = new Object();
	
	/**
	 * Singleton
	 *
	 * @param numOfThreads
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private DownloadQueue(int numOfThreads) {
		log.info("starting download queue with " + numOfThreads + " threads");
		this.queue = new PriorityBlockingQueue<Runnable>(numOfThreads, new ComparePriority());
		this.es = new DownloadThreadPoolExecutor(numOfThreads);
	}
	
	/**
	 * Adds a new Job to the downloadqueue which is then executed based on available threads and its priority.
	 *
	 * @param job
	 *            the job to be executed
	 */
	public synchronized void add(DownloadJob job) {
		try {
			this.es.execute(job);
		} catch (RejectedExecutionException e) {
			// silent catch - the download queue was shut down on purpose (by the user)
		}
	}
	
	public void addFinishListener(DownloadQueueFinishListener downloadQueueFinishListener) {
		this.downloadQueueFinishListeners.add(downloadQueueFinishListener);
	}
	
	public void addImageDownloadListener(ImageDownloadListener imageDownloadListener) {
		this.imageDownloadListeners.add(imageDownloadListener);
	}
	
	/**
	 * Shuts down the downloading Tasks
	 *
	 * @param timeoutSeconds
	 *            number in seconds to take at maximum
	 */
	public void shutdown(int timeoutSeconds) {
		// wait for all of the executor threads to finish
		this.es.shutdown();
		
		for (DownloadQueueFinishListener listener : this.downloadQueueFinishListeners)
			if (listener != null) {
				listener.onFinish();
			}
		
		try {
			if (!this.es.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
				// pool didn't terminate after the first try
				this.es.shutdownNow();
			}
			
			if (!this.es.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
				// pool didn't terminate after the second try
			}
		} catch (InterruptedException ex) {
			this.es.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * Shuts down the downloading Tasks and blocks until all of the running ones are finished.
	 */
	public void stop() {
		this.es.shutdown();
		try {
			this.es.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized DownloadQueue getInstance() {
		if (downloadQueueInstance == null) {
			int cores = Runtime.getRuntime().availableProcessors();
			downloadQueueInstance = new DownloadQueue(cores * Config.getDownloadThreadsPerCore());
		}
		return downloadQueueInstance;
	}
	
	public static abstract class ConDownloadJob extends DownloadJob {
		
		private FbConnection obj;
		
		public ConDownloadJob(final FbConnection obj) {
			super(obj.getPriority());
			this.obj = obj;
		}
		
		public abstract void onPageComplete(FbConnection obj);
		
		@Override
		/*
		 * This is run in its own thread
		 */
		public void run() {
			String tempType = this.obj.getType();
			do {
				// log.debug("loading connection page " + page + ": " + obj.getGraphUrl());
				this.obj = fbloader.load(this.obj);
				if (this.obj != null) {
					this.obj.setType(tempType);
					this.obj.onComplete();
					onPageComplete(this.obj);
				}
			} while (this.obj != null && this.obj.hasNextPage());
		}
		
	}
	
	public static abstract class DownloadJob implements Comparable<DownloadJob>, Runnable {
		
		protected int priority;
		protected static FacebookLoader fbloader = new FacebookLoader();
		
		public DownloadJob(int priority) {
			this.priority = priority;
		}
		
		@Override
		public int compareTo(DownloadJob o) {
			return this.priority - o.priority;
		}
		
		@Override
		public abstract void run();
		
		@Override
		public String toString() {
			return "DownloadJob(" + this.priority + ")";
		}
	}
	
	public abstract static class DownloadQueueFinishListener {
		
		public abstract void onFinish();
	}
	
	public class ImageDownloadJob extends DownloadJob {
		
		private String urls;
		
		public ImageDownloadJob(int priority, String url) {
			super(priority);
			this.urls = url;
		}
		
		@Override
		public void run() {
			// log.debug(toString());
			try {
				URL url = new URL(this.urls);
				InputStream in = new BufferedInputStream(url.openStream());
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int n = 0;
				while (-1 != (n = in.read(buf))) {
					out.write(buf, 0, n);
				}
				out.close();
				in.close();
				byte[] response = out.toByteArray();
				
				onComplete(response);
			} catch (IOException e) {
				log.error("can not download image " + this.urls, e);
			}
		}
		
		@Override
		public String toString() {
			return "ImageDownloadJob (" + this.urls + " - " + this.priority + ")";
		}
		
		private void onComplete(byte[] imageData) {
			if (imageData != null) {
				for (ImageDownloadListener listener : DownloadQueue.this.imageDownloadListeners)
					if (listener != null) {
						listener.onImageDownloaded(this.urls, imageData);
					}
			}
		}
	}
	
	public abstract static class ImageDownloadListener {
		
		public abstract void onImageDownloaded(String urls, byte[] response);
	}
	
	public static abstract class ObjDownloadJob extends DownloadJob {
		
		private FbObject obj;
		
		public ObjDownloadJob(final FbObject obj) {
			super(obj.getPriority());
			this.obj = obj;
		}
		
		public abstract void onComplete(FbObject obj);
		
		@Override
		/*
		 * This is run in its own thread
		 */
		public void run() {
			// log.debug("ObjDownloadJob runs on " + obj);
			IFbObject result = fbloader.load(this.obj);
			if (result != null) { // object might be null if we encounter a server error
				int lastDepth = this.obj.getDepth();
				
				this.obj = (FbObject) result;
				this.obj.setDepth(lastDepth + 1);
				this.obj.onComplete();
				onComplete(this.obj);
			}
		}
	}
	
	private static class ComparePriority<T extends DownloadJob> implements Comparator<T> {
		
		@Override
		public int compare(T o1, T o2) {
			return o1.compareTo(o2);
		}
	}
	
	private class DownloadThreadPoolExecutor extends ThreadPoolExecutor {
		
		AtomicInteger threadsRunning = new AtomicInteger(0);
		
		public DownloadThreadPoolExecutor(int numOfThreads) {
			super(numOfThreads, // core thread pool size
					numOfThreads, // maximum thread pool size
					1, // time to wait before resizing pool
					TimeUnit.SECONDS, DownloadQueue.this.queue);
			
		}
		
		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			int curThreads = this.threadsRunning.decrementAndGet();
			if (t == null && r instanceof Future<?>) {
				try {
					Future<?> future = (Future<?>) r;
					if (future.isDone()) {
						future.get();
					}
				} catch (CancellationException ce) {
					t = ce;
				} catch (ExecutionException ee) {
					t = ee.getCause();
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt(); // ignore/reset
				}
			}
			if (t != null) {
				log.error("got an exception", t);
			}
			synchronized (DownloadQueue.this.synchObj) {
				if (DownloadQueue.this.queue.isEmpty()) {
					if (curThreads == 0) {
						log.info("queue is finished - quiting");
						DownloadQueue.this.shutdown(Config.THREAD_SHUTDOWN_TIME_SEC);
					}
				}
			}
		}
		
		@Override
		protected void beforeExecute(Thread t, Runnable r) {
			super.beforeExecute(t, r);
			this.threadsRunning.incrementAndGet();
		}
	}
}
