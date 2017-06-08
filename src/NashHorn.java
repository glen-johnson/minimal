import java.nio.ByteBuffer;


import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;


public class NashHorn {
	private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
	String html;
	
	public static void main(String[] args) throws Exception {
		NashHorn horn = new NashHorn("index.html");
		System.out.println(horn.getHtml());
	}
	
	public NashHorn(String fileName) throws Exception {
	    String str = Charset
					.defaultCharset()
					.decode(ByteBuffer.wrap(Files.readAllBytes(Paths
							.get(fileName)))).toString();
		StringBuffer data = new StringBuffer(str);
		
		engine.put("nash", this);
		while(getTagValues(data));
		html = data.toString();
	}
	
	private boolean getTagValues(StringBuffer buf) throws Exception {
		int start= buf.indexOf("<%");
		int stop = buf.indexOf("%>");
		if (stop < 0 || stop < 0)
			return false;
		
	    String code = buf.substring(start+2,stop);
	    Object o = engine.eval(code);
	    if (o == null)
	    	o = "";
	    else
	    	if (o instanceof String)
	    		o = o.toString();
	    	else
	    		o = "";
	    
	  buf.replace(start, stop+2, o.toString());
	  if (start != 0 && buf.length() < start+1) {
		  if (buf.charAt(start)=='\n' && buf.charAt(start-1)=='\n')
			  buf.replace(start-1, start+1, "\n");
	  }
	    	
	    return true;
	}
	
	public String getHtml() {
		return html;
	}
	
	public String test() {
		return "thiis is a test";
	}

}
