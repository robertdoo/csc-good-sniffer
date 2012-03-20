package Filter;


import java.sql.Timestamp;
import java.util.Date;


/*
 * 用于主页面监控爬虫
 */

//程序运行时更新哈希表的分线程，每个5分钟保存一次更新后的哈希表

public class SaveHashTable extends Thread{
	//BloomFilter bf;
	public boolean stopFlag = false;
	public void setStopFlag(boolean stopFlag){
		this.stopFlag = stopFlag;
	}
	//停止更新哈希表
	public synchronized void stopThread(){
		this.setStopFlag(true);
		this.notify();
	}
	
	HashTableProcess htp;
	public SaveHashTable(int length){
		htp = new HashTableProcess(length);
	}
	public synchronized void run(){
		
		Timestamp lastStoreTime = new Timestamp(new Date().getTime());
		while(stopFlag==false){
			try{
				//每次保存的等待时间为5分钟
				this.wait(300000);
				
				Timestamp nowStoreTime = new Timestamp(new Date().getTime());
				htp.saveHashTable(lastStoreTime,nowStoreTime);
				lastStoreTime = nowStoreTime;
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		htp.getConnDB().closeConnection();
		System.out.println("主页面监控爬虫哈希表更新线程结束！");
	}
}

