package metaSearchEngine;

import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.beans.LinkBean;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.http.ConnectionManager;
import org.htmlparser.lexer.Page;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.TextExtractingVisitor;

import ContentExtract.GetTimes;

import ui.MainWindow.MainWindow;

import dataStruct.Result;
import dataStruct.Rule;

public class BaiduExtract {

	/**
	 * @param args
	 */
	ConnectionManager manager;
	NodeFilter table_filter = null;
	NodeFilter url_filter = null;
	NodeFilter cache_filter = null;

	NodeList table_list = null;
	NodeList url_list = null;
	NodeList cache_list = null;

	NodeIterator table_it = null;
	NodeIterator url_it = null;
	NodeIterator cache_it = null;
	
	Parser parser = null;
	Parser parser0 = null;
	Parser parser1 = null;
	Parser parser2 = null;

	String address = "";
	String title = "";
	String snippet = "";
	String cache = "";
	String time = "";

	public LinkedList<Result> results;
	public LinkedList<String> nextPages;
	//public LinkedList<String> urls;
	public LinkedList<Date> dates;
	private Hashtable<String, String> transTable;
	
	private boolean isAutoSearch;

	public static void main(String[] args) {
		// String url =
		// "http://www.baidu.com/s?lm=0&cl=1&lm=30&ct=1&q1=使用bing&q2=设置不包含关键字&q4=&ft=&rn=10&q6=";
		Rule rule = new Rule("", "简体中文", "南京理工大学", "计算机科学与技术学院", "模式识别", "doc", "", "", "其他", false,"");
		BaiduExtract test = new BaiduExtract(false);

		test.doSearch(rule, "10");
		// test.doSearch(url);

		test.getNextpage(test.getUrlByRule(rule, ""));
		
		/*  for(int i=0;i<test.results.size();i++){
		  System.out.println("Url: "+test.results.get(i).getUrl());
		  System.out.println("Title: "+test.results.get(i).getTitle());
		  System.out.println("Snippet: "+test.results.get(i).getContent());
		  System
		  .out.println("cache: "+test.results.get(i).getPageMirrorPath());
		  System.out.println("Time: "+test.results.get(i).getTime());
		  System.out.println("Filetype: "+test.results.get(i).getFiletype());
		  System
		  .out.println("Similarity: "+test.results.get(i).getSimilarity());
		  System.out.println(); }*/
		 
		/*for (int i = 0; i < test.nextPages.size(); i++) {
			System.out.println(test.nextPages.get(i));
		}

		test.getTimes(test.urls);
		for (int i = 0; i < test.dates.size(); i++) {
			System.out.println(test.dates.get(i));
		}*/

	}

	public BaiduExtract(boolean isAutoSearch) {
		results = new LinkedList<Result>();
		nextPages = new LinkedList<String>();
		//urls = new LinkedList<String>();
		dates = new LinkedList<Date>();

		transTable = new Hashtable<String, String>();
		transTable.put("任何语言", "0");
		transTable.put("简体中文", "1");
		transTable.put("英文", "2");// 实际表示繁体中文！！！
		
		this.isAutoSearch = isAutoSearch;
	}

	public int doSearch(String url) {
		// TODO Auto-generated method stub
		results.clear();
		dates.clear();
		try {
			manager = Page.getConnectionManager();
			URLConnection connection = manager.openConnection(url);
			connection.setConnectTimeout(10000);
			parser = new Parser(connection);
			parser.setEncoding("GB2312");

			table_filter = new OrFilter(new AndFilter(
					new TagNameFilter("table"), new HasAttributeFilter("class",
							"result")), new AndFilter(
					new TagNameFilter("table"), new HasAttributeFilter("mu")));
			url_filter = new AndFilter(new TagNameFilter("a"),
					new HasAttributeFilter("target", "_blank"));
			cache_filter = new AndFilter(new TagNameFilter("a"),
					new HasAttributeFilter("class", "m"));

			table_list = parser.parse(table_filter);
			table_it = table_list.elements();
			while (table_it.hasMoreNodes()) {
				Node node = (Node) table_it.nextNode();
				parser0 = new Parser(handelScript(node.toHtml()));
				parser1 = new Parser(node.toHtml());
				parser2 = new Parser(node.toHtml());

				TextExtractingVisitor visitor = new TextExtractingVisitor();
				parser0.visitAllNodesWith(visitor);

				String body = visitor.getExtractedText().replaceAll(" ", "")
						.replaceAll("	", "").replaceAll("\n", "");
				if (body.length() == 0)
					continue;

				time = getTime(body);

				url_list = parser1.parse(url_filter);
				url_it = url_list.elements();
				if (url_it.hasMoreNodes()) {
					String str = ((Node) url_it.nextNode()).toHtml();
					address = getUrl(str);
					//urls.add(address);
					title = getTitle(str);
				} else
					continue;

				snippet = getSnippet(body, title, time);

				cache_list = parser2.parse(cache_filter);
				cache_it = cache_list.elements();
				if (cache_it.hasMoreNodes()) {
					cache = getUrl(((Node) cache_it.nextNode()).toHtml());
				} else
					cache = "";

				/*
				System.out.println("title: " + title);
				System.out.println("address: " + address);
				System.out.println("snippet: " + snippet);
				System.out.println("cache: " + cache);
				System.out.println("time :" + time);
				System.out.println();
				*/

				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date datetime = null;
				try {
					datetime = format.parse(time);
				} catch (ParseException e) {

				}
				
				//去重
				//if(MainWindow.merp.isExisted(address))continue;
				if(isAutoSearch && !MainWindow.bf2.boomfilter(address))continue;
				
				Result result = new Result(title, snippet, address, datetime,
						"html", cache, "baidu");
				results.add(result);
			}
			
		} catch (Exception e) {
			 //e.printStackTrace();
			System.err.println("使用百度搜索引擎时出现异常！");
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

	public void getNextpage(Rule rule, String num) {
		String url = getUrlByRule(rule, num);
		getNextpage(url);
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

	public String getTitle(String str) {
		String Title = "";
		//int begin = str.indexOf("font size=\"3\"");
		int begin = str.indexOf("target=\"_blank\"");
		int end = 0;
		if (begin == -1) {
			begin = str.indexOf("al_c(this)})");
			if (begin == -1)
				return Title;
			str = str.substring(begin);
			str = str.substring(str.indexOf(">") + 1);
			end = str.indexOf("</a>");
			if (end == -1)
				return Title;
			Title = str.substring(0, end);
			Title = handleHTMLTag(Title);

			return Title;
		}
		str = str.substring(begin);
		str = str.substring(str.indexOf(">") + 1);
		//end = str.indexOf("</font>");
		end = str.indexOf("</a>");
		if (end == -1)
			return Title;
		Title = str.substring(0, end);
		Title = handleHTMLTag(Title);

		return Title;
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

	public String getTime(String str) {
		String Time = "";
		Pattern timePattern = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}");
		Matcher timeMatcher = timePattern.matcher(str);
		if (timeMatcher.find()) {
			Time = timeMatcher.group() + "  " + "00:00:00";
		}
		return Time;
	}

	public String getSnippet(String body, String title, String time) {
		String Snippet = body;
		String Title = title.replaceAll(" ", "").replaceAll("	", "")
				.replaceAll("\n", "");
		String[] times = time.split("  ");
		Snippet = Snippet.replaceAll(Title, "").replaceAll(times[0], "");
		Snippet = Snippet.replaceAll("-百度快照", "");
		if (Snippet.endsWith("..."))
			Snippet = Snippet.substring(0, Snippet.length() - 3);
		int end = Snippet.lastIndexOf("...");
		if (end != -1)
			Snippet = Snippet.substring(0, end + 3);
		return Snippet;
	}

	public void getNextpage(String url) {
		nextPages.clear();
		LinkBean lb = new LinkBean();
		lb.setURL(url);
		URL[] Urls = lb.getLinks();
		for (int i = 0, j = 0; i < Urls.length; i++) {
			if (Urls[i].toString().startsWith("http://www.baidu.com/s?wd=")
					&& j < 9) {
				nextPages.add(Urls[i].toString());
				j++;
			}
		}

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

		String q1 = "";
		String q2 = "";
		String q4 = "";

		for (int i = 0; i < words.length; i++) {
			if (words[i].startsWith("+"))
				q1 += "%2B" + UrlCodeExchange(words[i]);
			else if (q1.length() > 0)
				q1 += "+" + UrlCodeExchange(words[i]);
			else
				q1 += UrlCodeExchange(words[i]);
		}
		for (int i = 0; i < snippet.length; i++) {
			if (q2.length() == 0)
				q2 += UrlCodeExchange(snippet[i]);
			else
				q2 += "+" + UrlCodeExchange(snippet[i]);
		}
		for (int i = 0; i < unwantedWords.length; i++) {
			if (unwantedWords[i].startsWith("+"))
				q1 += "+-" + UrlCodeExchange(unwantedWords[i]);
			else if (q4.length() == 0)
				q4 += UrlCodeExchange(unwantedWords[i]);
			else
				q4 += "+" + UrlCodeExchange(unwantedWords[i]);
		}
		lang = translate(lang);
		if (lang == null) {
			lang = "0";
		}
		site = (site == null) ? "" : site.trim();
		System.out.println(fileType);
		fileType = fileType.equals("任何格式") ? "" : fileType;
		String url = "http://www.baidu.com/s?" + "&q1=" + q1 + // 包含的全部关键词
				"&q2=" + q2 + // 包含的完整关键字
				"&q3=" + "&q4=" + q4 + // 不包括的关键字
				"&rn=" + num.trim() + // 每页显示记录数
				"&lm=0" + // 时间
				"&ct=" + lang.trim() + // 语言
				"&ft=" + fileType.trim() + // 文件类型
				"&q5=" + "&q6=" + site.trim() + // 在哪个网页内进行搜索
				"&tn=baiduadv";

		return url;
	}

	public String UrlCodeExchange(String str) {
		try {
			if (str.startsWith("+"))
				return java.net.URLEncoder.encode(str.substring(1), "gb2312");
			else
				return java.net.URLEncoder.encode(str, "gb2312");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public void getTimes(LinkedList<String> urls) {
		GetTimes gt = new GetTimes();
		dates = gt.getTimes(urls);
	}

	String translate(String cn_str) {
		return transTable.get(cn_str);
	}

	public LinkedList<Result> getResult() {
		return results;
	}

	public LinkedList<String> getNextPages() {
		return nextPages;
	}

}
