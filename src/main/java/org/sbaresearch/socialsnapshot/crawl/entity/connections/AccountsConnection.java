package org.sbaresearch.socialsnapshot.crawl.entity.connections;

import java.util.ArrayList;

import org.sbaresearch.socialsnapshot.crawl.entity.FbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.fb.Application;
import org.sbaresearch.socialsnapshot.crawl.entity.fb.Page;

/**
 * @author Maurice Wohlkönig
 */
public class AccountsConnection extends FbConnection {
	
	private static final int PRIORITY = 10;
	
	ArrayList<AccountsConnectionData> data;
	
	public AccountsConnection(String type, String url, int priority) {
		super(type, url, PRIORITY + priority);
	}
	
	public ArrayList<Application> getApplicationObjects(int priority, int depth) {
		ArrayList<Application> list = new ArrayList<Application>();
		if (this.data != null) {
			for (FbObject entry : this.data) {
				if (((AccountsConnectionData) entry).category.equals("Application")) {
					list.add(new Application((AccountsConnectionData) entry, priority, depth));
				}
			}
		}
		return list;
	}
	
	@Override
	public Class<? extends FbObject> getConnectionObjectClass() {
		return AccountsConnectionData.class;
	}
	
	@Override
	public ArrayList<? extends FbObject> getConnectionObjects() {
		return this.data;
	}
	
	@Override
	public int getDataCount() {
		return this.data != null ? this.data.size() : 0;
	}
	
	public ArrayList<Page> getPageObjects(int priority, int depth) {
		ArrayList<Page> list = new ArrayList<Page>();
		if (this.data != null) {
			for (FbObject entry : this.data) {
				if (((AccountsConnectionData) entry).category.equals("Community")) {
					list.add(new Page((AccountsConnectionData) entry, priority, depth));
				}
			}
		}
		return list;
	}
	
	public static class AccountsConnectionData extends FbObject {
		
		public String category, name, access_token;
		
		public String id;
		public String[] perms;
		
		public AccountsConnectionData(int depth) {
			super(depth);
		}
		
		@Override
		public String getGraphUrl() {
			return genGraphUrl(this.id);
		}
		
		@Override
		public String getType() {
			return this.category;
		}
		
		@Override
		protected void handleConnection(FbConnection con) {
			
		}
		
		@Override
		protected String[] requestFields() {
			return null;
		}
	}
}
