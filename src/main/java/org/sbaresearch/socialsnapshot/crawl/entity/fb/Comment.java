package org.sbaresearch.socialsnapshot.crawl.entity.fb;

import java.io.Serializable;

import org.sbaresearch.socialsnapshot.crawl.entity.FbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.connections.FbConnection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Maurice Wohlkönig
 */
public class Comment extends FbObject implements Serializable {

	private static final long serialVersionUID = -4183067995245043513L;
	private static final String TYPE = "comment";
	@Expose
	String id, message;
	@Expose
	@SerializedName("can_remove")
	Boolean can_remove;
	@Expose
	String created_time;
	@Expose
	Integer like_count, likes; // attribute has different names in different objects ("like_count"=>photo, "likes"=>post)
	@Expose
	Boolean user_likes;
	@Expose
	User from;

	public Comment(int depth) {
		super(depth);
	}

	@Override
	public String getGraphUrl() {
		
		return null;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	protected void handleConnection(FbConnection con) {
		
	}

	@Override
	protected String[] requestFields() {

		return null;
	}

}
