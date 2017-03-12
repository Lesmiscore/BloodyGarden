package com.nao20010128nao.BloodyGarden;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jsoup.nodes.Document;

final class Utils {
	static String encode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	static boolean hasLoginFields(Document doc) {
		return !doc.select(
				"div.input--mail__wrapper," +
						"div.input--password__wrapper," +
						"input.input--email," +
						"input.input--password," +
						"input#input--email," +
						"input#input--password")
				.isEmpty();
	}

}
