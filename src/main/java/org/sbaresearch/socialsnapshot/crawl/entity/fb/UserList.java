package org.sbaresearch.socialsnapshot.crawl.entity.fb;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.sbaresearch.socialsnapshot.crawl.entity.IFbObject;
import org.sbaresearch.socialsnapshot.util.StringUtils;

/**
 * @author Maurice Wohlkönig
 */
public class UserList extends LinkedHashMap<String, User> implements IFbObject {
	
	private static final Logger log = Logger.getLogger(UserList.class);
	private static final long serialVersionUID = -4766912575959931963L;
	
	private ArrayList<String> userIds = new ArrayList<String>();
	
	public UserList(ArrayList<User> users) {
		log.debug("add " + users.size() + " users");
		for (User user : users) {
			this.userIds.add(user.id);
		}
	}
	
	@Override
	public String getGraphUrl() {
		String url = FB_GRAPH_URL + "?ids=" + StringUtils.join(this.userIds, ",");
		return url;
	}
	
	@Override
	public String getType() {
		return "userlist";
	}
	
}
