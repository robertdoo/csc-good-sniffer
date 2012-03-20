package spider;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import lucene.Index.IndexProcesser;
import Filter.BloomFilter;
import database.ConvertSpiderDataToDB;

/**
 * 主页面监控爬虫线程池副本，用来处理没有正常运行的爬虫
 * 
 * @author Owner
 * 
 */
public class SubThreadPool {
	/*
	 * 线程池统一管理一个存放线程对象的容器、停线程的对象、 数据库操作对象、索引对象
	 */
	public Vector<MainPageCrawler> vector;
	private Set<String> addresses;
	/**
	 * 构造函数
	 * 
	 * @param threadCount
	 * @param bf
	 * @param HashTableLength
	 */
	public SubThreadPool(BloomFilter bf, int HashTableLength, StopThread st,
			IndexProcesser indexprocesser, ConvertSpiderDataToDB saveToDB) {
		System.out.println("Starting sub thread pool...");
		vector = new Vector<MainPageCrawler>();
		for (int i = 1; i <= 10; i++) {
			MainPageCrawler thread = new MainPageCrawler(i, bf, st,
					indexprocesser, saveToDB,SpiderConf.SubThreadPool);
			thread.setPriority(Thread.MAX_PRIORITY);
			vector.addElement(thread);
			thread.start();
		}
		
		addresses = new HashSet<String>();
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
		if(addresses.contains(url))return false;
		// 从容器中找出空闲线程处理入口地址，若所有线程处于工作状态，则等待一分钟后再进行判断
		for (i = 0; i < vector.size(); i++) {
			MainPageCrawler currentThread = (MainPageCrawler) vector
					.elementAt(i);
			if (!currentThread.getIsRunning()) {
				System.out.println("Thread " + (i + 1) + " in SubThreadPool is processing: "
						+ url);
				addresses.add(url);
				currentThread.setUrl(url);
				currentThread.iniFilter();
				currentThread.setRound(1);
				currentThread.setIsRunningTrue();
			
				return true;
			}
		}
		if (i == vector.size()) {
			System.out.println("pool is full,try in another time");
			return false;
		}
		return false;
	}
	
	public void removeAddress(String url){
		if(addresses.contains(url))
			addresses.remove(url);
	}
}
