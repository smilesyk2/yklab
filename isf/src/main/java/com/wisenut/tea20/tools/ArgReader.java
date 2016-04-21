package com.wisenut.tea20.tools;

import java.util.HashMap;
import java.util.Map;

public class ArgReader {
	private Map<String, String> argumentMap = null;

	public ArgReader(String[] args) {
		argumentMap = new HashMap<String, String>();

		if (null == args || 0 == args.length) {
			return;
		}

		String key = "";
		String value = "";
		for (int i = 0; i < args.length; i++) {
			String term = args[i];
			if (term.charAt(0) == '-') {
				if (!"".equals(key)) {
					argumentMap.put(key, value);
				}

				key = term.substring(1);
				value = "";
				continue;
			}
			value = term;
		}

		if (0 != key.length() && !argumentMap.containsKey(key)) {
			argumentMap.put(key, value);
		}
	}

	public String getValue(String key) {
		return argumentMap.get(key);
	}

	public int getValueInt(String key) {
		int toReturn = 0;

		try {
			toReturn = Integer.parseInt(argumentMap.get(key));
		} catch (Exception e) {
		}

		return toReturn;
	}

	public boolean isSet(String key) {
		return (null == argumentMap.get(key) ? false : true);
	}
}
