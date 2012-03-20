package metaSearchEngine;

import java.net.URLConnection;
import java.util.LinkedList;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.http.ConnectionManager;
import org.htmlparser.lexer.Page;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.TextExtractingVisitor;

import ui.MainWindow.MainWindow;

import dataStruct.Result;
import dataStruct.Rule;

public class GoogleExtract {

	/**
	 * @param args
	 */
	String address = "";
	String title = "";
	String snippet = "";
	String cache = "";

	ConnectionManager manager;
	NodeFilter table_filter = null;
	NodeFilter url_filter = null;
	NodeFilter snippet_filter = null;
	NodeFilter cache_filter = null;

	NodeList table_list = null;
	NodeList url_list = null;
	NodeList snippet_list = null;
	NodeList cache_list = null;

	NodeIterator table_it = null;
	NodeIterator url_it = null;
	NodeIterator snippet_it = null;
	NodeIterator cache_it = null;
	
	Parser parser = null;
	Parser parser0 = null;
	Parser parser1 = null;
	Parser parser2 = null;

	public LinkedList<Result> results;
	//public LinkedList<String> nextPages;
	
	private boolean isAutoSearch;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GoogleExtract ge = new GoogleExtract(false);
		// String url =
		// "http://www.google.com.hk/search?hl=zh-CN&source=hp&q=%E5%8D%81%E5%9B%9B%E6%89%80&meta=&aq=f&aqi=&aql=&oq=&gs_rfai=";
		// ge.doSearch(url);

		Rule rule = new Rule("", "简体中文", "南京理工大学", "计算机科学与技术学院", "模式识别", "doc", "", "", "其他", false,"");
		ge.doSearch(rule, "30");
		/* for(int i=0;i<ge.results.size();i++){
			  System.out.println("Url: "+ge.results.get(i).getUrl());
			  System.out.println("Title: "+ge.results.get(i).getTitle());
			  System.out.println("Snippet: "+ge.results.get(i).getContent());
			  System
			  .out.println("cache: "+ge.results.get(i).getPageMirrorPath());
			  System.out.println("Time: "+ge.results.get(i).getTime());
			  System.out.println("Filetype: "+ge.results.get(i).getFiletype());
			  System
			  .out.println("Similarity: "+ge.results.get(i).getSimilarity());
			  System.out.println(); }*/
	}

	public GoogleExtract(boolean isAutoSearch) {
		results = new LinkedList<Result>();
		//nextPages = new LinkedList<String>();
		
		this.isAutoSearch = isAutoSearch;
	}

	public int doSearch(String url) {
		results.clear();
		try {
			manager = Page.getConnectionManager();
			URLConnection connection = manager.openConnection(url);
			connection.setConnectTimeout(10000);
			parser = new Parser(connection);
			parser.setEncoding("GB2312");

			/*
			 * URL myUrl = new URL(url); HttpURLConnection uc =
			 * (HttpURLConnection) myUrl.openConnection();
			 * uc.setRequestProperty("User-Agent", "vigorouswei@126.com");
			 * InputStream in = uc.getInputStream(); BufferedReader br = new
			 * BufferedReader(new InputStreamReader(in)); String line = null;
			 * StringBuffer sb = new StringBuffer(); while((line =
			 * br.readLine())!=null){ System.out.println(line);
			 * sb.append(line+"\r\n"); }
			 */

			// InputStream in = new FileInputStream(new File("十四所.txt"));
			/*
			 * byte[] tempbuff = new byte[512]; // 临时数组
			 * 
			 * byte[] buff = new byte[307200]; // 定义一下足够大的数组
			 * 
			 * int count = 0; // 读取字节个数
			 * 
			 * int rbyte = 0; // 每次读取的个数
			 * 
			 * while ((rbyte = in.read(tempbuff)) != -1) {
			 * 
			 * for (int i = 0; i < rbyte; i++)
			 * 
			 * buff[count + i] = tempbuff[i];
			 * 
			 * count += rbyte;
			 * 
			 * }
			 * 
			 * byte[] result = new byte[count];
			 * 
			 * for (int i = 0; i < count; i++)
			 * 
			 * result[i] = buff[i]; //进行字符编码转换 String output = new
			 * String(result, "utf-8"); int index = output.indexOf("<"); output
			 * = output.substring(index); System.out.println(output); Parser
			 * parser = new Parser(output); parser.setEncoding("UTF-8");
			 */

			/*
			 * Parser parser = new Parser(sb.toString());
			 * parser.setEncoding("utf-8");
			 */

			table_filter = new AndFilter(new TagNameFilter("li"),
					new HasAttributeFilter("class", "g"));
			url_filter = new AndFilter(new TagNameFilter("H3"),
					new HasAttributeFilter("class", "r"));
			snippet_filter = new AndFilter(new TagNameFilter("DIV"),
					new HasAttributeFilter("class", "s"));
			cache_filter = new AndFilter(new TagNameFilter("span"),
					new HasAttributeFilter("class", "gl"));

			table_list = parser.parse(table_filter);
			table_it = table_list.elements();
			while (table_it.hasMoreNodes()) {
				Node node = (Node) table_it.nextNode();
				parser0 = new Parser(handelScript(node.toHtml()));
				parser1 = new Parser(node.toHtml());
				parser2 = new Parser(node.toHtml());

				TextExtractingVisitor visitor = new TextExtractingVisitor();
				parser0.visitAllNodesWith(visitor);

				/*
				 * String body = visitor.getExtractedText(). replaceAll(" ",
				 * "").replaceAll("	","").replaceAll("\n", "");
				 * 
				 * System.out.println(body);
				 */

				url_list = parser1.parse(url_filter);
				url_it = url_list.elements();
				if (url_it.hasMoreNodes()) {
					Node url_node = (Node) url_it.nextNode();
					String str = url_node.toHtml();
					address = handleHTMLTag(getUrl(str));

					Parser url_parser = new Parser(str);
					TextExtractingVisitor url_visitor = new TextExtractingVisitor();
					url_parser.visitAllNodesWith(url_visitor);

					title = url_visitor.getExtractedText();
					title = title.replaceAll(" ", "").replaceAll("	", "")
							.replaceAll("\r\n", "");

				} else
					continue;

				snippet_list = parser2.parse(snippet_filter);
				snippet_it = snippet_list.elements();
				if (snippet_it.hasMoreNodes()) {
					Node snippet_node = (Node) snippet_it.nextNode();
					Parser cache_parser = new Parser(snippet_node.toHtml());
					Parser snippet_parser = new Parser(snippet_node.toHtml());

					TextExtractingVisitor snippet_visitor = new TextExtractingVisitor();
					snippet_parser.visitAllNodesWith(snippet_visitor);

					snippet = dropTail(snippet_visitor.getExtractedText());
					snippet = snippet.replaceAll(" ", "").replaceAll("	", "")
							.replaceAll("\n", "");

					cache_list = cache_parser.parse(cache_filter);
					cache_it = cache_list.elements();
					if (cache_it.hasMoreNodes()) {
						String str = ((Node) cache_it.nextNode()).toHtml();
						cache = handleHTMLTag(getUrl(str));
						if (cache.indexOf("webcache.googleusercontent.com") == -1)
							cache = "";
					} else
						continue;

				} else
					continue;

				/*
				System.out.println("title: " + title);
				System.out.println("address: " + address);
				System.out.println("snippet: " + snippet);
				System.out.println("cache: " + cache);
				System.out.println();
				*/

				//去重
				//if(MainWindow.merp.isExisted(address))continue;
				if(isAutoSearch && !MainWindow.bf2.boomfilter(address))continue;
				
				Result result = new Result(title, snippet, address, null, "html",
						cache, "google");
				results.add(result);
			}
		} catch (Exception e) {
			System.err.println("使用谷歌搜索引擎时出现异常");
			if (table_list != null)
				table_list = null;
			if (table_it != null)
				table_it = null;
			if (url_list != null)
				url_list = null;
			if (url_it != null)
				url_it = null;
			if (cache_list != null)
				cache_list = null;
			if (cache_it != null)
				cache_it = null;
			if (snippet_list != null)
				snippet_list = null;
			if (snippet_it != null)
				snippet_it = null;
			if(parser!=null)
				parser = null;
			if(parser0!=null)
				parser0 = null;
			if(parser1!=null)
				parser1 = null;
			if(parser2!=null)
				parser2 = null;
			return 1;
		}

		if (table_list != null)
			table_list = null;
		if (table_it != null)
			table_it = null;
		if (url_list != null)
			url_list = null;
		if (url_it != null)
			url_it = null;
		if (cache_list != null)
			cache_list = null;
		if (cache_it != null)
			cache_it = null;
		if (snippet_list != null)
			snippet_list = null;
		if (snippet_it != null)
			snippet_it = null;
		if(parser!=null)
			parser = null;
		if(parser0!=null)
			parser0 = null;
		if(parser1!=null)
			parser1 = null;
		if(parser2!=null)
			parser2 = null;
		return 0;
	}

	public int doSearch(Rule rule, String num) {
		String url = getUrlByRule(rule, num);
		System.out.println(url);
		return (doSearch(url));
	}

	static String handleHTMLTag(String str) {
		// 去除所有HTML标记
		str = str.replaceAll("<.*?>", "");
		// 将HTML特殊字符转换为其原本字符
		str = str.replaceAll("&amp;", "&");
		str = str.replaceAll("&lt;", "<");
		str = str.replaceAll("&gt;", ">");
		str = str.replaceAll("(&#39)|(&apos;);", "'");
		str = str.replaceAll("&quot;", "\"");
		str = str.replaceAll("&nbsp;", " ");
		str = str.replaceAll("&ordm", "°");
		str = str.replaceAll("'", "‘");
		// 去掉不必要的空格
		str = str.trim();
		return str;
	}

	public String handelScript(String str) {
		int begin = str.indexOf("<script>");
		if (begin == -1)
			return str;
		int end = str.indexOf("</script>");
		if (end == -1)
			return str;
		String head = str.substring(0, begin);
		String tail = str.substring(end + 9, str.length() - 1);
		return head + tail;

	}

	public String getUrlByRule(Rule rule, String Num) {
		String[] words = rule.getWords().split(" ");
		String[] snippet = rule.getSnippet().split(" ");
		String[] unwantedWords = rule.getUnwantedWords().split(" ");
		String lang = rule.getLanguage();
		String fileType = rule.getFileType();
		String site = rule.getSite();
		String num = Num;
		// String categoryName = rule.getCategoryName();

		String as_q = "";
		String as_epq = "";
		String as_eq = "";

		for (int i = 0; i < words.length; i++) {
			if (words[i].startsWith("+"))
				as_q += "%2B" + UrlCodeExchange(words[i]);
			else if (as_q.length() > 0)
				as_q += "+" + UrlCodeExchange(words[i]);
			else
				as_q += UrlCodeExchange(words[i]);
		}
		for (int i = 0; i < snippet.length; i++) {
			if (as_epq.length() == 0)
				as_epq += UrlCodeExchange(snippet[i]);
			else
				as_epq += "+" + UrlCodeExchange(snippet[i]);
		}
		for (int i = 0; i < unwantedWords.length; i++) {

			if (as_eq.length() == 0)
				as_eq += UrlCodeExchange(unwantedWords[i]);
			else
				as_eq += "+" + UrlCodeExchange(unwantedWords[i]);
		}

		site = (site == null) ? "" : site.trim();
		fileType = fileType.equals("任何格式") ? "" : fileType;
		String url = "http://www.google.com.hk/search?" + "&as_q=" + as_q + // 包含的全部关键词
				"+filetype%3A"+fileType.trim() + 
				"&as_epq=" + as_epq + // 包含的完整关键字
				"&as_eq=" + as_eq + // 不包括的关键字
				"&num=" + num.trim() + // 每页显示
				//"&as_ft=" + fileType.trim() + // 文件类型
				
				"&as_sitesearch=" + site.trim(); // 在哪个网页内进行搜索
		if (lang.equals("简体中文"))
			url += "&lr=" + "lang_zh_CN";
		else if (lang.equals("繁体中文"))
			url += "&lr=" + "lang_zh_TW";
		else if (lang.equals("英文"))
			url += "&lr=" + "lang_en";

		url += "&oe=gb2312";

		return url;
	}

	public String UrlCodeExchange(String str) {
		try {
			if (str.startsWith("+"))
				return java.net.URLEncoder.encode(str.substring(1), "utf-8");
			else
				return java.net.URLEncoder.encode(str, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getUrl(String str) {
		String cacheUrl = "";
		int begin = str.indexOf("href=");
		if (begin == -1)
			return cacheUrl;
		str = str.substring(begin + 6);
		int end = str.indexOf("\"");
		if (end == -1)
			return cacheUrl;
		cacheUrl = str.substring(0, end);
		return cacheUrl;
	}

	public String dropTail(String str) {
		int index = str.lastIndexOf("...");
		if (index == -1)
			return str;
		else
			return str.substring(0, index + 3);

	}

}
