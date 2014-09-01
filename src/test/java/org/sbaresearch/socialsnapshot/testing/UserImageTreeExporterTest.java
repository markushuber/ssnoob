package org.sbaresearch.socialsnapshot.testing;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sbaresearch.socialsnapshot.export.UserImageTreeExporter;

public class UserImageTreeExporterTest {
	
	private UserImageTreeExporter exporter;
	
	@Before
	public void setUp() throws Exception {
		this.exporter = new UserImageTreeExporter(System.getProperty("user.home") + File.separator + "fb_snapshot", "userid", "userid_2014-08-14_12-35-20_socialsnapshot.json");
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testExport() {
		this.exporter.export();
	}
	
}
