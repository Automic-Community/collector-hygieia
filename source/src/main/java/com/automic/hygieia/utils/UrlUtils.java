package com.automic.hygieia.utils;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

import com.automic.hygieia.exception.CollectorException;

public class UrlUtils {
	private UrlUtils() {
	}

	public static String combineUrl(String base, String relative) {
		URL url;
		try {
			url = new URL(base);

			String pathWithoutSlash = ensureNotStartWith(url.getPath(), "/");

			relative = ensureNotStartWith(relative, "/");

			if (StringUtils.startsWithIgnoreCase(relative, pathWithoutSlash)) {
				relative = relative.substring(pathWithoutSlash.length());
				relative = ensureNotStartWith(relative, "/");
			}

			if (!relative.startsWith("?")) {
				base = ensureEndsWith(base, "/");
			}

			String combinedUrl = normalizeUrl(base + relative);


			return combinedUrl;
		} catch (MalformedURLException e) {
			throw new CollectorException("Unable to combine base url " + base + " with " + relative);
		}
	}
	
	public static String normalizeUrl(String url) {
		return removeLeadingSlash(removeTrailingSlash(url));
	}
	
	private static String ensureNotStartWith(String s, String prefix) {
		while (s.startsWith(prefix) && s.length() > prefix.length()) {
			s = s.substring(prefix.length());
		}
		return s;
	}
	
	private static String ensureEndsWith(String s, String suffix) {
		if (!s.endsWith(suffix)) {
			return s + suffix;
		}
		return s;
	}
	
	private static String removeLeadingSlash(String url) {
		if (StringUtils.isBlank(url)) {
			return url;
		}
		return url.startsWith("/") ? url.substring(1) : url;
	}
	
	private static String removeTrailingSlash(String url) {
		if (StringUtils.isBlank(url)) {
			return url;
		}
		return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
	}
}
