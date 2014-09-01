package org.sbaresearch.socialsnapshot.crawl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.sbaresearch.socialsnapshot.Config;
import org.sbaresearch.socialsnapshot.crawl.entity.FbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.connections.FbConnection;
import org.sbaresearch.socialsnapshot.util.Utils;

import com.google.gson.JsonSyntaxException;
import com.rits.cloning.Cloner;

/**
 * @author Maurice Wohlkönig
 */
public class FacebookLoader {

	static Logger log = Logger.getLogger(FacebookLoader.class);
	private static String accessToken;

	/**
	 * Caches FbObject based on their url so multiple requests to the same url does not trigger a real http request
	 */
	ConcurrentHashMap<String, FbObject> objectCache = new ConcurrentHashMap<String, FbObject>();
	Cloner cloner = new Cloner();

	public FacebookLoader() {
		// cloner.setDumpClonedClasses(true);
	}

	public FbConnection load(FbConnection object) {
		try (BufferedReader reader = doStreamRequest(new URL(object.getGraphUrl()));) {
			if (reader != null) {
				FbConnection obj = Utils.getImportGson().fromJson(reader, object.getClass());
				return obj;
			}
		} catch (MalformedURLException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		} catch (JsonSyntaxException e) {
			log.error("could not parse result to object " + object.getType(), e);
		} finally {
		}
		return null;
	}

	public FbObject load(FbObject object) {
		return load(object, object.getClass());
	}

	public FbObject load(FbObject object, Type type) {
		log.info("Loading " + object);
		
		String url = object.getGraphUrl() + "&access_token=" + accessToken;
		if (this.objectCache.containsKey(url)) {
			try {
				return getCopy(url);
			} catch (ConcurrentModificationException e) {
				// ignore - just let it download again
			}
		}
		try (BufferedReader reader = doStreamRequest(new URL(url));) {
			if (reader != null) {
				FbObject obj = (FbObject) Utils.getImportGson().fromJson(reader, type);
				this.objectCache.put(url, obj);
				return obj;
			}
		} catch (MalformedURLException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		} catch (JsonSyntaxException e) {
			log.error("could not parse result to object " + object.getType(), e);
		}
		return null;
	}

	private BufferedReader doStreamRequest(URL url) throws IOException {
		// log.debug("requesting " + url);
		URLConnection connection = url.openConnection();
		final String charset = "UTF-8";
		connection.setRequestProperty("Accept-Charset", charset);
		
		connection.setRequestProperty("User-Agent", Config.getBrowserName());

		int status = ((HttpURLConnection) connection).getResponseCode();
		if (status != HttpURLConnection.HTTP_OK)
			// log.error("Remote server returned status " + status + " to request " + url.toString());
			return null;
		return new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));

	}

	private FbObject getCopy(String url) {
		FbObject objCopy = this.cloner.deepClone(this.objectCache.get(url));
		objCopy.setCopy(true);
		log.debug("returned this object from cache: " + url);
		return objCopy;
	}

	public static String getAccessToken() {
		return accessToken;
	}

	public static void setAccessToken(String accessToken) {
		FacebookLoader.accessToken = accessToken;
	}

}
