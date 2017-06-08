      package com.jacamars.minimal;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.session.SessionHandler;

import com.google.gson.Gson;


/**
 * Creates the HTTP handler for the Minimal server. This is an adaptation of the
 * Jacamar's Inc. prototype web server. This source code is licensed under
 * Apache Commons 2.
 * 
 * @author Ben M. Faul.
 *
 */


public class Zippy implements Runnable {
	/** The thread the server runs on */
	Thread me;
	/** The default HTTP port */
	public static Config config;

	public static StringBuilder resultsPart1;
	public static StringBuilder resultsPart2;
	public static StringBuilder resultsPart3;
	public static StringBuilder resultsPart4;

	/**
	 * Creates the default Server
	 * 
	 * @param args
	 *            . String[]. Args[0] contains the name of the users file, if
	 *            not, presume "users.json"
	 * @throws Exception
	 *             on network or JSON parsing errors.
	 */

	public static void main(String[] args) throws Exception {
		String configFile = "config.json";
		if (args.length > 0)
			configFile = args[0];
		new Zippy(configFile);
	}


	/**
	 * Creates the Skrambler instance and starts it. The Jetty side receives
	 * ajax calls for Mosaic actions.
	 * 
	 * @param configFile
	 *            String. The name of the users JSON file.
	 * @throws Exception
	 *             or network errors.
	 */

	public Zippy(String configFile) throws Exception {
		byte[] encoded = Files.readAllBytes(Paths.get(configFile));
		String str = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded))
				.toString();

		Gson gson = new Gson();
		config = (Config) gson.fromJson(str, Config.class);

		Core.getInstance().connectJedis();

		encoded = Files.readAllBytes(Paths.get(config.results1));
		str = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString();
		resultsPart1 = new StringBuilder(str);

		encoded = Files.readAllBytes(Paths.get(config.results2));
		str = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString();
		resultsPart2 = new StringBuilder(str);

		encoded = Files.readAllBytes(Paths.get(config.results3));
		str = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString();
		resultsPart3 = new StringBuilder(str);

		encoded = Files.readAllBytes(Paths.get(config.results4));
		str = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString();
		resultsPart4 = new StringBuilder(str);


		me = new Thread(this);
		me.start();
	}


	/**
	 * Starts the JETTY server
	 */

	 public void run() {
		Server server = new Server(config.port);
		Handler handler = new Handler();

		try {

			SessionHandler sh = new SessionHandler(); // org.eclipse.jetty.server.session.SessionHandler
			sh.addEventListener(new CustomListener());
			sh.setHandler(handler);
			server.setHandler(sh);
			server.start();
			server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
}


/**
 * The class that handles HTTP calls for Skrambler actions.
 * 
 * @author Ben M. Faul
 *
 */

@MultipartConfig

class Handler extends AbstractHandler {

	/**
	 * The property for temp files.
	 */
	private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(
			System.getProperty("java.io.tmpdir"));


	private NashHorn scripter;

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Cache-Control","max-age=86400");
		response.setHeader("Cache-Control","must-revalidate");
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		HttpSession session = request.getSession();
		long count = 0;

		//System.out.println("inside handle");

		try {
			count = Core.getInstance().logSession(session.getId());

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
                // System.out.println("CT: " + count);

		if(target == null)
			return;

		//System.out.println("target: " + target);


		//String userAgent = request.getHeader("User-Agent");
                //String ipAddress = this.getIpAddress(request);
               	//String referer   = request.getHeader("referer");


		String session_id = session.getId();
		String userAgent = "";
                String ipAddress = "";
               	String referer   = "";
		if(count == 1) {
			userAgent = request.getHeader("User-Agent");
                	ipAddress = this.getIpAddress(request);
               		referer   = request.getHeader("referer");
			if(userAgent == null)
				userAgent = "unknown";
			if(ipAddress == null)
				ipAddress = "unknown";
			if(referer == null)
				referer = "unknown";
			Core.getInstance().putCache(session_id, "UA", userAgent);
			Core.getInstance().putCache(session_id, "IP", ipAddress);
			Core.getInstance().putCache(session_id, "RR", referer);
		}
		else {
			userAgent = Core.getInstance().getCache(session_id, "UA");
			ipAddress = Core.getInstance().getCache(session_id, "IP");
			referer   = Core.getInstance().getCache(session_id, "RR");
			if(userAgent == null || ipAddress == null || referer == null) {
				System.out.println("session timeout on " + count);
				// this data may not be session accurate
				userAgent = request.getHeader("User-Agent");
                		ipAddress = this.getIpAddress(request);
               			referer   = request.getHeader("referer");
				if(userAgent == null)
					userAgent = "unknown";
				if(ipAddress == null)
					ipAddress = "unknown";
				if(referer == null)
					referer = "unknown";
				Core.getInstance().putCache(session_id, "UA", userAgent);
				Core.getInstance().putCache(session_id, "IP", ipAddress);
				Core.getInstance().putCache(session_id, "RR", referer);
			}
		}

		int code = 200;
		String html = "";
		Boolean clickQuery = false;
		Boolean isBot = false;
		Boolean googleBot = false;
		Boolean googleRef = false;
		Boolean gimmeRef = false;

		if((userAgent.contains("bot") || userAgent.contains("spider") || userAgent.contains("crawler"))) {
			isBot = true;
			if(userAgent.contains("oogle") || userAgent.contains("bing"))
				googleBot = true;
		}

		if((referer.contains("oogle") || referer.contains("bing"))) {
			googleRef = true;
        		// System.out.println("GOOGLEREF");
		}
		else if(referer.contains("gimme") && isBot == false) {
			gimmeRef = true;
        		// System.out.println("GIMMEREF");
		}

		InputStream body = request.getInputStream();
		baseRequest.setHandled(true);

		if((userAgent.contains("Baidu") || userAgent.contains("baidu"))) {
			//printHeader(ipAddress, referer, userAgent);
			//System.out.println("BAIDU: " + userAgent);
			//response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
       		}
		else if(target.endsWith(".php") || target.contains("workspace/Minimal")) {
			//printHeader(ipAddress, referer, userAgent);
			//System.out.println("BAD REQUEST 1: " + target);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		else if (target.equals("/") && isBot) {
			//printHeader(ipAddress, referer, userAgent);
        		//System.out.println("BOT HOME");
			target = "/index.html";
		}
		else if (target.equals("/")) {
			printHeader(ipAddress, referer, userAgent);
        		System.out.println("HOME");
			target = "/index.html";
		}

		else if (target.startsWith("/results") || target.startsWith("/search")) {

			String url = request.getQueryString();
			if(url == null) {
				//printHeader(ipAddress, referer, userAgent);
				//System.out.println("BAD REQUEST 2: NULL");
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			if(url.endsWith("town=set")) {
				//printHeader(ipAddress, referer, userAgent);
				//System.out.println("BAD REQUEST 3: " + url);
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			//System.out.println("URL: " + url);
			response.setStatus(HttpServletResponse.SC_OK);
			url = url.replaceAll("%20", " ");
			url = url.trim().replaceAll(" +", " ");

			String query = getQuery(url);

			if(query == null) {
				printHeader(ipAddress, referer, userAgent);
				System.out.println("HOME NULL");
				target = "/index.html";
			}
			else  {
	                        String decoded_query = null; 
	                        Boolean no_query     = true;
				Boolean domain_flag  = false;
				Boolean ajax_query   = false;
				String queryResults  = null;
				int num_rows         = 25;
				int start_rows       = 0;

				String ppattern = ("&start=([0-9]+)");
				Pattern pr = Pattern.compile(ppattern);
				Matcher pm = pr.matcher(query);
				if (pm.find()) {
					String rows = pm.group(1);
					start_rows = Integer.parseInt(rows);
					if(start_rows < 0) 
						start_rows = 0;
					query = query.substring(0, query.indexOf("&start"));
	       				System.out.println("START ROWS: " + start_rows);
				}

				// cosmo is an ajax request, mindy is a new search which gets a new page
	       			// System.out.println("MINDY OR COSMO:" + query);
				if(query.contains("&cosmo")) {
					// &cosmo is appended in javascript ajax incResults()
					// to prevent auto searching
					query = query.substring(0, query.indexOf("&cosmo"));
					ajax_query = true;
	       				//System.out.println("AJAX");
				}
				else if(query.contains("&mindy")) {
					// &mindy is appended in javascript new page doQuery()
					query = query.substring(0, query.indexOf("&mindy"));
					ajax_query = false;
	       				//System.out.println("NOT AJAX");
				}
				else {
	       				System.out.println("NO COSMO OR MINDY QUERY: " + query);
					query = null;
				}
				ajax_query = true;
				if(start_rows == 0)
					ajax_query = false;

				if(query != null) {

					no_query = false;
		                        decoded_query = cleanQuery(query); 
			        	StringBuilder page =  new StringBuilder(); 
		
					String ahtml[] = new String[2];

	       				System.out.println("QUERY: " + query);
					Boolean exception = false;
					int returned_rows[] = new int[1];
					try {
						ahtml = getQueryResults(response, decoded_query, domain_flag, num_rows, start_rows, returned_rows);
					} catch (Exception e) {
						exception = true;
					}

					if(exception == false) {
	       					//System.out.println("returned_rows: " + returned_rows[0]);
						int numResults = Integer.parseInt(ahtml[0]);
						queryResults = ahtml[1];

						if(ajax_query == false) {
				 			page.append(Zippy.resultsPart1);
				 			page.append(query);
				 			page.append(Zippy.resultsPart2);
							// page.append("return('/results?query="+query+"&cosmo&start='+page);\n");
							//page.append("}\n");

							//page.append("});\n");
							//page.append("</script>\n");
						}

						if(queryResults != null) 
				 			page.append(queryResults);
						else	
							appendNoResults(page);

						if(ajax_query == false) {
							if(returned_rows[0] >= 25)
				 				page.append(Zippy.resultsPart3);
							else
				 				page.append(Zippy.resultsPart4);
						}

					}
					else
						appendNoResults(page);
		
					html = page.toString();
					// System.out.println("Zippy HTML" + html);

					//saveHtml(html);
		

				}
				else {
					no_query = true;
			                StringBuilder page =  new StringBuilder(); 
					appendNoResults(page);
					html = page.toString();
				}

				baseRequest.setHandled(true);
	
				try {
					sendResponse(response, html);

				} catch (Exception e) { }

				return;
			}
		}


		else if (target.contains("/drequest")) {

			System.out.println("drequest");

			String url = request.getQueryString();
			if(url == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			System.out.println("DOMAIN QUERY URL: " + url);
			response.setStatus(HttpServletResponse.SC_OK);
			url = url.replaceAll(" ", "");
			url = url.replaceAll("%20", "");
			String query = getQuery(url);
			if(query != null && query.contains("&simba"))
				query = query.substring(0, query.indexOf("&simba"));
			else
				query = null;
			StringBuilder page =  new StringBuilder(); 

			if(query == null)
			{
				appendNoResults(page);
			}
			else {
	                        String decoded_query = null; 
	                        Boolean no_query     = true;
				Boolean domain_flag  = true;
				String queryResults  = null;
				int num_rows         = 25;
				int start_rows       = 0;
				String ahtml[] = new String[2];

				query = query.toLowerCase();
		                decoded_query = cleanQuery(query); 

				Boolean exception = false;
				int returned_rows[] = new int[1];
				try {
					ahtml = getQueryResults(response, decoded_query, domain_flag, num_rows, start_rows, returned_rows);
				} catch (Exception e) {
					exception = true;
				}
				if(exception == false) {
					String numResults   = ahtml[0];
					queryResults = ahtml[1];
					if(queryResults != null) {
				 		page.append(queryResults);
						if(numResults != null && numResults.contains("0")) {
                					String ip = this.getIpAddress(request);
							saveDomain(ip + " " + query + "\n");
						}
					}
					else
						appendNoResults(page);
				}
				else 
					appendNoResults(page);

			}
			html = page.toString();

			// System.out.println("Zippy HTML" + html);
			baseRequest.setHandled(true);
			try {
				sendResponse(response, html);
			} catch (Exception e) { }
			return;
		}

		else if (target.contains("/shopping/") && target.endsWith(".html")) {
			printHeader(ipAddress, referer, userAgent);
			System.out.println("SHOPPING: " + target);
		}
		else if (target.contains("/pinterest/") && target.endsWith(".png")) {
			printHeader(ipAddress, referer, userAgent);
			System.out.println("SHOPPING: " + target);
		}
		else if (target.contains("/samples/") && target.endsWith(".html")) {
			target = target.replaceAll("samples", "shopping");
			printHeader(ipAddress, referer, userAgent);
			System.out.println("SAMPLE: " + target);
		}
		else if (target.contains("gimme-log")) {
			printHeader(ipAddress, referer, userAgent);
			System.out.println("GIMME LOG REQUEST");
		}
		else if (target.contains("love-log")) {
			printHeader(ipAddress, referer, userAgent);
			System.out.println("LOVE LOG REQUEST");
		}
		else if (target.contains("page-post.html")) {
			printHeader(ipAddress, referer, userAgent);
			System.out.println("POST PAGE REQUEST");
		}
		else if (target.contains("page-tos.html")) {
			printHeader(ipAddress, referer, userAgent);
			System.out.println("TOS PAGE REQUEST");
		}
		else if (target.contains("page-faq.html")) {
			printHeader(ipAddress, referer, userAgent);
			System.out.println("FAQ PAGE REQUEST");
		}
		else if (target.contains("page-promote.html")) {
			printHeader(ipAddress, referer, userAgent);
			System.out.println("PROMOTE PAGE REQUEST");
		}
		else if (target.contains("page-about.html")) {
			printHeader(ipAddress, referer, userAgent);
			System.out.println("ABOUT PAGE REQUEST");
		}
		else if (target.contains("page-shop.html")) {
			printHeader(ipAddress, referer, userAgent);
			System.out.println("SEARCH PAGE REQUEST");
		}
		else if (target.contains("page-mall.html")) {
			printHeader(ipAddress, referer, userAgent);
			System.out.println("SHOPPING MALL REQUEST");
		}

		else if (target.contains("analytics")) {
			if (target.contains("ga-analytics=")) {
				// "ga-analytics" string is used so we dont raise flags about privacy
	                	target = target.substring(target.indexOf("=") + 1);

				// this case exists to tell us a user is visiting our stores
				printHeader(ipAddress, referer, userAgent);
        			System.out.println("STORE: " + target);
				return;
			}
			printHeader(ipAddress, referer, userAgent);
        		System.out.println("STORE: " + target);
			return;
		}

		else if (target.contains("robots.txt")) {
			//printHeader(ipAddress, referer, userAgent);
        		//System.out.println("ROBOTS: " + target);
		}

		else if (target.contains("sitemap.xml")) {
			//printHeader(ipAddress, referer, userAgent);
        		//System.out.println("SITEMAP: " + target);
		}

		else if (target.contains("/sc/")) {
                        // thumbnail
		}

		else if (target.contains("/sendMail")) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;

			//printHeader(ipAddress, referer, userAgent);
        		//System.out.println("MAIL");
			//try {
			//	Core.getInstance().sendMail(body);
			//} catch (Exception e) {
			//	e.printStackTrace();
			//	html = e.toString();
			//}
                        // target = "/";

			//html = "I Sent your mail!";
			//response.setStatus(HttpServletResponse.SC_OK);
			//baseRequest.setHandled(true);
			//response.getWriter().println(html);
			//return;

		}


                // else it is css, js, images or suggestion
		// you can delete this once we have everything known captured
		else if(! target.endsWith(".css") && 
			! target.endsWith(".js") &&
			! target.endsWith(".ico") &&
			! target.endsWith(".eot") &&
			! target.endsWith(".png") &&
			! target.endsWith(".jpg") &&
			! target.endsWith(".jpeg") &&
			! target.endsWith(".JPG") &&
			! target.endsWith(".woff") &&
			! target.endsWith(".woff2") &&
			! target.endsWith(".json")) {
        			System.out.println("UNKNOWN: " + target);
		}

		session.setAttribute("path", target);
		String type = request.getContentType();
		response.addHeader("Access-Control-Allow-Origin", "*");
		// String ipAddress = getIpAddress(request);

		try {

			if (type != null && type.contains("multipart/form-data")) {
				try {
					String result = "";
					// String result = Sqramble.mosaic.multiPart(target,
					// baseRequest, request, MULTI_PART_CONFIG);
					response.setStatus(HttpServletResponse.SC_OK);
					baseRequest.setHandled(true);
					response.getWriter().println(result);
				} catch (Exception err) {
					Gson gson = new Gson();
					response.setStatus(HttpServletResponse.SC_OK);
					Map m = new HashMap();
					m.put("error", true);
					m.put("message", "Internal error: " + err.toString());

					baseRequest.setHandled(true);
					response.getWriter().println(gson.toJson(m));
					err.printStackTrace();
				}

				baseRequest.setHandled(true);
				// response.getWriter().println(json); TBD
				return;

			}

			if (target.contains("favicon")) {
				response.setStatus(HttpServletResponse.SC_OK);
				baseRequest.setHandled(true);
				response.getWriter().println("");
				return;
			}

			
			if (target.contains("captureQuery")) {
				response.setStatus(HttpServletResponse.SC_OK);
				baseRequest.setHandled(true);
				response.getWriter().println(Core.getInstance().handleCapture(body));
				return;
			}


			type = null;

			//System.out.println(target);


			int x = target.lastIndexOf(".");
			if (x >= 0) {
				type = target.substring(x);
			}

			File f = null;
			if (type != null) {
				type = type.toLowerCase().substring(1);
				type = MimeTypes.substitute(type);
				response.setContentType(type);

				if (target.indexOf(".") == -1) 
					target += ".html";

				f = new File("www" + target);
				/*
				// file finder (dont use this as it allows bot to get stale links if files exist)
				if (f.exists() == false) {
					f = new File("www/support" + target);
					if (f.exists() == false) {
						f = new File(target);
						if (f.exists() == false) {
							f = new File("." + target);
							if (f.exists() == false) {
								f = new File(".." + target);
								if (f.exists() == false) {
									int k = target.indexOf("www");
									if (k > 0) {
										target = target.substring(k);
										f = new File(target);
										if (f.exists()==false) {
											response.setStatus(HttpServletResponse.SC_NOT_FOUND);
											baseRequest.setHandled(false);
											return;
										}
									}																					
								}
							}
						}
					}
				}
				*/
				if (f.exists() == false) {
        				System.out.println("FILE NOT FOUND: " + target);
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					baseRequest.setHandled(false);
					return;
				}

				/**
				if (type != null && type.endsWith("html")) {
					String page = Charset
							.defaultCharset()
							.decode(ByteBuffer.wrap(Files.readAllBytes(Paths
									.get(f.getPath())))).toString();
					if (page.indexOf("<%") != -1) {
						scripter = new NashHorn(page);
						scripter.setObject("core", Core.getInstance());
						scripter.setObject("ipaddress", ipAddress);
						scripter.setObject("session", session);
						try {
							page = scripter.getHtml();
						} catch (Exception error) {
							page = error.toString();
						}
					}

					response.setContentType("text/html");
					response.setStatus(HttpServletResponse.SC_OK);
					baseRequest.setHandled(true);
					response.getWriter().println(page);
					return;
				}
				*/
				
				/** 
				 * Compress js and css
				 */
				if (target.endsWith(".html") || target.endsWith(".js") || target.endsWith(".css")) {	
				    baseRequest.setHandled(true);
					response.setContentType(type);
					response.setStatus(HttpServletResponse.SC_OK);
					handleJsAndCss(response, f);
					return;					
				}

				FileInputStream fis = new FileInputStream(f);
				OutputStream out = response.getOutputStream();

				// write to out output stream
				while (true) {
					int bytedata = fis.read();
					if (bytedata == -1) {
						break;
					}
					try {
						out.write(bytedata);
					} catch (Exception error) {
						break; // screw it, pray that it worked....
					}
				}

				// flush and close streams.....
				fis.close();
				try {
					out.close();
				} catch (Exception error) {

				}

				response.setContentType(type);
				response.setStatus(HttpServletResponse.SC_OK);
				baseRequest.setHandled(true);
				return;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.err.println(e.toString());
		}
		baseRequest.setHandled(true);
		response.getWriter().println(html);
		response.setStatus(code);
	}


	/**
	 * Return the IP address of this
	 * 
	 * @param request
	 *            HttpServletRequest. The web browser's request object.
	 * @return String the ip:remote-port of this browswer's connection.
	 */

	public String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		ip += ":" + request.getRemotePort();
		return ip;
	}

	public void printHeader(String ipAddress, String referer, String userAgent) {
               	// String formatted = getTimeStamp();
               	String formatted = getDateString();
		String[] ip = ipAddress.split(":");
		System.out.println("\n=============================");
               	System.out.println("TS: " + formatted);
		System.out.println("IP: " + ip[0]);
		System.out.println("RR: " + referer);
		System.out.println("UA: " + userAgent);
	}


	public static byte[] compressGZip(String uncompressed) throws Exception {
	            ByteArrayOutputStream baos  = new ByteArrayOutputStream();
	            GZIPOutputStream gzos       = new GZIPOutputStream(baos);
	            byte [] uncompressedBytes   = uncompressed.getBytes();
	            gzos.write(uncompressedBytes, 0, uncompressedBytes.length);
	            gzos.close();
	            return baos.toByteArray();
	    }



	

       public void appendNoResults(StringBuilder page) {
		//page.append("<div class='col-lg-6 col-lg-offset-3 text-left'>\n");
		//page.append("<div class='sort-info'>\n");
		//page.append("No Results.");
		//page.append("</div>\n");
		//page.append("</div>\n");

		page.append("\n\n<div id='details' class='container top-buffer bottom-buffer'>\n");
		page.append("<div class='text-center top-buffer bottom-medbuffer'>");
		page.append("<h4><em>Wow, no posted store carries this item!</em><br>");
		page.append("Or...too many. The query should be at least 4 letters.<br>");
		page.append("If you know of a great business that<br>");
		page.append("has it for sale, please post their website above.</h4>");
		page.append("</div>\n");
		page.append("</div>\n");
        }


	public String getQuery(String url) {
	
		if(url == null || ! url.contains("=")) {
			System.out.println("NULL QUERY");
			return(null);
		}

		String query = url.substring(url.indexOf("=") + 1);

		if(query == null || query.length() < 3) {
			System.out.println("SHORT QUERY");
			return(null);
		}

		if(query.contains("&utm_")) {
	       		System.out.println("TWITTER: " + query);
			query = query.substring(0, query.indexOf("&utm_"));
			query = query + "&cosmo";
		}

		query = query.replaceAll("%20", " ");
		query = query.trim().replaceAll(" +", " ");

		if(query == null)  {
			System.out.println("MODIFIED QUERY IS NULL");
			return(null);
		}

		if(query.length() < 3)  {
			System.out.println("QUERY IS TOO SHORT: " + query);
			return(null);
		}

		if(query.length() > 80)  {
			System.out.println("QUERY IS TOO LONG: " + query);
			return(null);
		}

		return(query);
	}

	public Boolean testForDomain(String query) {
		if(query == null || query.contains(" ")) 
			return(false);

		Pattern p = Pattern.compile("\\.(?:com|net|biz)$", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(query);
		if(m.find()) 
			return(true);

		return(false);
	}
	
	void handleJsAndCss(HttpServletResponse response, File file) throws Exception {
		  byte fileContent[] = new byte[(int)file.length()];
		  FileInputStream fin = new FileInputStream(file);
		  fin.read(fileContent);
		  sendResponse(response, new String(fileContent));
	}
	
	public void sendResponse(HttpServletResponse response, String html) throws Exception {

		try {
			byte [] bytes = compressGZip(html);
			response.addHeader("Content-Encoding", "gzip");
			int sz = bytes.length;
			response.setContentLength(sz);
			response.getOutputStream().write(bytes);

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getOutputStream().println("");
		}
	}
	
 	public String cleanQuery(String query) {
		String cleanQuery = query;
		// cleanQuery = cleanQuery.replaceAll("AND|OR|NOT", "");
		cleanQuery = cleanQuery.replaceAll("\\+", " ");
		cleanQuery = cleanQuery.replaceAll("%20", " ");
		cleanQuery = cleanQuery.replaceAll("%26", "&");
		cleanQuery = cleanQuery.replaceAll("%22", "\"");
	        //cleanQuery = java.net.URLDecoder.decode(cleanQuery, "UTF-8"); 
		return(cleanQuery);
	}


	public String[] getQueryResults(HttpServletResponse response, String decoded_query, Boolean domain_flag, int num_rows, int start_rows, int[] returned_rows) throws Exception {

		if(decoded_query == null)
			return(null);

		if(domain_flag == null)
			domain_flag = false;

		String ahtml[] = new String[2];

		try {
			ahtml = Core.getInstance().doQuery(decoded_query, domain_flag, num_rows, start_rows, returned_rows);
			
		} catch (Exception e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
			System.err.println("Query error: " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return(null);
		}
		//String numResults = ahtml[0];
	    	//System.out.println("returned_rows getQueryResults: " + returned_rows[0]);

		//if(numResults == null) 
		//	return(null);

		//int numResults = Integer.parseInt(stuff);
		//if(numResults == 0)
		//	return(null);

		//String html = ahtml[1];
		//return(html);
		return(ahtml);
	}

	public String getTimeStamp() {
		Calendar calendar = Calendar.getInstance();
		int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
		SimpleDateFormat format1 = new SimpleDateFormat("YYYY DD HH:mm:ss");
		String formatted = format1.format(calendar.getTime());
		return(formatted);
	}

	public String getDateString() {
		SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		Date date = new Date();
		String formatted = format1.format(date);
		return(formatted);
	}

	public void saveHtml(String html) {
		BufferedWriter out = null;
		try  
		{
			FileWriter fstream = new FileWriter("html.txt", false); //true tells to append data.
			out = new BufferedWriter(fstream);
			out.write(html);
		}
		catch (IOException e)
		{
			System.err.println("Error: saveHtml " + e.getMessage());
		}
		finally
		{
			if(out != null) {
				try {
					out.close();
				} catch (Exception error) {
				}
			}
		}
	}


	public void saveDomain(String domain) {
		BufferedWriter out = null;
		String dateString = getDateString();
		try  
		{
			FileWriter fstream = new FileWriter("/home/glen/posted_domains.txt", true); //true tells to append data.
			out = new BufferedWriter(fstream);
			out.write(dateString + " " + domain);
		}
		catch (IOException e)
		{
			System.err.println("Error: saveDomain:" + e.getMessage());
		}
		finally
		{
			if(out != null) {
				try {
					out.close();
				} catch (Exception error) {
				}
			}
		}
	}
}
