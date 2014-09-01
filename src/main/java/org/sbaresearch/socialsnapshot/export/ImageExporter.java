package org.sbaresearch.socialsnapshot.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.sbaresearch.socialsnapshot.Config;

/**
 * @author Maurice Wohlkönig
 */
public class ImageExporter {
	
	private static final Logger log = Logger.getLogger(ImageExporter.class);
	private String snapshotid;
	
	public ImageExporter(String snapshotid) {
		this.snapshotid = snapshotid;
	}
	
	public void export(String url, byte[] imageData) {
		try {
			if (url.contains("?")) {
				url = url.substring(0, url.indexOf("?")); // strip get parameters
			}
			int pos = url.indexOf("/") + 2;
			String filename = this.snapshotid + "-images" + File.separator + url.substring(pos).replace("/", File.separator);
			File outputFile = new File(Config.getSnapshotDirectory(), filename);
			outputFile.getParentFile().mkdirs();
			
			log.info("Exporting " + url + " to " + outputFile.getAbsolutePath());
			try (FileOutputStream fos = new FileOutputStream(outputFile);) {
				fos.write(imageData);
			}
		} catch (IOException e) {
			log.error("Can not export image " + url);
		}
	}
}
