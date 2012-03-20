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
 * ��HTMl��ʽ����������
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
	private int TITLE_MAX_LENGTH = 30;  //�������󳤶�
	private int CONTENT_MAX_LENGTH = 90;  //����ժҪ����󳤶�
	private int PATH_MAX_LENGTH = 40;    //���ӵ���󳤶�
	
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
		sourceMap.put("baidu", "�ٶ�");
		sourceMap.put("bing", "��Ӧ");
		sourceMap.put("google", "�ȸ�");
		sourceMap.put("sogou", "�ѹ�");
		sourceMap.put("soso", "����");
		sourceMap.put("yahoo", "�Ż�");
		sourceMap.put("youdao", "�е�");
		sourceMap.put("spider", "����");
		sourceMap.put("", "δ֪");
		sourceMap.put(null, "null");
	}
	
	/**
	 * ����HTML��ʽ������������ȶԽ������ժҪ�����
	 */
	public String outputResultToHtmlString_abstract(SearchResult searchResult
			                                        , int indexBegin
			                                        , int indexEnd)
	{
		if (searchResult == null) {
			//System.out.println("û��������������HTML�ļ�ʧ��~��");
			imgPath = new File("img/error.png").getAbsolutePath();
			String text = "<h1 align='center'><img src='" + imgPath
					+ "'></h1><br><h3 align='center'><i>����ʧ��~</i></h3>"
					+ "<table><tr><td><p align='center'>�Ҳ��������ļ���������û�����������</p></td></tr></table>";
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
		// ���ΪHTML��ʽ
		if (results.size() == 0) {
			fileString += "<table><tr><td><p align='center'><i>�Բ���û���ҵ����ѯ��������Ľ��~</i></p></td></tr></table>";
			return htmlHead + fileString;
		}
		
		fileString += ("<table>\n");
		fileString += ("<tr><td>�����<b> " + results.size()
				+ " </b>���������ʱ<b> " + searchTime / 1000.0 + " </b>�룬�����ǵ�<b> "
				+ indexBegin + "-" + indexEnd + " </b>����</td></tr>\n");
		
		time_begin = new Date();
		for (int i = indexBegin; i <= indexEnd; i++) {
			Result result = results.get(i-1);

			if (result.getTime() == null)
				time = "";
			else
				time = sdf.format(result.getTime());
			
			filetype = result.getFiletype();
			filetype = filetype.trim().equals("") ? "html" : filetype;

			//�����ʱ����ժҪ
			titleAbs = pick.pickTitleAbstract(mustWord+" "+fullWord, result.getTitle());
			titleAbs = titleAbs.trim().equals("") ? "û�б���" : titleAbs;
			titleAbs = getHighlightText(mustWord+fullWord, titleAbs);

			//�����ʱ����ժҪ
			contentAbs = pick.pickContentAbstract(mustWord+" "+fullWord, result.getContent());
			contentAbs = contentAbs.trim().equals("") ? "û������" : contentAbs;
			contentAbs = getHighlightText(mustWord+" "+fullWord, contentAbs);// ����
			contentAbs = pickTime(time) + contentAbs;//����ʱ��

			// ��������ժҪ
			if (filetype.equals("html")) {// HTML�ļ�
				fileString += ("<tr><td><font size=4><a href=\"" + result.getUrl()
						+ "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("doc")) {// WORD�ļ�
				fileString += ("<tr><td>��DOC�ļ���<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("pdf")) {// PDF�ļ�
				fileString += ("<tr><td>��PDF�ļ���<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("xls")) {// PDF�ļ�
				fileString += ("<tr><td>��XLS�ļ���<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("ppt")) {// PDF�ļ�
				fileString += ("<tr><td>��PPT�ļ���<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("rtf")) {// PDF�ļ�
				fileString += ("<tr><td>��RTF�ļ���<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			}
			//���ʱ��
			fileString += ("<td width=150>" + time + "</td></tr>\n");
			// ������ݵ�ժҪ
			fileString += ("<tr><td>" + contentAbs + "</td></tr>\n");
			// ���url��ժҪ��������ҳ����
			fileString += ("<tr><td><b><font color='green'>"
					+ pick.pickPath(result.getUrl()) + " - </font></b>"
					+ "<font color='gray' size=3><a href=\""
					+ result.getPageMirrorPath() + "\" target=_blank>" + "����ҳ���ա�</a></font></td></tr>\n");
			// �����Դ
			fileString += ("<tr><td>��Դ��" + result.getSource() + "</td></tr>\n");
			fileString += "\n<tr><td>&nbsp</td></tr>\n";
		}
		fileString += "</table>\n";
		time_end = new Date();
		abstractTime = time_end.getTime() - time_begin.getTime();
		fileString += ("<br><br>��ȡժҪ�ܹ���ʱ<b> " + abstractTime / 1000.0 + " </b>�롣\n");

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
	 * ����HTML��ʽ�����������ֱ�������������ժҪ����
	 */
	public String outputResultToHtmlString(SearchResult searchResult
			                              , int indexBegin
			                              , int indexEnd)
	{
		if (searchResult == null) {
			System.out.println("û��������������HTML�ļ�ʧ��~��");
			imgPath = new File("img/error.png").getAbsolutePath();
			String text = "<h1 align='center'><img src='" + imgPath
					+ "'></h1><br><h3 align='center'><i>����ʧ��~</i></h3>"
					+ "<table><tr><td><p align='center'>�Ҳ��������ļ���������û�����������</p></td></tr></table>";
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
		// ���ΪHTML��ʽ
		if (results.size() == 0) {
			fileString += "<table><tr><td><p align='center'><i>�Բ���û���ҵ����ѯ��������Ľ��~</i></td></tr></table>";
			return fileString;
		}
		
		fileString += "<table>\n";
		fileString += ("<tr><td>�����<b> " + results.size()
				+ " </b>���������ʱ<b> " + searchTime / 1000.0 + " </b>�룬�����ǵ�<b> "
				+ indexBegin + "-" + indexEnd + " </b>����</td></tr>\n<tr><td>&nbsp</td></tr>\n");
	
		for (int i = indexBegin; i <= indexEnd; i++) {
			fileString += "\n";
			Result result = results.get(i - 1);

			if (result.getTime() == null)
				time = "";
			else
				time = sdf.format(result.getTime());

			titleAbs = getHighlightText(mustWord + fullWord, result.getTitle());
			titleAbs = titleAbs.trim().equals("") ? "û�б���" : titleAbs;

			contentAbs = getHighlightText(mustWord + fullWord, result.getContent());
			contentAbs = contentAbs.trim().equals("") ? "û������" : contentAbs;

			filetype = result.getFiletype();
			filetype = filetype.trim().equals("") ? "html" : filetype;
			
			// ��������ժҪ
			if (filetype.equals("html")) {// HTML�ļ�
				fileString += ("<tr><td><font size=4><a href=\"" + result.getUrl()
						+ "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("doc")) {// WORD�ļ�
				fileString += ("<tr><td>��DOC�ļ���<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("pdf")) {// PDF�ļ�
				fileString += ("<tr><td>��PDF�ļ���<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("xls")) {// PDF�ļ�
				fileString += ("<tr><td>��XLS�ļ���<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("ppt")) {// PDF�ļ�
				fileString += ("<tr><td>��PPT�ļ���<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else if (filetype.equals("rtf")) {// PDF�ļ�
				fileString += ("<tr><td>��RTF�ļ���<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			} else {
				fileString += ("<tr><td>" + filetype + "<font size=4><a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></font></td>\n");
			}
			fileString += "<td width=150>" + time + "</td></tr>\n";
			// ������ݵ�ժҪ
			fileString += ("<tr><td>" + contentAbs + "</td></tr>\n");
			// ���url��ժҪ��������ҳ����
			fileString += ("<tr><td><b><font color='green'>"
					+ pick.pickPath(result.getUrl()) + " - </font></b>"
					+ "<font color='gray' size=3><a href=\""
					+ result.getPageMirrorPath() + "\" target=_blank>" + "����ҳ���ա�</a></font></td></tr>\n");
			// �����Դ
			fileString += ("<tr><td>��Դ��" + sourceMap.get(result.getSource()) + "</td></tr>\n");
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
	 * �Ա����ʽ����HTML��ʽ������������ȶԽ������ժҪ�����
	 */
	public String outputResultToHtmlStringAsTable_abstract(SearchResult searchResult
			                                        , int indexBegin
			                                        , int indexEnd)
	{
		if (searchResult == null) {
			//System.out.println("û��������������HTML�ļ�ʧ��~��");
			imgPath = new File("img/error.png").getAbsolutePath();
			String text = "<h1 align='center'><img src='" + imgPath
					+ "'></h1><br><h3 align='center'><i>����ʧ��~</i></h3>"
					+ "<table><tr><td><p align='center'>�Ҳ��������ļ���������û�����������</p></td></tr></table>";
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
		// ���ΪHTML��ʽ
		if (results.size() == 0) {
			fileString += "<table><tr><td><p align='center'><i>�Բ���û���ҵ����ѯ��������Ľ��~</i></p></td></tr></table>";
			return htmlHead + fileString;
		}
		
		fileString += ("�����<b> " + results.size()
				+ " </b>���������ʱ<b> " + searchTime / 1000.0 + " </b>�룬�����ǵ�<b> "
				+ indexBegin + "-" + indexEnd + " </b>����\n");
		fileString += "<table width='100%' border='0' align='center' cellspacing='1' cellpadding='1' class='subject'>\n";
		fileString += "<tr>\n";
		fileString += "<td width='15%' height='25' align='center' class='tableth'>����</td>\n";
		fileString += "<td width='55%' align='center' class='tableth'>ժҪ</td>\n";
		fileString += "<td width='10%' align='center' class='tableth'>ʱ��</td>\n";
		fileString += "<td width='10%' align='center' class='tableth'>��Դ</td>\n";
		fileString += "<td width='10%' align='center' class='tableth'>��ҳ����</td>\n";
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

			//�����ʱ����ժҪ
			titleAbs = pick.pickTitleAbstract(mustWord+" "+fullWord, result.getTitle());
			titleAbs = titleAbs.trim().equals("") ? "û�б���" : titleAbs;
			titleAbs = getHighlightText(mustWord+fullWord, titleAbs);

			//�����ʱ����ժҪ
			contentAbs = pick.pickContentAbstract(mustWord+" "+fullWord, result.getContent());
			contentAbs = contentAbs.trim().equals("") ? "û������" : contentAbs;
			contentAbs = getHighlightText(mustWord+" "+fullWord, contentAbs);// ����
			contentAbs = pickTime(time) + contentAbs;//����ʱ��

			// ��������ժҪ
			if (filetype.equals("html")) {// HTML�ļ�
				fileString += ("<td class='tabletd'><a href=\"" + result.getUrl()
						+ "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("doc")) {// WORD�ļ�
				fileString += ("<td class='tabletd'>��DOC�ļ���<a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("pdf")) {// PDF�ļ�
				fileString += ("<td class='tabletd'>��PDF�ļ���<a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("xls")) {// PDF�ļ�
				fileString += ("<td class='tabletd'>��XLS�ļ���<font size=4><a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("ppt")) {// PDF�ļ�
				fileString += ("<td class='tabletd'>��PPT�ļ���<font size=4><a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("rtf")) {// PDF�ļ�
				fileString += ("<td class='tabletd'>��RTF�ļ���<font size=4><a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else {
				fileString += ("<td class='tabletd'>" + filetype + "<a href=\""
						+ result.getUrl() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			}
			//������ݵ�ժҪ
			fileString += ("<td class='tabletd'>" + contentAbs + "</td>\n");
			//���ʱ��
			fileString += "<td align='center' class='tabletd'>" + time + "</td>\n";
			// �����Դ
			fileString += ("<td align='center' class='tabletd'>" + result.getSource() + "</td>\n");
			//���url��ժҪ��������ҳ����
			fileString += ("<td align='center' class='tabletd'>"
					+ "<a href=\""
					+ result.getPageMirrorPath() + "\" target=_blank>" + "����ҳ���ա�</a></td>\n");
			fileString += "</tr>\n";
		}
		fileString += "</table>\n";
		time_end = new Date();
		abstractTime = time_end.getTime() - time_begin.getTime();
		fileString += ("<br><br>��ȡժҪ�ܹ���ʱ<b> " + abstractTime / 1000.0 + " </b>�롣\n");

		return htmlHead + fileString;
	}
	
	/**
	 * �Ա����ʽ����HTML��ʽ�����������ֱ�������������ժҪ����
	 */
	public String outputResultToHtmlStringAsTable(SearchResult searchResult
			                              , int indexBegin
			                              , int indexEnd)
	{
		if (searchResult == null) {
			System.out.println("û��������������HTML�ļ�ʧ��~��");
			imgPath = new File("img/error.png").getAbsolutePath();
			String text = "<h1 align='center'><img src='" + imgPath
					+ "'></h1><br><h3 align='center'><i>����ʧ��~</i></h3>"
					+ "<table><tr><td><p align='center'>�Ҳ��������ļ���������û�����������</p></td></tr></table>";
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
		// ���ΪHTML��ʽ
		if (results.size() == 0) {
			fileString += "<table><tr><td><p align='center'><i>�Բ���û���ҵ����ѯ��������Ľ��~</i></td></tr></table>";
			return fileString;
		}
		
		fileString += ("�����<b> " + results.size()
				+ " </b>���������ʱ<b> " + searchTime / 1000.0 + " </b>�룬�����ǵ�<b> "
				+ indexBegin + "-" + indexEnd + " </b>����\n");
		fileString += "<table width='100%' border='0' align='center' cellspacing='1' cellpadding='1' class='subject'>\n";
		fileString += "<tr>\n";
		fileString += "<td width='15%' height='25' align='center' class='tableth'>����</td>\n";
		fileString += "<td width='55%' align='center' class='tableth'>ժҪ</td>\n";
		fileString += "<td width='10%' align='center' class='tableth'>ʱ��</td>\n";
		fileString += "<td width='10%' align='center' class='tableth'>��Դ</td>\n";
		fileString += "<td width='10%' align='center' class='tableth'>��ҳ����</td>\n";
		fileString += "</tr>\n";
		
		for (int i = indexBegin; i <= indexEnd; i++) {
			fileString += "<tr>\n";
			Result result = results.get(i - 1);

			if (result.getTime() == null)
				time = "";
			else
				time = sdf.format(result.getTime());

			titleAbs = getHighlightText(mustWord + fullWord, result.getTitle());
			titleAbs = titleAbs.trim().equals("") ? "û�б���" : titleAbs;

			contentAbs = getHighlightText(mustWord + fullWord, result.getContent());
			contentAbs = contentAbs.trim().equals("") ? "û������" : contentAbs;

			filetype = result.getFiletype();
			filetype = filetype.trim().equals("") ? "html" : filetype;
			
			// ��������ժҪ
			if (filetype.equals("html")) {// HTML�ļ�
				fileString += ("<td class='tabletd'><a href=\"" + result.getUrl()
						+ "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("doc")) {// WORD�ļ�
				fileString += ("<td class='tabletd'>��DOC�ļ���<a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("pdf")) {// PDF�ļ�
				fileString += ("<td class='tabletd'>��PDF�ļ���<a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("xls")) {// XLS�ļ�
				fileString += ("<td class='tabletd'>��XLS�ļ���<font size=4><a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("ppt")) {// PPT�ļ�
				fileString += ("<td class='tabletd'>��PPT�ļ���<font size=4><a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else if (filetype.equals("rtf")) {// RTF�ļ�
				fileString += ("<td class='tabletd'>��RTF�ļ���<font size=4><a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			} else {
				fileString += ("<td class='tabletd'>" + filetype + "<a href=\""
						+ result.getUrl() + "\" title=\"" + result.getTitle() + "\" target=_blank>" + titleAbs + "</a></td>\n");
			}
			//������ݵ�ժҪ
			fileString += ("<td class='tabletd'>" + contentAbs + "</td>\n");
			//���ʱ��
			fileString += "<td align='center' class='tabletd'>" + time + "</td>\n";
			// �����Դ
			fileString += ("<td align='center' class='tabletd'>" + sourceMap.get(result.getSource()) + "</td>\n");
			//���url��ժҪ��������ҳ����
			fileString += ("<td align='center' class='tabletd'>"
					+ "<a href=\""
					+ result.getPageMirrorPath() + "\" target=_blank>" + "����ҳ���ա�</a></td>\n");
			fileString += "</tr>\n";
		}
		fileString += "</table>\n";

		return htmlHead + fileString;
	}
	
	//��������ֱ�����ַ����滻
	public String getHighlightText(String keyword, String text){
		IKSegmentation seg = new IKSegmentation(new StringReader(keyword));
		
		//�ִ�
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
	
	//ʱ���ժҪ
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
		time = time + year + "</b>��<b>" + month + "</b>��<b>" + day +"</b>�ա�";
		
		return time;
	}
	
	public String outputResultToHtmlTable(LinkedList<Result> results){
		Date now = new Date();
		String html = "<table border = \"1\" rules = \"rows\">\n"
		     + "<caption>����Ԥ����Ϣ</caption>\n"
		     + "<tr><th>����</th><th>ʱ��</th><th>��Դ</th></tr>\n";
		
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