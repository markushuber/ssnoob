package org.sbaresearch.socialsnapshot.crawl.entity;

import org.sbaresearch.socialsnapshot.crawl.DownloadQueue.ObjDownloadJob;
import org.sbaresearch.socialsnapshot.crawl.entity.fb.User;
import org.sbaresearch.socialsnapshot.util.StringUtils;

/**
 * @author Maurice Wohlkönig
 */
public abstract class InitialUserDownloadJob extends ObjDownloadJob {
	
	private final static InitialUserObject obj = new InitialUserObject();
	
	public InitialUserDownloadJob() {
		super(obj);
	}
	
	private static class InitialUserObject extends User {
		
		private static final long serialVersionUID = 5365251927794242145L;
		
		public InitialUserObject() {
			super(0); // initialize with highest priority
		}
		
		@Override
		public String getGraphUrl() {
			return FB_GRAPH_URL + "me/?metadata=1&fields=" + StringUtils.join(super.requestFields(), ",") + ",devices";
		}
	}
}
