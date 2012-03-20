package Filter;


import java.sql.Timestamp;
import java.util.Date;


/*
 * ������ҳ��������
 */

//��������ʱ���¹�ϣ��ķ��̣߳�ÿ��5���ӱ���һ�θ��º�Ĺ�ϣ��

public class SaveHashTable extends Thread{
	//BloomFilter bf;
	public boolean stopFlag = false;
	public void setStopFlag(boolean stopFlag){
		this.stopFlag = stopFlag;
	}
	//ֹͣ���¹�ϣ��
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
				//ÿ�α���ĵȴ�ʱ��Ϊ5����
				this.wait(300000);
				
				Timestamp nowStoreTime = new Timestamp(new Date().getTime());
				htp.saveHashTable(lastStoreTime,nowStoreTime);
				lastStoreTime = nowStoreTime;
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		htp.getConnDB().closeConnection();
		System.out.println("��ҳ���������ϣ������߳̽�����");
	}
}

