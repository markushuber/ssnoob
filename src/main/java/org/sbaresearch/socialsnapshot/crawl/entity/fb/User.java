package org.sbaresearch.socialsnapshot.crawl.entity.fb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.sbaresearch.socialsnapshot.crawl.DownloadQueue;
import org.sbaresearch.socialsnapshot.crawl.DownloadQueue.ObjDownloadJob;
import org.sbaresearch.socialsnapshot.crawl.entity.FbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.ImageDownloadUrl;
import org.sbaresearch.socialsnapshot.crawl.entity.SimpleFbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.connections.AccountsConnection;
import org.sbaresearch.socialsnapshot.crawl.entity.connections.FbConnection;
import org.sbaresearch.socialsnapshot.crawl.entity.connections.HomeConnection;
import org.sbaresearch.socialsnapshot.crawl.entity.connections.PhotoConnection;
import org.sbaresearch.socialsnapshot.crawl.entity.connections.PostConnection;
import org.sbaresearch.socialsnapshot.crawl.entity.connections.UserConnection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * https://developers.facebook.com/docs/reference/api/user/
 *
 * @author Maurice Wohlkönig
 */
public class User extends FbObject implements Serializable {
	
	private static final long serialVersionUID = -5920371888824163714L;
	
	private static final Logger log = Logger.getLogger(User.class);
	private static final String TYPE = "user";
	private static final int BASE_PRIORITY = 1;
	
	// JSON properties
	@Expose
	String id;
	
	@Expose
	String name;

	@Expose
	@SerializedName("first_name")
	String firstName;

	@Expose
	@SerializedName("middle_name")
	String middleName;

	@Expose
	@SerializedName("last_name")
	String lastName;

	@Expose
	String gender;

	@Expose
	String locale;

	@Expose
	ArrayList<UserLanguage> languages;

	@Expose
	String link;

	@Expose
	String username;

	@Expose
	Integer timezone;

	@SerializedName("updated_time")
	@Expose
	String updatedTime;

	@Expose
	Boolean verified;

	@Expose
	String bio;

	@Expose
	String birthday;

	@Expose
	UserCoverPhoto cover;

	@Expose
	Currency currency;

	@Expose
	ArrayList<UserEducation> education;

	@Expose
	String email;

	@Expose
	SimpleFbObject hometown;

	@Expose
	@SerializedName("interested_in")
	String[] interestedIn;

	@Expose
	SimpleFbObject location;

	@Expose
	String political;

	@Expose
	@SerializedName("payment_pricepoints")
	HashMap<String, ArrayList<UserPaymentPricepoints>> paymentPricepoints;

	@Expose
	UserProfilePicture picture;

	@Expose
	String quotes;

	@Expose
	@SerializedName("relationship_status")
	String relationshipStatus;

	@Expose
	String religion;

	@Expose
	@SerializedName("security_settings")
	UserSecuritySettings securitySettings;

	@Expose
	@SerializedName("significant_other")
	SimpleFbObject significantOther;

	@Expose
	String website;

	@Expose
	ArrayList<UserWork> work;

	@Expose
	@SerializedName("favorite_athletes")
	ArrayList<SimpleFbObject> favoriteAthletes;

	@Expose
	@SerializedName("home_feed")
	ArrayList<FbObject> homeFeed;

	@Expose
	/* Used in some list results */
	String category;

	/**
	 * @see https://developers.facebook.com/docs/reference/api/user/
	 */
	private static final String[] requestFields = new String[] { "id", "name", "first_name", "middle_name", "last_name", "gender", "locale", "languages", "link", "timezone", "updated_time",
			"verified", "bio", "birthday", "cover", "currency", "education", "email", "hometown", "interested_in", "location", "political", "payment_pricepoints", "picture", "quotes",
			"relationship_status", "religion", "security_settings", "significant_other", "website", "work" };

	// non JSON properties
	@Expose(deserialize = false)
	ArrayList<User> friends;

	@Expose(deserialize = false)
	ArrayList<Application> applications;

	@Expose(deserialize = false)
	ArrayList<Page> pages;

	@Expose(deserialize = false)
	ArrayList<Photo> photos;

	@Expose(deserialize = false)
	ArrayList<Post> posts;

	@Expose(deserialize = false)
	@SerializedName("mutual_friends")
	private ArrayList<User> mutualFriends;

	public User(int depth) {
		super(depth);
	}

	public User(int depth, String id) {
		this(depth);
		this.id = id;
	}

	/**
	 * @return the applications
	 */
	public ArrayList<Application> getApplications() {
		return this.applications;
	}

	/**
	 * @return the bio
	 */
	public String getBio() {
		return this.bio;
	}

	/**
	 * @return the birthday
	 */
	public String getBirthday() {
		return this.birthday;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return this.category;
	}

	/**
	 * @return the cover
	 */
	public UserCoverPhoto getCover() {
		return this.cover;
	}

	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return this.currency;
	}

	/**
	 * @return the education
	 */
	public ArrayList<UserEducation> getEducation() {
		return this.education;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * @return the favoriteAthletes
	 */
	public ArrayList<SimpleFbObject> getFavoriteAthletes() {
		return this.favoriteAthletes;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * @return the friends
	 */
	public ArrayList<User> getFriends() {
		return this.friends;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return this.gender;
	}

	@Override
	public String getGraphUrl() {
		return genGraphUrl(this.id);
	}

	/**
	 * @return the homeFeed
	 */
	public ArrayList<FbObject> getHomeFeed() {
		return this.homeFeed;
	}

	/**
	 * @return the hometown
	 */
	public SimpleFbObject getHometown() {
		return this.hometown;
	}

	public String getId() {
		return this.id;
	}

	/**
	 * @return the interestedIn
	 */
	public String[] getInterestedIn() {
		return this.interestedIn;
	}

	/**
	 * @return the languages
	 */
	public ArrayList<UserLanguage> getLanguages() {
		return this.languages;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return this.link;
	}

	/**
	 * @return the locale
	 */
	public String getLocale() {
		return this.locale;
	}

	/**
	 * @return the location
	 */
	public SimpleFbObject getLocation() {
		return this.location;
	}

	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return this.middleName;
	}

	/**
	 * @return the mutualFriends
	 */
	public ArrayList<User> getMutualFriends() {
		return this.mutualFriends;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the pages
	 */
	public ArrayList<Page> getPages() {
		return this.pages;
	}

	/**
	 * @return the paymentPricepoints
	 */
	public HashMap<String, ArrayList<UserPaymentPricepoints>> getPaymentPricepoints() {
		return this.paymentPricepoints;
	}

	/**
	 * @return the photos
	 */
	public ArrayList<Photo> getPhotos() {
		return this.photos;
	}

	/**
	 * @return the picture
	 */
	public UserProfilePicture getPicture() {
		return this.picture;
	}

	/**
	 * @return the political
	 */
	public String getPolitical() {
		return this.political;
	}

	/**
	 * @return the posts
	 */
	public ArrayList<Post> getPosts() {
		return this.posts;
	}
	
	/**
	 * @return the quotes
	 */
	public String getQuotes() {
		return this.quotes;
	}
	
	/**
	 * @return the relationshipStatus
	 */
	public String getRelationshipStatus() {
		return this.relationshipStatus;
	}

	/**
	 * @return the religion
	 */
	public String getReligion() {
		return this.religion;
	}

	/**
	 * @return the securitySettings
	 */
	public UserSecuritySettings getSecuritySettings() {
		return this.securitySettings;
	}

	/**
	 * @return the significantOther
	 */
	public SimpleFbObject getSignificantOther() {
		return this.significantOther;
	}

	/**
	 * @return the timezone
	 */
	public Integer getTimezone() {
		return this.timezone;
	}

	@Override
	public String getType() {
		return TYPE;
	}
	
	/**
	 * @return the updatedTime
	 */
	public String getUpdatedTime() {
		return this.updatedTime;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * @return the verified
	 */
	public Boolean getVerified() {
		return this.verified;
	}
	
	/**
	 * @return the website
	 */
	public String getWebsite() {
		return this.website;
	}
	
	/**
	 * @return the work
	 */
	public ArrayList<UserWork> getWork() {
		return this.work;
	}
	
	@Override
	public String toString() {
		return "User [id=" + this.id + ", name=" + this.name + ", cover=" + this.cover + "]";
	}
	
	protected void addApplication(Application obj) {
		if (this.applications == null) {
			this.applications = new ArrayList<Application>();
		}
		this.applications.add(obj);
		log.debug("got new application for " + this.name + ": " + obj.name);
	}
	
	protected void addFriend(User obj) {
		if (this.friends == null) {
			this.friends = new ArrayList<User>();
		}
		this.friends.add(obj);
		log.debug("got new friend for " + this.name + "(" + this.depth + "): " + obj.name + "(" + obj.depth + ") - total " + this.friends.size());
	}
	
	protected void addPage(Page obj) {
		if (this.pages == null) {
			this.pages = new ArrayList<Page>();
		}
		this.pages.add(obj);
		log.debug("got new page for " + this.name + ": " + obj.name);
	}
	
	@Override
	protected synchronized void handleConnection(FbConnection con) {
		final DownloadQueue dq = DownloadQueue.getInstance();
		if (con.getType().equals(FbConnection.TYPE_FRIENDS)) {
			UserConnection friendslist = (UserConnection) con;
			// log.debug(friendslist.getConnectionObjects().size() + " users in friendslist");
			for (FbObject user : friendslist.getConnectionObjects()) {
				user.setPriority(this.priority + User.BASE_PRIORITY);
				user.setDepth(this.depth);
				dq.add(new ObjDownloadJob(user) {
					
					@Override
					public void onComplete(FbObject obj) {
						addFriend((User) obj);
					}
				});
			}
			// dq.add(new UserListDownloadJob(priority + User.BASE_PRIORITY, new UserList((ArrayList<User>) friendslist.getConnectionObjects())) {
			// @Override
			// public void onComplete(UserList userlist) {
			// log.debug("users: " + userlist);
			// }
			// });
		} else if (con.getType().equals(FbConnection.TYPE_ACCOUNTS)) {
			AccountsConnection accountsList = (AccountsConnection) con;
			log.debug(accountsList.getConnectionObjects().size() + " accounts in accountlist of " + this.name);
			for (Application application : accountsList.getApplicationObjects(this.priority + User.BASE_PRIORITY, this.depth)) {
				dq.add(new ObjDownloadJob(application) {
					
					@Override
					public void onComplete(FbObject obj) {
						addApplication((Application) obj);
					}
				});
			}
			for (Page application : accountsList.getPageObjects(this.priority + User.BASE_PRIORITY, this.depth)) {
				dq.add(new ObjDownloadJob(application) {
					
					@Override
					public void onComplete(FbObject obj) {
						addPage((Page) obj);
					}
				});
			}
		} else if (con.getType().equals(FbConnection.TYPE_PHOTOS)) {
			PhotoConnection photos = (PhotoConnection) con;
			addPhotos(photos.getConnectionObjects());
		} else if (con.getType().equals(FbConnection.TYPE_POSTS)) {
			PostConnection photos = (PostConnection) con;
			addPosts(photos.getConnectionObjects());
		} else if (con.getType().equals(FbConnection.TYPE_HOME)) {
			HomeConnection home = (HomeConnection) con;
			addHomeFeed(home.getConnectionObjects());
		} else if (con.getType().equals(FbConnection.TYPE_MUTUALFRIENDS)) {
			UserConnection mutualfriends = (UserConnection) con;
			addMutualFriends(mutualfriends.getConnectionObjects());
		}
	}
	
	@Override
	protected String[] requestFields() {
		return requestFields;
	}
	
	private void addHomeFeed(ArrayList<? extends FbObject> connectionObjects) {
		if (this.homeFeed == null) {
			this.homeFeed = new ArrayList<>();
		}
		this.homeFeed.addAll(connectionObjects);
	}
	
	private void addMutualFriends(ArrayList<? extends User> connectionObjects) {
		if (this.mutualFriends == null) {
			this.mutualFriends = new ArrayList<>();
		}
		this.mutualFriends.addAll(connectionObjects);
	}
	
	private void addPhotos(ArrayList<Photo> connectionObjects) {
		if (this.photos == null) {
			this.photos = new ArrayList<>();
		}
		this.photos.addAll(connectionObjects);
	}
	
	private void addPosts(ArrayList<Post> connectionObjects) {
		if (this.posts == null) {
			this.posts = new ArrayList<>();
		}
		this.posts.addAll(connectionObjects);
	}
	
	public static class UserCoverPhoto implements Serializable {
		
		private static final long serialVersionUID = 3196358394495345421L;
		@Expose
		public String id;
		@Expose
		@ImageDownloadUrl(2)
		public String source;
		@Expose
		@SerializedName("offset_y")
		public String offsetY;
		
		@Override
		public String toString() {
			return "UserCoverPhoto [id=" + this.id + ", source=" + this.source + ", offsetY=" + this.offsetY + "]";
		}
		
	}
	
	public static class UserDevice implements Serializable {
		
		private static final long serialVersionUID = -611340797163541392L;
		@Expose
		String os, hardware;
	}
	
	public static class UserEducation implements Serializable {
		
		private static final long serialVersionUID = 7018956930242233491L;
		@Expose
		String type;
		@Expose
		UserEducationSchool school;
		@Expose
		HashMap<String, String> year;
		@Expose
		ArrayList<UserEducationClasses> classes;
		
		public static class UserEducationClasses implements Serializable {
			
			private static final long serialVersionUID = 7207523284563804898L;
			@Expose
			String name, id, description;
			
		}
		
		public static class UserEducationSchool {
			
			@Expose
			String name, id, type, year, degree;
			
		}
	}
	
	public static class UserLanguage implements Serializable {
		
		private static final long serialVersionUID = -1273248409668639769L;
		@Expose
		public String id, name;
	}
	
	public static class UserPaymentPricepoints implements Serializable {
		
		private static final long serialVersionUID = -3904700049490486913L;
		@Expose
		@SerializedName("user_price")
		String userPrice;
		@Expose
		Double credits;
		@Expose
		@SerializedName("local_currency")
		String localCurrency;
	}
	
	public static class UserProfilePicture implements Serializable {
		
		private static final long serialVersionUID = -4671180804996803364L;
		@Expose
		public UserProfilePictureData data;
		
		public static class UserProfilePictureData implements Serializable {
			
			private static final long serialVersionUID = 213286777659059907L;
			@Expose
			@ImageDownloadUrl(1)
			public String url;
			@Expose
			@SerializedName("is_silhouette")
			public boolean isSilhouette;
		}
	}
	
	public static class UserSecuritySettings implements Serializable {
		
		private static final long serialVersionUID = -1834637607336258285L;
		@Expose
		@SerializedName("secure_browsing")
		UserSecuritySettingsSecureBrowsing secureBrowsing;
		
		public static class UserSecuritySettingsSecureBrowsing implements Serializable {
			
			private static final long serialVersionUID = -4227176752231225383L;
			@Expose
			boolean enabled;
		}
	}
	
	public static class UserWork implements Serializable {
		
		private static final long serialVersionUID = -174741699881521898L;
		@Expose
		public UserWorkEmployer employer;
		@SerializedName("start_date")
		@Expose
		public String startDate;
		
		public static class UserWorkEmployer implements Serializable {
			
			private static final long serialVersionUID = 1510546922007577884L;
			@Expose
			public String id, name;
			
		}
	}
}
