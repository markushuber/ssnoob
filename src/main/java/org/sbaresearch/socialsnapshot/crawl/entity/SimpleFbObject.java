package org.sbaresearch.socialsnapshot.crawl.entity;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

/**
 * @author Maurice Wohlkönig
 */
public class SimpleFbObject implements Serializable {
	
	private static final long serialVersionUID = -875045303281236164L;
	@Expose
	String name, id;
}
