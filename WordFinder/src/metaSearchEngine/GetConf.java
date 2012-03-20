package metaSearchEngine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;

public class GetConf {
	static public final String BAIDU_HEAD = "BAIDU_HEAD";
	static public final String BAIDU_TRAIL = "BAIDU_TRAIL";
	static public final String BING_HEAD = "BING_HEAD";
	static public final String BING_TRAIL = "BING_TRAIL";
	static public final String GOOGLE_HEAD = "GOOGLE_HEAD";
	static public final String GOOGLE_TRAIL = "GOOGLE_TRAIL";
	static public final String SOGOU_HEAD = "SOGOU_HEAD";
	static public final String SOGOU_TRAIL = "SOGOU_TRAIL";
	static public final String SOSO_HEAD = "SOSO_HEAD";
	static public final String SOSO_TRAIL = "SOSO_TRAIL";
	static public final String YAHOO_HEAD = "YAHOO_HEAD";
	static public final String YAHOO_REAIL = "YAHOO_TRAIL";
	static public final String YOUDAO_HEAD = "YOUDAO_HEAD";
	static public final String YOUDAO_TRAIL = "YOUDAO_TRAIL";
	static private Hashtable<String,String> ht;
	static{
		ht = new Hashtable<String,String>();
		try{
			BufferedReader br = new BufferedReader(new FileReader("ContentExtract.conf"));
			String line = null;
			while((line=br.readLine())!=null){
				int begin = line.indexOf("=");
				ht.put(line.substring(0, begin), line.substring(begin+1,line.length()));
				//System.out.println(line.substring(0, begin)+" "+line.substring(begin+1));
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String get(String key){
		return ht.get(key);
	}

}
