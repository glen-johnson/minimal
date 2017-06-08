package com.jacamars.minimal;

import io.netty.util.internal.ThreadLocalRandom;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Capture {
	static HashMap<String, Answer> captureMap = new HashMap();
	static List<String> fileNames = new ArrayList();

	static String TDLINE = "<td><input type='image' src='www/capture/__FILENAME__' onclick='doCapture("
			+ "\"__UUID__" + "\"," + "\"__SESSION__" + "\"" + ", __FUN__, __CHECKER__);'/></td>";

	static String JS = null;

	public static String generate(String id, int selections, int tries, String fun, String checker)
			throws Exception {
		
		/**
		 * For now, reload every time
		 */
		File f = new File("www/support/template.txt");
		JS = Charset
				.defaultCharset()
				.decode(ByteBuffer.wrap(Files.readAllBytes(Paths
						.get(f.getPath())))).toString();

		
		
		File dir = new File("www/capture");
		
		
		if (fileNames.size() == 0) {

			String[] children = dir.list();
			if (children == null)
				throw new Exception("Can't generate file lists for capture");
			else {
				for (String child : children) {
					fileNames.add(child);
				}
			}
		}

		List<Integer> choices = new ArrayList();
		int i = 0;
		while (choices.size() < selections) {
			int x = ThreadLocalRandom.current().nextInt(0, fileNames.size());
			if (choices.contains(x) == false) {
				choices.add(x);
				i++;
			}
		}

		int answerIndex = ThreadLocalRandom.current().nextInt(0, selections);
		String answerName = fileNames.get(choices.get(answerIndex));
		int k = answerName.indexOf(".");
		answerName = answerName.substring(0, k);
		String answerHash = UUID.randomUUID().toString();

		Answer a = new Answer();
		a.value = answerHash;
		a.tries = tries;
		captureMap.put(id, a);

		/**
		 * Now generate the values
		 */
		String str = "<script>" + JS + "</script>";
		str += "<table id='capture'>\n<CAPTION><B>" + "Please select the "
				+ answerName + "</B></CAPTION>\n<tr>\n";
		for (i = 0; i < choices.size(); i++) {
			int index = choices.get(i);
			String uuid = null;
			if (i == answerIndex)
				uuid = answerHash;
			else
				uuid = UUID.randomUUID().toString();
			String fileName = fileNames.get(index);
			String name = fileName.substring(0, fileName.indexOf("."));
			String line = TDLINE.replaceAll("__FILENAME__", fileName);
			line = line.replaceAll("__UUID__", uuid);
			line = line.replaceAll("__SESSION__", id);
			line = line.replaceAll("__FUN__", fun);
			line = line.replaceAll("__CHECKER__", checker);

			str += line + "\n";
		}
		str += "</tr><tr><td align='center'><div id='capture-msg'></div></td></tr>\n";
		str += "</table>";
		return str;
	}

	public static boolean isValid(String id, String selection) throws Exception {
		Answer a = captureMap.get(id);
		if (a == null)
			throw new Exception("No answer for " + id);
		boolean t = selection.equals(a.value);
		if (t) {
			captureMap.remove(id);
			return true;
		}
		a.tries--;
		if (a.tries == 0)
			throw new Exception("Too many wrong answers");
		return false;
	}
}

class Answer {
	public String value;
	public int tries;

	public Answer() {

	}
}
