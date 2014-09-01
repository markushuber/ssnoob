package org.sbaresearch.socialsnapshot.crawl.entity.connections;

import java.util.ArrayList;

import org.sbaresearch.socialsnapshot.crawl.entity.FbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.fb.Post;

import com.google.gson.annotations.SerializedName;

/**
 * @author Maurice Wohlkönig
 */
public class PostConnection extends FbConnection {
	
	private static final int PRIORITY = 3;
	@SerializedName("data")
	ArrayList<Post> posts;
	
	public PostConnection(String type, String url, int priority) {
		super(type, url, PRIORITY + priority);
	}
	
	@Override
	public Class<? extends FbObject> getConnectionObjectClass() {
		return Post.class;
	}
	
	@Override
	public ArrayList<Post> getConnectionObjects() {
		return this.posts;
	}
	
	@Override
	public int getDataCount() {
		return this.posts != null ? this.posts.size() : 0;
	}
	
}
