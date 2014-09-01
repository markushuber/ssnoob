package org.sbaresearch.socialsnapshot.crawl.entity;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.sbaresearch.socialsnapshot.Config;
import org.sbaresearch.socialsnapshot.crawl.DownloadQueue;
import org.sbaresearch.socialsnapshot.crawl.DownloadQueue.ConDownloadJob;
import org.sbaresearch.socialsnapshot.crawl.entity.connections.FbConnection;
import org.sbaresearch.socialsnapshot.crawl.entity.fb.CommonFbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.fb.Photo;
import org.sbaresearch.socialsnapshot.crawl.entity.fb.Post;
import org.sbaresearch.socialsnapshot.crawl.entity.fb.Video;
import org.sbaresearch.socialsnapshot.util.StringUtils;
import org.sbaresearch.socialsnapshot.util.Utils;

/**
 * @author Maurice Wohlkönig
 */
public abstract class FbObject implements IFbObject {
	
	private static final Logger log = Logger.getLogger(FbObject.class);
	
	/**
	 * Depth in the snapshot tree. Level 0 is the user itself.
	 */
	protected int depth = 0;
	/**
	 * Priority for downloading of this fb object. 0 is highest.
	 */
	protected int priority = 0;
	protected FbMetadata metadata;
	protected boolean isCopy = false;
	
	static final HashMap<String, Type> strTypeToTypeMap = new HashMap<>();
	
	static {
		strTypeToTypeMap.put("photo", Photo.class);
		strTypeToTypeMap.put("status", Post.class);
		strTypeToTypeMap.put("video", Video.class);
	}
	
	public FbObject(int depth) {
		this.depth = depth;
	}
	
	public HashMap<String, String> getConnections() {
		if (this.metadata != null)
			return this.metadata.getConnections();
		return null;
	}
	
	public int getDepth() {
		return this.depth;
	}
	
	public int getPriority() {
		return this.priority;
	}
	
	public boolean isCopy() {
		return this.isCopy;
	}
	
	/**
	 * Indicates that this object is completely loaded and its child objects can be fetched (if necessary)
	 */
	public void onComplete() {
		checkAnnotations();
		/*
		 * only load child elements of this object if below max depth and this object is not a copy of an already existing object (the children are going to be loaded in the original)
		 */
		if (this.depth < Config.getMaxSnapshotDepth() && !this.isCopy) {
			final DownloadQueue dq = DownloadQueue.getInstance();
			Entry<String, String> entry = null;
			if (getConnections() != null) {
				FbConnection obj;
				for (Iterator<Entry<String, String>> itt = getConnections().entrySet().iterator(); itt.hasNext();) {
					entry = itt.next();
					obj = FbConnection.getObjectFor(entry.getKey(), entry.getValue(), this.priority);
					if (obj != null) {
						dq.add(new ConDownloadJob(obj) {
							
							@Override
							public void onPageComplete(FbConnection obj) {
								handleConnection(obj);
							}
						});
					}
				}
			}
		}
	}
	
	public void setCopy(boolean isCopy) {
		this.isCopy = isCopy;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	protected String genGraphUrl(String path) {
		String url = FB_GRAPH_URL + path + "?metadata=1";
		if (requestFields() != null) {
			url += "&fields=" + StringUtils.join(requestFields(), ",");
		}
		return url;
	}
	
	protected abstract void handleConnection(FbConnection con);
	
	protected abstract String[] requestFields();
	
	private void checkAnnotations() {
		try {
			Utils.searchForDownloadUrls(this);
		} catch (StackOverflowError e) {
			log.error("stack overflow on " + this, e);
		}
	}
	
	/**
	 * Returns the real class type for the given type string
	 *
	 * @param type
	 *            fb type string
	 * @return corresponding internal class type
	 */
	public static Type getTypeFor(String type) {
		if (strTypeToTypeMap.containsKey(type))
			return strTypeToTypeMap.get(type);
		return CommonFbObject.class;
	}
	
}
