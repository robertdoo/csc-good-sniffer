package spider;

import java.util.Vector;

import database.ConvertSpiderDataToDB;

import lucene.Index.IndexProcesser;

import Filter.BloomFilter1;
/**
 * 论坛搜索线程池
 */
public class ThreadPoolManager1 {
	/*
	 * 线程池统一管理一个存放线程对象的容器、停线程的对象、
	 * 数据库操作对象、索引对象
	 */
	private Vector<CrawlerThread> vector;
	private StopThread st;
	private IndexProcesser indexprocesser;
	private ConvertSpiderDataToDB saveToDB;
	
	/**
	 * 构造函数
	 * @param threadCount
	 * @param bf
	 * @param HashTableLength
	 */
	public ThreadPoolManager1(int threadCount, BloomFilter1 bf,
			int HashTableLength) {
		st = new StopThread();
		indexprocesser = new IndexProcesser(SpiderConf.DEEP_CRAWLER);
		saveToDB = new ConvertSpiderDataToDB("test");
		System.out.println("Starting thread pool...");
		vector = new Vector<CrawlerThread>();
		for (int i = 1; i <= threadCount; i++) {
			CrawlerThread thread = new CrawlerThread(i, bf, st,indexprocesser,saveToDB);
			thread.setPriority(Thread.MAX_PRIORITY);
			vector.addElement(thread);
			thread.start();
		}
	}

	/**
	 *  处理入口地址函数
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean process(String url) throws Exception {
		int i;
		// 从容器中找出空闲线程处理入口地址，若所有线程处于工作状态，则等待一分钟后再进行判断
		for (i = 0; i < vector.size(); i++) {
			CrawlerThread currentThread = (CrawlerThread) vector
					.elementAt(i);
			if (!currentThread.isRunning()) {
				System.out.println("Thread " + (i + 1) + " is processing: "
						+ url);
				currentThread.setUrl(url);
				currentThread.iniFilter();
				currentThread.setRunning(true);
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
	 *  终止线程的函数
	 * @param ThreadCounter
	 * @return
	 */
	public boolean stopThread(int ThreadCounter) {
		System.out.println("现在开始停止论坛爬虫!");
		try {
			st.setCrawlerFlag(true);
			boolean exitFlag = false;
			int sleepTime = 0;
			
			/*
			 * 下面的循环用来判断爬虫线程是否都已停止，当网络出现异常或者线程数目较多时
			 * 爬虫相应停止的命令较慢，在这种情况下如果30秒钟内没能让所有爬虫停止下来，停止 工作转到后台操作
			 */
			while (!exitFlag && sleepTime <= 30) {
				exitFlag = true;
				for (int i = 0; i < ThreadCounter; i++) {
					CrawlerThread thread = (CrawlerThread) vector.get(i);
					if (thread.isAlive()) {
						exitFlag = false;
					}
					System.err.println(thread.getName() + " "
							+ thread.toString() + " " + thread.getId()+" "+thread.isAlive());
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
