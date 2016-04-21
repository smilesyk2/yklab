package com.wisenut.tea20.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * String Utility Class.
 *
 * Provides simple functionalities about String objects.
 * 
 * @author hkseo@wisenut.co.kr
 */

public class StringTool {
	/**
	 * String 배열을 단일 String 문자열로 변환
	 * 
	 * @param items
	 * @param delimiter
	 * @return
	 */
	public static String arrayToString(String[] items, char delimiter) {
		if (items == null || items.length == 0) {
			return "";
		}

		StringBuffer toReturn = new StringBuffer();
		for (int i = 0; i < items.length; i++) {
			if (i != 0) {
				toReturn.append(delimiter);
			}
			toReturn.append(items[i]);
		}

		return toReturn.toString();
	}

	/**
	 * String 배열을 단일 String 문자열로 변환
	 * 
	 * @param items
	 * @param delimiter
	 * @return
	 */
	public static String arrayToString(String[] items, String delimiter) {
		if (items == null || items.length == 0) {
			return "";
		}

		StringBuffer toReturn = new StringBuffer();
		for (int i = 0; i < items.length; i++) {
			if (i != 0) {
				toReturn.append(delimiter);
			}
			toReturn.append(items[i]);
		}

		return toReturn.toString();
	}

	/**
	 * 단일 문자열을 구분자를 기준으로 String 배열로 변환
	 * 
	 * @param str
	 * @param delimiters
	 * @return
	 */
	public static String[] stringToArray(String str, String delimiters) {
		if (null == str || 0 == str.length()) {
			return new String[0];
		}

		List<String> result = new ArrayList<String>();

		String[] itemArray = str.split("[\\" + delimiters + "]", -1);
		for (String item : itemArray) {
			if (item == null) {
				result.add("");
			} else {
				result.add(item.trim());
			}
		}

		return result.toArray(new String[0]);
	}

	/**
	 * 임의의 문자열중 일부 문자를 다른 값으로 치환
	 * 
	 * @param resource
	 * @param before
	 * @param after
	 * @return
	 */
	public static String replace(String resource, String before, String after) {
		if (null == before || 0 == before.length()) {
			return resource;
		}

		StringBuffer toReturn = new StringBuffer();
		int pos = 0;
		int posBefore = 0;
		while ((pos = resource.indexOf(before, posBefore)) >= 0) {
			toReturn.append(resource.substring(posBefore, pos));
			toReturn.append(after);
			posBefore = pos + before.length();
		}

		if (posBefore < resource.length()) {
			toReturn.append(resource.substring(posBefore, resource.length()));
		}

		return toReturn.toString();
	}
}
