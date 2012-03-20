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
	private boolean stopFlag = false;// 线程结束标志
	private boolean isRunning = false;//线程暂停标志

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
				win.setStatusLabel("任务" + rule.getName() + "正在检索……",
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
				if (sp.monitorNewResults.size() != 0) {           // 刷新页面
					win.addNewWarning(warnResultList);
				}		
				win.setStatusLabel("检索完毕！",
						SWTResourceManager.getImage("img/info.gif"));
			}
		});

		String user = win.user;
		String operate = "实时监控";
		String detail = "使用规则为： " + rule.getName() + ",包含全部字句："
				+ rule.getWords() + ",包含完整字句：" + rule.getSnippet()
				+ ",不包含字句：" + rule.getUnwantedWords();
		LogProcess sl = new LogProcess();
		sl.saveLog(user, operate, detail);
		
	}

	public synchronized void run() {
		sp.monitorSearch(rule);
		if(rule.isRelative()){//使用关联词搜索
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
