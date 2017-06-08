package com.jacamars.minimal.response;

public class RegularQuery {
	public Query response;
	//public Highlighting highlighting;
	
	public RegularQuery() {
		
	}
	
	public String toHtml(String query, String numResults, int start, int[] returned_rows) {
		StringBuilder sb = new StringBuilder();
		
		//response.toHtml(sb, highlighting, query);
		response.toHtml(sb, query, numResults, start, returned_rows);
		
		return sb.toString();
	}
}
