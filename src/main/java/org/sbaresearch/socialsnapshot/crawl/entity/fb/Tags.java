package org.sbaresearch.socialsnapshot.crawl.entity.fb;

import java.io.Serializable;

import org.sbaresearch.socialsnapshot.crawl.entity.SimpleFbObject;

import com.google.gson.annotations.Expose;

/**
 * @author Maurice Wohlkönig
 */
public class Tags extends SimpleFbObject implements Serializable {
	
	private static final long serialVersionUID = 1825310793981787513L;
	@Expose
	Integer offset, length;
	@Expose
	String type;
}