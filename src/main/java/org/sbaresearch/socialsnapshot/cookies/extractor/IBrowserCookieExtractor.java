package org.sbaresearch.socialsnapshot.cookies.extractor;

import java.util.Set;

import com.gargoylesoftware.htmlunit.util.Cookie;

/**
 * A common interface defined for all cookie extractors
 * 
 * @author Stefan Haider (shaider@sba-research.org)
 */
public interface IBrowserCookieExtractor {

	/**
	 * Gets the cookies from the {@link IBrowserCookieExtractor} instance.
	 *
	 * @return the cookies
	 */
	public Set<Cookie> getCookies();
}
