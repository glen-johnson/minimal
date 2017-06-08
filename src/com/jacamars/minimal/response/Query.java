package com.jacamars.minimal.response;

import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

public class Query {
	public int numFound;
	public int start = 0;
	public List<Document> docs;
	
	//HashMap<String,String> hm_desc = new HashMap<>();
	//HashMap<String,String> hm_href    = new HashMap<>();

	public Query() {

	}

	public void toHtml(StringBuilder dt, String query, String numResults, int index, int[] returned_rows) {


		returned_rows[0] = 0;
		
		for (Document doc : docs) {

			//String html = doc.html;
			//System.out.println("html: " + html);
                	dt.append(doc.html);
			++index;
			++returned_rows[0];

		}

		if(index == 0) {
			dt.append("<div class='text-center top-buffer bottom-medbuffer'>");
			dt.append("<h3><em>Wow, no posted store carries this item!</em></h3>");
			dt.append("<h4>If you know of a great business that<br>");
			dt.append("has it for sale, please post their website above.</h4>");
			dt.append("</div>\n");
		}


	}
}

