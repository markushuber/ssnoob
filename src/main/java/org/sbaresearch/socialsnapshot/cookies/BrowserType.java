package org.sbaresearch.socialsnapshot.cookies;

/**
 * Enumeration for the different supported browser types.
 *
 * @author Stefan Haider (shaider@sba-research.org)
 */
public enum BrowserType {
	InternetExplorer, Chrome, Firefox, DEFAULT;
	
	public static BrowserType parseBrowserType(String browser) {
		if (browser.toLowerCase().matches("chrome.*"))
			return Chrome;
		else if (browser.toLowerCase().matches("firefox.*"))
			return Firefox;
		else if (browser.toLowerCase().matches("internet.?explorer.*"))
			return InternetExplorer;
		return DEFAULT;
	}
}
