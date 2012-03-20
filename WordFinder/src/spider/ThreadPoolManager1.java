package spider;

import java.util.Vector;

import database.ConvertSpiderDataToDB;

import lucene.Index.IndexProcesser;

import Filter.BloomFilter1;
/**
 * ��̳�����̳߳�
 */
public class ThreadPoolManager1 {
	/*
	 * �̳߳�ͳһ����һ������̶߳����������ͣ�̵߳Ķ���
	 * ���ݿ����������������
	 */
	private Vector<CrawlerThread> vector;
	private StopThread st;
	private IndexProcesser indexprocesser;
	private ConvertSpiderDataToDB saveToDB;
	
	/**
	 * ���캯��
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
	 *  ������ڵ�ַ����
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean process(String url) throws Exception {
		int i;
		// ���������ҳ������̴߳�����ڵ�ַ���������̴߳��ڹ���״̬����ȴ�һ���Ӻ��ٽ����ж�
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
	 *  ��ֹ�̵߳ĺ���
	 * @param ThreadCounter
	 * @return
	 */
	public boolean stopThread(int ThreadCounter) {
		System.out.println("���ڿ�ʼֹͣ��̳����!");
		try {
			st.setCrawlerFlag(true);
			boolean exitFlag = false;
			int sleepTime = 0;
			
			/*
			 * �����ѭ�������ж������߳��Ƿ���ֹͣ������������쳣�����߳���Ŀ�϶�ʱ
			 * ������Ӧֹͣ�������������������������30������û������������ֹͣ������ֹͣ ����ת����̨����
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
