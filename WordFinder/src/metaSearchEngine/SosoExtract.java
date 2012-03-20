package metaSearchEngine;

import java.net.URLConnection;
import java.util.LinkedList;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.http.ConnectionManager;
import org.htmlparser.lexer.Page;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.TextExtractingVisitor;

import ui.MainWindow.MainWindow;

import dataStruct.Result;
import dataStruct.Rule;

public class SosoExtract {

	/**
	 * @param args
	 */
	ConnectionManager manager;
	NodeFilter table_filter = null;
	NodeFilter url_filter = null;
	NodeFilter cache_filter = null;
	NodeFilter snippet_filter = null;
	// NodeFilter time_filter = null;

	NodeList table_list = null;
	NodeList url_list = null;
	NodeList cache_list = null;
	NodeList snippet_list = null;
	// NodeList time_list = null;

	NodeIterator table_it = null;
	NodeIterator url_it = null;
	// NodeIterator cache_it = null;
	NodeIterator snippet_it = null;

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
	public LinkedList<String> urls;
	//public LinkedList<Date> dates;
	
	private boolean isAutoSearch;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Rule rule = new Rule("", "任何语言", "+十四所 +雷达", "南京十四所", "航母 中国", "",
				"sina.com.cn", "", "其他", false,"");

		SosoExtract se = new SosoExtract(false);
		se.doSearch(rule, "20");
		for (int i = 0; i < se.results.size(); i++) {
			System.out.println("Url: " + se.results.get(i).getUrl());
			System.out.println("Title: " + se.results.get(i).getTitle());
			System.out.println("Snippet: " + se.results.get(i).getContent());
			System.out.println("cache: "
					+ se.results.get(i).getPageMirrorPath());
			System.out.println("Time: " + se.results.get(i).getTime());
			System.out.println("Filetype: " + se.results.get(i).getFiletype());
			System.out.println("Similarity: "
					+ se.results.get(i).getSimilarity());
			System.out.println();
		}
	}

	public SosoExtract(boolean isAutoSearch) {
		results = new LinkedList<Result>();
		nextPages = new LinkedList<String>();
		urls = new LinkedList<String>();
		//dates = new LinkedList<Date>();
		
		this.isAutoSearch = isAutoSearch;
	}

	public int doSearch(String url) {
		urls.clear();
		results.clear();
		try {
			manager = Page.getConnectionManager();
			URLConnection connection = manager.openConnection(url);
			connection.setConnectTimeout(10000);
			parser = new Parser(connection);
			parser.setEncoding("GB2312");

			table_filter = new TagNameFilter("li");
			url_filter = new TagNameFilter("h3");
			cache_filter = new TagNameFilter("a");
			snippet_filter = new TagNameFilter("p");
			// time_filter = new AndFilter(new TagNameFilter("span"),
			// new HasAttributeFilter("class","cdate"));

			table_list = parser.parse(table_filter);
			table_it = table_list.elements();

			while (table_it.hasMoreNodes()) {
				Node node = (Node) table_it.nextNode();
				parser0 = new Parser(node.toHtml());
				parser1 = new Parser(node.toHtml());
				parser2 = new Parser(node.toHtml());
				// Parser parser3 = new Parser(node.toHtml());

				url_list = parser0.parse(url_filter);
				url_it = url_list.elements();
				if (url_it.hasMoreNodes()) {
					String str = ((Node) url_it.nextNode()).toHtml();
					address = getUrl(str);
					urls.add(address);
					Parser titleParser = new Parser(str);
					title = getSource(titleParser);
				} else
					continue;

				snippet_list = parser1.parse(snippet_filter);
				snippet_it = snippet_list.elements();
				if (snippet_it.hasMoreNodes()) {
					String str = ((Node) snippet_it.nextNode()).toHtml();
					Parser snippetParser = new Parser(str);
					snippet = getSource(snippetParser);
					// System.out.println("Snippet: "+snippet);
				}

				cache_list = parser2.parse(cache_filter);
				if (cache_list.size() > 1) {
					String str = cache_list.elementAt(1).toHtml();
					cache = handleHTMLTag(getUrl(str));
					// System.out.println("Cache: "+cache);
				}

				/*
				 * time_list = parser3.parse(time_filter);
				 * if(time_list.size()>0){ String str =
				 * time_list.elementAt(0).toHtml(); Parser timeParser = new
				 * Parser(str); time = getSource(timeParser);
				 * System.out.println(time); }
				 */

				//去重
				//if(MainWindow.merp.isExisted(address))continue;
				if(isAutoSearch && !MainWindow.bf2.boomfilter(address))continue;
				
				Result result = new Result(title, snippet, address, null,
						"html", cache, "soso");
				results.add(result);

			}
		} catch (Exception e) {
			//e.printStackTrace();
			System.err.println("使用搜搜搜索引擎时出现异常");
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
			// if(cache_it!=null)cache_it = null;
			if (snippet_list != null)
				snippet_list = null;
			if (snippet_it != null)
				snippet_it = null;
			if (parser != null)
				parser = null;
			if (parser0 != null)
				parser0 = null;
			if (parser1 != null)
				parser1 = null;
			if (parser2 != null)
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
		// if(cache_it!=null)cache_it = null;
		if (snippet_list != null)
			snippet_list = null;
		if (snippet_it != null)
			snippet_it = null;
		if (parser != null)
			parser = null;
		if (parser0 != null)
			parser0 = null;
		if (parser1 != null)
			parser1 = null;
		if (parser2 != null)
			parser2 = null;
		return 0;

	}

	public int doSearch(Rule rule, String num) {
		int stateCode;
		String url = getUrlByRule(rule, num);
		System.out.println(url);
		stateCode = doSearch(url);
		getNextPages(url);
		int max;
		if (Integer.parseInt(num) / 10 <= nextPages.size())
			max = Integer.parseInt(num) / 10;
		else
			max = nextPages.size();
		for (int i = 1; i < max; i++) {
			stateCode = doSearch(nextPages.get(i - 1));
		}
		return stateCode;
	}

	public String getUrlByRule(Rule rule, String Num) {
		String[] words = rule.getWords().split(" ");
		String[] snippet = rule.getSnippet().split(" ");
		// String[] unwantedWords = rule.getUnwantedWords().split(" ");
		// String lang = rule.getLanguage();
		String fileType = rule.getFileType();
		String site = rule.getSite();
		// String num = Num;

		String q1 = "";
		String q2 = "";
		String q4 = "";

		for (int i = 0; i < words.length; i++) {
			if (q1.length() == 0) {
				q1 += UrlCodeExchange(words[i]);
				if (words[i].startsWith("+"))
					q1 = "%2B" + q1;
			} else {
				if (words[i].startsWith("+"))
					q1 += "+%2B" + UrlCodeExchange(words[i]);
				else
					q1 += "+" + UrlCodeExchange(words[i]);
			}
		}
		for (int i = 0; i < snippet.length; i++) {
			if (snippet[i].length() == 0)
				continue;
			if (snippet[i].startsWith("+"))
				q2 += "+%2B%22" + UrlCodeExchange(snippet[i]) + "%22";
			else
				q2 += "+%22" + UrlCodeExchange(snippet[i]) + "%22";
		}
		// 没有添加不包含关键字的功能！！！
		/*
		 * for(int i=0;i<unwantedWords.length;i++){ q4 +=
		 * "+-"+UrlCodeExchange(unwantedWords[i]); }
		 */

		if (site.length() != 0)
			site = "&site=" + site;
		
		fileType = fileType.equals("任何格式") ? "" : fileType;

		String url = "http://www.soso.com/q?w=" + q1 + q2 + q4 + site;
		
		if(fileType.length()!=0)url+="+filetype%3A"+fileType;
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

	public String getSource(Parser parser) throws Exception {
		TextExtractingVisitor visitor = new TextExtractingVisitor();
		parser.visitAllNodesWith(visitor);

		String source = visitor.getExtractedText().replaceAll(" ", "")
				.replaceAll("	", "").replaceAll("\n", "");
		return source;
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

	public void getNextPages(String url) {
		nextPages.clear();
		for (int i = 1; i < 10; i++) {
			String nextPage = url + "&pg=" + (i + 1);
			nextPages.add(nextPage);
		}
	}
}
