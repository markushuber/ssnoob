package org.sbaresearch.socialsnapshot.cookies;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.sbaresearch.socialsnapshot.cookies.extractor.ChromeCookieExtractor;
import org.sbaresearch.socialsnapshot.cookies.extractor.FirefoxCookieExtractor;
import org.sbaresearch.socialsnapshot.cookies.extractor.InternetExplorer;

import com.gargoylesoftware.htmlunit.util.Cookie;

/**
 * Central class for handling the browser cookie extraction.
 *
 * @author Stefan Haider (shaider@sba-research.org)
 */
public class CookieExtractor {
	
	private static CookieExtractor instance;
	private static Logger log = Logger.getLogger(CookieExtractor.class);

	private BrowserFinder findBrowser;

	private ChromeCookieExtractor chrome;
	private FirefoxCookieExtractor firefox;
	private InternetExplorer internetExplorer;

	private CookieExtractor() {
		this.findBrowser = BrowserFinder.getInstance();
	}

	public Set<Cookie> getAllCookies() {
		Set<Cookie> allCookies = new HashSet<Cookie>();
		for (BrowserType type : BrowserType.values()) {
			allCookies.addAll(getCookies(type));
		}
		return allCookies;
	}
	
	public Set<Cookie> getCookies(BrowserType type) {
		switch (type) {
			case Chrome:
				if (this.findBrowser.isChromeAvailable()) {
					this.chrome = new ChromeCookieExtractor();
					return this.chrome.getCookies();
				}
				break;
			case Firefox:
				if (this.findBrowser.isFirefoxAvailable()) {
					this.firefox = new FirefoxCookieExtractor();
					return this.firefox.getCookies();
				}
			case InternetExplorer:
				if (this.findBrowser.isIeAvailable()) {
					this.internetExplorer = new InternetExplorer();
					return this.internetExplorer.getCookies();
				}
				break;
			default:
				break;
		
		}
		return new HashSet<Cookie>();
	}
	
	/**
	 * Gets the singleton instance of this class.
	 *
	 * @return the instance
	 */
	public static CookieExtractor getInstance() {
		if (instance == null) {
			log.debug("Creating CookieExtractor");
			instance = new CookieExtractor();
		}
		return instance;
	}
}
