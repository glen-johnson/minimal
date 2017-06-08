package com.jacamars.minimal.response;

import java.io.File;
import java.util.List;

public class Document {
	public String id;
	//public String domain_name;
	public String company;
	public String title;
	//public String termfreq;
	public String description;
	//public String thumbnail;
	public String href;
	public String page_anchor;
	public int num_pages;
	public float score;
	// public List<String> anchor_hrefs;
	public String anchor_hrefs;
	public String html;



	public Document() {
		
	}
	
	public void toHtml(StringBuilder sb, int index, float maxScore, String query) {

		//System.out.println("Score: " + score);
		
		int show_image = 60;
                Boolean show_score = false;
		String company = this.company;
		String href = this.href;
		int num_pages = this.num_pages;
		String title = this.title;
		String page_anchor = this.page_anchor;

		String html = this.html;
                sb.append("<div class='result-cols bottom-medbuffer'>");
		sb.append(html);
		sb.append("</div>");
	}

}
