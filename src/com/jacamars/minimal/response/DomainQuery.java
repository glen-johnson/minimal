package com.jacamars.minimal.response;

import java.util.List;

public class DomainQuery {
	public DQuery response;
	public int numFound;
	public int start = 0;
	public List<DDocument> docs;

	public DomainQuery() {
		
	}
	
	public String toHtml() {
		StringBuilder sb = new StringBuilder();
		
		response.toHtml(sb);
		
		return sb.toString();
	}
}
