package org.sbaresearch.socialsnapshot.crawl.entity.fb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.sbaresearch.socialsnapshot.crawl.DownloadQueue;
import org.sbaresearch.socialsnapshot.crawl.DownloadQueue.ConDownloadJob;
import org.sbaresearch.socialsnapshot.crawl.FacebookLoader;
import org.sbaresearch.socialsnapshot.crawl.entity.FbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.connections.CommentsConnection;
import org.sbaresearch.socialsnapshot.crawl.entity.connections.FbConnection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Maurice Wohlkönig
 */
public class CommonFbObject extends FbObject {

	@Expose
	private String type, id, message, picture, link, name, description, source, icon, story;

	@Expose
	@SerializedName("created_time")
	Date createdTime;
	
	@Expose
	@SerializedName("updated_time")
	Date updatedTime;
	
	@Expose
	User from;
	
	@Expose
	Privacy privacy;
	
	@Expose
	@SerializedName("ssnoob_type")
	private String ssnooobType;
	
	@Expose
	@SerializedName("story_tags")
	HashMap<String, ArrayList<Tags>> storyTags;
	
	@Expose
	CommentsConnection comments;
	
	public CommonFbObject(int depth) {
		super(depth);
	}
	
	public CommonFbObject(String type) {
		this(0);
		this.type = type;
	}
	
	/**
	 * @return the comments
	 */
	public CommentsConnection getComments() {
		return this.comments;
	}
	
	/**
	 * @return the createdTime
	 */
	public Date getCreatedTime() {
		return this.createdTime;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @return the from
	 */
	public User getFrom() {
		return this.from;
	}
	
	@Override
	public String getGraphUrl() {
		return null;
	}
	
	/**
	 * @return the icon
	 */
	public String getIcon() {
		return this.icon;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * @return the link
	 */
	public String getLink() {
		return this.link;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the picture
	 */
	public String getPicture() {
		return this.picture;
	}
	
	/**
	 * @return the privacy
	 */
	public Privacy getPrivacy() {
		return this.privacy;
	}
	
	/**
	 * @return the source
	 */
	public String getSource() {
		return this.source;
	}
	
	/**
	 * @return the ssnooobType
	 */
	public String getSsnooobType() {
		return this.ssnooobType;
	}

	/**
	 * @return the story
	 */
	public String getStory() {
		return this.story;
	}

	/**
	 * @return the storyTags
	 */
	public HashMap<String, ArrayList<Tags>> getStoryTags() {
		return this.storyTags;
	}

	@Override
	public String getType() {
		return this.type;
	}

	/**
	 * @return the updatedTime
	 */
	public Date getUpdatedTime() {
		return this.updatedTime;
	}

	@Override
	public void onComplete() {
		super.onComplete();
		if (this.comments != null && this.comments.getCount() != null && this.comments.getCount() > 0) {
			DownloadQueue.getInstance()
			.add(new ConDownloadJob(new CommentsConnection(FbConnection.TYPE_COMMENTS, FB_GRAPH_URL + this.id + "/comments" + "?access_token=" + FacebookLoader.getAccessToken(),
					this.priority + 1)) {

				@Override
				public void onPageComplete(FbConnection obj) {
					handleConnection(obj);
				}
			});
		}
	}

	public void setType(String type) {
		this.type = type;
		this.ssnooobType = "commonobject/" + type;
	}

	@Override
	public String toString() {
		return "DefaultFbObject [type=" + this.type + ", depth=" + this.depth + ", priority=" + this.priority + "]";
	}

	@Override
	protected void handleConnection(FbConnection con) {
		if (con.getType().equals(FbConnection.TYPE_COMMENTS)) {
			if (this.comments != null) {
				this.comments.addAll(((CommentsConnection) con).getConnectionObjects());
			} else {
				this.comments = (CommentsConnection) con;
			}
		}
	}

	@Override
	protected String[] requestFields() {
		return null;
	}
}
