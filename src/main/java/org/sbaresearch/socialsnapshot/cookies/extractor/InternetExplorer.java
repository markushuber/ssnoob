package org.sbaresearch.socialsnapshot.cookies.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.sbaresearch.socialsnapshot.util.Utils;

import com.gargoylesoftware.htmlunit.util.Cookie;

/**
 * This class provides access to internet explorer cookies.
 *
 * @author Stefan Haider (shaider@sba-research.org), original code: markus.stoeckler
 */
public class InternetExplorer implements IBrowserCookieExtractor {

	/** String that contains the path to the cookie folder in Windows 8 */
	private static final String PathWIN8 = System.getProperty("user.home") + "\\AppData\\Local\\Microsoft\\Windows\\INetCookies\\";
	/** String that contains the path to the cookie folder in Windows 7 */
	private static final String PathWIN7 = System.getProperty("user.home") + "\\AppData\\Roaming\\Microsoft\\Windows\\Cookies\\";
	/** String that contains the path to the cookie folder in Windows XP */
	private static final String PathWINXP = System.getProperty("user.home") + "\\Cookies\\";

	/** {@link Set} of {@link Cookie} which contains all found cookies */
	private Set<Cookie> cookies;
	
	/** Log4j logging instance */
	private static Logger log = Logger.getLogger(InternetExplorer.class);

	/**
	 * Default constructor.
	 */
	public InternetExplorer() {
		this.cookies = new HashSet<Cookie>();
	}

	/**
	 * Reads all cookie .txt-files from IE.
	 */
	public void findCookies() {
		String pathToCookies = null;
		
		if (Utils.isDirectoryExisting(PathWIN8)) {
			pathToCookies = PathWIN8;
		} else if (Utils.isDirectoryExisting(PathWIN7)) {
			pathToCookies = PathWIN7;
		} else {
			pathToCookies = PathWINXP;
		}
		
		for (String file : getFiles(pathToCookies, null)) {
			BufferedReader fileReader = null;
			try {
				fileReader = new BufferedReader(new FileReader(file));
				String line = null;
				StringBuilder cookie = new StringBuilder();

				while ((line = fileReader.readLine()) != null) {
					cookie.append(line);
					cookie.append("\r\n");
				}
				parseCookieFile(cookie.toString());
			} catch (Exception e) {
				log.error("Error reading cookie file " + file, e);
			} finally {
				try {
					fileReader.close();
				} catch (IOException e) {
					log.error("Error closing file reader " + file, e);
				}
			}
		}
	}

	/**
	 * @return the cookies
	 */
	@Override
	public Set<Cookie> getCookies() {
		if (this.cookies.isEmpty()) {
			findCookies();
			log.info("Found " + this.cookies.size() + " Cookies for IE");
		}
		return this.cookies;
	}

	/**
	 * Reads all .txt-files in the IE Cookie folder recursively. (Also reads the low privilege folders "Low")
	 *
	 * @param directoryPath
	 *            the path to the IE Cookie folder
	 * @param cookieFilePaths
	 *            the collection containing the cookies, or null
	 * @return the paths of all found .txt-files
	 */
	private Set<String> getFiles(String directoryPath, Set<String> cookieFilePaths) {
		if (cookieFilePaths == null) {
			cookieFilePaths = new HashSet<String>();
		}
		
		try {
			DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directoryPath));

			for (Path filePath : stream) {
				if (filePath.getFileName().toString().contains(".txt")) {
					cookieFilePaths.add(filePath.toAbsolutePath().toString());
				} else if ((new File(filePath.toAbsolutePath().toString())).isDirectory()) {
					getFiles(filePath.toAbsolutePath().toString(), cookieFilePaths);
				}
			}
		} catch (IOException | DirectoryIteratorException x) {
			System.err.println(x);
		}

		return cookieFilePaths;
	}
	
	/**
	 * Separates a cookie string into {@link Cookie} objects.
	 *
	 * @param cookie
	 *            Content of one cookie .txt-file
	 */
	private void parseCookieFile(String cookie) {
		String[] rawCookies = null;
		String[] cookieData = null;
		
		rawCookies = cookie.split("\\*\r\n");
		for (String rawCookie : rawCookies) {
			cookieData = rawCookie.split("\r\n");
			if (cookieData[2].charAt(cookieData[2].length() - 1) == '/') {
				cookieData[2] = cookieData[2].substring(0, cookieData[2].length() - 1);
			}

			Cookie c = new Cookie(cookieData[2], cookieData[0], cookieData[1]);
			this.cookies.add(c);
			log.debug("Found cookie: " + c.toString());
		}
	}
}