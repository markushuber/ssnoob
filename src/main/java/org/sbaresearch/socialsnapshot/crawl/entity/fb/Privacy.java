package org.sbaresearch.socialsnapshot.crawl.entity.fb;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

/**
 * @author Maurice Wohlkönig
 */
public class Privacy implements Serializable {
	
	private static final long serialVersionUID = 7671915100751695420L;
	@Expose
	String value, allow, deny, networks, friends, description;
}
