package org.sbaresearch.socialsnapshot.crawl.entity.fb;

import java.io.Serializable;

import org.sbaresearch.socialsnapshot.crawl.entity.SimpleFbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.SimpleFbObjectDataList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Maurice Wohlkönig
 */
public class Video extends CommonFbObject implements Serializable {

	private static final long serialVersionUID = -4179492597970175348L;
	
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

	public Video(int depth) {
		super(depth);
	}

	@Override
	public String getType() {
		return "post";
	}

	@Override
	protected String[] requestFields() {
		return null;
	}

	public static class PostStoryTags extends SimpleFbObject {

		private static final long serialVersionUID = -8325457090837536535L;
		@Expose
		Integer offset, length;
		@Expose
		String type;
	}

}
