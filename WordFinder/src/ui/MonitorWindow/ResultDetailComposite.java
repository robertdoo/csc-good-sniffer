package ui.MonitorWindow;

import java.io.File;

import lucene.Search.ResultOutput;
import lucene.Search.SearchProcesser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import spider.SpiderConf;

import dataStruct.WarningResult;

public class ResultDetailComposite extends Composite {

	private ResultOutput HTMLPrinter = new ResultOutput();
	private WarningResult result;
	private String htmlHead, html, htmlEnd;
	private SearchProcesser sp = new SearchProcesser(SpiderConf.MAINPAGE_CRAWLER);
	private Browser browser;
	
	private final String cssPath = new File("htmlStyle.css").getAbsolutePath();
	
	public ResultDetailComposite(Composite parent, int style) {
		super(parent, style);
	}
	
	public ResultDetailComposite(Composite parent, WarningResult result) {
		super(parent, SWT.BORDER);
		this.result = result;
		createContent();
	}

	private void createContent(){	
		this.setLayout(new FillLayout());
		browser = new Browser(this, SWT.NONE);
		
		String imgPath = new File("img/wait.png").getAbsolutePath();
		htmlHead = "<html>\n<head>\n<title>敏感信息</title>\n" +
		        "<LINK href=\"" + cssPath + "\" rel=stylesheet type=text/css>" +
				"</head>\n<body>\n<table>";
		html = "<tr><td width=\"" + this.getParent().getBounds().width + "\"><h1 align='center'><img src='" + imgPath + "'></h1>"
				+ "<p align='center'>内容载入中……</p></td></th>";
		htmlEnd = "</table>\n</body>\n</html>";
		
		browser.setText(htmlHead + html + htmlEnd);
		new loadThread().start();
	}

	private void loadFile(){
		html = "<tr><th width=\"100\">时间：</th><td width=\"500\">" + result.getWarningTime() + "</td></tr>";
		html += "<tr><th width=\"100\">标题：</th><td width=\"500\">" 
			+ HTMLPrinter.getHighlightText(result.getKeyword(), result.getTitle())
			+ "</td><td><a href=\"" + result.getCache() + "\" target=_blank>【网页快照】</a></td></tr>";
		html += "<tr><th width=\"100\">来源网址：</th><td width=\"500\"><a href=\""
			+ result.getUrl() + "\" target=_blank>" + result.getUrl() 
			+ "</a></td></tr>";
		html += "<tr><th width=\"100\">规则：</th><td width=\"500\">" + result.getRuleName() + "</td></tr>";
		html += "<tr><th width=\"100\">关键词：</th><td width=\"500\">" + result.getKeyword() + "</td></tr>";
		
		String content = sp.getContentByTitle(result.getTitle());
		if(content == null)
			html += "<tr><th width=\"100\">内容</th><td width=\"500\">" 
				+ "找不到该结果对应的内容"
				+ "</td></tr>";
		else
			html += "<tr><th width=\"100\">内容</th><td width=\"500\">" 
				+ HTMLPrinter.getHighlightText(result.getKeyword(), content)
				+ "</td></tr>";
	}
	
	class loadThread extends Thread {
		Display display = Display.getDefault();
		public void run(){
			loadFile();
			display.asyncExec(new Runnable(){
				public void run(){
					browser.setText(htmlHead + html + htmlEnd);
				}
			});
		}
	}
}
