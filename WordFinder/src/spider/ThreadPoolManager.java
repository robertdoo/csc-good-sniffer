package spider;

import java.util.Vector;

import lucene.Index.IndexProcesser;
import Filter.BloomFilter;
import database.ConvertSpiderDataToDB;

/**
 * 主页面监控爬虫线程池
 * 
 * @author Owner
 * 
 */
public class ThreadPoolManager extends Thread {
	/*
	 * 线程池统一管理一个存放线程对象的容器、停线程的对象、 数据库操作对象、索引对象
	 */
	private Vector<MainPageCrawler> vector;
	private StopThread st;
	private IndexProcesser indexprocesser;
	private ConvertSpiderDataToDB saveToDB;
	private SubThreadPool subThreadPool;
	private ShowMessage showMessage;

	/**
	 * 构造函数
	 * 
	 * @param threadCount
	 * @param bf
	 * @param HashTableLength
	 */
	public ThreadPoolManager(int threadCount, BloomFilter bf,
			int HashTableLength) {
		st = new StopThread();
		indexprocesser = new IndexProcesser(SpiderConf.MAINPAGE_CRAWLER);
		saveToDB = new ConvertSpiderDataToDB("realtimeData");
		System.out.println("Starting thread pool...");
		vector = new Vector<MainPageCrawler>();
		for (int i = 1; i <= threadCount; i++) {
			MainPageCrawler thread = new MainPageCrawler(i, bf, st,
					indexprocesser, saveToDB,SpiderConf.ThreadPoolManager);
			thread.setPriority(Thread.NORM_PRIORITY);
			vector.addElement(thread);
			thread.start();
		}
		
		subThreadPool = new SubThreadPool(bf,HashTableLength,st,indexprocesser,saveToDB);
		showMessage = new ShowMessage(vector, st,subThreadPool);
		showMessage.start();
	}

	/**
	 * 处理入口地址函数
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean process(String url) throws Exception {
		int i;
		// 从容器中找出空闲线程处理入口地址，若所有线程处于工作状态，则等待一分钟后再进行判断
		for (i = 0; i < vector.size(); i++) {
			MainPageCrawler currentThread = (MainPageCrawler) vector
					.elementAt(i);
			if (!currentThread.getIsRunning()) {
				System.out.println("Thread " + (i + 1) + " is processing: "
						+ url);
				currentThread.setUrl(url);
				currentThread.iniFilter();
				currentThread.setIsRunning(true);
				return true;
			}
		}
		if (i == vector.size()) {
			System.out.println("pool is full,try in another time");
			return false;
		}
		return false;
	}

	/**
	 * 终止线程的函数
	 * 
	 * @param ThreadCounter
	 * @return
	 */
	public synchronized boolean stopThread(int ThreadCounter) {
		System.out.println("现在开始停止主页面监控爬虫!");
		try {
			st.setMainPageCrawlerFlag(true);
			boolean exitFlag = false;
			int sleepTime = 0;

			/*
			 * 下面的循环用来判断爬虫线程是否都已停止，当网络出现异常或者线程数目较多时
			 * 爬虫相应停止的命令较慢，在这种情况下如果30秒钟内没能让所有爬虫停止下来，停止 工作转到后台操作
			 */
			while (!exitFlag && sleepTime <= 30) {
				exitFlag = true;
				for (int i = 0; i < ThreadCounter; i++) {
					MainPageCrawler thread = (MainPageCrawler) vector.get(i);
					if (thread.isAlive()) {
						exitFlag = false;
					}
					System.err.println(thread.getName() + " "
							+ thread.toString() + " " + thread.getId() + " "
							+ thread.isAlive());
				}
				Thread.sleep(5000);
				sleepTime += 5;
			}

			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return true;
		}
	}

}
