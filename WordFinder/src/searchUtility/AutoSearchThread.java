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
 * ʵ�ֶ�ʱ�������̣߳����������������
 */
public class AutoSearchThread extends Thread {
	private Display display = Display.getDefault();
	private long interval;
	private Rule rule;
	private String sensativity = "��Ԥ��";
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
	private LinkedList<Result> results = new LinkedList<Result>();  //��������������ܵĽ��
	private int status;

	private boolean runningFlag = false;
	private boolean stopFlag = false;// �߳̽�����־
	private boolean isRunning = false;//�߳���ͣ��־
	
	/*
	 * ���캯������ڲ���Ϊ�����������Ӧ�Ĺ���
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
		//Ԫ����ʱ�Ѿ�ȥ��
		for(Result r : newResults)
			results.add(r);
	}
	
	public void doSearch(){
		results.clear(); 
		display.asyncExec(new Runnable(){
			public void run(){
				MainWindow.setStatusLabel("�Զ���������" + rule.getName() + "�����ڽ��С���",
						SWTResourceManager.getImage("img/info.gif"));
			}
		});
				
		if (baidu_selected) {
			status = baidu.doSearch(rule, rule.getCnt());// �ðٶ�����
			if (status == 1) {
				display.asyncExec(new Runnable(){
					public void run(){
						MainWindow.setStatusLabel("�ٶ���������" + rule.getName() + "��ʧ��!",
								SWTResourceManager.getImage("img/warning.gif"));
					}
				});
			} else {
				addResults(baidu.results);
			}
		}
		
		if (bing_selected) {
			status = bing.doSearch(rule, rule.getCnt());// �ñ�Ӧ����
			if (status == 1) {
				display.asyncExec(new Runnable(){
					public void run(){
						MainWindow.setStatusLabel("��Ӧ��������" + rule.getName() + "��ʧ��!",
								SWTResourceManager.getImage("img/warning.gif"));
					}
				});
			} else {
				addResults(bing.results);
			}
		}
		
		if (google_selected) {
			status = google.doSearch(rule, rule.getCnt());// �ùȸ�����
			if (status == 1) {
				display.asyncExec(new Runnable(){
					public void run(){
						MainWindow.setStatusLabel("�ȸ���������" + rule.getName() + "��ʧ��!",
								SWTResourceManager.getImage("img/warning.gif"));
					}
				});
			} else {
				addResults(google.results);
			}
		}
		
		if (sogou_selected) {
			status = sogou.doSearch(rule, rule.getCnt());// ���ѹ�����
			if (status == 1) {
				display.asyncExec(new Runnable(){
					public void run(){
						MainWindow.setStatusLabel("�ѹ���������" + rule.getName() + "��ʧ��!",
								SWTResourceManager.getImage("img/warning.gif"));
					}
				});
			} else {
				addResults(sogou.results);
			}
		}
		
		if (soso_selected) {
			status = soso.doSearch(rule, rule.getCnt());// ����������
			if (status == 1) {
				display.asyncExec(new Runnable(){
					public void run(){
						MainWindow.setStatusLabel("������������" + rule.getName() + "��ʧ��!",
								SWTResourceManager.getImage("img/warning.gif"));
					}
				});
			} else {
				addResults(soso.results);
			}
		}
		
		if (yahoo_selected) {
			status = yahoo.doSearch(rule, rule.getCnt());// ���Ż�����
			if (status == 1) {
				display.asyncExec(new Runnable(){
					public void run(){
						MainWindow.setStatusLabel("�Ż���������" + rule.getName() + "��ʧ��!",
								SWTResourceManager.getImage("img/warning.gif"));
					}
				});
			} else {
				addResults(yahoo.results);
			}
		}
		
		if (youdao_selected) {
			status = youdao.doSearch(rule, rule.getCnt());// ���е�����
			if (status == 1) {
				display.asyncExec(new Runnable(){
					public void run(){
						MainWindow.setStatusLabel("�е���������" + rule.getName() + "��ʧ��!",
								SWTResourceManager.getImage("img/warning.gif"));
					}
				});
			} else {
				addResults(youdao.results);
			}
		}
		
		MainWindow.merp.addResultList(results);   //д�����ݿ�
		MainWindow.meIndexProcesser.updateIndexByResultList(results);  //��������
		display.asyncExec(new Runnable(){
			public void run(){		
				MainWindow.setStatusLabel("�Զ���������" + rule.getName()
						+ "����ɣ�����" + results.size() + "�������",
						SWTResourceManager.getImage("img/info.gif"));
			}
		});	
	}
	
	public synchronized void run() {
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