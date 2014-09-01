package org.sbaresearch.socialsnapshot.testing;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sbaresearch.socialsnapshot.auth.fb.IFacebookAuthenticator;
import org.sbaresearch.socialsnapshot.auth.fb.IFacebookAuthenticator.IAuthenticatorCallback;
import org.sbaresearch.socialsnapshot.auth.fb.IFacebookAuthenticator.State;

public class FacebookAuthenticatorTest {
	
	IFacebookAuthenticator auth;
	// TODO insert valid cookie string
	@SuppressWarnings("unused")
	private static final String cookieString = "";
	@SuppressWarnings("unused")
	private static IAuthenticatorCallback callback = new IAuthenticatorCallback() {

		@Override
		public void onFailure(int errorCode, String errorMessage) {
			fail("Should work with this cookie");
		}

		@Override
		public void onSuccess(String accessToken) {
			Assert.assertNotNull(accessToken);
			Assert.assertTrue(accessToken.length() > 0);
		}

		@Override
		public void statusUpdate(State state) {
			// nothing
		}
	};
	
	@Before
	public void setUp() throws Exception {
		// TODO implement
		// Config.init(new String[0]);
		// this.auth = new FacebookAuthenticator();
	}
	
	@After
	public void tearDown() throws Exception {
		// TODO implement
		// this.auth.cleanUpFacebook();
	}

	@Test
	public void testGetAccessTokenIAuthenticatorCallbackCookie() {
		// TODO implement
		// this.auth.getAccessToken(callback, cookieString, BrowserType.Chrome);
	}
}
