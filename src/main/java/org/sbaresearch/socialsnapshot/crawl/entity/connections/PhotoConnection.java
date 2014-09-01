package org.sbaresearch.socialsnapshot.crawl.entity.connections;

import java.util.ArrayList;

import org.sbaresearch.socialsnapshot.crawl.entity.FbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.fb.Photo;

import com.google.gson.annotations.SerializedName;

/**
 * @author Maurice Wohlkönig
 */
public class PhotoConnection extends FbConnection {
	
	private static final int PRIORITY = 3;
	@SerializedName("data")
	ArrayList<Photo> photos;
	
	public PhotoConnection(String type, String url, int priority) {
		super(type, url, PRIORITY + priority);
	}
	
	@Override
	public Class<? extends FbObject> getConnectionObjectClass() {
		return Photo.class;
	}
	
	@Override
	public ArrayList<Photo> getConnectionObjects() {
		return this.photos;
	}
	
	@Override
	public int getDataCount() {
		return this.photos != null ? this.photos.size() : 0;
	}
	
}
