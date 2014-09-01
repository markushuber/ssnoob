package org.sbaresearch.socialsnapshot.crawl.entity;

/**
 * @author Maurice Wohlkönig
 */
public interface IFbObject {
	
	public static final String FB_GRAPH_URL = "https://graph.facebook.com/";
	
	/**
	 * @return the full URL to request this Object <em>without the request token</em>
	 */
	public String getGraphUrl();
	
	/**
	 * @return string representation of this objects type
	 */
	public String getType();

}
