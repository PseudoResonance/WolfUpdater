package io.github.wolfleader116.wolfupdater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonReader {

	private static String readAll(Reader rd) {
		StringBuilder sb = new StringBuilder();
		int cp;
		try {
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) {
		try {
			InputStream is = new URL(url).openStream();
			try {
				BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
				String jsonText = readAll(rd);
				JSONObject json = new JSONObject(jsonText);
				return json;
			} finally {
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}