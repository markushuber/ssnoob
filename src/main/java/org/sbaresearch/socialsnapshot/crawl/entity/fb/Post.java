package org.sbaresearch.socialsnapshot.crawl.entity.fb;

import java.io.Serializable;

import org.sbaresearch.socialsnapshot.crawl.entity.SimpleFbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.SimpleFbObjectDataList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Maurice Wohlkönig
 */
public class Post extends CommonFbObject implements Serializable {
	
	private static final long serialVersionUID = -61004524364224288L;
	@Expose
	String caption;
	@Expose
	@SerializedName("include_hidden")
	Boolean includeHidden;
	@Expose
	SimpleFbObject application;
	@Expose
	SimpleFbObjectDataList likes;
	@Expose
	@SerializedName("status_type")
	String statusType;
	
	public Post(int depth) {
		super(depth);
	}
	
	/**
	 * @return the application
	 */
	public SimpleFbObject getApplication() {
		return this.application;
	}
	
	/**
	 * @return the caption
	 */
	public String getCaption() {
		return this.caption;
	}
	
	/**
	 * @return the includeHidden
	 */
	public Boolean getIncludeHidden() {
		return this.includeHidden;
	}
	
	/**
	 * @return the likes
	 */
	public SimpleFbObjectDataList getLikes() {
		return this.likes;
	}
	
	/**
	 * @return the statusType
	 */
	public String getStatusType() {
		return this.statusType;
	}
	
	@Override
	public String getType() {
		return "post";
	}
	
	@Override
	protected String[] requestFields() {
		return null;
	}
}
