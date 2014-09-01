package org.sbaresearch.socialsnapshot.crawl.entity.connections;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.sbaresearch.socialsnapshot.crawl.entity.FbObject;
import org.sbaresearch.socialsnapshot.util.Utils;

/**
 * @author Maurice Wohlkönig
 */
public abstract class FbConnection {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(FbConnection.class);

	public static final String TYPE_MEMBER = "member";
	public static final String TYPE_FRIENDS = "friends";
	public static final String TYPE_ACCOUNTS = "accounts";
	public static final String TYPE_PHOTOS = "photos";
	public static final String TYPE_POSTS = "posts";
	public static final String TYPE_HOME = "home";
	public static final String TYPE_COMMENTS = "comments";
	public static final String TYPE_MUTUALFRIENDS = "mutualfriends";
	protected String type;
	protected String url;

	// this is set by the graph api response
	protected Paging paging;

	/**
	 * Priority for downloading of this fb object. 0 is highest.
	 */
	protected int priority = 0;

	public FbConnection(String type, String url, int priority) {
		this.type = type;
		this.url = url;
		this.priority = priority;
	}

	public abstract Class<? extends FbObject> getConnectionObjectClass();

	public abstract ArrayList<? extends FbObject> getConnectionObjects();

	public abstract int getDataCount();

	public String getGraphUrl() {
		return hasNextPage() ? this.paging.next : this.url;
	}

	public int getPriority() {
		return this.priority;
	}

	public String getType() {
		return this.type;
	}

	/**
	 * We can not check the paging {@link #getCount()} method as facebook does not always fill their lists up to the max
	 *
	 * @return true if there is a next page for this connection
	 */
	public boolean hasNextPage() {
		return this.paging != null && this.paging.next != null && this.paging.next.length() > 1;
	}

	public void onComplete() {
		Utils.searchForDownloadUrls(this);
	}

	public void setType(String tempType) {
		this.type = tempType;
	}

	public static FbConnection getObjectFor(String type, String url, int priority) {
		if (type.equals(TYPE_MEMBER) || type.equals(TYPE_FRIENDS))
			return new UserConnection(type, url, priority);
		if (type.equals(TYPE_ACCOUNTS) && url.contains("/me/")) // the accounts request always returns the accounts of the TOKEN owner user, so there is no need
			// to request them for all users
			return new AccountsConnection(type, url, priority);
		if (type.equals(TYPE_PHOTOS))
			return new PhotoConnection(type, url, priority);
		if (type.equals(TYPE_POSTS))
			return new PostConnection(type, url, priority);
		if (type.equals(TYPE_HOME) && url.contains("/me/"))
			return new HomeConnection(type, url, priority);
		if (type.equals(TYPE_MUTUALFRIENDS))
			return new UserConnection(type, url, priority);
		return null;
	}

	public static class Paging {

		String next;
	}
}
