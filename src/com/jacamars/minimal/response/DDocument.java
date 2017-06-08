package com.jacamars.minimal.response;

public class DDocument {
	public String id;
	public String href;
	public String description;

	public DDocument() {
		
	}
	
	public void toHtml(StringBuilder sb, int index) {
		String href         = this.href;
		// String description  = this.description;

		sb.append("<h4>"+href+"</h4>");
	}
}
