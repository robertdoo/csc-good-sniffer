package Filter;

import java.sql.Timestamp;
import java.util.Date;
/*
 * ������̳����
 */
public class SaveHashTable1 extends Thread{
	public boolean stopFlag = false;
	public void setStopFlag(boolean stopFlag){
		this.stopFlag = stopFlag;
	}
	//ֹͣ���¹�ϣ��
	public synchronized void stopThread(){
		this.setStopFlag(true);
		this.notify();
	}
	
	HashTableProcess1 htp;
	public SaveHashTable1(int length){
		htp = new HashTableProcess1(length);
	}
	public synchronized void run(){
		
		Timestamp lastStoreTime = new Timestamp(new Date().getTime());
		while(stopFlag==false){
			try{
				//ÿ�α���ĵȴ�ʱ��Ϊ5����
				this.wait(60000);
				
				Timestamp nowStoreTime = new Timestamp(new Date().getTime());
				htp.saveHashTable(lastStoreTime,nowStoreTime);
				lastStoreTime = nowStoreTime;
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		htp.getConnDB().closeConnection();
		System.out.println("��̳�������¹�ϣ���߳̽�����");
	}
}
