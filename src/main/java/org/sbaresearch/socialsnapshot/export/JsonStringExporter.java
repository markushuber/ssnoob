package org.sbaresearch.socialsnapshot.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.sbaresearch.socialsnapshot.Config;
import org.sbaresearch.socialsnapshot.crawl.entity.fb.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This class exports the whole FB snapshot into one json file.
 *
 * @author Stefan Haider (shaider@sba-research.org), original code: Maurice Wohlkönig
 */
public class JsonStringExporter {

	private static final Logger log = Logger.getLogger(JsonStringExporter.class);
	private String snapshotid;
	private Gson gson;

	/**
	 * Creates the exporter.
	 * 
	 * @param snapshotid
	 *            the user id of the user the export was initiated with
	 * @param prettyFormat
	 *            enables prette formatting of the json file
	 */
	public JsonStringExporter(String snapshotid, boolean prettyFormat) {
		this.snapshotid = snapshotid;
		GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
		if (prettyFormat) {
			builder.setPrettyPrinting();
		}
		this.gson = builder.create();
	}

	/**
	 * Exports a data structur via {@link Gson} to a file.
	 *
	 * @param rootUser
	 *            the Json data structure to export
	 * @return the path to the output file if export succeeds, or null otherwise
	 */
	public String export(User rootUser) {
		Writer out = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("_yyyy-MM-dd_HH-mm-ss_");
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			
			File jsonOutputFile = new File(Config.getSnapshotDirectory(), this.snapshotid + dateFormat.format(new Date(System.currentTimeMillis())) + "socialsnapshot.json");
			
			log.info("Exporting " + jsonOutputFile.getAbsolutePath());

			jsonOutputFile.getParentFile().mkdirs();
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonOutputFile), "UTF-8"));
			this.gson.toJson(rootUser, User.class, out);
			return jsonOutputFile.getAbsolutePath();
		} catch (FileNotFoundException e) {
			log.error(e);
			return null;
		} catch (UnsupportedEncodingException e) {
			log.error(e);
			return null;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
	}
}
