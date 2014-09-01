package org.sbaresearch.socialsnapshot.cookies;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.sbaresearch.socialsnapshot.util.Utils;

/**
 * This class searches for installed browsers on the current OS.
 *
 * @author Stefan Haider (shaider@sba-research.org), original code: martin grottenthaler, markus stoeckler, lukas wabro
 */
public class BrowserFinder {
	
	/** Boolean for installed or not installed */
	private boolean chromeAvailable = false, firefoxAvailable = false, ieAvailable = false;
	/** String for the version of the browser */
	private String firefoxVersion = null, chromeVersion = null, ieVersion = null;
	/** Log4j logging instance */
	private static Logger log = Logger.getLogger(BrowserFinder.class);
	
	private static final String firefoxUserAgentFormat = "Mozilla/5.0 (%1$s; rv:%2$s) Gecko/20100101 Firefox/%2$s";
	private static final String chromeUserAgentFormat = "Mozilla/5.0 (%1$s) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/%2$s Safari/537.36";
	private static final String ie11UserAgentFormat = "Mozilla/5.0 (%1$s; Trident/7.0; rv:11.0) like Gecko";
	private static final String ie10UserAgentFormat = "Mozilla/5.0 (%1$s; Trident/6.0; MSIE 10.0) like Gecko";
	private static final String ie9UserAgentFormat = "Mozilla/5.0 (%1$s; Trident/5.0; MSIE 9.0) like Gecko";
	private static final String defaultUserAgentFormat = firefoxUserAgentFormat;

	private static BrowserFinder instance;

	/**
	 * Constructor for the class FindBrowser. <br>
	 * Decides if the operating system is Linux or Windows.
	 *
	 * @param OS
	 *            contains the operating system, the version and the architecture
	 */
	private BrowserFinder() {
		if (Utils.getOsName().contains("Linux")) {
			this.ieAvailable = false;
			this.ieVersion = "0";
			handleLinux();
		} else if (Utils.getOsName().contains("Windows")) {
			this.ieAvailable = true;
			handleWindows();
		}
	}

	public String getChromeUserAgent() {
		if (isChromeAvailable())
			return String.format(chromeUserAgentFormat, Utils.getOsString(), getChromeVersion());
		return getDefaultUserAgent();
	}

	/**
	 * @return the chromeVersion
	 */
	public String getChromeVersion() {
		return this.chromeVersion;
	}

	public String getDefaultUserAgent() {
		return String.format(defaultUserAgentFormat, Utils.getOsString(), "31.0");
	}

	public String getFirefoxUserAgent() {
		if (isFirefoxAvailable())
			return String.format(firefoxUserAgentFormat, Utils.getOsString(), getFirefoxVersion());
		return getDefaultUserAgent();
	}

	/**
	 * @return the firefoxVersion
	 */
	public String getFirefoxVersion() {
		return this.firefoxVersion;
	}

	public String getIeUserAgent() {
		if (isIeAvailable()) {
			if (getIeVersion().equals("10.0"))
				return String.format(ie10UserAgentFormat, Utils.getOsString(), getIeVersion());
			else if (getIeVersion().equals("9.0"))
				return String.format(ie9UserAgentFormat, Utils.getOsString(), getIeVersion());
			else
				return String.format(ie11UserAgentFormat, Utils.getOsString(), getIeVersion());
		}
		return getDefaultUserAgent();
	}

	/**
	 * @return the ieVersion
	 */
	public String getIeVersion() {
		return this.ieVersion;
	}

	public String getUserAgent(BrowserType browser) {
		switch (browser) {
			case Chrome:
				return getChromeUserAgent();
			case Firefox:
				return getFirefoxUserAgent();
			case InternetExplorer:
				return getIeUserAgent();
			default:
				return getDefaultUserAgent();
		}
	}

	public String getUserAgent(String browser) {
		return getUserAgent(BrowserType.parseBrowserType(browser));
	}

	/**
	 * @return the chromeAvailable
	 */
	public boolean isChromeAvailable() {
		return this.chromeAvailable;
	}

	/**
	 * @return the firefoxAvailable
	 */
	public boolean isFirefoxAvailable() {
		return this.firefoxAvailable;
	}

	/**
	 * @return the ieAvailable
	 */
	public boolean isIeAvailable() {
		return this.ieAvailable;
	}

	/**
	 * Checks on Linux platforms if Firefox or Chrome is installed and reads the version if possible.
	 */
	private void handleLinux() {
		log.info("On Linux");
		log.warn("No Internet Explorer on Linux");

		Process search;
		BufferedReader input;
		String line;

		// check firefox
		try {
			search = Runtime.getRuntime().exec("firefox --version");
			input = new BufferedReader(new InputStreamReader(search.getInputStream()));

			while ((line = input.readLine()) != null) {
				if (line.contains("Firefox")) {
					this.firefoxAvailable = true;
					this.firefoxVersion = line.substring(16);

					log.info("Firefox " + this.firefoxVersion + " found");
				}
			}
			input.close();
		} catch (Exception e) {
			log.warn("Firefox not found", e);
			this.firefoxAvailable = false;
		}

		// check chrome
		try {
			search = Runtime.getRuntime().exec("google-chrome --version");
			input = new BufferedReader(new InputStreamReader(search.getInputStream()));

			while ((line = input.readLine()) != null) {
				if (line.contains("Chrome")) {
					this.chromeAvailable = true;
					this.chromeVersion = line.substring(14);
					// Log information
					log.info("Chrome " + this.chromeVersion + " found");
				}
			}
			input.close();
		} catch (Exception e) {
			log.warn("Chrome not found", e);
			this.chromeAvailable = false;
		}
		// Log information
		log.debug("Searching for Browsers done");
	}

	/**
	 * Checks on Windows platforms if the Internet Explorer, Firefox or Chrome is installed and reads the version if possible.
	 */
	private void handleWindows() {
		// Win64
		if (Utils.getOsArchitecture().contains("64") && Utils.getOsName().matches("Windows (\\d+(\\.\\d+)+)")) {
			String programFilesx86 = System.getenv("ProgramFiles(x86)");
			handleWindowsArchitecture(programFilesx86);
		}
		// Win32
		else if (Utils.getOsArchitecture().contains("86")) {
			String programFiles = System.getenv("ProgramFiles");
			handleWindowsArchitecture(programFiles);
		}
		log.info("Searching for Browsers done");
	}

	/**
	 * Helper for {@link #handleWindows()}
	 *
	 * @param programFilesDir
	 *            the programfiles directory to use
	 */
	private void handleWindowsArchitecture(String programFilesDir) {
		Process search;
		BufferedReader input;
		String line;

		// read InternetExplorer Version from Registry
		try {
			this.ieVersion = queryRegistry("\"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Internet Explorer\"", "/v", "svcVersion");

			if (this.ieVersion != null) {
				log.info("Internet Explorer " + this.ieVersion + " found");
			}
		} catch (IOException e) {
			log.error("Internet Explorer version not found", e);
		}
		// check if firefox is installed
		try {
			if (Utils.isFileExisting(programFilesDir + "\\Mozilla Firefox\\firefox.exe")) {
				this.firefoxAvailable = true;
				// Log information
				log.info("Firefox found");
			}
		} catch (Exception e) {
			log.warn("Firefox not found", e);
			this.firefoxAvailable = false;
		}
		// read firefox version
		if (this.firefoxAvailable) {
			try {
				search = Runtime.getRuntime().exec(programFilesDir + "\\Mozilla Firefox\\firefox.exe -v");
				input = new BufferedReader(new InputStreamReader(search.getInputStream()));

				while ((line = input.readLine()) != null) {
					this.firefoxVersion = line.substring(16);
				}
				input.close();
			} catch (Exception e) {
				log.warn("error while reading firefox versionnumber", e);
			}
			if (this.firefoxVersion != null && !this.firefoxVersion.isEmpty()) {
				log.info("Firefox " + this.firefoxVersion + " found");
			}
		}
		// check if chrome is installed
		try {
			if (Utils.isFileExisting(programFilesDir + "\\Google\\Chrome\\Application\\chrome.exe")) {
				this.chromeAvailable = true;
				// Log information
				log.info("Chrome found");
			}
		} catch (Exception e) {
			log.warn("Chrome not found", e);
			this.chromeAvailable = false;
		}
		// read chrome version from VisualelementsManifest.xml
		if (this.chromeAvailable) {
			try {
				this.chromeVersion = queryRegistry("\"HKEY_CURRENT_USER\\Software\\Google\\Chrome\\BLBeacon\"", "/v", "version");
			} catch (IOException e) {
				log.warn("chrome version not found in registry HKEY_CURRENT_USER\\\\Software\\\\Google\\\\Chrome\\\\BLBeacon", e);
			}
			if (this.chromeVersion == null || this.chromeVersion.isEmpty()) {
				try {
					this.chromeVersion = queryRegistry("\"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Google Chrome\"", "/v", "Version");
				} catch (IOException e) {
					log.warn("chrome version not found in registry HKEY_LOCAL_MACHINE\\\\SOFTWARE\\\\Microsoft\\\\Windows\\\\CurrentVersion\\\\Uninstall\\\\Google Chrome", e);
				}
			}
			if (this.chromeVersion == null || this.chromeVersion.isEmpty()) {
				try {
					input = new BufferedReader(new FileReader(programFilesDir + "\\Google\\Chrome\\Application\\VisualElementsManifest.xml"));
					while (input.readLine() != null) {
						line = input.readLine();
						if (line.contains("Logo")) {
							line = line.substring(12, 16);
							this.chromeVersion = line;
							input.close();
							break;
						}
					}
				} catch (Exception e) {
					log.warn("error while reading chrome versionnumber from VisualElementsManifest.xml", e);
				}
			}
			if (this.chromeVersion != null && !this.chromeVersion.isEmpty()) {
				log.info("Chrome " + this.chromeVersion + " found");
			}
		}
	}

	/**
	 * Queries the registry with the given parameters
	 *
	 * @param params
	 *            the parameters for the query
	 * @return the result string
	 * @throws IOException
	 */
	private String queryRegistry(String... params) throws IOException {
		List<String> colParams = new LinkedList<String>();
		colParams.add("reg");
		colParams.add("query");
		colParams.addAll(Arrays.asList(params));

		ProcessBuilder builder = new ProcessBuilder(colParams);
		Process p = null;
		String result = null;

		p = builder.start();

		Scanner scanner = new Scanner(p.getInputStream());
		while (scanner.hasNext()) {
			result = scanner.next();
		}
		scanner.close();
		p.destroy();

		return result;
	}

	/**
	 * Gets the singleton instance of this class.
	 *
	 * @return the instance
	 */
	public static BrowserFinder getInstance() {
		if (instance == null) {
			instance = new BrowserFinder();
		}
		return instance;
	}
}