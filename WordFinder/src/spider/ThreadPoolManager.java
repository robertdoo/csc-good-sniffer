package spider;

import java.util.Vector;

import lucene.Index.IndexProcesser;
import Filter.BloomFilter;
import database.ConvertSpiderDataToDB;

/**
 * ��ҳ���������̳߳�
 * 
 * @author Owner
 * 
 */
public class ThreadPoolManager extends Thread {
	/*
	 * �̳߳�ͳһ����һ������̶߳����������ͣ�̵߳Ķ��� ���ݿ����������������
	 */
	private Vector<MainPageCrawler> vector;
	private StopThread st;
	private IndexProcesser indexprocesser;
	private ConvertSpiderDataToDB saveToDB;
	private SubThreadPool subThreadPool;
	private ShowMessage showMessage;

	/**
	 * ���캯��
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
	 * ������ڵ�ַ����
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean process(String url) throws Exception {
		int i;
		// ���������ҳ������̴߳�����ڵ�ַ���������̴߳��ڹ���״̬����ȴ�һ���Ӻ��ٽ����ж�
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
	 * ��ֹ�̵߳ĺ���
	 * 
	 * @param ThreadCounter
	 * @return
	 */
	public synchronized boolean stopThread(int ThreadCounter) {
		System.out.println("���ڿ�ʼֹͣ��ҳ��������!");
		try {
			st.setMainPageCrawlerFlag(true);
			boolean exitFlag = false;
			int sleepTime = 0;

			/*
			 * �����ѭ�������ж������߳��Ƿ���ֹͣ������������쳣�����߳���Ŀ�϶�ʱ
			 * ������Ӧֹͣ�������������������������30������û������������ֹͣ������ֹͣ ����ת����̨����
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
