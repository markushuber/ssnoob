package org.sbaresearch.socialsnapshot.crawl.entity.fb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.sbaresearch.socialsnapshot.crawl.entity.FbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.ImageDownloadUrl;
import org.sbaresearch.socialsnapshot.crawl.entity.connections.CommentsConnection;
import org.sbaresearch.socialsnapshot.crawl.entity.connections.FbConnection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Maurice Wohlkönig
 */
public class Photo extends FbObject implements Serializable {
	
	private static final long serialVersionUID = -4468079192997171629L;
	
	private static final String TYPE = "photo";
	
	@Expose
	String id, name, link, icon, message;
	@Expose
	@ImageDownloadUrl(3)
	String picture, source;
	@Expose
	int height, width;
	@Expose
	@SerializedName("created_time")
	Date createdTime;
	@Expose
	@SerializedName("updated_time")
	Date updatedTime;
	@Expose
	ArrayList<PhotoImage> images;
	@Expose
	User from;
	@Expose
	CommentsConnection comments;
	@Expose
	@SerializedName("message_tags")
	HashMap<String, ArrayList<Tags>> storyTags;
	
	public Photo(int depth) {
		super(depth);
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
	 * @return the height
	 */
	public int getHeight() {
		return this.height;
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
	 * @return the images
	 */
	public ArrayList<PhotoImage> getImages() {
		return this.images;
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
	 * @return the source
	 */
	public String getSource() {
		return this.source;
	}
	
	/**
	 * @return the storyTags
	 */
	public HashMap<String, ArrayList<Tags>> getStoryTags() {
		return this.storyTags;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	/**
	 * @return the updatedTime
	 */
	public Date getUpdatedTime() {
		return this.updatedTime;
	}
	
	/**
	 * @return the width
	 */
	public int getWidth() {
		return this.width;
	}
	
	@Override
	public String toString() {
		return "Photo [id=" + this.id + ", name=" + this.name + ", link=" + this.link + ", icon=" + this.icon + ", picture=" + this.picture + ", source=" + this.source + ", height=" + this.height
				+ ", width=" + this.width + ", createdTime=" + this.createdTime + ", updatedTime=" + this.updatedTime + ", images=" + this.images + ", from=" + this.from + ", comments="
				+ this.comments + "]";
	}
	
	@Override
	protected void handleConnection(FbConnection con) {
		
	}
	
	@Override
	protected String[] requestFields() {
		return null;
	}
	
	public static class PhotoImage implements Serializable {
		
		private static final long serialVersionUID = -4317139100928949096L;
		@Expose
		public int height, width;
		@Expose
		@ImageDownloadUrl(5)
		public String source;
	}
	
}
