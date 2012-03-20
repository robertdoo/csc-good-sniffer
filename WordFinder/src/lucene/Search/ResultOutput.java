package lucene.Search;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

import systemConfig.SystemParameter;

import dataStruct.Result;
import dataStruct.SearchResult;

/**
 * 以HTMl形式输出结果的类
 */
public class ResultOutput {
	private final String preTag = "<font color='red'>";
	private final String postTag = "</font>";
	private final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private final long oneDay = 24*60*60*1000;
	private final String newImgPath = new File("img/newMsg.gif").getAbsolutePath();
	private final String cssPath = new File("htmlStyle.css").getAbsolutePath();
	private final String htmlHead = "<html>\n"
	     + "<head>\n"
	     + "<LINK href=\"" + cssPath + "\" rel=stylesheet type=text/css>"
	     + "<style type='text/css'>"
         + "  .subject {"
	     + "    BORDER-COLLAPSE:collapse;"
	     + "    BORDER: #a1a1a1 1px solid;"
	     + "    MARGIN:5px 0px 2px 0px;"
         + "  }"
         + "</style>"
	     + "<title></title>\n"
	     + "</head>\n"
	     + "<body>\n";
	private SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
	private HashMap<String, String> sourceMap;
	private int TITLE_MAX_LENGTH = 30;  //标题的最大长度
	private int CONTENT_MAX_LENGTH = 90;  //内容摘要的最大长度
	private int PATH_MAX_LENGTH = 40;    //链接的最大长度
	
	private PickAbstract pick = new PickAbstract(TITLE_MAX_LENGTH,
                                               CONTENT_MAX_LENGTH,
                                                  PATH_MAX_LENGTH);
	
	private long searchTime, abstractTime;
	private String mustWord, fullWord;
	private LinkedList<Result> results;
	private String titleAbs, contentAbs, filetype, time, imgPath;
	private Date time_begin, time_end;
	private String fileString;
	
	public ResultOutput(){
		this.sourceMap = new HashMap<String, String>();
		initialSourceMap();
	}

	private void initialSourceMap() {
		sourceMap.put("baidu", "百度");
		sourceMap.put("bing", "必应");
		sourceMap.put("google", "谷歌");
		sourceMap.put("sogou", "搜狗");
		sourceMap.put("soso", "搜搜");
		sourceMap.put("yahoo", "雅虎");
		sourceMap.put("youdao", "有道");
		sourceMap.put("spider", "爬虫");
		sourceMap.put("", "未知");
		sourceMap.put(null, "null");
	}
	
	/**
	 * 返回HTML形式的搜索结果，先对结果进行摘要再输出
	 */
	public String outputResultToHtmlString_abstract(SearchResult searchResult
			                                        , int indexBegin
			                                        , int indexEnd)
	{
		if (searchResult == null) {
			//System.out.println("没有搜索结果，输出HTML文件失败~！");
			imgPath = new File("img/error.png").getAbsolutePath();
			String text = "<h1 align='center'><img src='" + imgPath
					+ "'></h1><br><h3 align='center'><i>搜索失败~</i></h3>"
					+ "<table><tr><td><p align='center'>找不到索引文件，或者是没有搜索结果！</p></td></tr></table>";
			return htmlHead + text;
		}

		String para = "";
		para = SystemParameter.parameters.getProperty("output.titleLength");
		if(para != null)TITLE_MAX_LENGTH = Integer.valueOf(para);
		para = SystemParameter.parameters.getProperty("output.contentLength");
		if(para != null)CONTENT_MAX_LENGTH = Integer.valueOf(para);
		para = SystemParameter.parameters.getProperty("output.urlLength");
		if(para != null)PATH_MAX_LENGTH = Integer.valueOf(para);
		
		searchTime = searchResult.getSearchTime();
		mustWord = searchResult.getRule().getWords();
		fullWord = searchResult.getRule().getSnippet();
		results = searchResult.getResults();

		fileString = "";
		// 输出为HTML格式
		if (results.size() == 0) {
			fileString += "<table><tr><td><p align='center'><i>对不起，没有找到与查询条件相符的结果~</i></p></td></tr></table>";
			return htmlHead + fileString;
		}
		
		fileString += ("<table>\n");
		fileString += ("<tr><td>共获得<b> " + results.size()
				+ " </b>条结果，用时<b> " + searchTime / 1000.0 + " </b>秒，以下是第<b> "
				+ indexBegin + "-" + indexEnd + " </b>条。</td></tr>\n");
		
		time_begin = new Date();
		for (int i = indexBegin; i <= indexEnd; i++) {
			Result result = results.get(i-1);

			if (result.getTime() == null)
				time = "";
			else
				time = sdf.format(result.getTime());
			
			filetype = result.getFiletype();
			filetype = filetype.trim().equals("") ? "html" : filetype;

			//输出的时候再摘要
			titleAbs = pick.pickTitleAbstract(mustWord+" "+fullWord, result.getTitle());
			titleAbs = titleAbs.trim().equals("") ? "没有标题" : titleAbs;
			titleAbs = getHighlightText(mustWord+fullWord, titleAbs);

			//输出的时候再摘要
			contentAbs = pick.pickContentAbstract(mustWord+" "+fullWord, result.getContent());
			contentAbs = contentAbs.trim().equals("") ? "没有内容" : contentAbs;
			contentAbs = getHighlightText(mustWord+" "+fullWord, contentAbs);// 高亮
			contentAbs = pickTime(time) + contentAbs;//加入时间

			// 输出标题的摘要
			if (filetype.equals("html")) {// HTML文件
				fileString += ("<tr><td><font size=4><a href=\"" + result.getUrl()
						+ "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("doc")) {// WORD文件
				fileString += ("<tr><td>【DOC文件】<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("pdf")) {// PDF文件
				fileString += ("<tr><td>【PDF文件】<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("xls")) {// PDF文件
				fileString += ("<tr><td>【XLS文件】<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("ppt")) {// PDF文件
				fileString += ("<tr><td>【PPT文件】<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("rtf")) {// PDF文件
				fileString += ("<tr><td>【RTF文件】<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			}
			//输出时间
			fileString += ("<td width=150>" + time + "</td></tr>\n");
			// 输出内容的摘要
			fileString += ("<tr><td>" + contentAbs + "</td></tr>\n");
			// 输出url的摘要，包括网页快照
			fileString += ("<tr><td><b><font color='green'>"
					+ pick.pickPath(result.getUrl()) + " - </font></b>"
					+ "<font color='gray' size=3><a href=\""
					+ result.getPageMirrorPath() + "\" target=_blank>" + "【网页快照】</a></font></td></tr>\n");
			// 输出来源
			fileString += ("<tr><td>来源：" + result.getSource() + "</td></tr>\n");
			fileString += "\n<tr><td>&nbsp</td></tr>\n";
		}
		fileString += "</table>\n";
		time_end = new Date();
		abstractTime = time_end.getTime() - time_begin.getTime();
		fileString += ("<br><br>提取摘要总共用时<b> " + abstractTime / 1000.0 + " </b>秒。\n");

		return htmlHead + fileString;
	}
	
	public String outputResultToHtmlString_abstract(SearchResult searchResult){
		if(searchResult == null)
			return outputResultToHtmlString_abstract(searchResult, 0, 0);
		else
			return outputResultToHtmlString_abstract(searchResult,
					                        1,
					                        searchResult.getCountOfResults());
	}
	
	/**
	 * 返回HTML形式的搜索结果，直接输出，不进行摘要处理
	 */
	public String outputResultToHtmlString(SearchResult searchResult
			                              , int indexBegin
			                              , int indexEnd)
	{
		if (searchResult == null) {
			System.out.println("没有搜索结果，输出HTML文件失败~！");
			imgPath = new File("img/error.png").getAbsolutePath();
			String text = "<h1 align='center'><img src='" + imgPath
					+ "'></h1><br><h3 align='center'><i>搜索失败~</i></h3>"
					+ "<table><tr><td><p align='center'>找不到索引文件，或者是没有搜索结果！</p></td></tr></table>";
			return text;
		}

		String para = "";
		para = SystemParameter.parameters.getProperty("output.titleLength");
		if(para != null)TITLE_MAX_LENGTH = Integer.valueOf(para);
		para = SystemParameter.parameters.getProperty("output.contentLength");
		if(para != null)CONTENT_MAX_LENGTH = Integer.valueOf(para);
		para = SystemParameter.parameters.getProperty("output.urlLength");
		if(para != null)PATH_MAX_LENGTH = Integer.valueOf(para);
		
		searchTime = searchResult.getSearchTime();
		mustWord = searchResult.getRule().getWords();
		fullWord = searchResult.getRule().getSnippet();
		results = searchResult.getResults();
		
		fileString = "";
		// 输出为HTML格式
		if (results.size() == 0) {
			fileString += "<table><tr><td><p align='center'><i>对不起，没有找到与查询条件相符的结果~</i></td></tr></table>";
			return fileString;
		}
		
		fileString += "<table>\n";
		fileString += ("<tr><td>共获得<b> " + results.size()
				+ " </b>条结果，用时<b> " + searchTime / 1000.0 + " </b>秒，以下是第<b> "
				+ indexBegin + "-" + indexEnd + " </b>条。</td></tr>\n<tr><td>&nbsp</td></tr>\n");
	
		for (int i = indexBegin; i <= indexEnd; i++) {
			fileString += "\n";
			Result result = results.get(i - 1);

			if (result.getTime() == null)
				time = "";
			else
				time = sdf.format(result.getTime());

			titleAbs = getHighlightText(mustWord + fullWord, result.getTitle());
			titleAbs = titleAbs.trim().equals("") ? "没有标题" : titleAbs;

			contentAbs = getHighlightText(mustWord + fullWord, result.getContent());
			contentAbs = contentAbs.trim().equals("") ? "没有内容" : contentAbs;

			filetype = result.getFiletype();
			filetype = filetype.trim().equals("") ? "html" : filetype;
			
			// 输出标题的摘要
			if (filetype.equals("html")) {// HTML文件
				fileString += ("<tr><td><font size=4><a href=\"" + result.getUrl()
						+ "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("doc")) {// WORD文件
				fileString += ("<tr><td>【DOC文件】<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("pdf")) {// PDF文件
				fileString += ("<tr><td>【PDF文件】<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("xls")) {// PDF文件
				fileString += ("<tr><td>【XLS文件】<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("ppt")) {// PDF文件
				fileString += ("<tr><td>【PPT文件】<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("rtf")) {// PDF文件
				fileString += ("<tr><td>【RTF文件】<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else {
				fileString += ("<tr><td>" + filetype + "<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			}
			fileString += "<td width=150>" + time + "</td></tr>\n";
			// 输出内容的摘要
			fileString += ("<tr><td>" + contentAbs + "</td></tr>\n");
			// 输出url的摘要，包括网页快照
			fileString += ("<tr><td><b><font color='green'>"
					+ pick.pickPath(result.getUrl()) + " - </font></b>"
					+ "<font color='gray' size=3><a href=\""
					+ result.getPageMirrorPath() + "\" target=_blank>" + "【网页快照】</a></font></td></tr>\n");
			// 输出来源
			fileString += ("<tr><td>来源：" + sourceMap.get(result.getSource()) + "</td></tr>\n");
			fileString += "\n<tr><td>&nbsp</td></tr>\n";
		}
		fileString += "</table>\n";

		return htmlHead + fileString;
	}
	
	public String outputResultToHtmlString(SearchResult searchResult){
		if(searchResult == null)
			return outputResultToHtmlString(searchResult, 0, 0);
		else
			return outputResultToHtmlString(searchResult,
					                        1,
					                        searchResult.getCountOfResults());
	}
	
	/**
	 * 以表格形式返回HTML形式的搜索结果，先对结果进行摘要再输出
	 */
	public String outputResultToHtmlStringAsTable_abstract(SearchResult searchResult
			                                        , int indexBegin
			                                        , int indexEnd)
	{
		if (searchResult == null) {
			//System.out.println("没有搜索结果，输出HTML文件失败~！");
			imgPath = new File("img/error.png").getAbsolutePath();
			String text = "<h1 align='center'><img src='" + imgPath
					+ "'></h1><br><h3 align='center'><i>搜索失败~</i></h3>"
					+ "<table><tr><td><p align='center'>找不到索引文件，或者是没有搜索结果！</p></td></tr></table>";
			return htmlHead + text;
		}

		String para = "";
		para = SystemParameter.parameters.getProperty("output.titleLength");
		if(para != null)TITLE_MAX_LENGTH = Integer.valueOf(para);
		para = SystemParameter.parameters.getProperty("output.contentLength");
		if(para != null)CONTENT_MAX_LENGTH = Integer.valueOf(para);
		para = SystemParameter.parameters.getProperty("output.urlLength");
		if(para != null)PATH_MAX_LENGTH = Integer.valueOf(para);
		
		searchTime = searchResult.getSearchTime();
		mustWord = searchResult.getRule().getWords();
		fullWord = searchResult.getRule().getSnippet();
		results = searchResult.getResults();

		fileString = "";
		// 输出为HTML格式
		if (results.size() == 0) {
			fileString += "<table><tr><td><p align='center'><i>对不起，没有找到与查询条件相符的结果~</i></p></td></tr></table>";
			return htmlHead + fileString;
		}
		
		fileString += ("共获得<b> " + results.size()
				+ " </b>条结果，用时<b> " + searchTime / 1000.0 + " </b>秒，以下是第<b> "
				+ indexBegin + "-" + indexEnd + " </b>条。\n");
		fileString += "<table width='100%' border='0' align='center' cellspacing='1' cellpadding='1' class='subject'>\n";
		fileString += "<tr>\n";
		fileString += "<td width='15%' height='25' align='center' class='tableth'>标题</td>\n";
		fileString += "<td width='55%' align='center' class='tableth'>摘要</td>\n";
		fileString += "<td width='10%' align='center' class='tableth'>时间</td>\n";
		fileString += "<td width='10%' align='center' class='tableth'>来源</td>\n";
		fileString += "<td width='10%' align='center' class='tableth'>网页快照</td>\n";
		fileString += "</tr>\n";
		
		time_begin = new Date();
		for (int i = indexBegin; i <= indexEnd; i++) {
			fileString += "<tr>\n";
			Result result = results.get(i-1);

			if (result.getTime() == null)
				time = "";
			else
				time = sdf.format(result.getTime());
			
			filetype = result.getFiletype();
			filetype = filetype.trim().equals("") ? "html" : filetype;

			//输出的时候再摘要
			titleAbs = pick.pickTitleAbstract(mustWord+" "+fullWord, result.getTitle());
			titleAbs = titleAbs.trim().equals("") ? "没有标题" : titleAbs;
			titleAbs = getHighlightText(mustWord+fullWord, titleAbs);

			//输出的时候再摘要
			contentAbs = pick.pickContentAbstract(mustWord+" "+fullWord, result.getContent());
			contentAbs = contentAbs.trim().equals("") ? "没有内容" : contentAbs;
			contentAbs = getHighlightText(mustWord+" "+fullWord, contentAbs);// 高亮
			contentAbs = pickTime(time) + contentAbs;//加入时间

			// 输出标题的摘要
			if (filetype.equals("html")) {// HTML文件
				fileString += ("<td class='tabletd'><a href=\"" + result.getUrl()
						+ "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("doc")) {// WORD文件
				fileString += ("<td class='tabletd'>【DOC文件】<a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("pdf")) {// PDF文件
				fileString += ("<td class='tabletd'>【PDF文件】<a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("xls")) {// PDF文件
				fileString += ("<td class='tabletd'>【XLS文件】<font size=4><a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("ppt")) {// PDF文件
				fileString += ("<td class='tabletd'>【PPT文件】<font size=4><a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("rtf")) {// PDF文件
				fileString += ("<td class='tabletd'>【RTF文件】<font size=4><a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else {
				fileString += ("<td class='tabletd'>" + filetype + "<a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			}
			//输出内容的摘要
			fileString += ("<td class='tabletd'>" + contentAbs + "</td>\n");
			//输出时间
			fileString += "<td align='center' class='tabletd'>" + time + "</td>\n";
			// 输出来源
			fileString += ("<td align='center' class='tabletd'>" + result.getSource() + "</td>\n");
			//输出url的摘要，包括网页快照
			fileString += ("<td align='center' class='tabletd'>"
					+ "<a href=\""
					+ result.getPageMirrorPath() + "\" target=_blank>" + "【网页快照】</a></td>\n");
			fileString += "</tr>\n";
		}
		fileString += "</table>\n";
		time_end = new Date();
		abstractTime = time_end.getTime() - time_begin.getTime();
		fileString += ("<br><br>提取摘要总共用时<b> " + abstractTime / 1000.0 + " </b>秒。\n");

		return htmlHead + fileString;
	}
	
	/**
	 * 以表格形式返回HTML形式的搜索结果，直接输出，不进行摘要处理
	 */
	public String outputResultToHtmlStringAsTable(SearchResult searchResult
			                              , int indexBegin
			                              , int indexEnd)
	{
		if (searchResult == null) {
			System.out.println("没有搜索结果，输出HTML文件失败~！");
			imgPath = new File("img/error.png").getAbsolutePath();
			String text = "<h1 align='center'><img src='" + imgPath
					+ "'></h1><br><h3 align='center'><i>搜索失败~</i></h3>"
					+ "<table><tr><td><p align='center'>找不到索引文件，或者是没有搜索结果！</p></td></tr></table>";
			return text;
		}

		String para = "";
		para = SystemParameter.parameters.getProperty("output.titleLength");
		if(para != null)TITLE_MAX_LENGTH = Integer.valueOf(para);
		para = SystemParameter.parameters.getProperty("output.contentLength");
		if(para != null)CONTENT_MAX_LENGTH = Integer.valueOf(para);
		para = SystemParameter.parameters.getProperty("output.urlLength");
		if(para != null)PATH_MAX_LENGTH = Integer.valueOf(para);
		
		searchTime = searchResult.getSearchTime();
		mustWord = searchResult.getRule().getWords();
		fullWord = searchResult.getRule().getSnippet();
		results = searchResult.getResults();
		
		fileString = "";
		// 输出为HTML格式
		if (results.size() == 0) {
			fileString += "<table><tr><td><p align='center'><i>对不起，没有找到与查询条件相符的结果~</i></td></tr></table>";
			return fileString;
		}
		
		fileString += ("共获得<b> " + results.size()
				+ " </b>条结果，用时<b> " + searchTime / 1000.0 + " </b>秒，以下是第<b> "
				+ indexBegin + "-" + indexEnd + " </b>条。\n");
		fileString += "<table width='100%' border='0' align='center' cellspacing='1' cellpadding='1' class='subject'>\n";
		fileString += "<tr>\n";
		fileString += "<td width='15%' height='25' align='center' class='tableth'>标题</td>\n";
		fileString += "<td width='55%' align='center' class='tableth'>摘要</td>\n";
		fileString += "<td width='10%' align='center' class='tableth'>时间</td>\n";
		fileString += "<td width='10%' align='center' class='tableth'>来源</td>\n";
		fileString += "<td width='10%' align='center' class='tableth'>网页快照</td>\n";
		fileString += "</tr>\n";
		
		for (int i = indexBegin; i <= indexEnd; i++) {
			fileString += "<tr>\n";
			Result result = results.get(i - 1);

			if (result.getTime() == null)
				time = "";
			else
				time = sdf.format(result.getTime());

			titleAbs = getHighlightText(mustWord + fullWord, result.getTitle());
			titleAbs = titleAbs.trim().equals("") ? "没有标题" : titleAbs;

			contentAbs = getHighlightText(mustWord + fullWord, result.getContent());
			contentAbs = contentAbs.trim().equals("") ? "没有内容" : contentAbs;

			filetype = result.getFiletype();
			filetype = filetype.trim().equals("") ? "html" : filetype;
			
			// 输出标题的摘要
			if (filetype.equals("html")) {// HTML文件
				fileString += ("<td class='tabletd'><a href=\"" + result.getUrl()
						+ "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("doc")) {// WORD文件
				fileString += ("<td class='tabletd'>【DOC文件】<a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("pdf")) {// PDF文件
				fileString += ("<td class='tabletd'>【PDF文件】<a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("xls")) {// XLS文件
				fileString += ("<td class='tabletd'>【XLS文件】<font size=4><a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("ppt")) {// PPT文件
				fileString += ("<td class='tabletd'>【PPT文件】<font size=4><a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("rtf")) {// RTF文件
				fileString += ("<td class='tabletd'>【RTF文件】<font size=4><a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else {
				fileString += ("<td class='tabletd'>" + filetype + "<a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			}
			//输出内容的摘要
			fileString += ("<td class='tabletd'>" + contentAbs + "</td>\n");
			//输出时间
			fileString += "<td align='center' class='tabletd'>" + time + "</td>\n";
			// 输出来源
			fileString += ("<td align='center' class='tabletd'>" + sourceMap.get(result.getSource()) + "</td>\n");
			//输出url的摘要，包括网页快照
			fileString += ("<td align='center' class='tabletd'>"
					+ "<a href=\""
					+ result.getPageMirrorPath() + "\" target=_blank>" + "【网页快照】</a></td>\n");
			fileString += "</tr>\n";
		}
		fileString += "</table>\n";

		return htmlHead + fileString;
	}
	
	//高亮化，直接用字符串替换
	public String getHighlightText(String keyword, String text){
		IKSegmentation seg = new IKSegmentation(new StringReader(keyword));
		
		//分词
		Lexeme lex = null;
		try {
			while((lex = seg.next())!=null){
				//System.out.println(lex.getLexemeText());
				text = text.replaceAll(lex.getLexemeText(), preTag+lex.getLexemeText()+postTag);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		seg = null;
		return text;
	}
	
	//时间的摘要
	public String pickTime(String timeString){
		if(timeString.equals(""))
			return "";
		
		String time = "<b>";
		
		String timeRegex = "(.*)-(.*)-(.*)";
		String year = "", month = "", day = "";
		Pattern pt_time = Pattern.compile(timeRegex);
		Matcher mc = pt_time.matcher(timeString);
		if(mc.find()){
			year = mc.group(1);
			month = mc.group(2);
			day = mc.group(3).substring(0, Math.min(2, mc.group(3).length()));
		}
		time = time + year + "</b>年<b>" + month + "</b>月<b>" + day +"</b>日…";
		
		return time;
	}
	
	public String outputResultToHtmlTable(LinkedList<Result> results){
		Date now = new Date();
		String html = "<table border = \"1\" rules = \"rows\">\n"
		     + "<caption>最新预警信息</caption>\n"
		     + "<tr><th>标题</th><th>时间</th><th>来源</th></tr>\n";
		
		for(Result r : results){
			html += "<tr>\n";
			html += "<td><a href=\"" + r.getUrl() + "\" target=_blank>" + r.getTitle() + "</a>";
			if((now.getTime() - r.getTime().getTime()) < oneDay)
				html += "<img src=\"" + newImgPath + "\">";
			html +=	"</td>\n";
			html += "<td align=\"center\" width=150><font size=2>" + sdf.format(r.getTime()) + "</font></td>\n";
			html += "<td align=\"center\" width=150>" + sourceMap.get(r.getSource()) + "</td>\n";
			html += "</tr>\n";
		}
		
		html += "</table>\n";
		
		return htmlHead + html;
	}
}