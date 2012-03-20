package spider;

import java.util.Vector;

/**
 * 显示爬虫状态及调整爬虫
 * 
 * @author Owner
 * 
 */
public class ShowMessage extends Thread {
	private Vector<MainPageCrawler> vector;
	private StopThread stopThread;
	private SubThreadPool subThreadPool;
	private int[] timeDistance;
	private long[] preRound;
	private int[] subTimeDistance;
	private long[] subPreRound;

	public ShowMessage(Vector<MainPageCrawler> vector, StopThread stopThread,
			SubThreadPool subThreadPool) {
		this.vector = vector;
		this.stopThread = stopThread;
		this.subThreadPool = subThreadPool;

		timeDistance = new int[vector.size()];
		preRound = new long[vector.size()];
		subTimeDistance = new int[subThreadPool.vector.size()];
		subPreRound = new long[subThreadPool.vector.size()];

	}

	public void run() {
		long[] rounds = new long[vector.size()];
		long min;
		long total;
		long average;
		int count;
		long[] subRounds = new long[subThreadPool.vector.size()];
		long subMin;
		long subTotal;
		long subAverage;
		while (!stopThread.isMainPageCrawlerFlag()) {
			try {
				total = 0;
				min = Long.MAX_VALUE;
				for (int i = 0; i < vector.size(); i++) {
					MainPageCrawler thread = (MainPageCrawler) vector.get(i);
					rounds[i] = thread.getRound();
					if (rounds[i] < min) {
						min = rounds[i];
					}

					if (rounds[i] == preRound[i])
						timeDistance[i]++;
					else
						timeDistance[i] = 0;
					preRound[i] = rounds[i];

					total += rounds[i];
					System.err.println("ThreadPoolManager中线程 " + (i + 1)
							+ "循环 " + rounds[i] + " 次");
				}

				average = total / vector.size();

				System.err.println("ThreadPoolManager中最少的经过了" + min
						+ "次循环，平均经过" + average + "次循环");
				if (min == 0)
					min = 1;
				if (average > min * 3) {
					for (int i = 0; i < vector.size(); i++) {
						if (rounds[i] * 3 < average || timeDistance[i] >= 10) {
							try {
								subThreadPool.process(vector.get(i)
										.getEntryUrl());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}

				count = 0;
				subTotal = 0;
				subMin = Long.MAX_VALUE;
				for (int i = 0; i < subThreadPool.vector.size(); i++) {
					MainPageCrawler thread = (MainPageCrawler) subThreadPool.vector
							.get(i);
					if (thread.getIsRunning() == true) {
						count++;
						subRounds[i] = thread.getRound();
						if (subRounds[i] < subMin) {
							subMin = subRounds[i];
						}

						if (subRounds[i] == subPreRound[i]) {
							subTimeDistance[i]++;
							if (subTimeDistance[i] >= 5) {
								System.err.println("监控链接"
										+ thread.getEntryUrl() + "失败，将重新尝试！");
								thread.setIsRunningFalse();
								subThreadPool.removeAddress(thread
										.getEntryUrl());
								subTimeDistance[i] = 0;
								thread.setRound(0);
							}
						} else {
							subTimeDistance[i] = 0;
						}
						subPreRound[i] = subRounds[i];

						subTotal += subRounds[i];
						System.err.println("SbuThreadPool内线程 " + (i+1) + "循环 "
								+ subRounds[i] + " 次");
					}
				}

				if (count > 0)
					subAverage = subTotal / count;
				else {
					subAverage = 0;
					subMin = 0;
				}

				System.err.println("SbuThreadPool 中有" + count + "个线程被调用");
				System.err.println("SbuThreadPool 中最少的经过了" + subMin
						+ "次循环，平均经过" + subAverage + "次循环");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.err.println("The thread of showMessage has stopped!");

	}

}
