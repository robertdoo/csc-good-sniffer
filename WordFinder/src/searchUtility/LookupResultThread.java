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

public class LookupResultThread extends Thread {

	private Display display = Display.getDefault();
	private CBrowserComposite browser;
	private SearchResult searchResult;
	private String ruleName, beginTime, endTime;
	private Rule rule;
	
	private ResultSensativity rs = new ResultSensativity();
	
	public LookupResultThread(String ruleName, String beginTime, String endTime) {
		this.ruleName = ruleName;
		this.beginTime = beginTime;
		this.endTime = endTime;
	}
	
	public void run() {
		rule = MainWindow.rp.getRuleByName(ruleName);
		if(rule == null)return;
		/*
		if(rule.isRelative()){//ʹ�ù���������
			rule.setWords(MainWindow.swp.replaceSynonym(rule.getWords()));
			rule.setUnwantedWords(MainWindow.swp.replaceSynonym(rule.getUnwantedWords()));
			rule.setSnippet(MainWindow.swp.replaceSynonym(rule.getSnippet()));
		}
		*/
		if(rule.isRelative()){//ʹ�ù���������
			rule.setWords(rule.getWords() + " " + rule.getSysnonymWords());
		}
		display.asyncExec(new Runnable(){
			public void run(){
				browser = null;
				CTabItem[] items = MainWindow.lookupResultFolder.getItems();
				for(int i=0;i<items.length;i++)
					if(items[i].getText().equals("Ԫ��������" + rule.getName())){
						browser = (CBrowserComposite)items[i].getControl();
						MainWindow.lookupResultFolder.setSelection(items[i]);
						break;
					}
				if(browser == null){
					CTabItem newItem = new CTabItem(MainWindow.lookupResultFolder, SWT.CLOSE);
					newItem.setText("Ԫ��������" + rule.getName());
					newItem.setToolTipText("Ԫ��������" + rule.getName() + "��ʱ�����䣺"
							+ beginTime + "��" + endTime);
					browser =
						new CBrowserComposite(MainWindow.lookupResultFolder, 1, null);
					newItem.setControl(browser);
					MainWindow.lookupResultFolder.setSelection(newItem);
				}
				
				browser.outWait();
				browser.setSearchInfo(getCurrentTime() + "\n");
				browser.appendSearchInfo("��������" + rule.getName() + "\n");
				browser.appendSearchInfo("�ؼ��ʣ�" + rule.getWords() + "\n");
				browser.appendSearchInfo("ʱ�����䣺" + beginTime + "��" + endTime + "\n");
			}
		});
		searchResult = MainWindow.metaEngineSearcher.search(rule, beginTime, endTime);
		searchResult = rs.calculateSensativity(searchResult);
		display.asyncExec(new Runnable(){
			public void run(){
				browser.setResultCopy(searchResult);
				browser.appendSearchInfo("������ϣ��õ�����"
						+ (searchResult==null?0:searchResult.getCountOfResults())
						+ "�������");
			}
		});
	}
	
	public String getCurrentTime(){
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	}
}