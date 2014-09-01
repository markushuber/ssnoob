package org.sbaresearch.socialsnapshot.util;

import java.io.File;

import net.anthavio.phanbedder.Phanbedder;

import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.sbaresearch.socialsnapshot.cookies.BrowserFinder;
import org.sbaresearch.socialsnapshot.cookies.BrowserType;

/**
 * Factory class for creating a new WebDriver instance
 *
 * @author Stefan Haider (shaider@sba-research.org), original code: Maurice Wohlkönig
 */
public class WebDriverFactory {

	private static WebDriverFactory factory;
	private static DesiredCapabilities capabilities;

	private WebDriverFactory() {

	}

	// public RemoteWebDriver newChromeDriver() {
	// return new ChromeDriver(capabilities);
	// }
	//
	// public RemoteWebDriver newFirefoxDriver() {
	// return new FirefoxDriver(capabilities);
	// }
	//
	// public RemoteWebDriver newInternetExplorerDriver() {
	// return new InternetExplorerDriver(capabilities);
	// }

	public RemoteWebDriver newPhantomJSDriver(BrowserType browser) {
		File phantomjs = Phanbedder.unpack();

		capabilities = DesiredCapabilities.htmlUnit();
		capabilities.setJavascriptEnabled(true);
		capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjs.getAbsolutePath());

		// capabilities.setCapability("phantomjs.page.settings.userAgent", BrowserFinder.getInstance().getUserAgent(browser));
		capabilities.setBrowserName(BrowserFinder.getInstance().getUserAgent(browser));
		return new PhantomJSDriver(capabilities);
	}

	public static final WebDriverFactory getInstance() {
		if (factory == null) {
			factory = new WebDriverFactory();
		}
		return factory;
	}
}
