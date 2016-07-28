package com.nao20010128nao.BloodyGarden;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.nao20010128nao.BloodyGarden.network.Http;
import com.nao20010128nao.BloodyGarden.network.Header.GetHeader;
import com.nao20010128nao.BloodyGarden.network.Header.PostHeader;
import com.nao20010128nao.BloodyGarden.structures.Bookmarks;
import com.nao20010128nao.BloodyGarden.structures.Chat;
import com.nao20010128nao.BloodyGarden.structures.Contacts;
import com.nao20010128nao.BloodyGarden.structures.Followers;
import com.nao20010128nao.BloodyGarden.structures.Group;
import com.nao20010128nao.BloodyGarden.structures.MakePrivateGroupResult;
import com.nao20010128nao.BloodyGarden.structures.Me;
import com.nao20010128nao.BloodyGarden.structures.Notifications;
import com.nao20010128nao.BloodyGarden.structures.Pokes;
import com.nao20010128nao.BloodyGarden.structures.PrivateGroups;
import com.nao20010128nao.BloodyGarden.structures.PublicGroups;
import com.nao20010128nao.BloodyGarden.structures.User;

public class BasicAPI {
	private Http NetworkAPI = new Http();
	private Gson gson = new Gson();

	public BasicAPI() {

	}

	public boolean login(String mail, String password) {
		GetHeader header1 = new GetHeader()
				.setHost("lobi.co")
				.setConnection(true)
				.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");
		String source = this.NetworkAPI.get("https://lobi.co/signin", header1);
		String csrf_token = Pattern.get_string(source, Pattern.csrf_token, "\"");

		String post_data = String.format("csrf_token=%s&email=%s&password=%s", csrf_token, mail, password);
		PostHeader header2 = new PostHeader()
				.setHost("lobi.co")
				.setConnection(true)
				.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6")
				.setOrigin("https://lobi.co")
				.setReferer("https://lobi.co/signin");

		String result = this.NetworkAPI.post_x_www_form_urlencoded("https://lobi.co/signin", post_data, header2);
		return result.indexOf("ログインに失敗しました") == -1;
	}

	public boolean twitterLogin(String mail, String password) {
		GetHeader header1 = new GetHeader()
				.setHost("lobi.co")
				.setConnection(true)
				.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");
		String source = this.NetworkAPI.get("https://lobi.co/signup/twitter", header1);
		String authenticity_token = Pattern.get_string(source, Pattern.authenticity_token, "\"");
		String redirect_after_login = Pattern.get_string(source, Pattern.redirect_after_login, "\"");
		String oauth_token = Pattern.get_string(source, Pattern.oauth_token, "\"");

		String post_data = "";
		try {
			post_data = String.format(
					"authenticity_token=%s&redirect_after_login=%s&oauth_token=%s&session%%5Busername_or_email%%5D=%s&session%%5Bpassword%%5D=%s",
					authenticity_token, URLEncoder.encode(redirect_after_login, "UTF-8"), oauth_token, mail, password);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		PostHeader header2 = new PostHeader()
				.setHost("api.twitter.com")
				.setConnection(true)
				.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6")
				.setOrigin("https://api.twitter.com");

		String source2 = this.NetworkAPI.post_x_www_form_urlencoded("https://api.twitter.com/oauth/authorize",
				post_data, header2);
		if (source2.indexOf("Twitterにログイン") > -1)
			return false;

		return this.NetworkAPI.get(Pattern.get_string(source2, Pattern.twitter_redirect_to_lobi, "\""), header1)
				.indexOf("ログインに失敗しました") == -1;
	}

	public Me getMe() {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		try {
			String result_json = this.NetworkAPI.get("https://web.lobi.co/api/me?fields=premium", header);
			return gson.fromJson(result_json, Me.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public PublicGroups getPublicGroupList() {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		List<PublicGroups> result = new ArrayList<PublicGroups>();
		int index = 1;
		while (true)
			try {
				List<PublicGroups> pg = gson.fromJson(this.NetworkAPI.get(
						"https://web.lobi.co/api/public_groups?count=1000&page=" + index + "&with_archived=1", header),
						new TypeToken<List<PublicGroups>>() {
						}.getType());
				index++;
				if (pg.get(0).items.length == 0)
					break;
				result.addAll(pg);
			} catch (JsonParseException e) {
				e.printStackTrace();
			}
		if (result.size() > 0)
			return result.get(0);
		return null;
	}

	public PrivateGroups getPrivateGroupList() {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		List<PrivateGroups> result = new ArrayList<PrivateGroups>();
		int index = 1;
		while (true)
			try {
				List<PrivateGroups> pg = gson.fromJson(
						this.NetworkAPI.get("https://web.lobi.co/api/groups?count=1000&page=" + index, header),
						new TypeToken<List<PrivateGroups>>() {
						}.getType());
				index++;
				if (pg.get(0).items.length == 0)
					break;
				result.addAll(pg);
			} catch (JsonParseException e) {
				e.printStackTrace();
			}
		if (result.size() > 0)
			return result.get(0);
		return null;
	}

	public Notifications getNotifications() {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		try {
			return gson.fromJson(this.NetworkAPI
					.get("https://web.lobi.co/api/info/notifications?platform=any&last_cursor=0", header),
					Notifications.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Contacts getContacts(String uid) {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		try {
			return gson.fromJson(
					this.NetworkAPI.get("https://web.lobi.co/api/user/" + uid + "/contacts", header), Contacts.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Followers getFollowers(String uid) {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		try {
			return gson.fromJson(
					this.NetworkAPI.get("https://web.lobi.co/api/user/" + uid + "/followers", header), Followers.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Group getGroup(String uid) {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		try {
			return gson.fromJson(
					this.NetworkAPI.get(
							"https://web.lobi.co/api/group/" + uid
									+ "?error_flavor=json2&fields=group_bookmark_info%2Capp_events_info",
							header),
					Group.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getGroupMembersCount(String uid) {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		try {
			Integer result = gson.fromJson(
					this.NetworkAPI.get("https://web.lobi.co/api/group/" + uid
							+ "?error_flavor=json2&fields=group_bookmark_info%2Capp_events_info", header),
					Group.class).members_count;
			return result == null ? 0 : (int) result;
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public User[] getGroupMembers(String uid) {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		List<User> result = new ArrayList<User>();
		String next = "0";
		int limit = 10000;
		while (limit-- > 0)
			try {
				Group g = gson.fromJson(
						this.NetworkAPI.get("https://web.lobi.co/api/group/" + uid + "?members_cursor=" + next, header),
						Group.class);
				for (User user : g.members)
					result.add(user);
				if (g.members_next_cursor == null)
					break;
				if (g.members_next_cursor == 0)
					break;
				next = g.members_next_cursor.toString();
			} catch (JsonParseException e) {
				e.printStackTrace();
			}
		return result.toArray(new User[0]);
	}

	public Chat[] getThread(String uid, int count) {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		try {
			List<Chat> result = gson.fromJson(
					this.NetworkAPI.get("https://web.lobi.co/api/group/" + uid + "/chats?count=" + count, header),
					new TypeToken<List<Chat>>() {
					}.getType());
			return result.toArray(new Chat[0]);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Pokes getPokes(String groupId, String chatId) {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		try {
			return gson.fromJson(
					this.NetworkAPI.get("https://web.lobi.co/api/group/" + groupId + "/chats/pokes?id=" + chatId,
							header),
					Pokes.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Bookmarks getBookmarks() {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		try {
			return gson.fromJson(
					this.NetworkAPI.get("https://web.lobi.co/api/me/bookmarks", header),
					Bookmarks.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void good(String group_id, String chat_id) {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		String post_data = "id=" + chat_id;
		this.NetworkAPI.post_x_www_form_urlencoded(
				"https://web.lobi.co/api/group/" + group_id + "/chats/like", post_data, header);
	}

	public void unGood(String group_id, String chat_id) {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		String post_data = "id=" + chat_id;
		this.NetworkAPI.post_x_www_form_urlencoded(
				"https://web.lobi.co/api/group/" + group_id + "/chats/unlike", post_data, header);
	}

	public void bad(String group_id, String chat_id) {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		String post_data = "id=" + chat_id;
		this.NetworkAPI.post_x_www_form_urlencoded(
				"https://web.lobi.co/api/group/" + group_id + "/chats/like", post_data, header);
	}

	public void unBad(String group_id, String chat_id) {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		String post_data = "id=" + chat_id;
		this.NetworkAPI.post_x_www_form_urlencoded(
				"https://web.lobi.co/api/group/" + group_id + "/chats/unlike", post_data, header);
	}

	public void follow(String user_id) {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		String post_data = "users=" + user_id;
		this.NetworkAPI.post_x_www_form_urlencoded("https://web.lobi.co/api/me/contacts", post_data, header);
	}

	public void unFollow(String user_id) {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		String post_data = "users=" + user_id;
		this.NetworkAPI.post_x_www_form_urlencoded("https://web.lobi.co/api/me/contacts/remove", post_data, header);
	}

	// Original name is "MakeThread"
	public void newThread(String group_id, String message, boolean shout) {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		String post_data = "type=" + (shout ? "shout" : "normal") + "&lang=ja&message=" + message;
		this.NetworkAPI.post_x_www_form_urlencoded("https://web.lobi.co/api/group/" + group_id + "/chats", post_data,
				header);
	}

	public void reply(String group_id, String thread_id, String message) {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		String post_data = "type=normal&lang=ja&message=" + message + "&reply_to=" + thread_id;
		this.NetworkAPI.post_x_www_form_urlencoded("https://web.lobi.co/api/group/" + group_id + "/chats", post_data,
				header);
	}

	// Original name is "RemoveGroup"
	public void deleteGroup(String group_id) {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		String post_data = "";
		this.NetworkAPI.post_x_www_form_urlencoded("https://web.lobi.co/api/group/" + group_id + "/chats", post_data,
				header);
	}

	// Original name is "MakePrivateThread"
	public MakePrivateGroupResult newPrivateThread(String user_id) {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		String post_data = "user=" + user_id;
		return gson
				.fromJson(this.NetworkAPI.post_x_www_form_urlencoded("https://web.lobi.co/api/groups/1on1s", post_data,
						header), MakePrivateGroupResult.class);
	}

	// Original name is "RemoveGroup"
	public void changeProfile(String name, String description) {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36")
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6");

		String post_data = "name=" + name + "&description=" + description;
		this.NetworkAPI.post_x_www_form_urlencoded("https://web.lobi.co/api/me/profile", post_data, header);
	}

	private static class Pattern {
		public static String csrf_token = "<input type=\"hidden\" name=\"csrf_token\" value=\"";
		public static String authenticity_token = "<input name=\"authenticity_token\" type=\"hidden\" value=\"";
		public static String redirect_after_login = "<input name=\"redirect_after_login\" type=\"hidden\" value=\"";
		public static String oauth_token = "<input id=\"oauth_token\" name=\"oauth_token\" type=\"hidden\" value=\"";
		public static String twitter_redirect_to_lobi = "<a class=\"maintain-context\" href=\"";

		public static String get_string(String source, String pattern, String end_pattern) {
			int start = source.indexOf(pattern) + pattern.length();
			int end = source.indexOf(end_pattern, start + 1);
			return source.substring(start, end);
		}
	}
}
