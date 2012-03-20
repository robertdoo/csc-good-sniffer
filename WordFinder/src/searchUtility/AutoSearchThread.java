package searchUtility;

import java.util.LinkedList;

import org.eclipse.swt.widgets.Display;

import com.swtdesigner.SWTResourceManager;

import dataStruct.Result;
import dataStruct.Rule;

import ui.MainWindow.MainWindow;
import metaSearchEngine.BaiduExtract;
import metaSearchEngine.BingExtract;
import metaSearchEngine.GoogleExtract;
import metaSearchEngine.SogouExtract;
import metaSearchEngine.SosoExtract;
import metaSearchEngine.YahooExtract;
import metaSearchEngine.YoudaoExtract;

/*
 * 实现定时搜索的线程，可以设置搜索间隔
 */
public class AutoSearchThread extends Thread {
	private Display display = Display.getDefault();
	private long interval;
	private Rule rule;
	private String sensativity = "不预警";
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
	private LinkedList<Result> results = new LinkedList<Result>();  //所有搜索引擎汇总的结果
	private int status;

	private boolean runningFlag = false;
	private boolean stopFlag = false;// 线程结束标志
	private boolean isRunning = false;//线程暂停标志
	
	/*
	 * 构造函数，入口参数为与搜索任务对应的规则
	 */
	public AutoSearchThread(Rule rule, long interval,
			            boolean baidu_selected,
			            boolean bing_selected,
			            boolean google_selected,
			            boolean sogou_selected,
			            boolean soso_selected,
			            boolean yahoo_selected,
			            boolean youdao_selected) 
	{
		super();
		this.rule = rule;
		this.interval = interval;
		this.baidu_selected = baidu_selected;
		this.bing_selected = bing_selected;
		this.google_selected = google_selected;
		this.sogou_selected = sogou_selected;
		this.soso_selected = soso_selected;
		this.yahoo_selected = yahoo_selected;
		this.youdao_selected = youdao_selected;
		
		if(baidu_selected)baidu = new BaiduExtract(true);
		if(bing_selected)bing = new BingExtract(true);
		if(google_selected)google = new GoogleExtract(true);
		if(sogou_selected)sogou = new SogouExtract(true);
		if(soso_selected)soso = new SosoExtract(true);
		if(yahoo_selected)yahoo = new YahooExtract(true);
		if(youdao_selected)youdao = new YoudaoExtract(true);
	}
	
	public Rule getRule() {
		return rule;
	}

	public String getSensativity() {
		return sensativity;
	}

	public boolean isBaidu_selected() {
		return baidu_selected;
	}

	public boolean isBing_selected() {
		return bing_selected;
	}

	public boolean isGoogle_selected() {
		return google_selected;
	}

	public boolean isSogou_selected() {
		return sogou_selected;
	}

	public boolean isSoso_selected() {
		return soso_selected;
	}

	public boolean isYahoo_selected() {
		return yahoo_selected;
	}

	public boolean isYoudao_selected() {
		return youdao_selected;
	}
	
	private void addResults(LinkedList<Result> newResults) {
		//元搜索时已经去重
		for(Result r : newResults)
			results.add(r);
	}
	
	public void doSearch(){
		results.clear(); 
		display.asyncExec(new Runnable(){
			public void run(){
				MainWindow.setStatusLabel("自动搜索：”" + rule.getName() + "“正在进行……",
						SWTResourceManager.getImage("img/info.gif"));
			}
		});
				
		if (baidu_selected) {
			status = baidu.doSearch(rule, rule.getCnt());// 用百度搜索
			if (status == 1) {
				display.asyncExec(new Runnable(){
					public void run(){
						MainWindow.setStatusLabel("百度搜索规则“" + rule.getName() + "”失败!",
								SWTResourceManager.getImage("img/warning.gif"));
					}
				});
			} else {
				addResults(baidu.results);
			}
		}
		
		if (bing_selected) {
			status = bing.doSearch(rule, rule.getCnt());// 用必应搜索
			if (status == 1) {
				display.asyncExec(new Runnable(){
					public void run(){
						MainWindow.setStatusLabel("必应搜索规则“" + rule.getName() + "”失败!",
								SWTResourceManager.getImage("img/warning.gif"));
					}
				});
			} else {
				addResults(bing.results);
			}
		}
		
		if (google_selected) {
			status = google.doSearch(rule, rule.getCnt());// 用谷歌搜索
			if (status == 1) {
				display.asyncExec(new Runnable(){
					public void run(){
						MainWindow.setStatusLabel("谷歌搜索规则“" + rule.getName() + "”失败!",
								SWTResourceManager.getImage("img/warning.gif"));
					}
				});
			} else {
				addResults(google.results);
			}
		}
		
		if (sogou_selected) {
			status = sogou.doSearch(rule, rule.getCnt());// 用搜狗搜索
			if (status == 1) {
				display.asyncExec(new Runnable(){
					public void run(){
						MainWindow.setStatusLabel("搜狗搜索规则“" + rule.getName() + "”失败!",
								SWTResourceManager.getImage("img/warning.gif"));
					}
				});
			} else {
				addResults(sogou.results);
			}
		}
		
		if (soso_selected) {
			status = soso.doSearch(rule, rule.getCnt());// 用搜搜搜索
			if (status == 1) {
				display.asyncExec(new Runnable(){
					public void run(){
						MainWindow.setStatusLabel("搜搜搜索规则“" + rule.getName() + "”失败!",
								SWTResourceManager.getImage("img/warning.gif"));
					}
				});
			} else {
				addResults(soso.results);
			}
		}
		
		if (yahoo_selected) {
			status = yahoo.doSearch(rule, rule.getCnt());// 用雅虎搜索
			if (status == 1) {
				display.asyncExec(new Runnable(){
					public void run(){
						MainWindow.setStatusLabel("雅虎搜索规则“" + rule.getName() + "”失败!",
								SWTResourceManager.getImage("img/warning.gif"));
					}
				});
			} else {
				addResults(yahoo.results);
			}
		}
		
		if (youdao_selected) {
			status = youdao.doSearch(rule, rule.getCnt());// 用有道搜索
			if (status == 1) {
				display.asyncExec(new Runnable(){
					public void run(){
						MainWindow.setStatusLabel("有道搜索规则“" + rule.getName() + "”失败!",
								SWTResourceManager.getImage("img/warning.gif"));
					}
				});
			} else {
				addResults(youdao.results);
			}
		}
		
		MainWindow.merp.addResultList(results);   //写进数据库
		MainWindow.meIndexProcesser.updateIndexByResultList(results);  //更新索引
		display.asyncExec(new Runnable(){
			public void run(){		
				MainWindow.setStatusLabel("自动搜索：”" + rule.getName()
						+ "“完成，更新" + results.size() + "条结果。",
						SWTResourceManager.getImage("img/info.gif"));
			}
		});	
	}
	
	public synchronized void run() {
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
					wait(interval);
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