package org.sbaresearch.socialsnapshot.cookies.extractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import org.apache.log4j.Logger;
import org.sbaresearch.socialsnapshot.util.Utils;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;

import com.gargoylesoftware.htmlunit.util.Cookie;

/**
 * This class provides access to firefox cookies.
 *
 * @author Stefan Haider (shaider@sba-research.org), original code: Martin Grottenthaler
 */
public class FirefoxCookieExtractor extends AbstractSQLiteBrowserCookieExtractor {
	
	private static final String firefoxTable = "moz_cookies";
	
	private static final String[] firefoxColumn = { "name", "value", "expiry", "path", "baseDomain", "isSecure", "isHttpOnly" };

	static {
		log = Logger.getLogger(FirefoxCookieExtractor.class);
	}

	/**
	 * Default constructor.
	 */
	public FirefoxCookieExtractor() {
		super(firefoxTable);
	}

	@Override
	protected void prepareFile(File target) {
		if (Utils.getOsName().equals("Linux")) {
			prepareFileFromPath(System.getProperty("user.home") + File.separator + ".mozilla" + File.separator + "firefox", target);
		} else if (Utils.getOsName().contains("Windows")) {
			String profilePath = null;
			
			// Pre Win 7
			profilePath = System.getProperty("user.home") + File.separator + "Application Data" + File.separator + "Mozilla" + File.separator + "Firefox" + File.separator + "Profiles";
			// Win 7+
			if (!(new File(profilePath)).exists()) {
				profilePath = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "Mozilla" + File.separator + "Firefox" + File.separator
						+ "Profiles";
			}
			prepareFileFromPath(profilePath, target);
		}
	}
	
	@Override
	protected void readCookiesFromCursor(ISqlJetCursor cursor) throws NumberFormatException, SqlJetException {
		String[] cookiesStr = new String[7];
		
		try {
			if (!cursor.eof()) {
				do {
					Date expires = new Date();

					boolean secure = false;
					boolean httpOnly = false;
					
					for (int i = 0; i < firefoxColumn.length; i++) {
						cookiesStr[i] = cursor.getString(firefoxColumn[i]);
					}
					
					// firefox: unix time (sometimes unix time in microseconds)
					expires.setTime(Long.parseLong(cookiesStr[2]) * 1000);
					
					if (!cookiesStr[5].equals(0)) {
						secure = true;
					}
					
					if (!cookiesStr[6].equals(0)) {
						httpOnly = true;
					}
					
					Cookie c = new Cookie(cookiesStr[4], cookiesStr[0], cookiesStr[1], cookiesStr[3], expires, secure, httpOnly);
					this.cookies.add(c);
					log.debug("Found cookie: " + c.toString());
				} while (cursor.next());
			}
		} finally {
			cursor.close();
		}
	}
	
	private void prepareFileFromPath(String profilePath, File target) {
		File source = null;
		
		File folder = new File(profilePath);
		File[] fileList = folder.listFiles();
		
		String profile = null;
		
		for (int i = 0; i < fileList.length; i++) {
			File file = fileList[i];
			
			if (file.getName().contains(".default")) {
				profile = file.getName();
			}
		}
		
		source = new File(folder, profile + File.separator + "cookies.sqlite");

		try {
			Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			log.error("Error copying " + target.getName(), e);
		}
		
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(target, "rw");
		} catch (FileNotFoundException e) {
			log.error("Error opening " + target.getName(), e);
		}

		// change byte 18 and 19 of file to 01 in case of firefox
		try {
			raf.seek(18);
			raf.write(01);
			
			raf.seek(19);
			raf.write(01);
		} catch (IOException e) {
			log.error("Error writing " + target.getName(), e);
		} finally {
			try {
				raf.close();
			} catch (IOException e) {
				log.error("Error closing " + target.getName(), e);
			}
		}
		target.deleteOnExit();
	}
}
