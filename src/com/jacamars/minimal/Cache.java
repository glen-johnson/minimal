package com.jacamars.minimal;

public class Cache {
	int maxkeys = 10000;
	public int maxage = 12 * 60 * 60;
	public String redis = "localhost";
	
	public Cache() {
		
	}
}
