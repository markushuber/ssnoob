package org.sbaresearch.socialsnapshot.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Helper class for string operation related stuff.
 *
 * @author Maurice Wohlkönig 30.11.2011
 * @since Core 3.0
 * @version 1.0
 */
public class StringUtils {
	
	private static final int SECONDS_PER_MINUTE = 60;
	
	private StringUtils() {
		
	}
	
	/**
	 * @param string
	 *            the string to fill
	 * @param character
	 *            what character to fill with
	 * @param length
	 *            the length on to which the string gets filled with <code>character</code>
	 * @return a new string with the given length padded (at the end) with <code>character</code>
	 */
	public static String fill(String string, char character, int length) {
		StringBuilder s = new StringBuilder(string);
		while (s.length() <= length) {
			s.insert(0, character);
		}
		return s.toString();
	}
	
	/**
	 * Strips all non numeric characters from a given string
	 *
	 * @param str
	 *            the string to strip
	 * @return a new string with only the numbers
	 */
	public static String getOnlyNumerics(String str) {
		if (str == null)
			return null;
		StringBuffer strBuff = new StringBuffer();
		char c;
		
		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);
			if (Character.isDigit(c)) {
				strBuff.append(c);
			}
		}
		return strBuff.toString();
	}
	
	/**
	 * Gets parameters from a string url
	 *
	 * @param url
	 *            the url to get parameters from
	 * @return a hashmap of all parameters
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, List<String>> getUrlParameters(String url) throws UnsupportedEncodingException {
		Map<String, List<String>> params = new HashMap<String, List<String>>();
		String[] urlParts = url.split("\\?");
		if (urlParts.length > 1) {
			String query = urlParts[1];
			for (String param : query.split("&")) {
				String pair[] = param.split("=");
				String key = URLDecoder.decode(pair[0], "UTF-8");
				String value = "";
				if (pair.length > 1) {
					value = URLDecoder.decode(pair[1], "UTF-8");
				}
				List<String> values = params.get(key);
				if (values == null) {
					values = new ArrayList<String>();
					params.put(key, values);
				}
				values.add(value);
			}
		}
		return params;
	}
	
	/**
	 * @param str
	 *            a string to examine
	 * @return true if the given string contains digits, spaces and * symbols
	 */
	@SuppressWarnings("deprecation")
	public static boolean isOnlyNumbersAndSpacesAndStars(String str) {
		char c;
		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);
			if (!Character.isDigit(c) && !Character.isSpace(c) && c != '*')
				return false;
		}
		return true;
	}
	
	/**
	 * Joins a iteratable list of objects by the given separator
	 *
	 * @param iterator
	 *            the list iterator (objects .toString method will be used for the element representation)
	 * @param separator
	 *            the string which should separate the elements
	 * @return a new string of the objects
	 */
	public static String join(Iterator<?> iterator, String separator) {
		if (iterator == null)
			return "";
		// handle null, zero and one elements before building a buffer
		Object first = iterator.next();
		if (!iterator.hasNext())
			return first.toString();
		// two or more elements
		StringBuilder buf = new StringBuilder();
		if (first != null) {
			buf.append(first);
		}
		while (iterator.hasNext()) {
			if (separator != null) {
				buf.append(separator);
			}
			Object obj = iterator.next();
			if (obj != null) {
				buf.append(obj);
			}
		}
		return buf.toString();
	}
	
	/**
	 * Joins a ArrayList of Strings with the given separator
	 *
	 * @param list
	 *            the ArrayList of Strings to join
	 * @param separator
	 *            a separator string
	 * @return a new string representation of the list
	 */
	public static String join(List<String> list, String separator) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String entry : list) {
			if (i != 0) {
				sb.append(separator);
			}
			sb.append(entry);
			i++;
		}
		return sb.toString();
	}
	
	/**
	 * Joins a string array with the given separator
	 *
	 * @param list
	 *            the String array of items to join
	 * @param separator
	 *            the separator string
	 * @return a new string representation of the array
	 */
	public static String join(String[] list, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.length; i++) {
			if (i != 0) {
				sb.append(separator);
			}
			sb.append(list[i]);
		}
		return sb.toString();
	}
	
	/**
	 * Returns the minutes:secods of a given time interval
	 *
	 * @param seconds
	 *            the seconds to convert
	 * @return the minutes:seconds representation of the interval (numbers are not padded with zeros - see {@link #minutesFromSecondPadded(int)} for that)
	 */
	public static String minutesFromSecond(int seconds) {
		return String.format("%d:%02d", seconds / SECONDS_PER_MINUTE, seconds % SECONDS_PER_MINUTE);
	}
	
	/**
	 * Returns the minutes:secods of a given time interval
	 *
	 * @param seconds
	 *            the seconds to convert
	 * @return the minutes:seconds representation of the interval (numbers are padded with zeros to 00:00)
	 */
	public static String minutesFromSecondPadded(int seconds) {
		return String.format("%02d:%02d", seconds / SECONDS_PER_MINUTE, seconds % SECONDS_PER_MINUTE);
	}
	
	/**
	 * prepends a number with a zero if it is less than 10
	 *
	 * @param c
	 *            the number to prepend
	 * @return a string representation of the number with a length of at least 2
	 */
	public static String pad(final int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}
	
	/**
	 * prepends a string with a zero ("0") char if it is less than 2 characters long
	 *
	 * @param c
	 *            the string to prepend
	 * @return the string with a "0" prepended
	 */
	public static String pad(final String c) {
		return c.length() == 1 ? "0" + c : c;
	}
	
	/**
	 * Removes empty (or null) element from a given string array and returns a copy without thouse elements
	 *
	 * @param allElements
	 * @return
	 */
	public static String[] removeEmptyElements(String[] firstArray) {
		List<String> stringList = new ArrayList<String>();
		
		for (String string : firstArray) {
			if (string != null && string.trim().length() > 0) {
				stringList.add(string);
			}
		}
		
		firstArray = stringList.toArray(new String[stringList.size()]);
		return firstArray;
		
	}
	
	/**
	 * reverses a string
	 *
	 * @param string
	 *            the string to reverse
	 * @return a new string in reversed order
	 */
	public static String reverse(String string) {
		return new StringBuffer(string).reverse().toString();
	}
	
	/**
	 * Converts a time interval from minutes to a minutes/hours time string.<br/>
	 * I.E.: 123 > "2 h 3 min", 45 > "45 min"
	 *
	 * @param minutes
	 *            the minutes to convert
	 * @return the time interval string
	 */
	public static String timeFromMinutes(Long minutes) {
		if (minutes > 60) {
			Long hours = (long) Math.floor(minutes / 60);
			minutes -= hours * 60;
			return hours + " h " + minutes + " min";
		} else
			return minutes + " min";
	}
	
	/**
	 * Replaces &, <, >, " in a string with their respective XML representation
	 *
	 * @param str
	 *            the string to encode
	 * @return a "save" encoded xml string (i.E. for usage in xml attributes)
	 */
	public static String xmlEncode(String str) {
		if (str == null)
			return null;
		str = str.replace("&", "&amp;");
		str = str.replace("<", "&lt;");
		str = str.replace(">", "&gt;");
		str = str.replace("\"", "&quot;");
		return str;
	}
}
