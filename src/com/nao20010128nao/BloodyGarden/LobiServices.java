package com.nao20010128nao.BloodyGarden;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.nao20010128nao.BloodyGarden.network.Http;
import com.nao20010128nao.BloodyGarden.network.Header.GetHeader;
import com.nao20010128nao.BloodyGarden.network.Header.PostHeader;
import com.nao20010128nao.BloodyGarden.structures.Bookmarks;
import com.nao20010128nao.BloodyGarden.structures.Chat;
import com.nao20010128nao.BloodyGarden.structures.Contacts;
import com.nao20010128nao.BloodyGarden.structures.Followers;
import com.nao20010128nao.BloodyGarden.structures.FreeKV;
import com.nao20010128nao.BloodyGarden.structures.GameSearchResult;
import com.nao20010128nao.BloodyGarden.structures.Group;
import com.nao20010128nao.BloodyGarden.structures.MakePrivateGroupResult;
import com.nao20010128nao.BloodyGarden.structures.MakePublicGroupResult;
import com.nao20010128nao.BloodyGarden.structures.Me;
import com.nao20010128nao.BloodyGarden.structures.Notifications;
import com.nao20010128nao.BloodyGarden.structures.Pokes;
import com.nao20010128nao.BloodyGarden.structures.PrivateGroups;
import com.nao20010128nao.BloodyGarden.structures.PublicGroups;
import com.nao20010128nao.BloodyGarden.structures.User;

public class LobiServices {
	private Gson gson = new Gson();
	public static String PC_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36";
	public static String MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5 Build/MOB30Y) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.85 Mobile Safari/537.36";

	public LobiServices() {

	}

	public boolean login(String mail, String password) throws MalformedURLException, URISyntaxException, IOException {
		GetHeader header1 = new GetHeader()
				.setHost("lobi.co")
				.setConnection(true)
				.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja");
		String source = Http.get("https://lobi.co/signin", header1);
		String csrf_token = Jsoup.parse(source).select("input[name=\"csrf_token\"]").get(0).attr("value");

		String post_data = String.format("csrf_token=%s&email=%s&password=%s", csrf_token, encode(mail),
				password);
		PostHeader header2 = new PostHeader()
				.setHost("lobi.co")
				.setConnection(true)
				.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja")
				.setOrigin("https://lobi.co")
				.setReferer("https://lobi.co/signin");

		String result = Http.post_x_www_form_urlencoded("https://lobi.co/signin", post_data, header2);
		try {
			Class.forName("android.util.Log").getMethod("d", String.class, String.class).invoke(null, "login_data",
					result);
		} catch (Throwable e) {

		}
		return result.indexOf("ログインに失敗しました") == -1 & result.indexOf("failed signin") == -1
				& !hasLoginFields(Jsoup.parse(result));
	}

	public boolean twitterLogin(String mail, String password)
			throws MalformedURLException, IOException, URISyntaxException {
		GetHeader header1 = new GetHeader()
				.setHost("lobi.co")
				.setConnection(true)
				.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");
		String source = Http.get("https://lobi.co/signup/twitter", header1);
		Document sourceDom = Jsoup.parse(source);
		String authenticity_token = sourceDom.select("input[name=\"authenticity_token\"]").get(0).text();
		String redirect_after_login = sourceDom.select("input[name=\"redirect_after_login\"]").get(0).text();
		String oauth_token = sourceDom.select("input[name=\"oauth_token\"]").get(0).text();

		String post_data = String.format(
				"authenticity_token=%s&redirect_after_login=%s&oauth_token=%s&session%%5Busername_or_email%%5D=%s&session%%5Bpassword%%5D=%s",
				authenticity_token, redirect_after_login, oauth_token, mail, password);
		PostHeader header2 = new PostHeader()
				.setHost("api.twitter.com")
				.setConnection(true)
				.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4")
				.setOrigin("https://api.twitter.com");

		String source2 = Http.post_x_www_form_urlencoded("https://api.twitter.com/oauth/authorize",
				post_data, header2);
		if (source2.indexOf("Twitterにログイン") > -1)
			return false;
		Document source2Dom = Jsoup.parse(source2);
		String result = Http.get(source2Dom.select("a.maintain-context").get(0).text(), header1);
		return result.indexOf("ログインに失敗しました") == -1 & result.indexOf("failed signin") == -1
				& !hasLoginFields(Jsoup.parse(result));
	}

	private static boolean hasLoginFields(Document doc) {
		if (doc.select(
				"div.input--mail__wrapper," +
						"div.input--password__wrapper," +
						"input.input--email," +
						"input.input--password," +
						"input#input--email," +
						"input#input--password")
				.isEmpty())
			return false;
		return true;
	}

	public Me getMe() throws MalformedURLException, IOException, URISyntaxException {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		try {
			String result_json = Http.get("https://web.lobi.co/api/me?fields=premium", header);
			return gson.fromJson(result_json, Me.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public PublicGroups getPublicGroupList() throws MalformedURLException, IOException, URISyntaxException {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		List<PublicGroups> result = new ArrayList<PublicGroups>();
		int index = 1;
		while (true)
			try {
				List<PublicGroups> pg = gson.fromJson(Http.get(
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

	public PrivateGroups getPrivateGroupList() throws MalformedURLException, IOException, URISyntaxException {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		List<PrivateGroups> result = new ArrayList<PrivateGroups>();
		int index = 1;
		while (true)
			try {
				List<PrivateGroups> pg = gson.fromJson(
						Http.get("https://web.lobi.co/api/groups?count=1000&page=" + index, header),
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

	public Notifications getNotifications() throws MalformedURLException, IOException, URISyntaxException {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		try {
			return gson.fromJson(Http
					.get("https://web.lobi.co/api/info/notifications?platform=any&last_cursor=0", header),
					Notifications.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Contacts getContacts(String uid) throws MalformedURLException, IOException, URISyntaxException {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		try {
			return gson.fromJson(
					Http.get("https://web.lobi.co/api/user/" + uid + "/contacts", header), Contacts.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Followers getFollowers(String uid) throws MalformedURLException, IOException, URISyntaxException {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		try {
			return gson.fromJson(
					Http.get("https://web.lobi.co/api/user/" + uid + "/followers", header), Followers.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Group getGroup(String uid) throws MalformedURLException, IOException, URISyntaxException {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		try {
			return gson.fromJson(
					Http.get(
							"https://web.lobi.co/api/group/" + uid
									+ "?error_flavor=json2&fields=group_bookmark_info%2Capp_events_info",
							header),
					Group.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getGroupMembersCount(String uid) throws MalformedURLException, IOException, URISyntaxException {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		try {
			Integer result = gson.fromJson(
					Http.get("https://web.lobi.co/api/group/" + uid
							+ "?error_flavor=json2&fields=group_bookmark_info%2Capp_events_info", header),
					Group.class).members_count;
			return result == null ? 0 : (int) result;
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public User[] getGroupMembers(String uid) throws MalformedURLException, IOException, URISyntaxException {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		List<User> result = new ArrayList<User>();
		String next = "0";
		int limit = 10000;
		while (limit-- > 0)
			try {
				Group g = gson.fromJson(
						Http.get("https://web.lobi.co/api/group/" + uid + "?members_cursor=" + next, header),
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

	public Chat[] getThread(String uid, int count) throws MalformedURLException, IOException, URISyntaxException {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		try {
			List<Chat> result = gson.fromJson(
					Http.get("https://web.lobi.co/api/group/" + uid + "/chats?count=" + count, header),
					new TypeToken<List<Chat>>() {
					}.getType());
			return result.toArray(new Chat[0]);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Pokes getPokes(String groupId, String chatId) throws MalformedURLException, IOException, URISyntaxException {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		try {
			return gson.fromJson(
					Http.get("https://web.lobi.co/api/group/" + groupId + "/chats/pokes?id=" + chatId,
							header),
					Pokes.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Bookmarks getBookmarks() throws MalformedURLException, IOException, URISyntaxException {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		try {
			return gson.fromJson(
					Http.get("https://web.lobi.co/api/me/bookmarks", header),
					Bookmarks.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void good(String group_id, String chat_id) throws MalformedURLException, URISyntaxException, IOException {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		String post_data = "id=" + chat_id;
		Http.post_x_www_form_urlencoded(
				"https://web.lobi.co/api/group/" + group_id + "/chats/like", post_data, header);
	}

	public void unGood(String group_id, String chat_id) throws MalformedURLException, URISyntaxException, IOException {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		String post_data = "id=" + chat_id;
		Http.post_x_www_form_urlencoded(
				"https://web.lobi.co/api/group/" + group_id + "/chats/unlike", post_data, header);
	}

	public void bad(String group_id, String chat_id) throws MalformedURLException, URISyntaxException, IOException {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		String post_data = "id=" + chat_id;
		Http.post_x_www_form_urlencoded(
				"https://web.lobi.co/api/group/" + group_id + "/chats/like", post_data, header);
	}

	public void unBad(String group_id, String chat_id) throws MalformedURLException, URISyntaxException, IOException {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		String post_data = "id=" + chat_id;
		Http.post_x_www_form_urlencoded(
				"https://web.lobi.co/api/group/" + group_id + "/chats/unlike", post_data, header);
	}

	public void follow(String user_id) throws MalformedURLException, URISyntaxException, IOException {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		String post_data = "users=" + user_id;
		Http.post_x_www_form_urlencoded("https://web.lobi.co/api/me/contacts", post_data, header);
	}

	public void unFollow(String user_id) throws MalformedURLException, URISyntaxException, IOException {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		String post_data = "users=" + user_id;
		Http.post_x_www_form_urlencoded("https://web.lobi.co/api/me/contacts/remove", post_data, header);
	}

	// Original name is "MakeThread"
	public com.nao20010128nao.BloodyGarden.structures.Thread newThread(String group_id, String message, boolean shout)
			throws MalformedURLException, URISyntaxException, IOException {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4")
				.setReferer("https://web.lobi.co/group/" + group_id);

		String postData = "type=" + (shout ? "shout" : "normal") + "&lang=ja&message=" + encode(message);
		String result = Http.post_x_www_form_urlencoded("https://web.lobi.co/api/group/" + group_id + "/chats",
				postData, header);
		return gson.fromJson(result, com.nao20010128nao.BloodyGarden.structures.Thread.class);
	}

	public void reply(String group_id, String thread_id, String message)
			throws MalformedURLException, URISyntaxException, IOException {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		String post_data = "type=normal&lang=ja&message=" + encode(message) + "&reply_to=" + thread_id;
		Http.post_x_www_form_urlencoded("https://web.lobi.co/api/group/" + group_id + "/chats", post_data,
				header);
	}

	// Original name is "RemoveGroup"
	public void deleteGroup(String group_id) throws MalformedURLException, URISyntaxException, IOException {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		String post_data = "";
		Http.post_x_www_form_urlencoded("https://web.lobi.co/api/group/" + group_id + "/chats", post_data,
				header);
	}

	// Original name is "MakePrivateThread"
	public MakePrivateGroupResult newPrivateThread(String user_id)
			throws JsonSyntaxException, MalformedURLException, URISyntaxException, IOException {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		String post_data = "user=" + user_id;
		return gson
				.fromJson(Http.post_x_www_form_urlencoded("https://web.lobi.co/api/groups/1on1s", post_data,
						header), MakePrivateGroupResult.class);
	}

	public void changeProfile(String name, String description)
			throws MalformedURLException, URISyntaxException, IOException {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		String post_data = "name=" + encode(name) + "&description=" + encode(description);
		Http.post_x_www_form_urlencoded("https://web.lobi.co/api/me/profile", post_data, header);
	}

	public MakePublicGroupResult newPublicGroup(String name, String desc, String game)
			throws MalformedURLException, URISyntaxException, IOException {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("application/json, text/plain, */*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4")
				.setReferer("https://web.lobi.co/home/public-group");

		String post_data = "name=" + encode(name) + "&description=" + encode(desc);
		if (game != null)
			post_data += "&game_uid=" + game;
		String result = Http.post_x_www_form_urlencoded("https://web.lobi.co/api/public_groups", post_data, header);
		try {
			return gson.fromJson(result, MakePublicGroupResult.class);
		} catch (Throwable e) {
			throw new RuntimeException(result, e);
		}
	}

	public GameSearchResult searchGame(String keyword, int page)
			throws JsonSyntaxException, MalformedURLException, IOException, URISyntaxException {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("*/*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		return gson.fromJson(
				Http.get("https://web.lobi.co/api/games/search?q=" + encode(keyword) + "&page=" + page, header),
				GameSearchResult.class);
	}

	public boolean deleteAccount() throws MalformedURLException, URISyntaxException, IOException {
		PostHeader header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("*/*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4")
				.setReferer("https://web.lobi.co/setting");

		String postData = "why=" + encode("唐澤貴洋愛す覚悟しろ無能");
		String token = Http.post_x_www_form_urlencoded("https://web.lobi.co/api/me/remove/confirm", postData,
				header);
		token = gson.fromJson(token, FreeKV.class).get("remove_token");

		postData = "remove_token=" + token;
		header = new PostHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("*/*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4")
				.setReferer("https://web.lobi.co/setting");
		String data = Http.post_x_www_form_urlencoded("https://web.lobi.co/api/me/remove/complete", postData,
				header);
		return gson.fromJson(data, FreeKV.class).get("success").equals("1");
	}

	public static boolean checkAvailable() {
		GetHeader header = new GetHeader()
				.setHost("web.lobi.co")
				.setConnection(true)
				.setAccept("*/*")
				.setUserAgent(PC_USER_AGENT)
				.setAcceptLanguage("ja,en-US;q=0.8,en;q=0.6,fr;q=0.4");

		try {
			return !Http.get("https://web.lobi.co/", header).equalsIgnoreCase("slow down");
		} catch (Exception e) {
			return false;
		}
	}

	private static String encode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static boolean makeNewAccount(String mail, String password)
			throws MalformedURLException, IOException, URISyntaxException {
		GetHeader header1 = new GetHeader()
				.setHost("lobi.co")
				.setConnection(true)
				.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
				.setUserAgent(MOBILE_USER_AGENT)
				.setAcceptLanguage("ja");
		String source = Http.get("https://lobi.co/inapp/signup/password", header1);
		String csrf_token = Jsoup.parse(source).select("input[name=\"csrf_token\"]").get(0).attr("value");

		String post_data = String.format("csrf_token=%s&email=%s&password=%s&password_confirm=%s", csrf_token,
				encode(mail),
				password,
				password);
		PostHeader header2 = new PostHeader()
				.setHost("lobi.co")
				.setConnection(true)
				.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
				.setUserAgent(MOBILE_USER_AGENT)
				.setAcceptLanguage("ja")
				.setOrigin("https://lobi.co")
				.setReferer("https://lobi.co/inapp/signup/password");

		String result = Http.post_x_www_form_urlencoded("https://lobi.co/inapp/signup/password", post_data, header2);
		return result.indexOf("仮登録完了") == -1 | !hasLoginFields(Jsoup.parse(result));
	}
}
