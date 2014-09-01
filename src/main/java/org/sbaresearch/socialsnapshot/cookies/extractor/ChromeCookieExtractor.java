package org.sbaresearch.socialsnapshot.cookies.extractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.sbaresearch.socialsnapshot.util.Utils;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;

import com.gargoylesoftware.htmlunit.util.Cookie;
import com.sun.jna.platform.win32.Crypt32Util;

/**
 * This class provides access to google chromes cookies.
 *
 * @author Stefan Haider (shaider@sba-research.org), original code: Martin Grottenthaler
 */
public class ChromeCookieExtractor extends AbstractSQLiteBrowserCookieExtractor {
	
	private static final String chromeTable = "cookies";
	
	private static final String[] chromeCollumn = { "name", "encrypted_value", "expires_utc", "path", "host_key", "secure", "httponly" };
	
	static {
		log = Logger.getLogger(ChromeCookieExtractor.class);
	}
	
	private Cipher aesCipher;
	
	/**
	 * Default constructor.
	 */
	public ChromeCookieExtractor() {
		super(chromeTable);
		if (Utils.onLinux()) {
			initLinuxCipher();
		}
	}

	@Override
	protected void prepareFile(File target) {
		File source = null;
		
		if (Utils.getOsName().equals("Linux")) {
			source = new File(System.getProperty("user.home") + File.separator + ".config" + File.separator + "google-chrome" + File.separator + "Default" + File.separator + "Cookies");
		}

		// Pre Win 7
		if (Utils.getOsName().contains("Windows")) {
			source = new File(System.getProperty("user.home") + File.separator + "Application Data" + File.separator + "Google" + File.separator + "Chrome" + File.separator + "User Data"
					+ File.separator + "Default" + File.separator + "Cookies");
			// Win 7+
			if (!source.exists()) {
				source = new File(System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Local" + File.separator + "Google" + File.separator + "Chrome" + File.separator
						+ "User Data" + File.separator + "Default" + File.separator + "Cookies");
			}
		}
		
		try {
			Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			log.error("Error copying " + target.getName(), e);
		}
	}

	@Override
	protected void readCookiesFromCursor(ISqlJetCursor cursor) throws NumberFormatException, SqlJetException {
		String[] cookiesStr = new String[7];
		
		try {
			if (!cursor.eof()) {
				do {
					Date expires = new Date();
					;
					boolean secure = false;
					boolean httpOnly = false;

					for (int i = 0; i < chromeCollumn.length; i++) {
						cookiesStr[i] = cursor.getString(chromeCollumn[i]);
					}
					byte[] encryptedCookie = cursor.getBlobAsArray(chromeCollumn[1]);
					byte[] decryptedCookie = null;
					// decrypt cookies on windows
					if (Utils.onWindows()) {
						decryptedCookie = Crypt32Util.cryptUnprotectData(encryptedCookie);
					}
					// decryption of cookies on linux http://stackoverflow.com/questions/23153159/decrypting-chrome-iums-cookies
					if (Utils.onLinux()) {
						String encryptedString = new String(encryptedCookie);
						// if cookies are encrypted "v10" is a the prefix (has to be removed before decryption)
						if (encryptedString.startsWith("v10")) {
							encryptedCookie = Arrays.copyOfRange(encryptedCookie, 3, encryptedCookie.length);
						}
						try {
							decryptedCookie = this.aesCipher.doFinal(encryptedCookie);
						} catch (IllegalBlockSizeException | BadPaddingException e) {
							log.error("Error decrypting chrome cookies on linux", e);
						}
					}
					cookiesStr[1] = new String(decryptedCookie);
					
					// chrome: ldap time (if 17 digits add 1 random digit(use 0 out of convenience))
					cookiesStr[2] = cookiesStr[2].concat("0");
					long timeNoC = Long.parseLong(cookiesStr[2]);

					// source: http://www.epochconverter.com/epoch/ldap-timestamp.php
					// formula 1: LDAP time = (time()+11644473600)*10000000
					// formala 2: LDAP time / 10000000 - 11644473600 = time()
					timeNoC = timeNoC / 10000000L - 11644473600L;

					expires.setTime(timeNoC * 1000);
					
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

	private void initLinuxCipher() {
		try {
			byte[] salt = "saltysalt".getBytes();
			char[] password = "peanuts".toCharArray();
			char[] iv = new char[16];
			Arrays.fill(iv, ' ');
			int keyLength = 16;

			int iterations = 1;

			PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength * 8);
			SecretKeyFactory pbkdf2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			
			byte[] aesKey = pbkdf2.generateSecret(spec).getEncoded();
			
			SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
			
			this.aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			this.aesCipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(new String(iv).getBytes()));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | InvalidKeySpecException e) {
			log.error("Error initializing decryption for chrome cookies on linux", e);
		}
	}
}
