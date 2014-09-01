package org.sbaresearch.socialsnapshot.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author Maurice Wohlkönig
 */
@Deprecated
public class Database {
	
	private static final String TABLE_METADATA = "ssnoob_metadata";
	
	private static final int CURRENT_VERSION = 2;
	
	private static final String TABLE_API_KEYS = "graph_api_keys";
	
	private static Logger log = Logger.getLogger(Database.class);
	
	private static Connection connection;
	
	static {
		// load the sqlite-JDBC driver using the current class loader
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			log.equals(e1);
		}
	}
	
	public static synchronized String hasValidAPIKeySaved(final String uniqueToken) {
		try {
			PreparedStatement st = getConnection().prepareStatement("SELECT * FROM " + TABLE_API_KEYS + " WHERE identifier=?");
			st.setString(1, uniqueToken);
			ResultSet res = st.executeQuery();
			if (res.next()) {
				Date validUntil = new Date(res.getLong("valid_until"));
				if (validUntil.after(new Date()))
					return res.getString("apikey");
			}
		} catch (SQLException e) {
			log.error("DB exception", e);
		}
		return null;
	}
	
	public static synchronized void saveAPIKey(final String uniqueToken, final String apiKey, final Date validUntil) {
		try {
			PreparedStatement st = getConnection().prepareStatement("REPLACE INTO " + TABLE_API_KEYS + " (identifier, apikey, valid_until) VALUES (?,?,?) ");
			st.setString(1, uniqueToken);
			st.setString(2, apiKey);
			st.setLong(3, validUntil.getTime());
			st.executeUpdate();
		} catch (SQLException e) {
			log.error("cant save API key", e);
		}
		
	}
	
	private synchronized static Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				connection = DriverManager.getConnection("jdbc:sqlite:ssnoob.db");
				
				initDatabaseIfNeccessary(connection);
			}
		} catch (SQLException e) {
			log.error("DB exception", e);
		}
		return connection;
	}
	
	private static void initDatabaseIfNeccessary(Connection connection2) throws SQLException {
		Statement st = connection2.createStatement();
		int version = 0;
		try {
			st.setQueryTimeout(4);
			ResultSet res = st.executeQuery("SELECT * FROM " + TABLE_METADATA + " WHERE key='db_version'");
			if (res.next()) {
				version = res.getInt("value");
				log.info("current DB version is " + version);
				if (version < CURRENT_VERSION) {
					updateFromVersion(st, version);
				}
			}
		} catch (SQLException e) {
			updateFromVersion(st, version);
		}
	}
	
	@SuppressWarnings("unused")
	private static Statement newStatement() throws SQLException {
		final Statement statement = getConnection().createStatement();
		statement.setQueryTimeout(10);
		return statement;
	}
	
	private static void updateFromVersion(Statement st, int startVersion) {
		log.info("updating Database from level " + startVersion + " to " + CURRENT_VERSION);
		try {
			// do incremental updates from the specified start version up until CURRENT_VERSION (thats why there are no breaks in there!)
			switch (startVersion) {
				case 0: // no database available
					st.executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE_METADATA + " (key STRING PRIMARY KEY, value STRING)");
				case 1: // initial database (9.1.2013)
					st.executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE_API_KEYS + " (identifier STRING PRIMARY KEY, apikey STRING, valid_until NUMERIC)");
				default: // in the end update the db version info
					st.executeUpdate("REPLACE INTO " + TABLE_METADATA + " (`key`, `value`) VALUES ('db_version','" + CURRENT_VERSION + "')");
			}
		} catch (SQLException e) {
			log.error("cant update from version " + startVersion + " to " + CURRENT_VERSION, e);
		}
	}
}
