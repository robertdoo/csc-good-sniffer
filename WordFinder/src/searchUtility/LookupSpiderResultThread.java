package searchUtility;

import java.text.SimpleDateFormat;
import java.util.Date;

import lucene.Search.ResultSensativity;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Display;

import ui.MainWindow.CBrowserComposite;
import ui.MainWindow.MainWindow;
import dataStruct.Rule;
import dataStruct.SearchResult;

public class LookupSpiderResultThread extends Thread {

	private Display display = Display.getDefault();
	private CBrowserComposite browser;
	private SearchResult searchResult;
	private String ruleName, beginTime, endTime;
	private Rule rule;
	
	private ResultSensativity rs = new ResultSensativity();
	
	public LookupSpiderResultThread(String ruleName, String beginTime, String endTime){
		this.ruleName = ruleName;
		this.beginTime = beginTime;
		this.endTime = endTime;
	}
	
	public void run() {
		rule = MainWindow.rp.getRuleByName(ruleName);
		if(rule == null)return;
		/*
		if(rule.isRelative()){//使用关联词搜索
			rule.setWords(MainWindow.swp.replaceSynonym(rule.getWords()));
			rule.setUnwantedWords(MainWindow.swp.replaceSynonym(rule.getUnwantedWords()));
			rule.setSnippet(MainWindow.swp.replaceSynonym(rule.getSnippet()));
		}
		*/
		if(rule.isRelative()){//使用关联词搜索
			rule.setWords(rule.getWords() + " " + rule.getSysnonymWords());
		}
		display.asyncExec(new Runnable(){
			public void run(){
				browser = null;
				CTabItem[] items = MainWindow.lookupSpiderResultFolder.getItems();
				for(int i=0;i<items.length;i++)
					if(items[i].getText().equals("论坛搜索规则：" + rule.getName())){
						browser = (CBrowserComposite)items[i].getControl();
						MainWindow.lookupSpiderResultFolder.setSelection(items[i]);
						break;
					}
				if(browser == null){
					CTabItem newItem = new CTabItem(MainWindow.lookupSpiderResultFolder, SWT.CLOSE);
					newItem.setText("论坛搜索规则：" + rule.getName());
					newItem.setToolTipText("论坛搜索规则：" + rule.getName() + "，时间区间："
							+ beginTime + "到" + endTime);
					browser =
						new CBrowserComposite(MainWindow.lookupSpiderResultFolder, 2, null);
					newItem.setControl(browser);
					MainWindow.lookupSpiderResultFolder.setSelection(newItem);
				}
				
				browser.outWait();
				browser.setSearchInfo(getCurrentTime() + "\n");
				browser.appendSearchInfo("搜索规则：" + rule.getName() + "\n");
				browser.appendSearchInfo("关键词：" + rule.getWords() + "\n");
				browser.appendSearchInfo("时间区间：" + beginTime + "到" + endTime + "\n");
			}
		});
		searchResult = MainWindow.spiderSearcher.search(rule, beginTime, endTime);
		searchResult = rs.calculateSensativity(searchResult);
		display.asyncExec(new Runnable(){
			public void run(){
				browser.setResultCopy(searchResult);
				browser.appendSearchInfo("查找完毕！得到共计"
						+ (searchResult==null?0:searchResult.getCountOfResults())
						+ "条结果。");
			}
		});
	}
	
	public String getCurrentTime(){
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	}
}
