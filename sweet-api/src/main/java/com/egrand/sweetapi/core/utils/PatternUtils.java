package com.egrand.sweetapi.core.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 正则相关工具包
 *
 */
public class PatternUtils {

	private static final Map<String, Pattern> CACHED_PATTERNS = new ConcurrentHashMap<>();

	public static boolean match(String content, String regex) {
		Pattern pattern = CACHED_PATTERNS.get(regex);
		if (pattern == null) {
			pattern = Pattern.compile(regex);
			CACHED_PATTERNS.put(regex, pattern);
		}
		return pattern.matcher(content).find();
	}
}
