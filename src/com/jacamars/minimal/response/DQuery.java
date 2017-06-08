package com.jacamars.minimal.response;

import java.util.List;

public class DQuery {
	public int numFound;
	public int start = 0;
	public List<DDocument> docs;

	public DQuery() {
		
	}
	
	public String toHtml(StringBuilder sb) {
		
		int index = 1;

		sb.append("<div class='container'>\n");
		sb.append("<div class='col-md-8 col-md-offset-2 bottom-buffer'>\n");

		// System.out.println("DQuery");
		int numResults = 0;
		for (DDocument doc : docs) {
			++numResults;
			break;
		}
		if(numResults > 0) 
			sb.append("<h3>This site is posted. Here are some pages we have:</h3>");
		
		for (DDocument doc : docs) {
			String href         = doc.href;
			sb.append("<h4>"+href+"</h4>");
			
			if (++index > 200)
				break;
		}

		if(index == 1) {
			sb.append("<div class='text-center'>\n");
			sb.append("<h2>You are the first to post this site!<br>\n");
			sb.append("It will be viewable after we do a quick look-see.<br><br>\n");
			sb.append("P.S. Feel free to add as many as you like.</h3>");
			sb.append("</div>");
		}

		sb.append("</div>");
		sb.append("</div>\n");
		sb.append("</div>\n");
		
		return sb.toString();
	}
}
