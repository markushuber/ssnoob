package org.sbaresearch.socialsnapshot.util;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.sbaresearch.socialsnapshot.Config;
import org.sbaresearch.socialsnapshot.crawl.DownloadQueue;
import org.sbaresearch.socialsnapshot.crawl.entity.FbObject;
import org.sbaresearch.socialsnapshot.crawl.entity.ImageDownloadUrl;

import com.gargoylesoftware.htmlunit.util.Cookie;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * @author Stefan Haider (shaider@sba-research.org), original code: Maurice Wohlkönig
 */
public class Utils {

	static Logger log = Logger.getLogger(Utils.class);
	
	public static final String IMPORT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	public static Gson gson;
	
	public static String cookieCollectionToString(Collection<Cookie> cookies) {
		StringBuilder sb = new StringBuilder();
		for (Cookie c : cookies) {
			if (sb.length() != 0) {
				sb.append(';');
			}
			sb.append(c.getName());
			sb.append('=');
			sb.append(c.getValue());
		}
		return sb.toString();
	}

	public static Set<Cookie> filterCookiesForDomain(Set<Cookie> allCookies, String cookieDomain) {
		Set<Cookie> filteredCookies = new HashSet<Cookie>();
		if (allCookies != null) {
			for (Cookie element : allCookies) {
				if (element.getDomain().contains(cookieDomain) && element.getValue().trim().length() > 0) {
					filteredCookies.add(element);
				}
			}
		}
		return filteredCookies;
	}

	public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
		return getAllFields(fields, type, -1, 0);
	}
	
	public static List<Field> getAllFields(List<Field> fields, Class<?> type, int maxDepth) {
		return getAllFields(fields, type, maxDepth, 0);
	}
	
	public static Gson getImportGson() {
		if (gson == null) {
			gson = new GsonBuilder().setDateFormat(IMPORT_DATE_FORMAT).registerTypeAdapter(FbObject.class, new FbObjectDeserializer()).create();
		}
		return gson;
	}
	
	public static String getOsArchitecture() {
		return System.getProperty("os.arch");
	}

	public static String getOsName() {
		return System.getProperty("os.name");
	}

	public static String getOsString() {
		if (onWindows())
			return "Windows NT " + getOsVersion();
		else if (onLinux())
			return getOsName() + " " + getOsVersion();
		else
			return getOsName();
	}
	
	public static String getOsVersion() {
		return System.getProperty("os.version");
	}
	
	public static boolean isDirectoryExisting(String path) {
		File f = new File(path);
		return f.isDirectory();
	}
	
	public static boolean isFileExisting(String path) {
		File f = new File(path);
		return f.isFile();
	}
	
	public static boolean isPathExisting(String path) {
		File f = new File(path);
		return f.exists();
	}

	public static boolean onLinux() {
		return getOsName().contains("Linux");
	}

	public static boolean onWindows() {
		return getOsName().contains("Windows");
	}
	
	/**
	 * Walks all fields and their subfields recursively and checks them for Download specific annotations
	 *
	 * @param obj
	 *            the object to analyze
	 */
	public static void searchForDownloadUrls(Object obj) {
		Set<Object> visited = new HashSet<Object>();
		searchForDownloadUrls(obj, visited);
	}
	
	private static List<Field> getAllFields(List<Field> fields, Class<?> type, int maxDepth, int curDepth) {
		for (Field field : type.getDeclaredFields()) {
			fields.add(field);
		}
		
		if (type.getSuperclass() != null && (curDepth <= maxDepth) || maxDepth == -1) {
			fields = getAllFields(fields, type.getSuperclass(), maxDepth, curDepth++);
		}
		
		return fields;
	}
	
	private static void searchForDownloadUrls(Object obj, Set<Object> visited) {
		if (obj == null || !Config.isImageDownloadEnabled())
			return;
		ImageDownloadUrl annotation;
		int basePriority = 0, newpriority;
		if (obj instanceof FbObject) {
			basePriority = ((FbObject) obj).getPriority();
		}
		
		try {
			for (Field field : Utils.getAllFields(new LinkedList<Field>(), obj.getClass(), 1)) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				field.setAccessible(true);
				// if (obj instanceof PhotoConnection && field.getName().equals("photos")) {
				// log.debug("### searching for download urls in " + field.getName());
				// }
				if (field.getType() == String.class && (annotation = field.getAnnotation(ImageDownloadUrl.class)) != null) {
					newpriority = basePriority + annotation.value();
					if (newpriority <= Config.getMaxImagePriority()) {
						Object fieldObj = field.get(obj);
						if (fieldObj != null) {
							DownloadQueue.getInstance().add(DownloadQueue.getInstance().new ImageDownloadJob(newpriority, fieldObj.toString()));
						}
					}
				} else {
					try {
						Object child = field.get(obj);
						if (!visited.contains(child)) {
							visited.add(child);
							if (child instanceof Object[]) {
								for (Object childd : ((Object[]) child)) {
									searchForDownloadUrls(childd, visited);
								}
							} else {
								searchForDownloadUrls(child, visited);
							}
						}
					} catch (Exception e) {
						log.error("can not access field " + field.getName() + " of " + obj);
					}
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.error("can not access field value of " + obj, e);
		}
	}
	
	static class FbObjectDeserializer implements JsonDeserializer<FbObject> {
		
		@Override
		public FbObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jobject = (JsonObject) json;
			String type = jobject.get("type").getAsString();
			Type typeOf = FbObject.getTypeFor(type);
			return context.deserialize(json, typeOf);
		}
	}
}
