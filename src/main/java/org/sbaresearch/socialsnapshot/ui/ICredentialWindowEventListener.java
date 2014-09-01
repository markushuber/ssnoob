package org.sbaresearch.socialsnapshot.ui;

import org.sbaresearch.socialsnapshot.cookies.BrowserType;

/**
 * Listener for varios Window events
 *
 * @author Maurice Wohlkönig
 */
public interface ICredentialWindowEventListener {

	/**
	 * Will be called when the window was closed
	 */
	public void onClose();

	/**
	 * Will be called when the user pressed start on the cookie tab
	 *
	 * @param usercookie
	 */
	public boolean onUserCookieProvided(String usercookie, BrowserType browser);

	/**
	 * Will be called when the user pressed start on the user credentials tab
	 *
	 * @param username
	 *            the entered username
	 * @param password
	 *            the entered password
	 */
	public boolean onUserCredentialsProvided(String username, String password);
}