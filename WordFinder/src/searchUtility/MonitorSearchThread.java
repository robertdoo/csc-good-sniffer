package searchUtility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import lucene.Search.PickAbstract;
import lucene.Search.SearchProcesser;

import org.eclipse.swt.widgets.Display;

import com.swtdesigner.SWTResourceManager;

import dataStruct.Result;
import dataStruct.Rule;
import dataStruct.WarningResult;
import database.LogProcess;

import ui.MonitorWindow.MonitorWindow;

import spider.SpiderConf;

public class MonitorSearchThread extends Thread {

	private Display display = Display.getDefault();
	private MonitorWindow win;
	private Rule rule;
	private String sensativity;
	private LinkedList<WarningResult> warnResultList;
	private SearchProcesser sp = new SearchProcesser(SpiderConf.MAINPAGE_CRAWLER);
	private PickAbstract pa = new PickAbstract();
	private String currentTime;

	private boolean runningFlag = false;
	private boolean stopFlag = false;// �߳̽�����־
	private boolean isRunning = false;//�߳���ͣ��־

	public MonitorSearchThread(MonitorWindow win, Rule rule, String sensativity) {

		this.win = win;
		this.rule = rule;
		this.sensativity = sensativity;
		
		warnResultList = new LinkedList<WarningResult>();
	}

	public Rule getRule() {
		return rule;
	}

	public String getSensativity() {
		return sensativity;
	}

	public LinkedList<WarningResult> getWarnResultList() {
		return warnResultList;
	}

	public void doSearch() {			
		display.asyncExec(new Runnable() {
			public void run() {
				win.setStatusLabel("����" + rule.getName() + "���ڼ�������",
						SWTResourceManager.getImage("img/info.gif"));
			}
		});
		currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		sp.monitorSearch(rule);   
		warnResultList.clear();
		for (Result newR : sp.monitorNewResults)
			warnResultList.add(
					new WarningResult(currentTime,
							newR.getTitle(),
							newR.getUrl(),
							pa.pickContentAbstract(rule.getWords(),newR.getContent()),
							rule.getName(),
							rule.getWords(),
							newR.getPageMirrorPath(),
							true)
					);
		display.asyncExec(new Runnable() {
			public void run() {	
				if (sp.monitorNewResults.size() != 0) {           // ˢ��ҳ��
					win.addNewWarning(warnResultList);
				}		
				win.setStatusLabel("������ϣ�",
						SWTResourceManager.getImage("img/info.gif"));
			}
		});

		String user = win.user;
		String operate = "ʵʱ���";
		String detail = "ʹ�ù���Ϊ�� " + rule.getName() + ",����ȫ���־䣺"
				+ rule.getWords() + ",���������־䣺" + rule.getSnippet()
				+ ",�������־䣺" + rule.getUnwantedWords();
		LogProcess sl = new LogProcess();
		sl.saveLog(user, operate, detail);
		
	}

	public synchronized void run() {
		sp.monitorSearch(rule);
		if(rule.isRelative()){//ʹ�ù���������
			rule.setWords(rule.getWords() + rule.getSysnonymWords());
		}
		
		while (!stopFlag) {
			try {
				if(!isRunning){
					wait();
				}
				if(!stopFlag){
					doSearch();
					wait(30000);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public synchronized void setRunningFlag(boolean flag) {
		this.runningFlag = flag;
	}

	public boolean getRunningFlag() {
		return this.runningFlag;
	}

	public synchronized void setStopFlag(boolean flag) {
		this.stopFlag = flag;
		if (flag) {
			notifyAll();
		}
	}

	public boolean getStopFlag() {
		return stopFlag;
	}

	public synchronized void setIsRunning(boolean flag) {
		this.isRunning = flag;
		if(flag)
			notify();
	}

	public boolean getIsRunning() {
		return this.isRunning;
	}

}
