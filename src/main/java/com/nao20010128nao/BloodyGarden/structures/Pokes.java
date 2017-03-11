package com.nao20010128nao.BloodyGarden.structures;

public class Pokes {
	public static class PokeUserItem {
		public long created_date;
		public String type;
		public User user;
	}

	public String next_cursor;
	public PokeUserItem[] users;
}
