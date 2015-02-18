package org.sbaresearch.socialsnapshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;
import org.kohsuke.args4j.Option;

/**
 * @author Stefan Haider (shaider@sba-research.org), original code: Maurice Wohlkönig
 */
public final class Config {
	
	/**
	 * The config file name
	 */
	public static final String DEFAULT_CONFIG_FILE = "socialsnapshot.config";

	/**
	 * How long should we wait for the download queue to stop after finish
	 */
	public static final int THREAD_SHUTDOWN_TIME_SEC = 12;

	protected static final String IMAGE_DOWNLOAD_ENABLED = "image_download_enabled";
	protected static final String SNAPSHOT_DEPTH = "snapshot_depth";
	protected static final String SNAPSHOT_DIRECTORY = "snapshot_directory";
	protected static final String DOWNLOAD_THREADS_PER_CORE = "download_threads_per_core";
	protected static final String GRAPH_APP_ID = "graph_app_id";
	protected static final String GRAPH_APP_WEBSITE = "graph_app_website";
	protected static final String MAXIMUM_IMAGE_PRIORITY = "maximum_image_priority";

	private static final String DEFAULT_BROWSER_NAME = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36";
	private static final Integer DEFAULT_DOWNLOAD_THREADS = 10;
	private static final String DEFAULT_GRAPH_APP_ID = "122649464553932";
	private static final String DEFAULT_GRAPH_APP_WEBSITE = "https://ssnoobapp.nysos.net/";
	private static final Boolean DEFAULT_IMAGE_DOWNLOAD_ENABLED = true;
	private static final Integer DEFAULT_IMAGE_PRIORITY = 5;
	private static final Integer DEFAULT_MAX_SNAPSHOT_DEPTH = 5;
	private static final String DEFAULT_SNAPSHOT_DIRECTORY = System.getProperty("user.home") + File.separator + "fb_snapshot";
	
	/**
	 * The logger for this class
	 */
	private static final Logger log = Logger.getLogger(Config.class);

	/**
	 * internal instance of the configuration
	 */
	private static Config instance = new Config();
	
	@Option(name = "-token", usage = "supply an access token directly instead of an username/cookie")
	private String accessToken = null;
	
	// receives other command line parameters than options
	@Argument
	private List<String> arguments = new ArrayList<String>();
	
	@Option(name = "-browserName", usage = "set the user agend string for the build in browser")
	private String browserName = DEFAULT_BROWSER_NAME;
	
	@Option(name = "-c", usage = "the config file to use")
	private String configFile = DEFAULT_CONFIG_FILE;
	
	@Option(name = "-downloadThreads", usage = "download threads per core")
	private int downloadThreads = -1;
	
	@Option(name = "-graphAppId", usage = "the graph app id of your graph app")
	private String graphAppId;
	
	@Option(name = "-graphNamespace", usage = "the namespace of the graph app")
	private String graphAppNamespace;
	
	@Option(name = "-graphAppSecret", usage = "the app secret of your graph app")
	private String graphAppSecret;
	
	private String graphAppWebsite;

	@Option(name = "-imageDownload", usage = "boolean value for dis/enabling image download")
	private static Boolean imageDownloadEnabled;
	
	private boolean initDone = false;
	
	@Option(name = "-loginDeviceName", usage = "the name SSNOOB should use if asked for a device name")
	private String loginDeviceName;
	
	@Option(name = "-maxImagePriority", usage = "the priority image download tasks should have\n(1 is the highest priority)")
	private int maxImagePriority = -1;
	
	@Option(name = "-max", usage = "the max depth of the snapshot")
	private int maxSnapshotDepth = -1;
	
	private Properties prop = new Properties();
	
	@Option(name = "--help", usage = "shows this help")
	private boolean showHelp = false;
	
	@Option(name = "-d", usage = "the directory where the snapshots will be placed\n(will be created if not present)")
	private String snapshotDirectory;
	
	public static String getAccessToken() {
		return instance.accessToken;
	}
	
	/**
	 * Gets other command line parameters, after the call to {@link Config#init(String[])}
	 *
	 * @return
	 */
	public static List<String> getArguments() {
		checkState();
		return instance.arguments;
	}

	public static String getBrowserName() {
		checkState();
		return instance.browserName;
	}
	
	public static int getDownloadThreadsPerCore() {
		if (instance.downloadThreads == -1) {
			try {
				instance.downloadThreads = Integer.parseInt(instance.prop.getProperty(DOWNLOAD_THREADS_PER_CORE, DEFAULT_DOWNLOAD_THREADS + ""));
			} catch (NumberFormatException e) {
				log.error("config file syntax error: download_threads_per_core must be an integer");
				instance.downloadThreads = DEFAULT_DOWNLOAD_THREADS;
			}
		}
		return instance.downloadThreads;
	}
	
	public static String getGraphAppId() {
		checkState();
		if (instance.graphAppId != null)
			return instance.graphAppId;
		return instance.graphAppId = instance.prop.getProperty(GRAPH_APP_ID, DEFAULT_GRAPH_APP_ID);
	}
	
	public static String getGraphAppWebsite() {
		checkState();
		if (instance.graphAppWebsite != null)
			return instance.graphAppWebsite;
		return instance.graphAppWebsite = instance.prop.getProperty(GRAPH_APP_WEBSITE, DEFAULT_GRAPH_APP_WEBSITE);
	}
	
	/**
	 * @return the instance
	 */
	public static Config getInstance() {
		return instance;
	}
	
	public static int getMaxImagePriority() {
		checkState();
		if (instance.maxImagePriority == -1) {
			try {
				instance.maxImagePriority = Integer.parseInt(instance.prop.getProperty(MAXIMUM_IMAGE_PRIORITY, DEFAULT_IMAGE_PRIORITY + ""));
			} catch (NumberFormatException e) {
				log.error("config file syntax error: maximum_image_priority must be an integer");
				instance.maxImagePriority = DEFAULT_IMAGE_PRIORITY;
			}
		}
		return instance.maxImagePriority;
	}
	
	/**
	 * @return The maximum depth of the Social Snapshot
	 */
	public static int getMaxSnapshotDepth() {
		if (instance.maxSnapshotDepth == -1) {
			try {
				instance.maxSnapshotDepth = Integer.parseInt(instance.prop.getProperty(SNAPSHOT_DEPTH, DEFAULT_MAX_SNAPSHOT_DEPTH + ""));
			} catch (NumberFormatException e) {
				log.error("config file syntax error: snapshot_depth must be an integer");
				instance.maxSnapshotDepth = DEFAULT_MAX_SNAPSHOT_DEPTH;
			}
		}
		return instance.maxSnapshotDepth;
	}
	
	public static String getSnapshotDirectory() {
		checkState();
		if (instance.snapshotDirectory != null)
			return instance.snapshotDirectory;
		return instance.snapshotDirectory = instance.prop.getProperty(SNAPSHOT_DIRECTORY, DEFAULT_SNAPSHOT_DIRECTORY);
	}
	
	/**
	 * Initialize the config.
	 *
	 * @param args
	 *            the command line args
	 * @return true if initialization was successful, false otherwise - you should stop the continuation of the application in this case.
	 */
	public static boolean init(String[] args) {
		return instance.initByArgs(args);
	}

	public static boolean isImageDownloadEnabled() {
		checkState();
		if (Config.imageDownloadEnabled == null) {
			Config.imageDownloadEnabled = Boolean.parseBoolean(instance.prop.getProperty(IMAGE_DOWNLOAD_ENABLED, DEFAULT_IMAGE_DOWNLOAD_ENABLED.toString()));
		}
		return Config.imageDownloadEnabled;
	}
	
	/**
	 * @param browserName
	 *            the browserName to set
	 */
	public static void setBrowserName(String browserName) {
		instance.browserName = browserName;
	}
	
	/**
	 * @param imageDownloadEnabled
	 *            the imageDownloadEnabled to set
	 */
	public static void setImageDownloadEnabled(Boolean imageDownloadEnabled) {
		Config.imageDownloadEnabled = imageDownloadEnabled;
	}
	
	/**
	 * @param maxSnapshotDepth
	 *            the maxSnapshotDepth to set
	 */
	public static void setMaxSnapshotDepth(int maxSnapshotDepth) {
		instance.maxSnapshotDepth = maxSnapshotDepth;
	}
	
	/**
	 * @param snapshotDirectory
	 *            the snapshotDirectory to set
	 */
	public static void setSnapshotDirectory(String snapshotDirectory) {
		instance.snapshotDirectory = snapshotDirectory;
	}
	
	private static void checkState() {
		if (!instance.initDone)
			throw new IllegalStateException("Call Config.inig(args) before calling this and check for return value true");
	}
	
	private Config() {
	}
	
	/**
	 * Generates the config file if not present and fills it with default values
	 */
	private void createDefaultProperties() {
		// set the properties value
		this.prop.setProperty(IMAGE_DOWNLOAD_ENABLED, DEFAULT_IMAGE_DOWNLOAD_ENABLED.toString());
		this.prop.setProperty(SNAPSHOT_DEPTH, DEFAULT_MAX_SNAPSHOT_DEPTH.toString());
		this.prop.setProperty(SNAPSHOT_DIRECTORY, DEFAULT_SNAPSHOT_DIRECTORY);
		this.prop.setProperty(DOWNLOAD_THREADS_PER_CORE, DEFAULT_DOWNLOAD_THREADS.toString());
		this.prop.setProperty(MAXIMUM_IMAGE_PRIORITY, DEFAULT_IMAGE_PRIORITY.toString());
		this.prop.setProperty(GRAPH_APP_ID, DEFAULT_GRAPH_APP_ID);
		this.prop.setProperty(GRAPH_APP_WEBSITE, DEFAULT_GRAPH_APP_WEBSITE);

		// save properties to project config file
		try {
			this.prop.store(new FileOutputStream(DEFAULT_CONFIG_FILE), null);
		} catch (IOException e) {
			log.error("can not create properties file", e);
		}
	}
	
	private boolean initByArgs(String[] args) {
		CmdLineParser parser = new CmdLineParser(instance);
		try {
			parser.parseArgument(args);
			
			if (this.showHelp) {
				parser.printUsage(System.out);
				return false;
			}
			
			// after parsing arguments, you should check
			// if enough arguments are given.
			if (this.arguments.size() > 2)
				throw new CmdLineException(parser, "Invalid number of arguments");
			
			this.initDone = true;
			
			// load properties from config file
			loadPropertiesFile();
			return true;
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println("java -jar SSNOOB.jar [cookiestring|username password] [options]");
			// print the list of available options
			parser.printUsage(System.err);
			System.err.println();
			
			// print option sample. This is useful some time
			System.err.println("  Examples:\n   java SSNOOB.jar username password " + parser.printExample(ExampleMode.REQUIRED) + "\n   java SSNOOB.jar cookiestring "
					+ parser.printExample(ExampleMode.REQUIRED) + "");
		}
		return false;
	}
	
	private boolean loadPropertiesFile() {
		try {
			this.prop.load(new FileInputStream(this.configFile));
			return true;
		} catch (IOException e) {
			log.info("can not load properties file");
			if (this.configFile.equals(DEFAULT_CONFIG_FILE)) {
				log.info("creating default config file");
				createDefaultProperties();
			}
			return false;
		}
	}
}
