package spider;

import Filter.BloomFilter;
import database.EntryAddressProcess;

/**
 * 测试主页面监控爬虫的类
 * @author Owner
 *
 */
public class TestRealTimeCrawler extends Thread{

	/**
	 * @param args
	 */
	
		int THREAD_NUMBER;// 设置线程数目
		final int HashTableLength = 1000000;
		final static long crawlTime = 60000;
		ThreadPoolManager manager;
		EntryAddressProcess eap = new EntryAddressProcess();

		public void run() {
			try {

				boolean flag = false;

				Queue entryAddress; // 读入入口地址

				entryAddress = eap.getAllEntryAddress();
				THREAD_NUMBER = eap.getTableLength();
				BloomFilter bf = new BloomFilter(HashTableLength);// 创建过滤器

				manager = new ThreadPoolManager(THREAD_NUMBER, bf,
						HashTableLength);// 启动线程池

				CreateDir cd = new CreateDir();
				cd.createDir();

				String address;

				System.out.println(THREAD_NUMBER);

				// manager.process("http://bbs.51job.com/");
				while (!entryAddress.isQueueEmpty()) { // 循环爬取入口地址
					address = entryAddress.deQueue();
					flag = manager.process(address);
					while (!flag) {
						System.out.println("waiting....");
						Thread.sleep(60000);
						flag = manager.process(address);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void stopThread() {
			THREAD_NUMBER = eap.getTableLength();
			manager.stopThread(THREAD_NUMBER);
		}

		public static void main(String[] args) {
			TestRealTimeCrawler t = new TestRealTimeCrawler();
			t.start();
			/*
			 * try{ Thread.sleep(crawlTime); t.stopThread();
			 * }catch(InterruptedException e ){ e.printStackTrace(); }
			 */

		}

	}

