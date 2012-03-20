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
		if(rule.isRelative()){//ʹ�ù���������
			rule.setWords(rule.getWords() + " " + rule.getSysnonymWords());
		}
		display.asyncExec(new Runnable(){
			public void run(){
				MainWindow.imediateSearchBrowser.outWait();
				MainWindow.imediateSearchBrowser.setSearchInfo(getCurrentTime() + "\n");
				MainWindow.imediateSearchBrowser.appendSearchInfo("��������" + rule.getName() + "\n");
				MainWindow.imediateSearchBrowser.appendSearchInfo("�ؼ��ʣ�" + rule.getWords() + "\n\n");
			}
		});
		
		if (baidu_selected) {
			display.syncExec(new Runnable() {
				public void run() {
					MainWindow.imediateSearchBrowser.appendSearchInfo("�ٶ�������������");
				}
			});
			status = baidu.doSearch(rule, rule.getCnt());// �ðٶ�����
			display.asyncExec(new Runnable() {
				public void run() {
					if (status == 1) {
						MainWindow.setStatusLabel("�ٶ���������" + rule.getName()
								+ "��ʧ��!", SWTResourceManager
								.getImage("img/warning.gif"));
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("�ٶ�����ʧ�ܡ�\n");
					} else {
						addResults(baidu.results);
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("�ٶ�������"
										+ baidu.results.size() + "�������\n");
					}
				}
			});
		}
		
		if (bing_selected) {
			display.syncExec(new Runnable() {
				public void run() {
					MainWindow.imediateSearchBrowser.appendSearchInfo("��Ӧ������������");
				}
			});
			status = bing.doSearch(rule, rule.getCnt());// �ñ�Ӧ����
			display.asyncExec(new Runnable() {
				public void run() {
					if (status == 1) {
						MainWindow.setStatusLabel("��Ӧ��������" + rule.getName()
								+ "��ʧ��!", SWTResourceManager
								.getImage("img/warning.gif"));
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("��Ӧ����ʧ�ܡ�\n");
					} else {
						addResults(bing.results);
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("��Ӧ������" + bing.results.size()
										+ "�������\n");
					}
				}
			});
		}
		
		if (google_selected) {
			display.syncExec(new Runnable() {
				public void run() {
					MainWindow.imediateSearchBrowser.appendSearchInfo("�ȸ�������������");
				}
			});
			status = google.doSearch(rule, rule.getCnt());// �ùȸ�����
			display.asyncExec(new Runnable() {
				public void run() {
					if (status == 1) {
						MainWindow.setStatusLabel("�ȸ���������" + rule.getName()
								+ "��ʧ��!", SWTResourceManager
								.getImage("img/warning.gif"));
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("�ȸ�����ʧ�ܡ�\n");
					} else {
						addResults(google.results);
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("�ȸ�������"
										+ google.results.size() + "�������\n");
					}
				}
			});
		}
		
		if (sogou_selected) {
			display.syncExec(new Runnable() {
				public void run() {
					MainWindow.imediateSearchBrowser.appendSearchInfo("�ѹ�������������");
				}
			});
			status = sogou.doSearch(rule, rule.getCnt());// ���ѹ�����
			display.asyncExec(new Runnable() {
				public void run() {
					if (status == 1) {
						MainWindow.setStatusLabel("�ѹ���������" + rule.getName()
								+ "��ʧ��!", SWTResourceManager
								.getImage("img/warning.gif"));
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("�ѹ�����ʧ�ܡ�\n");
					} else {
						addResults(sogou.results);
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("�ѹ�������"
										+ sogou.results.size() + "�������\n");
					}
				}
			});
		}
		
		if (soso_selected) {
			display.syncExec(new Runnable() {
				public void run() {
					MainWindow.imediateSearchBrowser.appendSearchInfo("����������������");
				}
			});
			status = soso.doSearch(rule, rule.getCnt());// ����������
			display.asyncExec(new Runnable() {
				public void run() {
					if (status == 1) {
						MainWindow.setStatusLabel("������������" + rule.getName()
								+ "��ʧ��!", SWTResourceManager
								.getImage("img/warning.gif"));
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("��������ʧ�ܡ�\n");
					} else {
						addResults(soso.results);
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("����������" + soso.results.size()
										+ "�������\n");
					}
				}
			});
		}
		
		if (yahoo_selected) {
			display.syncExec(new Runnable() {
				public void run() {
					MainWindow.imediateSearchBrowser.appendSearchInfo("�Ż�������������");
				}
			});
			status = yahoo.doSearch(rule, rule.getCnt());// ���Ż�����
			display.asyncExec(new Runnable() {
				public void run() {
					if (status == 1) {
						MainWindow.setStatusLabel("�Ż���������" + rule.getName()
								+ "��ʧ��!", SWTResourceManager
								.getImage("img/warning.gif"));
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("�Ż�����ʧ�ܡ�\n");
					} else {
						addResults(yahoo.results);
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("�Ż�������"
										+ yahoo.results.size() + "�������\n");
					}
				}
			});
		}
		
		if (youdao_selected) {
			display.syncExec(new Runnable() {
				public void run() {
					MainWindow.imediateSearchBrowser.appendSearchInfo("�е�������������");
				}
			});
			status = youdao.doSearch(rule, rule.getCnt());// ���е�����
			display.asyncExec(new Runnable() {
				public void run() {
					if (status == 1) {
						MainWindow.setStatusLabel("�е���������" + rule.getName()
								+ "��ʧ��!", SWTResourceManager
								.getImage("img/warning.gif"));
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("�е�����ʧ�ܡ�\n");
					} else {
						addResults(youdao.results);
						MainWindow.imediateSearchBrowser
								.appendSearchInfo("�е�������"
										+ youdao.results.size() + "�������\n");
					}
				}
			});
		}
		//����
		searchResult = rs.calculateSensativity(searchResult);
		display.syncExec(new Runnable(){
			public void run(){
				MainWindow.imediateSearchBrowser.setResultCopy(searchResult);
				MainWindow.imediateSearchBrowser.appendSearchInfo("������ϣ�����" + searchResult.getCountOfResults() + "�������\n");
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
