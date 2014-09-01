package org.sbaresearch.socialsnapshot.crawl.entity;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

/**
 * @author Maurice Wohlkönig
 */
public class SimpleFbObjectDataList {
	
	@Expose
	ArrayList<SimpleFbObject> data;
	@Expose
	Integer count;
}
