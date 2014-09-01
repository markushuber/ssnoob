package org.sbaresearch.socialsnapshot.crawl.entity.fb;

import java.io.Serializable;

import org.sbaresearch.socialsnapshot.crawl.entity.FbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.connections.AccountsConnection.AccountsConnectionData;
import org.sbaresearch.socialsnapshot.crawl.entity.connections.FbConnection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Maurice Wohlkönig
 */
public class Application extends FbObject implements Serializable {
	
	private static final long serialVersionUID = -8249824271537761677L;
	
	public static final String TYPE = "application";
	
	@Expose
	String id, name, link, namespace;
	@Expose
	@SerializedName("icon_url")
	String iconUrl;
	@Expose
	@SerializedName("logo_url")
	String logoUrl;
	@Expose
	@SerializedName("monthly_active_users")
	String monthlyActiveUsers;
	
	/**
	 * Constructor used when creating from a user/accounts connection
	 *
	 * @param entry
	 * @param priority
	 * @param depth
	 */
	public Application(AccountsConnectionData entry, int priority, int depth) {
		this(depth);
		this.priority = priority;
		this.id = entry.id;
		this.name = entry.name;
	}
	
	public Application(int depth) {
		super(depth);
	}
	
	@Override
	public String getGraphUrl() {
		return genGraphUrl(this.id);
	}
	
	@Override
	public String getType() {
		return "account";
	}
	
	@Override
	public String toString() {
		return "Application [id=" + this.id + ", name=" + this.name + ", link=" + this.link + ", namespace=" + this.namespace + ", iconUrl=" + this.iconUrl + ", logoUrl=" + this.logoUrl
				+ ", monthlyActiveUsers=" + this.monthlyActiveUsers + "]";
	}
	
	@Override
	protected void handleConnection(FbConnection con) {
		
	}
	
	@Override
	protected String[] requestFields() {
		return null;
	}
	
}
