package com.jacamars.minimal;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.security.auth.login.Configuration;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.jacamars.minimal.response.DomainQuery;
import com.jacamars.minimal.response.Query;
import com.jacamars.minimal.response.RegularQuery;

import redis.clients.jedis.Jedis;

public enum Core {

	INSTANCE;

	public Jedis jedis;
	static String SOLR = null;

	public static Core getInstance() {
		if (SOLR == null) {
			SOLR = "http://" + Zippy.config.search.host + ":" + Zippy.config.search.port +"/solr/";
		}
		return INSTANCE;
	}

	public void connectJedis() throws Exception {
		jedis = new Jedis("localhost", 6379);
		jedis.connect();
		jedis.select(0);
	}
	
	public String [] doQuery(String query, Boolean isDomain, int num_rows, int start_rows, int[] returned_rows) throws Exception {
		long time = System.currentTimeMillis();
		HttpPostGet http = new HttpPostGet();
		String endPoint =  null;
		String ar[] = new String[3];
		ar[0] = null;
		ar[1] = null;
		ar[2] = null;

		if(query == null || query.length() < 3 || query.length() > 60) 
			return ar;

	        //System.out.println("BEFORE TRIM: " + query);
		String key = query.trim();
                key = key.replaceFirst("^#", ""); // hashtag
                key = key.replaceAll("[^\\x20-\\x7e]", "");
		key = key.replaceAll("%26", "and");
		key = key.replaceAll("&", "and");
                key = java.net.URLDecoder.decode(key, "UTF-8"); 
		key = key.replaceAll("[\\+\\s]+", " ");


	        //System.out.println("KEY: " + key);

		int nbytes = 0;
		byte bytes [] = null;
		boolean regularQuery  = true;
		key.trim();

		if(isDomain || ! key.contains(" ")) {
			if(isDomain == true) {
				// query parser is specified in the 'domain' request handler solrconfig.xml
				regularQuery = false;
				// standard query parser
				String q            = "domain_name%3A" + key;
				String fl           = "&fl=id,href";
				String details      = "&rows=40&start=0&wt=json&omitHeader=true";
				endPoint = SOLR + "collection1/select" + "?q=" + q + fl + details;
			}
			else {
				// standard query parser
				String fl = "&fl=id,html";
				String q_title    = "(title:" + key + ")^10000.0";
				String q_desc     = "(description:" + key + ")^100.0";
				String q_field_1  = "(field_1:" + key + ")^5.0";
				String q          = q_title + "%20OR%20" + q_desc + "%20OR%20" + q_field_1;
				endPoint = SOLR + "collection1/single" + "?q=" + q + fl + "&rows=" + num_rows + "&start=" + start_rows + "&wt=json";
			}
		}
		else {  // multi word query, use dismax query parser

			key = key.replaceAll("\\s+", "+");
			key = key.replaceAll("%20", "+");

			int num_words = 2;
			if(key.contains("+")) {
				String parts [] = key.split("\\+");
				num_words = parts.length;
				if(num_words > 4)
					num_words = 4;
			}

			// specify Dismax
			String qp      = "defType=dismax";

			// The user's query
			String q       = "&q=" + key;

			// The qf parameter introduces a list of fields, each of
			// which is assigned a boost factor to increase or decrease
			// that particular field's importance in the query.
			String qf      = "&qf=title^1.8+description^1.5+field_1";

			
			// if 3 or less words in query, all must be present
			// 4 or more, 75% must be present
			String mm      = "&mm=3%3C75%25";

			// ------------------------------------------------------
			// OK, we now have a set of docs that match the above
			// Lets filter and sort.
			// ------------------------------------------------------

			// boost the score of documents in cases where all of the terms
			// in the q parameter appear in close proximity
			String pf      = "&pf=title^1.8+description^1.5+field_1";

			// the amount of "phrase slop" to apply to queries specified with the pf parameter
			String ps      = "&ps=" + num_words * 4;

			//  amount of slop permitted on phrase queries explicitly
			// included in the user's query string with the qf parameter
			String qs      = "&qs=" + num_words * 4;

			// for multiple field matches, suppress the scores of the lower scoring fields.
			// example: if description and field_1 match, suppress the score addition of field_1
			// so that other docs with description matches dont get trumped by field_1
			String tie     = "&tie=0.1";

			// fields returned
			String fl      = "&fl=id,html";

			// where to start and how many to return
			String rows = "&rows=" + num_rows;
			String start = "&start=" + start_rows;

			// build the query
			endPoint = SOLR + "collection1/multiple" + "?" + qp + q + qf + mm + pf + ps + qs + tie + fl + start + rows;
		}
	        System.out.println("EP: " + endPoint);

		String contents = http.sendGet(endPoint,15000,15000);
		String head = contents;
		if(contents.length() > 100) {
		   head = contents.substring(0, 100);
		}
	        //System.out.println("CONTENTS: " + head);

		String numResults = "0";
		String pattern = "\"numFound\":(\\d+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(head);
		if (m.find( )) 
			numResults = m.group(1);
		else
	        	System.out.println("cannot get numResults from head");

	        System.out.println("NUM RESULTS: " + numResults);
		ar[0] = numResults;
		//if(numResults == "0") {
		//   ar[1] = null;
		//   return ar;
		//}

		ObjectMapper mapper = new ObjectMapper();

		if (regularQuery) {
			RegularQuery solr = mapper.readValue(contents, RegularQuery.class);
			contents = solr.toHtml(query, numResults, start_rows, returned_rows);
		        //putCache(key,contents);
		} else {
			DomainQuery solr = mapper.readValue(contents, DomainQuery.class);
			contents = solr.toHtml();
		}
		ar[1] = contents;
		return ar;
	}

	public String logical_query(String key) {

		key = key.replaceAll("\\(", " ");
		key = key.replaceAll("\\)", " ");
		key = key.replaceAll("\"", " ");
		key = key.replaceAll("\\s+", "+");
		String parts[] = key.split("\\+");
		int num_words = parts.length;
		key = "%28field_1:%22";
		boolean is_logical = false;
		String last_logical = null;
		int phrase_cnt = 0;
		int word_cnt = 0;
		for (int i=0;i<num_words;i++) {

			is_logical = parts[i].startsWith("OR") || parts[i].startsWith("AND") || parts[i].startsWith("NOT");

			if(word_cnt == 0 && is_logical==true)
				continue;

			if(is_logical == false) {
				if(last_logical != null && phrase_cnt > 0) {
					key = key + "%20" + last_logical + "%20%28field_1:%22";
					last_logical = null;
				}
					
				if(word_cnt==0) {
					key = key + parts[i];
				}
				else {
					key = key + "+" + parts[i];
				}
				++word_cnt;
			}
			else {
				++phrase_cnt;
				word_cnt = 0;
				key = key + "%22%29";

				last_logical = null;
				if(parts[i].startsWith("OR"))
					last_logical = "OR";
				else if(parts[i].startsWith("AND"))
					last_logical = "AND";
				else if(parts[i].startsWith("NOT"))
					last_logical = "AND%20NOT";
			}
		}
		key = key + "%22%29";
		return(key);
	}

	public String location_query(String key) {

		key = key.replaceAll("\\(", " ");
		key = key.replaceAll("\\)", " ");
		key = key.replaceAll("\"", " ");
		key = key.replaceAll("\\s+", "+");

		String parts[] = key.split("\\+");
		int num_words = parts.length;
		key = "field_1:%22";
		boolean locale_flag = false;
		int word_cnt = 0;
		for (int i=0;i<num_words;i++) {

			String word = parts[i];
			if(locale_flag == false) {
				if(word.matches("[A-Z][a-z]+")) {
					locale_flag = true;
					key = key + "%22+AND+%22" + word;
					word_cnt = 0;
					continue;
				}
			}
			if(word_cnt == 0) {
				key = key + word;
			}
			else {
				key = key + "+" + word;
			}
			++word_cnt;
		}
		key = key + "%22";
		return(key);
	}

	public String query_mods(String phrase) {

	        System.out.println("query_mods: " + phrase);

		if(phrase.length() < 4) {
			return(phrase);
		}
                
		if(phrase.length() >= 4) {
		   return(phrase);
		}

		int num_words         = 1;
		String partial_phrase = phrase;
		String plural         = null;
		String singular       = null;

		if(phrase.contains(" ")) {
			String[] words = phrase.split(" ");
			num_words = words.length;
			plural = words[num_words - 1];
			partial_phrase = words[0];
			if(num_words > 2) {
				for (int i=1;i<num_words-1;i++) {
					partial_phrase = partial_phrase + " " + words[i];
				}
			}
		}

		String pattern = "s$";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(phrase);
		if (! m.find( )) {
			singular = null;
			String modified_query = get_mod_query(phrase, plural, singular, num_words);
			return(modified_query);
		}

		else { // find the singular of the plural word
			if(plural == null) { // single word
				plural = phrase;
			}

	        	System.out.println("PLURAL: " + plural);
			int length = plural.length();
	
			if(plural.length() > 5) {
				// babies -> baby, cookies -> cookie
				pattern = "[^aeiou]ies$";
				r = Pattern.compile(pattern);
				m = r.matcher(plural);
				if (m.find( )) {
					singular = plural.replaceAll("ies$", "y");
			        	System.out.println("IES: " + singular);
					String modified_query = get_mod_query(partial_phrase, plural, singular, num_words);
					return(modified_query);
				}
		
				// crutches -> crutch, bushes -> bush, boxes -> box
				pattern = "(?:ch|s|sh|x|z)es$";
				r = Pattern.compile(pattern);
				m = r.matcher(plural);
				if (m.find( )) {
		   			singular = plural.replaceAll("es$", "");
			        	System.out.println("ES: " + singular);
					String modified_query = get_mod_query(partial_phrase, plural, singular, num_words);
					return(modified_query);
		 		   }
		
				// elves -> elf
				pattern = "ves$";
				r = Pattern.compile(pattern);
				m = r.matcher(plural);
				if (m.find( )) {
					singular = plural.replaceAll("ves$", "f");
			        	System.out.println("VES: " + singular);
					String modified_query = get_mod_query(partial_phrase, plural, singular, num_words);
					return(modified_query);
				}
		
				pattern = "os$";
				r = Pattern.compile(pattern);
				m = r.matcher(plural);
				if (m.find( )) {
					singular = plural.replaceAll("s$", "");
			        	System.out.println("OS: " + singular);
					String modified_query = get_mod_query(partial_phrase, plural, singular, num_words);
					return(modified_query);
				}
		
				pattern = "oes$";
				r = Pattern.compile(pattern);
				m = r.matcher(plural);
				if (m.find( )) {
					//singular = plural.replaceAll("es$", "");
			        	//System.out.println("OES: " + singular);
					//String modified_query = get_mod_query(partial_phrase, plural, singular, num_words);
					//return(modified_query);
				}
		
				pattern = "[aeiou][aeiou]s$";
				r = Pattern.compile(pattern);
				m = r.matcher(plural);
				if (m.find( )) {
					//singular = plural.replaceAll("s$", "");
			        	//System.out.println("OOS: " + singular);
					//String modified_query = get_mod_query(partial_phrase, plural, singular, num_words);
					//return(modified_query);
				}
		
				pattern = "[^aeiou]s$";
				r = Pattern.compile(pattern);
				m = r.matcher(plural);
				if (m.find( )) {
					singular = plural.replaceAll("s$", "");
			        	System.out.println("S: " + singular);
					String modified_query = get_mod_query(partial_phrase, plural, singular, num_words);
					return(modified_query);
				}
			}
			singular = plural.replaceAll("s$", "");
        		System.out.println("DEFAULT: " + singular);
			String modified_query = get_mod_query(partial_phrase, plural, singular, num_words);
			return(modified_query);
		}
	}

	public String get_mod_query(String phrase, String plural, String singular, int num_words) {
		if(phrase == null) {
			System.out.println("FAULT: get_mod_query: no phrase value");
			return(null);
		}
		String query = null;
		if(num_words == 1 && singular != null && phrase.length() != singular.length()) {
			query = "q='" + phrase + "' OR '" + singular + "'";
		}
		else if(num_words > 1 && singular != null && plural != null) {
			query = "q='" + phrase + " " + singular + "'OR'" + phrase + " " + plural + "'";
		}
		else {
			query = "q=" + phrase;
		}
		System.out.println("MULT: " + query);
		return(query);
	}
	


		


	public synchronized  long logSession(String id) throws Exception {
		if(jedis.hexists(id, "ID"))
			return 2;
		jedis.hset(id, "ID", id);
		jedis.expire(id,  300);
		return 1;
	}


	public void sendMail(InputStream in) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		 JsonNode rootNode = mapper.readTree(in);
		 String name = rootNode.get("from").asText();
		 String from = rootNode.get("email").asText();
		 String subject = rootNode.get("subject").asText();
		 String body = rootNode.get("body").asText();

		// save the email as a file for backup
                 try {
                 	String allFields = "\nNAME: " + name + "\n" + "FROM: " + from + "\n" + "SUBJECT: " + subject + "\n" + "BODY: " + body + "\n\n";
                	File file = new File("/home/gimme/email.log");
                        if(!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file,true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(allFields);
			bw.close();
		 } catch (IOException ioe) {
			System.out.println("Append email file exception occurred:");
			ioe.printStackTrace();
                 }


			final String username = "glen@solr1.com";
			final String password = "";

			Properties props = new Properties();
			props.put("mail.smtp.auth", "false");
			props.put("mail.smtp.host", "localhost");
			props.put("mail.smtp.port", "25");

			Session session = Session.getInstance(props,
			  new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			  });

				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("support@gimmeusa.com"));
				message.setSubject(subject);
				message.setText(body);

				Transport.send(message);

				System.out.println("EM: email sent");
	}

	public void sendMail(String from, String subject, String body) throws Exception {

		final String username = "glen@solr1.com";
		final String password = "";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "false");
		props.put("mail.smtp.host", "localhost");
		props.put("mail.smtp.port", "25");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("support@gimmeusa.com"));
			message.setSubject(subject);
			message.setText(body);

			Transport.send(message);
			System.out.println("Done");
	}
	

	public String handleCapture(InputStream in) throws Exception {
		 ObjectMapper mapper = new ObjectMapper();
		 Gson gson = new Gson();
		 JsonNode rootNode = mapper.readTree(in);
		 String id = rootNode.get("id").asText();
		 String answer = rootNode.get("answer").asText();
		 Map r = new HashMap();
		 try {
			 boolean ok = Capture.isValid(id,answer);
			 r.put("status", ok);
		 } catch (Exception error) {
			 r.put("error", true);
		 }
		 return gson.toJson(r);
	}
	

	public String emitCapture(HttpSession session, int count, int tries, String jsName, String checker) throws Exception {
		return Capture.generate(session.getId(), count, tries, jsName, checker);
	}

	public synchronized String getCache(String session_id, String field) {
		String value = jedis.hget(session_id, field);
		return value;
	}


	public synchronized void putCache(String session_id, String field, String value) {
		jedis.hset(session_id, field, value);
	}
}

