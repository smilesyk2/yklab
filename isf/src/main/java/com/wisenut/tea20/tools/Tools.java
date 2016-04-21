package com.wisenut.tea20.tools;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

import com.wisenut.tea20.types.*;

public class Tools {
	private static String REQUEST_ID_HEADER = "";
	private static String PREVIOUS_TIMESTAMP = "";
	private static int REQUEST_ID_IDX = 0;

	private static void setReqIdHeader() {
		REQUEST_ID_HEADER = "";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			REQUEST_ID_HEADER = addr.getHostName() + "-" + addr.getHostAddress() + "-";
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (REQUEST_ID_HEADER.equals("")) {
			REQUEST_ID_HEADER = "UNDEFINED-" + (int) (Math.random() * 1024) + "-";
		}
	}

	private static String getReqIdBody() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
		String body = formatter.format(new Date());
		if (body.equals(PREVIOUS_TIMESTAMP)) {
			REQUEST_ID_IDX++;
		} else {
			PREVIOUS_TIMESTAMP = body;
			REQUEST_ID_IDX = 0;
		}

		return body + "-" + Integer.toString(REQUEST_ID_IDX, 16);
	}

	public static String getUniqueId() {
		String tail = "";

		synchronized (REQUEST_ID_HEADER) {
			if ("".equals(REQUEST_ID_HEADER)) {
				setReqIdHeader();
			}
			tail = getReqIdBody();
		}

		return tail;
	}

	public static int parseInt(String str) {
		int toReturn = 0;

		try {
			toReturn = Integer.parseInt(str.trim());
		} catch (Exception e) {
			toReturn = 0;
		}

		return toReturn;
	}

	public static long parseLong(String str) {
		long toReturn = 0;

		try {
			toReturn = Long.parseLong(str.trim());
		} catch (Exception e) {
			toReturn = 0;
		}

		return toReturn;
	}

	public static double parseDouble(String str) {
		double toReturn = 0;

		try {
			toReturn = Double.parseDouble(str.trim());
		} catch (Exception e) {
			toReturn = 0;
		}

		return toReturn;
	}

	public static long parseTime(String str) {
		long toReturn = 0;

		if (null == str || 0 == str.length()) {
			return toReturn;
		}

		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			java.util.Date date = formatter.parse(str.trim());
			toReturn = date.getTime();
		} catch (Exception e) {
			toReturn = 0;
		}

		return toReturn;
	}

	public static String getTimeString(long time) {
		String toReturn = "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			toReturn = formatter.format(new Date(time));
		} catch (Exception e) {
		}

		return toReturn;
	}

	public static String padEmptyItem(String item, String delimiter) {
		StringBuffer toReturn = new StringBuffer();

		if (null == item || 0 == item.length() || null == delimiter || 0 == delimiter.length()) {
			return item;
		}

		int size = item.length();
		char delimChar = delimiter.charAt(0);

		if (item.charAt(0) == delimChar) {
			toReturn.append(' ');
		}

		int idx = 0;
		for (; idx < size; idx++) {
			char ch = item.charAt(idx);
			toReturn.append(ch);

			if (ch == delimChar) {
				if (idx == (size - 1) || item.charAt(idx + 1) == delimChar) {
					toReturn.append(' ');
				}
			}
		}

		return toReturn.toString();
	}

	static <T> String getSerializedString(Vector<Pair<T>> obj, String itemDelimiter, String valueDelimiter) {
		if (null == obj || 0 == obj.size() || null == itemDelimiter || 1 != itemDelimiter.length() || null == valueDelimiter
				|| 1 != valueDelimiter.length()) {
			return "";
		}

		StringBuffer toReturn = new StringBuffer("");

		for (int i = 0; i < obj.size(); i++) {
			if (0 != toReturn.length()) {
				toReturn.append(itemDelimiter);
			}

			Pair<T> item = obj.get(i);
			if (null == item) {
				continue;
			}
			toReturn.append(item.key());
			toReturn.append(valueDelimiter);
			toReturn.append(item.value());
		}

		return toReturn.toString();
	}

	static <T> Vector<Pair<T>> getVector(String obj, String itemDelimiter, String valueDelimiter) {
		Vector<Pair<T>> toReturn = new Vector<Pair<T>>();

		if (null == obj || 0 == obj.length() || null == itemDelimiter || 1 != itemDelimiter.length() || null == valueDelimiter
				|| 1 != valueDelimiter.length()) {
			return toReturn;
		}

		return toReturn;
	}

	public static <T> String getSerializedString(List<Pair<T>> obj, String itemDelimiter, String valueDelimiter) {
		if (null == obj || 0 == obj.size() || null == itemDelimiter || 0 == itemDelimiter.length() || null == valueDelimiter
				|| 0 == valueDelimiter.length()) {
			return "";
		}

		StringBuffer toReturn = new StringBuffer("");

		for (int i = 0; i < obj.size(); i++) {
			if (0 != toReturn.length()) {
				toReturn.append(itemDelimiter);
			}

			Pair<T> item = obj.get(i);
			if (null == item) {
				continue;
			}
			toReturn.append(item.key());
			toReturn.append(valueDelimiter);
			toReturn.append(item.value());
		}

		return toReturn.toString();
	}

	public static void setPairListStr(String str, String itemDelimiter, String valueDelimiter, List<Pair<String>> target) {
		if (null == str || 0 == str.length() || null == target || null == itemDelimiter || 1 != itemDelimiter.length()
				|| null == valueDelimiter || 1 != valueDelimiter.length()) {
			return;
		}

		StringTokenizer tokenizer = new StringTokenizer(str, itemDelimiter);
		while (tokenizer.hasMoreTokens()) {
			String item = tokenizer.nextToken();
			//it's import to retain position, so I'll pad with empty element
			if (null == item || 0 == item.length()) {
				target.add(new Pair<String>("", ""));
				continue;
			}
			StringTokenizer valueTokenizer = new StringTokenizer(item, valueDelimiter);
			String key = valueTokenizer.nextToken();
			String value = "";
			if (valueTokenizer.hasMoreTokens()) {
				value = valueTokenizer.nextToken();
			}

			target.add(new Pair<String>(key, value));
		}
	}

	public static List<Pair<String>> getPairListStr(String obj, String itemDelimiter, String valueDelimiter) {
		List<Pair<String>> toReturn = new ArrayList<Pair<String>>();
		if (null == obj || 0 == obj.length() || null == itemDelimiter || 1 != itemDelimiter.length() || null == valueDelimiter
				|| 1 != valueDelimiter.length()) {
			return toReturn;
		}

		StringTokenizer tokenizer = new StringTokenizer(obj, itemDelimiter);
		while (tokenizer.hasMoreTokens()) {
			String item = tokenizer.nextToken();

			//it's import to retain position, so I'll pad with empty element
			if (null == item || 0 == item.length()) {
				toReturn.add(new Pair<String>("", ""));
				continue;
			}
			StringTokenizer valueTokenizer = new StringTokenizer(item, valueDelimiter);
			String key = valueTokenizer.nextToken();
			String value = "";
			if (valueTokenizer.hasMoreTokens()) {
				value = valueTokenizer.nextToken();
			}

			toReturn.add(new Pair<String>(key, value));
		}

		return toReturn;
	}

	public static void setPairListInt(String str, String itemDelimiter, String valueDelimiter, List<Pair<Integer>> target) {
		if (null == str || 0 == str.length() || null == itemDelimiter || null == target || 1 != itemDelimiter.length()
				|| null == valueDelimiter || 1 != valueDelimiter.length()) {
			return;
		}

		StringTokenizer tokenizer = new StringTokenizer(str, itemDelimiter);
		while (tokenizer.hasMoreTokens()) {
			String item = tokenizer.nextToken();
			
			//it's import to retain position, so I'll pad with empty element
			if (null == item || 0 == item.length()) {
				target.add(new Pair<Integer>("", 0));
				continue;
			}
			StringTokenizer valueTokenizer = new StringTokenizer(item, valueDelimiter);
			String key = valueTokenizer.nextToken();
			String valueStr = "";
			int value = 0;

			if (valueTokenizer.hasMoreTokens()) {
				valueStr = valueTokenizer.nextToken();
				try {
					value = Integer.parseInt(valueStr);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			target.add(new Pair<Integer>(key, new Integer(value)));
		}

	}

	public static List<Pair<Integer>> getPairListInt(String obj, String itemDelimiter, String valueDelimiter) {
		List<Pair<Integer>> toReturn = new ArrayList<Pair<Integer>>();
		if (null == obj || 0 == obj.length() || null == itemDelimiter || 1 != itemDelimiter.length() || null == valueDelimiter
				|| 1 != valueDelimiter.length()) {
			return toReturn;
		}

		StringTokenizer tokenizer = new StringTokenizer(obj, itemDelimiter);
		while (tokenizer.hasMoreTokens()) {
			String item = tokenizer.nextToken();
			
			//it's import to retain position, so I'll pad with empty element
			if (null == item || 0 == item.length()) {
				toReturn.add(new Pair<Integer>("", 0));
				continue;
			}
			StringTokenizer valueTokenizer = new StringTokenizer(item, valueDelimiter);
			String key = valueTokenizer.nextToken();
			String valueStr = "";
			int value = 0;

			if (valueTokenizer.hasMoreTokens()) {
				valueStr = valueTokenizer.nextToken();
				try {
					value = Integer.parseInt(valueStr);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			toReturn.add(new Pair<Integer>(key, new Integer(value)));
		}

		return toReturn;
	}
	
	public static List<Pair<Double>> getPairListDouble(String obj, String itemDelimiter, String valueDelimiter) {
		List<Pair<Double>> toReturn = new ArrayList<Pair<Double>>();
		if (null == obj || 0 == obj.length() || null == itemDelimiter || 1 != itemDelimiter.length() || null == valueDelimiter
				|| 1 != valueDelimiter.length()) {
			return toReturn;
		}

		StringTokenizer tokenizer = new StringTokenizer(obj, itemDelimiter);
		while (tokenizer.hasMoreTokens()) {
			String item = tokenizer.nextToken();
			
			//it's import to retain position, so I'll pad with empty element
			if (null == item || 0 == item.length()) {
				toReturn.add(new Pair<Double>("", 0.0));
				continue;
			}
			StringTokenizer valueTokenizer = new StringTokenizer(item, valueDelimiter);
			String key = valueTokenizer.nextToken();
			String valueStr = "";
			double value = 0;

			if (valueTokenizer.hasMoreTokens()) {
				valueStr = valueTokenizer.nextToken();
				try {
					value = Double.parseDouble(valueStr);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			toReturn.add(new Pair<Double>(key, new Double(value)));
		}

		return toReturn;
	}

	public static boolean isEqualSet(String[] set1, String[] set2) {
		if (null == set1 && null == set2) {
			return true;
		}
		if (null == set1 || null == set2) {
			return false;
		}
		if (set1.length != set2.length) {
			return false;
		}

		Set<String> set = new HashSet<String>();
		for (int i = 0; i < set2.length; i++) {
			set.add(set2[i]);
		}

		for (int i = 0; i < set1.length; i++) {
			if (!set.contains(set1[i])) {
				return false;
			}
		}

		return true;
	}

	public static String getWebUnicodeString(String str) {
		int idx = 0;
		int idxBefore = 0;
		StringBuffer toReturn = new StringBuffer();

		while ((idx = str.indexOf("&#", idxBefore)) >= 0) {
			if (idxBefore != idx) {
				toReturn.append(str.substring(idxBefore, idx));
			}
			int endToken = str.indexOf(';', idx);
			if (endToken < 0) {
				break;
			}
			String token = str.substring(idx, endToken);

			if (token.length() > 2 && token.charAt(0) == '&' && token.charAt(1) == '#') {
				if (token.charAt(2) == 'x') {
					if (token.length() > 3) {
						try {
							toReturn.append((char) Integer.parseInt(token.substring(3), 16));
						} catch (Exception e) {
							toReturn.append(token);
						}
					}
				} else {
					try {
						toReturn.append((char) Integer.parseInt(token.substring(2)));
					} catch (Exception e) {
						toReturn.append(token);
					}
				}
			} else {
				toReturn.append(token);
			}
			idxBefore = endToken + 1;
		}

		if (idxBefore < str.length()) {
			toReturn.append(str.substring(idxBefore, str.length()));
		}

		return toReturn.toString();
	}

	/**
	 * 배열의 아이템을 구분자로 엮은 하나의 문자열로 변환
	 * @param items
	 * @param delimiter
	 * @return
	 */
	public static String connectByDelimiter(String[] items, String delimiter) {
		StringBuffer buffer = new StringBuffer();

		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				buffer.append(items[i] != null ? items[i] : "");
				if (i < (items.length - 1)) {
					buffer.append(",");
				}
			}
		}

		return buffer.toString();
	}

	/**
	 * 서버에서 받은 연관키워드 목록 문자열을 구분자를 사용해 Pair 목록으로 변환
	 * @param source
	 * @param itemDelimiter
	 * @param valueDelimiter
	 * @return
	 */
	public static List<Pair<String>> getRelationPairList(String source, String itemDelimiter, String valueDelimiter) {
		List<Pair<String>> result = new ArrayList<Pair<String>>();

		if (source != null && !source.equals("")) {
			StringTokenizer tokenizer = new StringTokenizer(source, itemDelimiter);
			while (tokenizer.hasMoreTokens()) {
				String item = tokenizer.nextToken(); //item : relation:term:term
				if (item == null || item.equals("")) {
					result.add(new Pair<String>("", ""));
					continue;
				}

				StringTokenizer valueTokenizer = new StringTokenizer(item, valueDelimiter);
				int valueTokenCount = valueTokenizer.countTokens();
				if (valueTokenCount > 0) {
					int i = 0;
					String mainKeyword = "";
					StringBuffer relativeKeyword = new StringBuffer();
					while (valueTokenizer.hasMoreTokens()) {
						if (i == 0) {
							mainKeyword = valueTokenizer.nextToken();
						} else {
							relativeKeyword.append(valueTokenizer.nextToken());
							if (i < (valueTokenCount - 1)) {
								relativeKeyword.append(",");
							}
						}
						i++;
					}

					result.add(new Pair<String>(mainKeyword, relativeKeyword.toString()));
				}
			}
		}

		return result;
	}

	/**
	 * 문자열이 포함하고 있는 구분자를 다른 구분자로 변경
	 * @param source
	 * @param fromDelimiter
	 * @param toDelimiter
	 * @return
	 */
	public static String convertDelimiter(String source, String fromDelimiter, String toDelimiter) {
		if (source != null) {
			source = source.replaceAll(fromDelimiter, toDelimiter);
		} else {
			source = "";
		}

		return source;
	}

	public static List<HistoryInfo> getHistList(String obj
			, String itemDelimiter, String valueDelimiter) {

		List<HistoryInfo> result = new ArrayList<HistoryInfo>();

		if (null == obj || 0 == obj.length()
				|| null == itemDelimiter
				|| 1 != itemDelimiter.length()
				|| null == itemDelimiter
				|| 1 != valueDelimiter.length()) {
			return result;
		}

		StringTokenizer tokenizer = new StringTokenizer(obj, itemDelimiter);
		while (tokenizer.hasMoreTokens()) {
			String item = tokenizer.nextToken();

			//it's import to retain position, so I'll pad with empty element
			if (null == item || 0 == item.length()) {
				result.add(new HistoryInfo());
				continue;
			}
			StringTokenizer valueTokenizer = new StringTokenizer(item.trim(), valueDelimiter);
			List<String> values = new ArrayList<String>();
			while (valueTokenizer.hasMoreTokens()) {
				values.add(valueTokenizer.nextToken());
			}

			if (values.size() == 5) {
				String last = values.get(4).replaceAll("(\\r|\\n|\\t)", "");
				HistoryInfo historyInfo = new HistoryInfo(
						Integer.parseInt(values.get(0)), values.get(1)
						, values.get(2), values.get(3), last);
				result.add(historyInfo);
			}
		}
		return result;
	}

	public static void main(String[] args) {
		System.out.println("[" + padEmptyItem("", ":") + "]");
		System.out.println("[" + padEmptyItem(":", ":") + "]");
		System.out.println("[" + padEmptyItem(":b", ":") + "]");
		System.out.println("[" + padEmptyItem("a:", ":") + "]");
		System.out.println("[" + padEmptyItem("::c:d:", ":") + "]");
		System.out.println("[" + padEmptyItem("a::c", ":") + "]");
		System.out.println("[" + padEmptyItem("a:b:::e", ":") + "]");
		System.out.println("[" + padEmptyItem("a:b:::e:", ":") + "]");
		System.out.println("[" + padEmptyItem(":this:::a:", ":") + "]");
		System.out.println(getWebUnicodeString("this is &#50864;&#52404;&#44397; &#53469;&#48176; topic"));
	}
}
