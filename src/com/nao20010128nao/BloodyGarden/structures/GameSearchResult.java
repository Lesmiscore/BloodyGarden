package com.nao20010128nao.BloodyGarden.structures;

public class GameSearchResult {
	public int next_page;
	public int count;
	public DataEntry[] data;

	public static class DataEntry {
		public Info info;
		public int bookmarks_count;
		public int gamelists_count;
		public String icon;
		public Object app;
		public String uid;
		public String name;
		public String[] tracking_url;// TODO: check it is correct
		public int groups_count;
		public String cover;
		public String genres;
		public int members_count;
		public String official_site_url;
		public String appstore_url;
		public int comments_count;
		public String playstore_url;

		public static class Info {
			public String alias_name;
		}
	}
}
