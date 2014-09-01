package org.sbaresearch.socialsnapshot;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.sbaresearch.socialsnapshot.auth.fb.FacebookAuthenticator;
import org.sbaresearch.socialsnapshot.auth.fb.IFacebookAuthenticator;
import org.sbaresearch.socialsnapshot.auth.fb.IFacebookAuthenticator.IAuthenticatorCallback;
import org.sbaresearch.socialsnapshot.auth.fb.IFacebookAuthenticator.State;
import org.sbaresearch.socialsnapshot.cookies.BrowserType;
import org.sbaresearch.socialsnapshot.crawl.DownloadQueue;
import org.sbaresearch.socialsnapshot.crawl.DownloadQueue.DownloadQueueFinishListener;
import org.sbaresearch.socialsnapshot.crawl.DownloadQueue.ImageDownloadListener;
import org.sbaresearch.socialsnapshot.crawl.FacebookLoader;
import org.sbaresearch.socialsnapshot.crawl.entity.FbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.InitialUserDownloadJob;
import org.sbaresearch.socialsnapshot.crawl.entity.fb.User;
import org.sbaresearch.socialsnapshot.export.ImageExporter;
import org.sbaresearch.socialsnapshot.export.JsonStringExporter;
import org.sbaresearch.socialsnapshot.export.UserImageTreeExporter;
import org.sbaresearch.socialsnapshot.ui.ICredentialWindowEventListener;
import org.sbaresearch.socialsnapshot.ui.MainWindow;

/**
 * Main entry point for the program. everything from starting UI, starting authentication to FB, downloading is handleded from here.
 *
 * @author Stefan Haider (shaider@sba-research.org), original code: Maurice Wohlkönig
 */
public class SSNOOB {

	public static final String cookieDomain = "facebook.com";
	
	private static Logger log = Logger.getLogger(SSNOOB.class);
	
	private static User rootUser;
	private static ImageExporter imageExporter;
	
	private static IAuthenticatorCallback accessTokenCallback = new IAuthenticatorCallback() {
		
		@Override
		public void onFailure(int errorCode, String errorMessage) {
			log.error("authentication failed: " + errorMessage);
			window.downloadFinished();
		}
		
		@Override
		public void onSuccess(String accessToken) {
			// set the access token to be used by the downloader
			FacebookLoader.setAccessToken(accessToken);
			
			log.info("authentication finished - access token: " + accessToken);
			
			startDownload();
		}

		@Override
		public void statusUpdate(State state) {
			log.debug("login status changed to " + state.getNumber());
		}
	};
	
	private static long startTime;
	
	private static Thread onShutdown = new Thread(new Runnable() {
		
		@Override
		public void run() {
			if (!exportStarted) {
				DownloadQueue.getInstance().stop();
				// called by downloadqueue onFinish
				// startExport();
			}
		}
	});
	
	private static MainWindow window;

	private static boolean exportStarted;
	
	private static IFacebookAuthenticator authenticator;
	
	/**
	 * Main entry point for the program
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (!Config.init(args)) // false means there was an error parsing the options
			return;
		
		authenticator = new FacebookAuthenticator();
		
		Runtime.getRuntime().addShutdownHook(onShutdown);

		List<String> arg = Config.getArguments();
		
		if (arg.size() == 1) {
			startTime = System.nanoTime();
			log.debug("starting authentication with cookie");
			authenticator.getAccessToken(accessTokenCallback, args[0], BrowserType.DEFAULT);
		} else if (arg.size() == 2) {
			startTime = System.nanoTime();
			log.debug("starting authentication with password");
			authenticator.getAccessToken(accessTokenCallback, args[0], args[1]);
		} else {
			if (Config.getAccessToken() != null) {
				startTime = System.nanoTime();
				log.debug("received access token from command line");
				FacebookLoader.setAccessToken(Config.getAccessToken());
				startDownload();
			} else {
				
				window = new MainWindow(new ICredentialWindowEventListener() {
					
					@Override
					public void onClose() {
						DownloadQueue.getInstance().shutdown(5000);
						// called by downloadqueue onFinish
						// startExport();
					}
					
					@Override
					public boolean onUserCookieProvided(final String usercookie, final BrowserType browser) {
						startTime = System.nanoTime();
						
						log.info("Trying to get FB access token..");
						
						Thread t = new Thread(new Runnable() {

							@Override
							public void run() {
								authenticator.getAccessToken(accessTokenCallback, usercookie, browser);
							}
						});
						t.start();
						return true;
					}
					
					@Override
					public boolean onUserCredentialsProvided(final String username, final String password) {
						startTime = System.nanoTime();
						
						log.info("Trying to get FB access token..");
						
						Thread t = new Thread(new Runnable() {

							@Override
							public void run() {
								authenticator.getAccessToken(accessTokenCallback, username, password);
							}
						});
						t.start();

						return true;
					}
				});
				window.setVisible(true);
			}
		}
		
	}
	
	protected static void startDownload() {
		
		DownloadQueue q = DownloadQueue.getInstance();
		
		q.addFinishListener(new DownloadQueueFinishListener() {
			
			@Override
			public void onFinish() {
				log.info("download finished");
				startExport();
				// reset ui
				window.downloadFinished();
			}
		});
		
		q.addImageDownloadListener(new ImageDownloadListener() {
			
			@Override
			public void onImageDownloaded(String url, byte[] imageData) {
				if (imageExporter != null) {
					imageExporter.export(url, imageData);
					
				} else {
					log.error("imageExporter not set!");
				}
			}
		});
		
		log.debug("starting download");
		q.add(new InitialUserDownloadJob() {
			
			@Override
			public void onComplete(FbObject obj) {
				log.debug("Initial User = " + (obj));
				rootUser = (User) obj;
				imageExporter = new ImageExporter(rootUser.getId());
			}
		});
	}
	
	protected static void startExport() {
		// clean up facebook, remove app
		authenticator.cleanUpFacebook();

		exportStarted = true;
		log.info("starting export");
		if (rootUser != null) {
			log.info("Exporting json file..");
			String jsonPath = new JsonStringExporter(rootUser.getId(), true).export(rootUser);
			log.info("Exported to " + Config.getSnapshotDirectory() + "/" + rootUser.getId());
			
			log.info("Exporting image tree..");
			String userImageTree = new UserImageTreeExporter(Config.getSnapshotDirectory(), rootUser.getId(), jsonPath).export();
			log.info("Exported user image tree to directory " + userImageTree);
		} else {
			log.warn("no root user -> no data to export");
		}
		long timeNeeded = (System.nanoTime() - startTime);
		log.info("process took " + (TimeUnit.NANOSECONDS.toSeconds(timeNeeded)) + " seconds");
	}
}
