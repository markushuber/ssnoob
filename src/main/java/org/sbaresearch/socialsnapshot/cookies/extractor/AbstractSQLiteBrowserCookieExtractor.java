package org.sbaresearch.socialsnapshot.cookies.extractor;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import com.gargoylesoftware.htmlunit.util.Cookie;

/**
 * This class provides a common base for reading cookies from a sqlite db. (Chrome, Firefox)
 *
 * @author Stefan Haider (shaider@sba-research.org), original code: Martin Grottenthaler
 */
public abstract class AbstractSQLiteBrowserCookieExtractor implements IBrowserCookieExtractor {
	
	protected Set<Cookie> cookies;
	protected String tableName;
	protected File dbFile;
	/** Log4j logging instance */
	protected static Logger log;

	/**
	 * The constructor called by implementing classes.
	 *
	 * @param tableName
	 *            the name of the table to access in the db
	 * @throws IOException
	 */
	public AbstractSQLiteBrowserCookieExtractor(String tableName) {
		this.cookies = new HashSet<Cookie>();
		try {
			this.dbFile = File.createTempFile(getClass().getSimpleName(), ".sqlite");
			this.dbFile.deleteOnExit();
		} catch (IOException e) {
			log.error("Error creating temporary cookie db file", e);
		}
		this.tableName = tableName;
	}
	
	@Override
	public Set<Cookie> getCookies() {
		if (this.cookies.isEmpty()) {
			readSQLite();
			log.info("Found " + this.cookies.size() + " Cookies in " + this.dbFile.getName());
		}
		return this.cookies;
	}

	/**
	 * Reads a sqlite file with the help of {@link #prepareFile()} and {@link #readCookiesFromCursor(ISqlJetCursor)}.
	 */
	public void readSQLite() {
		prepareFile(this.dbFile);
		
		if (!this.dbFile.exists()) {
			log.error("Can't find " + this.dbFile.getName() + ". File doesn't exist. Cancelling cookie extraction.");
			return;
		}
		
		SqlJetDb db = null;
		try {
			db = SqlJetDb.open(this.dbFile, false);
		} catch (SqlJetException e) {
			log.error("Error opening sqlite db: " + this.dbFile, e);
		}
		
		try {
			ISqlJetTable table = db.getTable(this.tableName);
			
			db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
			readCookiesFromCursor(table.order(table.getPrimaryKeyIndexName()));
		} catch (SqlJetException e) {
			log.error("Error reading from sqlite db: " + this.dbFile, e);
		} finally {
			try {
				db.commit();
			} catch (SqlJetException e) {
				log.error("Error finishing transaction for db: " + this.dbFile, e);
			}
		}

		try {
			db.close();
		} catch (SqlJetException e) {
			log.error("Error closing db: " + this.dbFile, e);
		}
	}

	/**
	 * Prepares cookie files. Copies them to working directory. In case of firefox change file format because this version of SQLJet doesn't support wal format.
	 *
	 * @param target
	 *            the target file to copy the sqlite db
	 */
	protected abstract void prepareFile(File target);
	
	/**
	 * Reads records from SQLite file.
	 *
	 * @param cursor
	 *            the cursor to process
	 * @throws NumberFormatException
	 * @throws SqlJetException
	 */
	protected abstract void readCookiesFromCursor(ISqlJetCursor cursor) throws NumberFormatException, SqlJetException;
}
