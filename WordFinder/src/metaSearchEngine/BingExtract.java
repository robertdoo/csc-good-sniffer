package metaSearchEngine;

import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.LinkedList;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.beans.LinkBean;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
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

public class BingExtract {

	/**
	 * @param args
	 */
	ConnectionManager manager;

	NodeFilter table_filter = null;
	NodeFilter cache_filter = null;
	NodeFilter snippet_filter = null;
	NodeFilter title_filter = null;

	NodeList table_list = null;
	NodeList cache_list = null;
	NodeList snippet_list = null;
	NodeList title_list = null;

	NodeIterator table_it = null;
	NodeIterator cache_it = null;
	NodeIterator snippet_it = null;
	NodeIterator title_it = null;
	
	Parser parser = null;
	Parser parser0 = null;
	Parser parser1 = null;
	Parser parser2 = null;
	Parser parser3 = null;

	String url = "";
	String title = "";
	String snippet = "";
	String cache = "";
	String address = "";

	public LinkedList<Result> results;
	public LinkedList<String> nextPages;
	public LinkedList<String> urls;
	public LinkedList<Date> dates;
	
	private boolean isAutoSearch;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Rule rule = new Rule("", "任何语言", "十四所 ", "", "", "", "", "", "其他", false,"");
		BingExtract be = new BingExtract(false);
		System.out.println(be.getUrlByRule(rule, "20"));
		System.out.println();
		// be.doSearch("http://cn.bing.com/search?q=%E5%B0%8F%E6%9C%88%E6%9C%88&form=QBLH&filt=all");
		be.doSearch(rule, "100");
		// be.getNextpage(be.getUrlByRule(rule,"10"));
		for (int i = 0; i < be.results.size(); i++) {
			System.out.println("Url: " + be.results.get(i).getUrl());
			System.out.println("Title: " + be.results.get(i).getTitle());
			System.out.println("Snippet: " + be.results.get(i).getContent());
			System.out.println("cache: "
					+ be.results.get(i).getPageMirrorPath());
			System.out.println("Time: " + be.results.get(i).getTime());
			System.out.println("Filetype: " + be.results.get(i).getFiletype());
			System.out.println("Similarity: "
					+ be.results.get(i).getSimilarity());
			System.out.println();
		}
		/*
		 * for(int i=0;i<be.nextPages.size();i++){
		 * System.out.println(be.nextPages.get(i)); }
		 */
		/*
		 * be.getTimes(be.urls); for(int i=0;i<be.dates.size();i++){
		 * System.out.println(be.dates.get(i)); }
		 */
	}

	public BingExtract(boolean isAutoSearch) {
		results = new LinkedList<Result>();
		nextPages = new LinkedList<String>();
		urls = new LinkedList<String>();
		dates = new LinkedList<Date>();
		
		this.isAutoSearch = isAutoSearch;
	}

	public int doSearch(String url) {
		results.clear();
		urls.clear();
		try {
			manager = Page.getConnectionManager();
			URLConnection connection = manager.openConnection(url);
			connection.setConnectTimeout(10000);
			parser = new Parser(connection);
			parser.setEncoding("GB2312");

			table_filter = new AndFilter(new TagNameFilter("div"),
					new HasAttributeFilter("class", "sa_cc"));
			cache_filter = new AndFilter(new TagNameFilter("div"),
					new HasAttributeFilter("class", "sb_meta"));
			title_filter = new AndFilter(new TagNameFilter("div"),
					new HasAttributeFilter("class", "sb_tlst"));
			snippet_filter = new TagNameFilter("p");

			table_list = parser.parse(table_filter);
			table_it = table_list.elements();
			while (table_it.hasMoreNodes()) {
				Node node = (Node) table_it.nextNode();
				parser0 = new Parser(handelScript(node.toHtml()));
				parser1 = new Parser(node.toHtml());
				parser2 = new Parser(node.toHtml());
				parser3 = new Parser(node.toHtml());

				TextExtractingVisitor visitor = new TextExtractingVisitor();
				parser0.visitAllNodesWith(visitor);

				String body = visitor.getExtractedText().replaceAll(" ", "")
						.replaceAll("	", "").replaceAll("\n", "");
				if (body.length() == 0)
					continue;

				snippet_list = parser1.parse(snippet_filter);
				snippet_it = snippet_list.elements();
				if (snippet_it.hasMoreNodes()) {
					snippet = ((Node) snippet_it.nextNode()).toHtml();
					if (snippet.length() >= 4)
						snippet = snippet.substring(3, snippet.length() - 4);
					snippet = handleHTMLTag(snippet);
				} else
					continue;

				title_list = parser2.parse(title_filter);
				title_it = title_list.elements();
				if (title_it.hasMoreNodes()) {
					Node title_node = ((Node) title_it.nextNode());
					Parser title_parser = new Parser(title_node.toHtml());
					TextExtractingVisitor visitor1 = new TextExtractingVisitor();
					title_parser.visitAllNodesWith(visitor1);

					title = visitor1.getExtractedText().replaceAll(" ", "")
							.replaceAll("	", "").replaceAll("\n", "");

					address = getUrl(title_node.toHtml());
					urls.add(address);
				} else
					continue;

				cache_list = parser3.parse(cache_filter);
				cache_it = cache_list.elements();
				if (cache_it.hasMoreNodes()) {
					Node cache_node = (Node) cache_it.nextNode();
					cache = handleHTMLTag(getUrl(cache_node.toHtml()));
				}

				/*
				  System.out.println("title: "+title);
				  System.out.println("url: "+url);
				  System.out.println("snippet: "+snippet);
				  System.out.println("cache: "+cache); System.out.println();
				*/

				//去重
				//if(MainWindow.merp.isExisted(address))continue;
				if(isAutoSearch && !MainWindow.bf2.boomfilter(address))continue;
				
				Result result = new Result(title, snippet, address, null,
						   "html", cache, "bing");
				results.add(result);

			}
		} catch (Exception e) {
			System.err.println("使用必应搜索引擎时出现异常");
			if (table_list != null)
				table_list = null;
			if (table_it != null)
				table_it = null;
			if (title_list != null)
				title_list = null;
			if (title_it != null)
				title_it = null;
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
			if(parser3!=null)
				parser3 = null;
			return 1;
		}
		if (table_list != null)
			table_list = null;
		if (table_it != null)
			table_it = null;
		if (title_list != null)
			title_list = null;
		if (title_it != null)
			title_it = null;
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
		if(parser3!=null)
			parser3 = null;
		return 0;
	}

	public int doSearch(Rule rule, String num) {
		int stateCode;
		String url = getUrlByRule(rule, num);
		System.out.println(url);
		stateCode = doSearch(url);
		getNextpage(url);
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
		String head = "http://cn.bing.com/search?q=";
		String tail = "&form=QBLH&filt=all&qs=n&sk=";
		String url = head;
		String[] words = rule.getWords().split(" ");
		String[] snippet = rule.getSnippet().split(" ");
		String[] unwantedWords = rule.getUnwantedWords().split(" ");
		String lang = rule.getLanguage();
		String fileType = rule.getFileType();
		String site = rule.getSite();
		// String num = Num;
		for (int i = 0; i < words.length; i++) {
			if (words[i] != "")
				url += UrlCodeExchange(words[i]) + "+";
		}
		for (int i = 0; i < snippet.length; i++) {
			if (snippet[i] != "")
				url += "%22" + UrlCodeExchange(snippet[i]) + "%22" + "+";
		}
		for (int i = 0; i < unwantedWords.length; i++) {
			if (unwantedWords[i] != "")
				url += "-" + UrlCodeExchange(unwantedWords[i]) + "+";
		}
		if (lang.equals("简体中文"))
			url += "language%3A" + "zh_chs" + "+";
		else if (lang.equals("繁体中文"))
			url += "language%3A" + "zh_cht" + "+";
		else if (lang.equals("英文"))
			url += "language%3A" + "en" + "+";
		if (!fileType.equals("任何格式")&&(fileType.length()!=0))
			url += "filetype%3A" + fileType + "+";
		if (site.length() != 0)
			url += "site%3A" + site + "+";

		if (url.endsWith("+"))
			url = url.substring(0, url.length() - 1);

		url += tail;
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

	public String getUrl(String str) {
		String url = "";
		int begin = str.indexOf("href=");
		if (begin != -1)
			str = str.substring(begin + 6);
		else
			return url;
		int end = str.indexOf("\"");
		if (end != -1)
			str = str.substring(0, end);
		else
			return url;
		url = str;
		return url;
	}

	public void getNextpage(String url) {
		nextPages.clear();
		LinkBean lb = new LinkBean();
		lb.setURL(url);
		URL[] Urls = lb.getLinks();
		for (int i = 0, j = 0; i < Urls.length; i++) {
			if (Urls[i].toString().startsWith("http://cn.bing.com/search?q=")
					&& Urls[i].toString().indexOf("first=") != -1 && j < 5) {
				nextPages.add(handleHTMLTag(Urls[i].toString()));
				j++;
			}
		}

	}

	public void getTimes(LinkedList<String> urls) {
		GetTimes gt = new GetTimes();
		dates = gt.getTimes(urls);
	}
}
