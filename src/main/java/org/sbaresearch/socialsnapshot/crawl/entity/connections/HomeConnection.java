package org.sbaresearch.socialsnapshot.crawl.entity.connections;

import java.util.ArrayList;

import org.sbaresearch.socialsnapshot.crawl.entity.FbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.fb.User;

import com.google.gson.annotations.SerializedName;

/**
 * @author Maurice Wohlkönig
 */
public class HomeConnection extends FbConnection {
	
	private static final int PRIORITY = 2;
	@SerializedName("data")
	private ArrayList<FbObject> homeFeedEntries;
	
	public HomeConnection(String type, String url, int priority) {
		super(type, url, PRIORITY + priority);
	}
	
	@Override
	public Class<? extends FbObject> getConnectionObjectClass() {
		return User.class;
	}
	
	@Override
	public ArrayList<? extends FbObject> getConnectionObjects() {
		return this.homeFeedEntries;
	}
	
	@Override
	public int getDataCount() {
		return this.homeFeedEntries != null ? this.homeFeedEntries.size() : 0;
	}
	
	@Override
	public void onComplete() {
		if (this.homeFeedEntries != null) {
			for (FbObject obj : this.homeFeedEntries) {
				obj.onComplete();
			}
		}
	}
	
}
