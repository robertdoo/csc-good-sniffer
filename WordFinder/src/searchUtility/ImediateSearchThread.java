package searchUtility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import lucene.Search.ResultSensativity;

import org.eclipse.swt.widgets.Display;

import metaSearchEngine.BaiduExtract;
import metaSearchEngine.BingExtract;
import metaSearchEngine.GoogleExtract;
import metaSearchEngine.SogouExtract;
import metaSearchEngine.SosoExtract;
import metaSearchEngine.YahooExtract;
import metaSearchEngine.YoudaoExtract;

import com.swtdesigner.SWTResourceManager;

import dataStruct.Result;
import dataStruct.Rule;
import dataStruct.SearchResult;

import ui.MainWindow.MainWindow;

public class ImediateSearchThread extends Thread {

	private Display display = Display.getDefault();
	private SearchResult searchResult;
	
	private Rule rule;
	private boolean baidu_selected, bing_selected, google_selected,
    sogou_selected, soso_selected, yahoo_selected,
    youdao_selected;
	private BaiduExtract baidu;
	private BingExtract bing;
	private GoogleExtract google;
	private SogouExtract sogou;
	private SosoExtract soso;
	private YahooExtract yahoo;
	private YoudaoExtract youdao;
	private int status;
	
	private ResultSensativity rs = new ResultSensativity();
	
	public ImediateSearchThread(Rule rule,
			                  boolean baidu_selected,
                              boolean bing_selected,
                              boolean google_selected,
                              boolean sogou_selected,
                              boolean soso_selected,
                              boolean yahoo_selected,
                              boolean youdao_selected) {
		super();
		this.rule = rule;
		this.baidu_selected = baidu_selected;
		this.bing_selected = bing_selected;
		this.google_selected = google_selected;
		this.sogou_selected = sogou_selected;
		this.soso_selected = soso_selected;
		this.yahoo_selected = yahoo_selected;
		this.youdao_selected = youdao_selected;
		searchResult = new SearchResult(0L, rule, new LinkedList<Result>());
		
		if(baidu_selected)baidu = new BaiduExtract(false);
		if(bing_selected)bing = new BingExtract(false);
		if(google_selected)google = new GoogleExtract(false);
		if(sogou_selected)sogou = new SogouExtract(false);
		if(soso_selected)soso = new SosoExtract(false);
		if(yahoo_selected)yahoo = new YahooExtract(false);
		if(youdao_selected)youdao = new YoudaoExtract(false);
	}

	public void run() {
		if(rule.isRelative()){//使用关联词搜索
			rule.setWords(rule.getWords() + " " + rule.getSysnonymWords());
		}
		display.asyncExec(new Runnable(){
			public void run(){
				MainWindow.imediateSearchBrowser.outWait();
				MainWindow.imediateSearchBrowser.setSearchInfo(getCurrentTime() + "\n");
				MainWindow.imediateSearchBrowser.appendSearchInfo("搜索规则：" + rule.getName() + "\n");
				MainWindow.imediateSearchBrowser.appendSearchInfo("关键词：" + rule.getWords() + "\n\n");
			}
		});
		
		if (baidu_selected) {
			display.syncExec(new Runnable() {
				public void run() {
					MainWindow.imediateSearchBrowser.appendSearchInfo("百度正在搜索……");
				}
			});
			status = baidu.doSearch(rule, rule.getCnt());// 用百度搜索
			display.asyncExec(new Runnable() {
				public void run() {
					if (status == 1) {
						MainWindow.setStatusLabel("百度搜索规则“" + rule.getName()
								+ "”失败!", SWTResourceManager
								.getImage("img/warning.gif"));
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("百度搜索失败。\n");
					} else {
						addResults(baidu.results);
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("百度搜索到"
										+ baidu.results.size() + "条结果。\n");
					}
				}
			});
		}
		
		if (bing_selected) {
			display.syncExec(new Runnable() {
				public void run() {
					MainWindow.imediateSearchBrowser.appendSearchInfo("必应正在搜索……");
				}
			});
			status = bing.doSearch(rule, rule.getCnt());// 用必应搜索
			display.asyncExec(new Runnable() {
				public void run() {
					if (status == 1) {
						MainWindow.setStatusLabel("必应搜索规则“" + rule.getName()
								+ "”失败!", SWTResourceManager
								.getImage("img/warning.gif"));
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("必应搜索失败。\n");
					} else {
						addResults(bing.results);
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("必应搜索到" + bing.results.size()
										+ "条结果。\n");
					}
				}
			});
		}
		
		if (google_selected) {
			display.syncExec(new Runnable() {
				public void run() {
					MainWindow.imediateSearchBrowser.appendSearchInfo("谷歌正在搜索……");
				}
			});
			status = google.doSearch(rule, rule.getCnt());// 用谷歌搜索
			display.asyncExec(new Runnable() {
				public void run() {
					if (status == 1) {
						MainWindow.setStatusLabel("谷歌搜索规则“" + rule.getName()
								+ "”失败!", SWTResourceManager
								.getImage("img/warning.gif"));
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("谷歌搜索失败。\n");
					} else {
						addResults(google.results);
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("谷歌搜索到"
										+ google.results.size() + "条结果。\n");
					}
				}
			});
		}
		
		if (sogou_selected) {
			display.syncExec(new Runnable() {
				public void run() {
					MainWindow.imediateSearchBrowser.appendSearchInfo("搜狗正在搜索……");
				}
			});
			status = sogou.doSearch(rule, rule.getCnt());// 用搜狗搜索
			display.asyncExec(new Runnable() {
				public void run() {
					if (status == 1) {
						MainWindow.setStatusLabel("搜狗搜索规则“" + rule.getName()
								+ "”失败!", SWTResourceManager
								.getImage("img/warning.gif"));
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("搜狗搜索失败。\n");
					} else {
						addResults(sogou.results);
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("搜狗搜索到"
										+ sogou.results.size() + "条结果。\n");
					}
				}
			});
		}
		
		if (soso_selected) {
			display.syncExec(new Runnable() {
				public void run() {
					MainWindow.imediateSearchBrowser.appendSearchInfo("搜搜正在搜索……");
				}
			});
			status = soso.doSearch(rule, rule.getCnt());// 用搜搜搜索
			display.asyncExec(new Runnable() {
				public void run() {
					if (status == 1) {
						MainWindow.setStatusLabel("搜搜搜索规则“" + rule.getName()
								+ "”失败!", SWTResourceManager
								.getImage("img/warning.gif"));
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("搜搜搜索失败。\n");
					} else {
						addResults(soso.results);
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("搜搜搜索到" + soso.results.size()
										+ "条结果。\n");
					}
				}
			});
		}
		
		if (yahoo_selected) {
			display.syncExec(new Runnable() {
				public void run() {
					MainWindow.imediateSearchBrowser.appendSearchInfo("雅虎正在搜索……");
				}
			});
			status = yahoo.doSearch(rule, rule.getCnt());// 用雅虎搜索
			display.asyncExec(new Runnable() {
				public void run() {
					if (status == 1) {
						MainWindow.setStatusLabel("雅虎搜索规则“" + rule.getName()
								+ "”失败!", SWTResourceManager
								.getImage("img/warning.gif"));
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("雅虎搜索失败。\n");
					} else {
						addResults(yahoo.results);
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("雅虎搜索到"
										+ yahoo.results.size() + "条结果。\n");
					}
				}
			});
		}
		
		if (youdao_selected) {
			display.syncExec(new Runnable() {
				public void run() {
					MainWindow.imediateSearchBrowser.appendSearchInfo("有道正在搜索……");
				}
			});
			status = youdao.doSearch(rule, rule.getCnt());// 用有道搜索
			display.asyncExec(new Runnable() {
				public void run() {
					if (status == 1) {
						MainWindow.setStatusLabel("有道搜索规则“" + rule.getName()
								+ "”失败!", SWTResourceManager
								.getImage("img/warning.gif"));
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("有道搜索失败。\n");
					} else {
						addResults(youdao.results);
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("有道搜索到"
										+ youdao.results.size() + "条结果。\n");
					}
				}
			});
		}
		//排序
		searchResult = rs.calculateSensativity(searchResult);
		display.syncExec(new Runnable(){
			public void run(){
				MainWindow.imediateSearchBrowser.setResultCopy(searchResult);
				MainWindow.imediateSearchBrowser.appendSearchInfo("搜索完毕！共有" + searchResult.getCountOfResults() + "条结果。\n");
			}
		});
	}
	
	public String getCurrentTime(){
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	}
	
	public void addResults(LinkedList<Result> newResults) {
		for (Result r : newResults)
			if (!searchResult.contains(r))
				searchResult.addResult(r);
	}
}
