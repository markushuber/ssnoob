package org.sbaresearch.socialsnapshot.crawl.entity.connections;

import java.util.ArrayList;

import org.sbaresearch.socialsnapshot.crawl.entity.FbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.fb.User;

import com.google.gson.annotations.SerializedName;

/**
 * @author Maurice Wohlkönig
 */
public class UserConnection extends FbConnection {
	
	private static final int PRIORITY = 2;
	@SerializedName("data")
	private ArrayList<User> users;
	
	public UserConnection(String type, String url, int priority) {
		super(type, url, PRIORITY + priority);
	}
	
	@Override
	public Class<? extends FbObject> getConnectionObjectClass() {
		return User.class;
	}
	
	@Override
	public ArrayList<? extends User> getConnectionObjects() {
		return this.users;
	}
	
	@Override
	public int getDataCount() {
		return this.users != null ? this.users.size() : 0;
	}
	
}
