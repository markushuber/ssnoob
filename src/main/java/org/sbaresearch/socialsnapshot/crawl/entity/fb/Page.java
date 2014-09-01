package org.sbaresearch.socialsnapshot.crawl.entity.fb;

import java.io.Serializable;
import java.util.HashMap;

import org.sbaresearch.socialsnapshot.crawl.entity.FbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.connections.AccountsConnection.AccountsConnectionData;
import org.sbaresearch.socialsnapshot.crawl.entity.connections.FbConnection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Maurice Wohlkönig
 */
public class Page extends FbObject implements Serializable {

	private static final long serialVersionUID = -3766664556380506496L;

	public static final String TYPE = "application";

	/**
	 * Permissions the user has in this page. Only set when created from a user accounts connection list
	 */
	@Expose(deserialize = false)
	String[] perms;

	@Expose
	String id, name, link, website, about, category, mission, products, username, phone, awards;
	@Expose
	@SerializedName("can_post")
	boolean canPost;
	@Expose
	@SerializedName("is_published")
	boolean isPublished;
	@Expose
	@SerializedName("new_like_count")
	int newLikeCount;
	@Expose
	@SerializedName("promotion_ineligible_reason")
	String promotionIneligibleReason;
	@Expose
	@SerializedName("talking_about_count")
	int talkingAboutCount;
	@Expose
	@SerializedName("unread_message_count")
	int unreadMessageCount;
	@Expose
	@SerializedName("unread_notif_count")
	int unreadNotifCount;
	@Expose
	@SerializedName("unseen_message_count")
	int unseenMessageCount;
	@Expose
	@SerializedName("were_here_count")
	int wereHereCount;
	int likes, checkins;
	@Expose
	PageCoverPhoto cover;
	@Expose
	PageLocation location;
	@Expose
	@SerializedName("global_brand_parent_page")
	Page brandParentPage;
	@Expose
	PageHours hours;
	@Expose
	@SerializedName("general_manager")
	String generalManager;
	@Expose
	@SerializedName("payment_options")
	PagePaymentOptions paymentOptions;
	@Expose
	@SerializedName("public_transit")
	String publicTransit;
	@Expose
	@SerializedName("restaurant_services")
	PageRestaurant restaurantServices;
	@Expose
	@SerializedName("restaurant_specialties")
	PageRestaurant restaurantSpecialities;

	/**
	 * Constructor used when creating from a user/accounts connection
	 *
	 * @param entry
	 * @param priority
	 * @param depth
	 */
	public Page(AccountsConnectionData entry, int priority, int depth) {
		this(depth);
		this.priority = priority;
		this.id = entry.id;
		this.name = entry.name;
		this.perms = entry.perms;
	}

	public Page(int depth) {
		super(depth);
	}

	@Override
	public String getGraphUrl() {
		return genGraphUrl(this.id);
	}

	@Override
	public String getType() {
		return "account";
	}

	@Override
	protected void handleConnection(FbConnection con) {

	}

	@Override
	protected String[] requestFields() {
		return null;
	}

	public static class PageCoverPhoto implements Serializable {

		private static final long serialVersionUID = 2990552512949710003L;
		@Expose
		@SerializedName("cover_id")
		public String id;
		@Expose
		public String source;
		@Expose
		@SerializedName("offset_y")
		public String offsetY;
	}

	public static class PageHours extends HashMap<String, String> {

		private static final long serialVersionUID = -785847814718955746L;
	}

	public static class PageLocation implements Serializable {

		private static final long serialVersionUID = 542640761871844652L;
		@Expose
		public String street, city, state, country, zip;
		@Expose
		public double latitude, longitude;
	}

	public static class PagePaymentOptions implements Serializable {

		private static final long serialVersionUID = -1484268828891919012L;
		@Expose
		@SerializedName("cash_only")
		int cashOnly;
		@Expose
		int visa, amex, mastercard, discover;
	}

	public static class PageRestaurant extends HashMap<String, Integer> {

		private static final long serialVersionUID = 2029652610553790215L;
	}
}
