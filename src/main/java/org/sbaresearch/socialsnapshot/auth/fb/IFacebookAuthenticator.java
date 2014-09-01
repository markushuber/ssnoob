package org.sbaresearch.socialsnapshot.auth.fb;

import java.util.Set;

import org.sbaresearch.socialsnapshot.cookies.BrowserType;

import com.gargoylesoftware.htmlunit.util.Cookie;

/**
 * @author Maurice Wohlkönig
 */
public interface IFacebookAuthenticator {

	/**
	 * Trys to connect to Facebook to check if given cookies are valid.
	 *
	 * @param cookie
	 *            the cookies to check
	 * @param browser
	 *            the user agent to use
	 * @return true if cookies are valid, false otherwise
	 */
	public abstract boolean checkSessionCookieValidity(Set<Cookie> cookie, BrowserType browser);

	/**
	 * Trys to restore Facebook user to original state by removing the app from the account.
	 */
	public abstract void cleanUpFacebook();

	/**
	 * Logs into the Facebook Graph API using the specified settings and calling the <em>callback</em> with the result.
	 *
	 * @param callback
	 *            a callback to be invoked when the access token is received or an error occurs
	 * @param cookie
	 *            a Facebook cookie string to use for logging in
	 */
	public abstract void getAccessToken(IAuthenticatorCallback callback, Set<Cookie> cookie, BrowserType browser);
	
	/**
	 * Logs into the Facebook Graph API using the specified settings and calling the <em>callback</em> with the result.
	 *
	 * @param callback
	 *            a callback to be invoked when the access token is received or an error occurs
	 * @param cookie
	 *            a Facebook cookie string to use for logging in
	 */
	public abstract void getAccessToken(IAuthenticatorCallback callback, String cookie, BrowserType browser);
	
	/**
	 * Logs into the Facebook Graph API using the specified settings and calling the <em>callback</em> with the result.
	 *
	 * @param callback
	 *            a callback to be invoked when the access token is received or an error occurs
	 * @param mail
	 *            the username of the user to authenticate
	 * @param password
	 *            the password of the user to authenticate
	 */
	public abstract void getAccessToken(IAuthenticatorCallback callback, String mail, String password);

	public interface IAuthenticatorCallback {

		public static int ERROR_CODE = 2;
		public static int ERROR_COOKIES = 1;

		void onFailure(int errorCode, final String errorMessage);

		void onSuccess(final String accessToken);

		void statusUpdate(State state);
	}

	public static enum State {
		INIT(2), CHECKLOCAL(1), CHECKCOOKIES(3), ADD_APPLICATION(4), SAVING_TOKEN(5);

		private int number;

		State(int number) {
			this.number = number;
		}

		public int getNumber() {
			return number;
		}
	}
}