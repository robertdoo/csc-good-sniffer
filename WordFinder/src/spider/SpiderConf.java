package spider;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import database.ConnDB;

public class SpiderConf {
	static public final int DEEP_CRAWLER = 0;
	static public final int MAINPAGE_CRAWLER = 1;
	static public final String MAINPAGECRAWLER_DOC_PATH = "MAINPAGECRAWLER_DOC_PATH";
	static public final String MAINPAGECRAWLER_HTML_PATH = "MAINPAGECRAWLER_HTML_PATH";
	static public final String MAINPAGECRAWLER_PDF_PATH = "MAINPAGECRAWLER_PDF_PATH";
	static public final String MAINPAGECRAWLER_XLS_PATH = "MAINPAGECRAWLER_XLS_PATH";
	static public final String SPIDER_DOC_PATH = "SPIDER_DOC_PATH";
	static public final String SPIDER_HTML_PATH = "SPIDER_HTML_PATH";
	static public final String SPIDER_PDF_PATH = "SPIDER_PDF_PATH";
	static public final String SPIDER_XLS_PATH = "SPIDER_XLS_PATH";
	static public final String SPIDER_IMG_PATH = "SPIDER_IMG_PATH";
	static public final String SPIDER_URL_PATH = "SPIDER_URL_PATH";
	static public final String MAINPAGECRAWLER_INDEX_PATH = "MAINPAGECRAWLER_INDEX_PATH";
	static public final String SPIDER_INDEX_PATH = "SPIDER_INDEX_PATH";
	static public final String METASEARCH_INDEX_PATH = "METASEARCH_INDEX_PATH";
	static public final int SubThreadPool = 1;
	static public final int ThreadPoolManager = 2;
	static private Hashtable<String,String> pathTable;
	static private Hashtable<String,String> ht;
	static{
		ht = new Hashtable<String,String>();
		ConnDB connDB = new ConnDB("test");
		connDB.connectToDB();
		String sql = "select url,name from url";
		ResultSet resultSet = connDB.executeQuery(sql);
		try{
		while(resultSet.next()){
			String url = resultSet.getString(1);
			String name = resultSet.getString(2);
			ht.put(url, name);
		}
		}catch(SQLException e){
			System.err.println("SpiderConf≥ı ºªØ ß∞‹£°");
		}
		connDB.closeConnection();
	}
	
	static{
		pathTable = new Hashtable<String,String>();
		try{
			BufferedReader br = new BufferedReader(new FileReader("Spider.conf"));
			String line = null;
			while((line=br.readLine())!=null){
				int begin = line.indexOf("=");
				pathTable.put(line.substring(0, begin), line.substring(begin+1,line.length()));
				//System.out.println(line.substring(0, begin)+" "+line.substring(begin+1));
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String getNameByUrl(String key){
		return ht.get(key);
	}
	
	public  static String getPathByName(String name){
		return pathTable.get(name);
	}
	
}
