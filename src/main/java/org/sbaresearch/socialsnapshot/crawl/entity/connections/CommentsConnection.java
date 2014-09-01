package org.sbaresearch.socialsnapshot.crawl.entity.connections;

import java.util.ArrayList;

import org.sbaresearch.socialsnapshot.crawl.entity.FbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.fb.Comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Maurice Wohlkönig
 */
public class CommentsConnection extends FbConnection {
	
	@Expose
	@SerializedName("data")
	ArrayList<Comment> comments;
	
	@Expose
	Integer count;
	
	public CommentsConnection(String type, String url, int priority) {
		super(type, url, priority);
	}
	
	public void addAll(ArrayList<Comment> comments) {
		if (this.comments == null) {
			this.comments = new ArrayList<>();
		}
		this.comments.addAll(comments);
	}
	
	@Override
	public Class<? extends FbObject> getConnectionObjectClass() {
		return Comment.class;
	}
	
	@Override
	public ArrayList<Comment> getConnectionObjects() {
		return this.comments;
	}
	
	public Integer getCount() {
		return this.count;
	}
	
	@Override
	public int getDataCount() {
		return this.comments != null ? this.comments.size() : 0;
	}
}