package com.jacamars.minimal.response;

import java.util.ArrayList;
import java.util.List;

public class Field {
	public List<String> anchors;
	public List<String> field_1;
	StringBuilder signature;
	
	public Field() {
		super();
	}
	
	public void toHtml(StringBuilder sb) {
		
		sb.append("<div class='context'>");
		sb.append("\"");
		for (String field : anchors) {
                	field  =  field.replaceAll("\"", "");
			sb.append(field);
		}
		//sb.append("\"");
		sb.append("</div>");
	}

	public StringBuilder getContext() {
		
		signature = new StringBuilder();
		if(anchors != null) {
                	int size = anchors.size();
			for (String f : anchors) {
				signature.append(f);
				if(--size > 0)
				   signature.append("...");
			}
		}

		else if(field_1 != null) {
                	int size = field_1.size();
			for (String f : field_1) {
				signature.append(f);
				if(--size > 0)
				   signature.append("...");
			}
		}
		return signature;

	}
}
