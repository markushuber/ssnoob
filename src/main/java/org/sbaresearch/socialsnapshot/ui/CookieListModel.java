package org.sbaresearch.socialsnapshot.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

import com.gargoylesoftware.htmlunit.util.Cookie;

/**
 * {@link ListModel} used for {@link JList} of {@link MainWindow}. This {@link ListModel} organizes the cookies for a given domain per browser. It displays all the cookies of this domain as a
 * concatenated string per browser.
 *
 * @author Stefan Haider (shaider@sba-research.org)
 */
public class CookieListModel extends DefaultListModel<String> {
	
	private static final long serialVersionUID = -6253724959368103725L;
	
	private Map<String, Set<Cookie>> cookieMap;
	private String cookieDomain;
	private List<String> browserList;

	/**
	 * @param domain
	 *            the cookie domain to organize
	 */
	public CookieListModel(String domain) {
		this.cookieMap = new HashMap<String, Set<Cookie>>();
		this.cookieDomain = domain;
		this.browserList = new LinkedList<String>();
	}

	/**
	 * @param element
	 *            the cookie collection to add to the browsers cookieset
	 * @param browser
	 *            the cookies source browser
	 */
	public void addAllElements(Collection<Cookie> elements, String browser) {
		if (elements != null) {
			for (Cookie c : elements) {
				addElement(c, browser);
			}
		}
	}
	
	/**
	 * @param element
	 *            the cookie to add to the browsers cookieset
	 * @param browser
	 *            the cookies source browser
	 */
	public void addElement(Cookie element, String browser) {
		if (element != null && browser != null) {
			Set<Cookie> browserCookieSet = this.cookieMap.get(browser);
			if (browserCookieSet == null) {
				browserCookieSet = new HashSet<Cookie>();
				this.cookieMap.put(browser, browserCookieSet);

				this.browserList.add(browser);
			}
			if (element.getDomain().contains(this.cookieDomain) && element.getValue().trim().length() > 0) {
				browserCookieSet.add(element);
			}

			int index = this.browserList.indexOf(browser);
			if (index > -1 && super.getSize() >= (index + 1)) {
				super.remove(index);
			}
			super.add(index, browser + "   " + browserCookieSet.size() + " cookies");
		}
	}

	/**
	 * Gets the browser string for the givend index in the {@link JList}
	 *
	 * @param listIndex
	 *            the index of the browser to retrieve
	 * @return the browser string
	 */
	public String getBrowser(int listIndex) {
		return this.browserList.get(listIndex);
	}
	
	/**
	 * Gets the cookies stored in this {@link ListModel} for a given index in the {@link JList}
	 *
	 * @param listIndex
	 *            the index of the cookie to retrieve
	 * @return the cookies
	 */
	public Set<Cookie> getBrowsersCookies(int listIndex) {
		return this.cookieMap.get(getBrowser(listIndex));
	}
	
	/**
	 * Gets the cookies stored in this {@link ListModel} for a given (selected) element String
	 *
	 * @param browser
	 *            the browser string for the cookies to retrieve
	 * @return the cookies
	 */
	public Set<Cookie> getBrowsersCookies(String browser) {
		return this.cookieMap.get(browser);
	}
}
