package spider;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import lucene.Index.IndexProcesser;
import Filter.BloomFilter;
import database.ConvertSpiderDataToDB;

/**
 * ��ҳ���������̳߳ظ�������������û���������е�����
 * 
 * @author Owner
 * 
 */
public class SubThreadPool {
	/*
	 * �̳߳�ͳһ����һ������̶߳����������ͣ�̵߳Ķ��� ���ݿ����������������
	 */
	public Vector<MainPageCrawler> vector;
	private Set<String> addresses;
	/**
	 * ���캯��
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
	 * ������ڵ�ַ����
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean process(String url) throws Exception {
		int i;
		if(addresses.contains(url))return false;
		// ���������ҳ������̴߳�����ڵ�ַ���������̴߳��ڹ���״̬����ȴ�һ���Ӻ��ٽ����ж�
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
