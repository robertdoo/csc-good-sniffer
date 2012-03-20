package searchUtility;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Display;

import ui.MainWindow.CBrowserComposite;
import ui.MainWindow.MainWindow;
import dataStruct.Rule;
import dataStruct.SearchResult;

public class LookupWarningResultThread extends Thread {
	private Display display = Display.getDefault();
	private CBrowserComposite browser;
	private SearchResult searchResult;
	private String ruleName, beginTime, endTime;
	private Rule rule;
	
	public LookupWarningResultThread(String ruleName, String beginTime, String endTime){
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
		display.syncExec(new Runnable(){
			public void run(){
				browser = null;
				CTabItem[] items = MainWindow.lookupWarningResultFolder.getItems();
				for(int i=0;i<items.length;i++)
					if(items[i].getText().equals("预警信息查询规则：" + rule.getName())){
						browser = (CBrowserComposite)items[i].getControl();
						break;
					}
				if(browser == null){
					CTabItem newItem = new CTabItem(MainWindow.lookupWarningResultFolder, SWT.CLOSE);
					newItem.setText("预警信息查询规则：" + rule.getName());
					newItem.setToolTipText("预警信息查询规则：" + rule.getName() + "，时间区间："
							+ beginTime + "到" + endTime);
					browser =
						new CBrowserComposite(MainWindow.lookupWarningResultFolder, 2, null);
					newItem.setControl(browser);
					MainWindow.lookupWarningResultFolder.setSelection(newItem);
				}
				
				browser.outWait();
				browser.setSearchInfo(getCurrentTime() + "\n");
				browser.appendSearchInfo("搜索规则：" + rule.getName() + "\n");
				browser.appendSearchInfo("关键词：" + rule.getWords() + "\n");
				browser.appendSearchInfo("时间区间：" + beginTime + "到" + endTime + "\n");
			}
		});
		searchResult = MainWindow.monitorSearcher.search(rule, beginTime, endTime);
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
