package com.nao20010128nao.BloodyGarden.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import com.nao20010128nao.BloodyGarden.network.Header.GetHeader;
import com.nao20010128nao.BloodyGarden.network.Header.PostHeader;

public class Http {

	public static CookieManager cookie;
	static {
		if (CookieHandler.getDefault() == null) {
			cookie = new CookieManager();
			CookieHandler.setDefault(cookie);
		} else
			cookie = (CookieManager) CookieHandler.getDefault();

	}

	private Http() {

	}

	public static String get(String url, GetHeader header) {
		String result = "";
		try {
			URI uri = new URI(url);
			HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("GET");
			if (header.Host.equals(""))
				connection.setRequestProperty("Host", header.Host);
			if (header.Connection)
				connection.setRequestProperty("Connection", "Keep-Alive");
			if (header.Accept.equals(""))
				connection.setRequestProperty("Accept", header.Accept);
			if (header.UserAgent.equals(""))
				connection.setRequestProperty("User-Agent", header.UserAgent);
			if (header.Referer.equals(""))
				connection.setRequestProperty("Referer", header.Referer);
			if (header.AcceptEncoding.equals(""))
				connection.setRequestProperty("Accept-Encoding", header.AcceptEncoding);
			if (header.AcceptLanguage.equals(""))
				connection.setRequestProperty("Accept-Language", header.AcceptLanguage);

			String location = connection.getHeaderField("Location");
			if (location != null) {
				GetHeader redirect_header = new GetHeader();
				if (header.Host.equals(""))
					redirect_header.setHost(header.Host);
				if (header.Connection)
					redirect_header.setConnection(header.Connection);
				if (header.Accept.equals(""))
					redirect_header.setAccept(header.Accept);
				if (header.UserAgent.equals(""))
					redirect_header.setUserAgent(header.UserAgent);
				if (header.Referer.equals(""))
					redirect_header.setReferer(header.Referer);
				if (header.AcceptEncoding.equals(""))
					redirect_header.setAcceptEncoding(header.AcceptEncoding);
				if (header.AcceptLanguage.equals(""))
					redirect_header.setAcceptLanguage(header.AcceptLanguage);
				result = get((location.indexOf("http") != 0 ? url.substring(0, url.lastIndexOf("/")) : "") + location,
						redirect_header);
			} else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
				StringBuilder sb = new StringBuilder();
				char[] buffer = new char[8192];
				int r = 0;
				while (true) {
					r = reader.read(buffer);
					if (r <= 0)
						break;
					sb.append(buffer, 0, r);
				}
				result = sb.toString();
			}
		} catch (URISyntaxException e) {

		} catch (MalformedURLException e) {

		} catch (IOException e) {

		}
		return result;
	}

	public static String post(String url, String data, PostHeader header) {
		String result = "";
		try {
			URI uri = new URI(url);
			HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			if (header.Host.equals(""))
				connection.setRequestProperty("Host", header.Host);
			if (header.Connection)
				connection.setRequestProperty("Connection", "Keep-Alive");
			if (header.Accept.equals(""))
				connection.setRequestProperty("Accept", header.Accept);
			if (header.Origin.equals(""))
				connection.setRequestProperty("Origin", header.Origin);
			if (header.UserAgent.equals(""))
				connection.setRequestProperty("User-Agent", header.UserAgent);
			if (header.ContentType.equals(""))
				connection.setRequestProperty("Content-Type", header.ContentType);
			if (header.Referer.equals(""))
				connection.setRequestProperty("Referer", header.Referer);
			if (header.AcceptEncoding.equals(""))
				connection.setRequestProperty("Accept-Encoding", header.AcceptEncoding);
			if (header.AcceptLanguage.equals(""))
				connection.setRequestProperty("Accept-Language", header.AcceptLanguage);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
			writer.write(data);
			writer.close();

			String location = connection.getHeaderField("Location");
			if (location != null) {
				GetHeader redirect_header = new GetHeader();
				if (header.Host.equals(""))
					redirect_header.setHost(header.Host);
				if (header.Connection)
					redirect_header.setConnection(header.Connection);
				if (header.Accept.equals(""))
					redirect_header.setAccept(header.Accept);
				if (header.UserAgent.equals(""))
					redirect_header.setUserAgent(header.UserAgent);
				if (header.Referer.equals(""))
					redirect_header.setReferer(header.Referer);
				if (header.AcceptEncoding.equals(""))
					redirect_header.setAcceptEncoding(header.AcceptEncoding);
				if (header.AcceptLanguage.equals(""))
					redirect_header.setAcceptLanguage(header.AcceptLanguage);
				result = get((location.indexOf("http") != 0 ? url.substring(0, url.lastIndexOf("/")) : "") + location,
						redirect_header);
			} else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
				StringBuilder sb = new StringBuilder();
				char[] buffer = new char[8192];
				int r = 0;
				while (true) {
					r = reader.read(buffer);
					if (r <= 0)
						break;
					sb.append(buffer, 0, r);
				}
				result = sb.toString();
			}
		} catch (URISyntaxException e) {
			result = "error1";
		} catch (MalformedURLException e) {
			result = "error2";
		} catch (IOException e) {
			result = "error3";
		}
		return result;
	}

	public static String post_x_www_form_urlencoded(String url, String data, PostHeader header) {
		String result = "";
		try {
			URI uri = new URI(url);
			HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			if (header.Host.equals(""))
				connection.setRequestProperty("Host", header.Host);
			if (header.Connection)
				connection.setRequestProperty("Connection", "Keep-Alive");
			if (header.Accept.equals(""))
				connection.setRequestProperty("Accept", header.Accept);
			if (header.Origin.equals(""))
				connection.setRequestProperty("Origin", header.Origin);
			if (header.UserAgent.equals(""))
				connection.setRequestProperty("User-Agent", header.UserAgent);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			if (header.Referer.equals(""))
				connection.setRequestProperty("Referer", header.Referer);
			if (header.AcceptEncoding.equals(""))
				connection.setRequestProperty("Accept-Encoding", header.AcceptEncoding);
			if (header.AcceptLanguage.equals(""))
				connection.setRequestProperty("Accept-Language", header.AcceptLanguage);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
			writer.write(data);
			writer.close();

			String location = connection.getHeaderField("Location");
			if (location != null) {
				GetHeader redirect_header = new GetHeader();
				if (header.Host.equals(""))
					redirect_header.setHost(header.Host);
				if (header.Connection)
					redirect_header.setConnection(header.Connection);
				if (header.Accept.equals(""))
					redirect_header.setAccept(header.Accept);
				if (header.UserAgent.equals(""))
					redirect_header.setUserAgent(header.UserAgent);
				if (header.Referer.equals(""))
					redirect_header.setReferer(header.Referer);
				if (header.AcceptEncoding.equals(""))
					redirect_header.setAcceptEncoding(header.AcceptEncoding);
				if (header.AcceptLanguage.equals(""))
					redirect_header.setAcceptLanguage(header.AcceptLanguage);
				result = get((location.indexOf("http") != 0 ? url.substring(0, url.lastIndexOf("/")) : "") + location,
						redirect_header);
			} else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
				StringBuilder sb = new StringBuilder();
				char[] buffer = new char[8192];
				int r = 0;
				while (true) {
					r = reader.read(buffer);
					if (r <= 0)
						break;
					sb.append(buffer, 0, r);
				}
				result = sb.toString();
			}
		} catch (URISyntaxException e) {
			result = "error1";
		} catch (MalformedURLException e) {
			result = "error2";
		} catch (IOException e) {
			result = "error3";
		}
		return result;
	}

	public static String post_form_data(String url, String boundary, String[] data, PostHeader header) {
		String result = "";
		try {
			String post_data = "";
			for (String cdata : data)
				post_data += "--" + boundary + "\r\n" + cdata + "\r\n";

			URI uri = new URI(url);
			HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			if (header.Host.equals(""))
				connection.setRequestProperty("Host", header.Host);
			if (header.Connection)
				connection.setRequestProperty("Connection", "Keep-Alive");
			if (header.Accept.equals(""))
				connection.setRequestProperty("Accept", header.Accept);
			if (header.Origin.equals(""))
				connection.setRequestProperty("Origin", header.Origin);
			if (header.UserAgent.equals(""))
				connection.setRequestProperty("User-Agent", header.UserAgent);
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			if (header.Referer.equals(""))
				connection.setRequestProperty("Referer", header.Referer);
			if (header.AcceptEncoding.equals(""))
				connection.setRequestProperty("Accept-Encoding", header.AcceptEncoding);
			if (header.AcceptLanguage.equals(""))
				connection.setRequestProperty("Accept-Language", header.AcceptLanguage);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
			writer.write(post_data);
			writer.close();

			String location = connection.getHeaderField("Location");
			if (location != null) {
				GetHeader redirect_header = new GetHeader();
				if (header.Host.equals(""))
					redirect_header.setHost(header.Host);
				if (header.Connection)
					redirect_header.setConnection(header.Connection);
				if (header.Accept.equals(""))
					redirect_header.setAccept(header.Accept);
				if (header.UserAgent.equals(""))
					redirect_header.setUserAgent(header.UserAgent);
				if (header.Referer.equals(""))
					redirect_header.setReferer(header.Referer);
				if (header.AcceptEncoding.equals(""))
					redirect_header.setAcceptEncoding(header.AcceptEncoding);
				if (header.AcceptLanguage.equals(""))
					redirect_header.setAcceptLanguage(header.AcceptLanguage);
				result = get((location.indexOf("http") != 0 ? url.substring(0, url.lastIndexOf("/")) : "") + location,
						redirect_header);
			} else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
				StringBuilder sb = new StringBuilder();
				char[] buffer = new char[8192];
				int r = 0;
				while (true) {
					r = reader.read(buffer);
					if (r <= 0)
						break;
					sb.append(buffer, 0, r);
				}
				result = sb.toString();
			}
		} catch (URISyntaxException e) {
			result = "error1";
		} catch (MalformedURLException e) {
			result = "error2";
		} catch (IOException e) {
			result = "error3";
		}
		return result;
	}
}
