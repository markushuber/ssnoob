package org.sbaresearch.socialsnapshot.auth.fb;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sbaresearch.socialsnapshot.Config;
import org.sbaresearch.socialsnapshot.cookies.BrowserFinder;
import org.sbaresearch.socialsnapshot.cookies.BrowserType;
import org.sbaresearch.socialsnapshot.util.Database;
import org.sbaresearch.socialsnapshot.util.WebDriverFactory;

/**
 * This class uses the Selenium WebDriver to login to facebook and retrieve a access token.
 *
 * @author Stefan Haider (shaider@sba-research.org) original code: Maurice Wohlkönig
 */
@SuppressWarnings("deprecation")
public class FacebookAuthenticator implements IFacebookAuthenticator {

	private static Logger log = Logger.getLogger(FacebookAuthenticator.class);

	/**
	 * A number of cookie names that should be set in order to be able to access Facebook via Header Injection.
	 */
	protected static Set<String> cookieNames = new HashSet<String>();
	static {
		cookieNames.add("locale");
		cookieNames.add("datr");
		cookieNames.add("lu");
		cookieNames.add("sct");
		cookieNames.add("x-referer");
		cookieNames.add("lsd");
		cookieNames.add("c_user");
		cookieNames.add("cur_max_lag");
		cookieNames.add("sid");
		cookieNames.add("xs");
		cookieNames.add("e");
		cookieNames.add("openid_p");
		cookieNames.add("s");
		cookieNames.add("p");
		cookieNames.add("csm");
		cookieNames.add("presence");
	};

	/**
	 * The driver used for web scraping
	 */
	protected RemoteWebDriver driver;
	
	/**
	 * The browser type to use, used for generating user agent string
	 */
	protected BrowserType browser;

	/**
	 * Contains the unique token generated for the specified user identification credentials
	 */
	protected String uniqueToken;
	
	private Set<Cookie> sessionCookies;

	/**
	 * A list of permissions the app should ask for
	 *
	 * @see https://developers.facebook.com/docs/facebook-login/permissions/
	 */
	protected static final String[] appNeededPermissions = new String[] { "read_friendlists", "read_stream", "read_requests", "read_mailbox", "read_insights", "user_about_me", "friends_about_me",
			"user_activities", "friends_activities", "user_birthday", "friends_birthday", "user_checkins", "friends_checkins", "user_education_history", "friends_education_history", "user_events",
			"friends_events", "user_groups", "friends_groups", "user_hometown", "friends_hometown", "user_likes", "friends_likes", "user_location", "friends_location", "user_notes", "friends_notes",
			"user_photos", "friends_photos", "user_questions", "friends_questions", "user_relationships", "friends_relationships", "user_relationship_details", "friends_relationship_details",
			"user_religion_politics", "friends_religion_politics", "user_status", "friends_status", "user_videos", "friends_videos", "user_website", "friends_website", "user_work_history",
			"friends_work_history", "friends_online_presence", "user_online_presence", "manage_pages" };
	
	public FacebookAuthenticator() {

	}
	
	/**
	 * Checks if there is a valid access token saved for the specified identifier
	 *
	 * @param callback
	 *            a callback which will be notified in case of a found token
	 * @param uniqueIdentifier
	 *            the unique identifier specified for the token
	 * @return true if a token was found false otherwise
	 * @deprecated not used as it's not working with removing the app after every download session
	 */
	@Deprecated
	public boolean checkLocal(IAuthenticatorCallback callback, String uniqueIdentifier) {
		callback.statusUpdate(State.CHECKLOCAL);
		String savedToken = Database.hasValidAPIKeySaved(uniqueIdentifier);
		if (savedToken != null) {
			callback.onSuccess(savedToken);
			return true;
		} else
			return false;
	}

	@Override
	public boolean checkSessionCookieValidity(Set<com.gargoylesoftware.htmlunit.util.Cookie> cookie, BrowserType browser) {
		this.browser = browser;
		Config.setBrowserName(BrowserFinder.getInstance().getUserAgent(browser));
		prepareCookies(cookie);
		initDriver();
		boolean valid = checkForCookies();
		disposeDriver();
		return valid;
	}
	
	@Override
	public void cleanUpFacebook() {
		try {
			if (this.driver == null) {
				initDriver();
			}
			if (checkForCookies()) {
				log.info("Try to remove FB app from user again");

				this.driver.get("https://www.facebook.com/settings?tab=applications");
				
				WebElement webSiteElement = null;
				
				try {
					// find the application in list
					webSiteElement = this.driver.findElement(By.xpath("//*[@id=\"application-li-" + Config.getGraphAppId() + "\"]/div[1]/a[2]"));
					webSiteElement.click();
				} catch (NoSuchElementException e) {
					// when the app was eventually partially added before
					log.debug("Delete app element not found on page", e);
				}
				try {
					// check delete all box
					webSiteElement = this.driver.findElement(By.name("delete_app_actions"));
					webSiteElement.click();
				} catch (NoSuchElementException e) {
					// when the app was eventually partially added before
					log.debug("Delete app checkbox not found on page", e);
				}
				try {
					// remove app //*
					webSiteElement = this.driver.findElement(By.id("pop_content"));
					webSiteElement = webSiteElement.findElement(By.name("ok"));
					webSiteElement.click();
				} catch (NoSuchElementException e) {
					// when the app was eventually partially added before
					log.debug("Delete app ok button not found on page", e);
				}
			}
			
		} catch (Exception e) {
			log.error("Error occured removing FB app", e);
		} finally {
			disposeDriver();
		}
	}

	@Override
	public void getAccessToken(IAuthenticatorCallback callback, Set<com.gargoylesoftware.htmlunit.util.Cookie> cookie, BrowserType browser) {
		prepareCookies(cookie);
		getAccessTokenCommon(callback, browser);
	}

	@Override
	public void getAccessToken(IAuthenticatorCallback callback, String cookie, BrowserType browser) {
		prepareCookies(cookie);
		getAccessTokenCommon(callback, browser);
	}

	@Override
	public void getAccessToken(final IAuthenticatorCallback callback, String mail, String password) {
		this.browser = BrowserType.DEFAULT;
		Config.setBrowserName(BrowserFinder.getInstance().getUserAgent(this.browser));
		// if (checkLocal(callback, createUniqueIdentifier(mail, password)))
		// return;

		try {
			callback.statusUpdate(State.INIT);
			initDriver();

			// log in using the input fields
			WebElement element = this.driver.findElement(By.id("email"));
			element.sendKeys(mail);

			(new WebDriverWait(this.driver, 15)).until(new ExpectedCondition<Boolean>() {

				@Override
				public Boolean apply(WebDriver driver) {
					try {
						return driver.findElement(By.id("pass")) != null;
					} catch (NoSuchElementException e) {
						return false;
					}
				}

			});

			element = this.driver.findElement(By.id("pass"));
			element.sendKeys(password);

			element.submit();
			
			// unknown login source
			if (this.driver.getCurrentUrl().contains("facebook.com/checkpoint/")) {
				try {
					element = this.driver.findElementByName("submit[Continue]");
					if (element != null) {
						element.click();
					}
				} catch (NoSuchElementException e) {
					log.error("Error passing facebook.com/checkpoint/", e);
				}
				// try {
				// element = driver.findElement(By.id("machine_name"));
				// } catch (NoSuchElementException e) {
				// element = driver.findElementByName("submit[Continue]");
				// if (element != null) {
				// element.click();
				// element = driver.findElementByName("submit[This is Okay]");
				// element.click();
				// element = driver.findElement(By.id("machine_name"));
				// } else
				// return;
				// }
				// element.sendKeys(Config.getFakeLoginDeviceName());
				//
				// element = driver.findElement(By.name("submit[Save Device]"));
				// element.click();
			}

			callback.statusUpdate(State.CHECKCOOKIES);
			if (checkForCookies()) {
				onLoggedIn(callback);
			} else {
				callback.onFailure(IAuthenticatorCallback.ERROR_COOKIES, "The user login cookies where not set after a reload - something went wrong");
			}
		} catch (Exception e) {
			log.error("was unable to retrieve access token - please try again", e);
		} finally {
			// driver needed for cleanup don't dispose
			// disposeDriver();
		}
	}

	protected void onLoggedIn(IAuthenticatorCallback callback) {
		addApplication(callback);
	}

	/**
	 * Adds the specified application to the users profile while in a running session
	 *
	 * @param callback
	 */
	private void addApplication(IAuthenticatorCallback callback) {
		callback.statusUpdate(State.ADD_APPLICATION);

		// add the app to the user
		this.driver.get(Config.getGraphAppWebsite());

		WebElement elem = null;
		String authUrl = null;
		String accessToken = null;
		
		try {
			elem = this.driver.findElement(By.id("addapp"));
			authUrl = elem.getAttribute("href");
		} catch (NoSuchElementException e) {
			// when there is something really wrong
			log.debug("No fb auth button found on page, maybe already authenticated");
		}
		if (authUrl != null) {
			this.driver.get(authUrl);

			try {
				// grand the application basic privileges
				elem = this.driver.findElement(By.name("__CONFIRM__")); // By.name("grant_required_clicked"));
				elem.click();
			} catch (NoSuchElementException e) {
				// when the app was eventually partially added before
				log.debug("No confirm element found");
			}

			this.driver.navigate().refresh();

			try {
				// grant the application extended privileges
				elem = this.driver.findElement(By.name("__CONFIRM__")); // By.name("grant_clicked"));
				elem.click();
			} catch (NoSuchElementException e) {
				// app was already confirmed before
				log.debug("No confirm element #2 found");
			}

			(new WebDriverWait(this.driver, 35)).until(new ExpectedCondition<Boolean>() {

				@Override
				public Boolean apply(WebDriver driver) {
					try {
						return driver.getCurrentUrl().contains("code=");
					} catch (UnhandledAlertException e) { // handle a present error
						// dialog
						driver.switchTo().alert().accept();
						return false;
					}
				}

			});
		}
		
		try {
			elem = this.driver.findElement(By.id("token"));
			accessToken = elem.getAttribute("value");
		} catch (NoSuchElementException e) {
			// when there is something really wrong
			log.error("No fb auth token found on page");
		}
		
		if (accessToken != null && accessToken.length() > 0) {
			// Date validUntil = getValidUntilFromSource(this.driver.getPageSource());
			// callback.statusUpdate(State.SAVING_TOKEN);
			// DONT save API key in DB
			// Database.saveAPIKey(this.uniqueToken, accessToken, validUntil);
			callback.onSuccess(accessToken);
		} else {
			callback.onFailure(IAuthenticatorCallback.ERROR_CODE, "there was no error code returned by the Graph API");
		}

	}

	/**
	 * Checks for a running session if the user cookie is set (ie the user was sucessfully logged in)
	 *
	 * @return true if login, false otherwise
	 */
	private boolean checkForCookies() {
		// reload page for cookie test
		this.driver.get("http://www.facebook.com");

		Cookie userCookie = this.driver.manage().getCookieNamed("c_user");

		return userCookie != null && !userCookie.getValue().isEmpty();
	}

	/**
	 * Creates a unique identifier out of the specified cookie string
	 */
	private String createUniqueIdentifier(String cookie) {
		byte[] bytesOfMessage;
		try {
			bytesOfMessage = cookie.getBytes("UTF-8");

			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(bytesOfMessage);
			BigInteger bigInt = new BigInteger(1, thedigest);
			this.uniqueToken = bigInt.toString(16);
			return this.uniqueToken;
		} catch (NoSuchAlgorithmException e) {
			log.error("Error creating hashing method", e);
		} catch (UnsupportedEncodingException e) {
			log.error("Error encoding cookie string", e);
		}
		return null;
	}
	
	/**
	 * Creates a unique identifier of the specified email and password
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private String createUniqueIdentifier(String mail, String password) {
		return createUniqueIdentifier(mail + password);
	}

	private void disposeDriver() {
		if (this.driver != null) {
			try {
				this.sessionCookies = this.driver.manage().getCookies();

				this.driver.close();
				this.driver.quit();
				this.driver = null;
			} catch (Exception e) {
				// silent exception
			}
		}
	}

	private void getAccessTokenCommon(IAuthenticatorCallback callback, BrowserType browser) {
		this.browser = browser;
		Config.setBrowserName(BrowserFinder.getInstance().getUserAgent(browser));
		// if (checkLocal(callback, createUniqueIdentifier(cookie)))
		// return;

		try {
			
			callback.statusUpdate(State.INIT);
			initDriver();

			callback.statusUpdate(State.CHECKCOOKIES);
			if (checkForCookies()) {
				onLoggedIn(callback);
			} else {
				callback.onFailure(IAuthenticatorCallback.ERROR_COOKIES, "The user login cookies where not set after a reload - something went wrong");
			}
		} catch (Exception e) {
			log.error("was unable to retrieve access token - please try again", e);
		} finally {
			// driver needed for cleanup don't dispose
			// disposeDriver();
		}
	}
	
	@SuppressWarnings("unused")
	@Deprecated
	private Date getValidUntilFromSource(String source) {
		int index = 0;
		int validSeconds = 600;
		if ((index = source.indexOf("expires=")) != -1) {
			try {
				validSeconds = Integer.parseInt(source.substring(index + 8));
			} catch (NumberFormatException e) {
				log.error("Cant parse access token validat time - setting lifetime of 600 seconds");
			}
		} else {
			log.error("Cant find expiriration info in browser source - using lifetime of 600 seconds");
		}

		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.SECOND, validSeconds);
		return cal.getTime();
	}

	/**
	 * Initializes the browser window if there are cookies in {@link #sessionCookies} they are added to the session.
	 */
	private void initDriver() {
		if (this.driver == null) {
			this.driver = WebDriverFactory.getInstance().newPhantomJSDriver(this.browser);
			
			// Open Facebook and wait until the page has loaded;
			this.driver.get("http://www.facebook.com/");
			
			// now you can set cookies
			if (this.sessionCookies != null && !this.sessionCookies.isEmpty()) {
				for (Cookie c : this.sessionCookies) {
					this.driver.manage().addCookie(c);
				}
			}
		}
	}
	
	private void prepareCookies(Set<com.gargoylesoftware.htmlunit.util.Cookie> cookie) {
		this.sessionCookies = new HashSet<Cookie>();
		// ADD ALL THE COOKIES
		for (com.gargoylesoftware.htmlunit.util.Cookie singleCookie : cookie) {
			String name, value;
			Cookie nCookie;

			name = singleCookie.getName();
			value = singleCookie.getValue();

			if (cookieNames.contains(name)) {

				nCookie = new Cookie(name, value, ".facebook.com", "/", singleCookie.getExpires());
				this.sessionCookies.add(nCookie);
			}
			
		}
	}
	
	private void prepareCookies(String cookie) {
		this.sessionCookies = new HashSet<Cookie>();
		// cookies are done in ...
		final Date cookieDate = new Date(new Date().getTime() + (3600 * 24));
		// ADD ALL THE COOKIES
		for (String singleCookie : cookie.split(";")) {
			String name, value;
			Cookie nCookie;
			int equalsIndex = singleCookie.indexOf('=');
			if (equalsIndex != -1) {
				name = singleCookie.substring(0, equalsIndex).trim();
				value = singleCookie.substring(equalsIndex + 1).trim();

				if (cookieNames.contains(name)) {

					nCookie = new Cookie(name, value, ".facebook.com", "/", cookieDate);
					this.sessionCookies.add(nCookie);
				}
			}
		}
	}
}
