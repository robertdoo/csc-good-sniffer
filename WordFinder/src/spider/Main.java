package spider;



import Filter.BloomFilter1;
import database.EntryAddressProcess;


/**
 * ������̳�������
 * @author Owner
 *
 */
public class Main extends Thread{
	int THREAD_NUMBER;//�����߳���Ŀ
	final int HashTableLength = 1000000;
	final static long crawlTime = 60000;
	ThreadPoolManager1 manager;
	EntryAddressProcess eap = new EntryAddressProcess();
	
	
	public  void run() {
		try{
			
			boolean flag = false;
			
			Queue entryAddress;   //������ڵ�ַ
			
			entryAddress = eap.getAllEntryAddress();
			THREAD_NUMBER = eap.getTableLength();
			BloomFilter1 bf = new BloomFilter1(HashTableLength);//����������
			
			manager = new ThreadPoolManager1(THREAD_NUMBER,bf,HashTableLength);//�����̳߳�
			
			CreateDir cd = new CreateDir();
			cd.createDir();
			
			String address;
			
			System.out.println(THREAD_NUMBER);
			
			//manager.process("http://bbs.51job.com/");
			while(!entryAddress.isQueueEmpty()){ //ѭ����ȡ��ڵ�ַ
				address = entryAddress.deQueue();
				flag = manager.process(address);
				while(!flag){
					System.out.println("waiting....");
					Thread.sleep(60000);
					flag = manager.process(address);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
}
	public void stopThread(){
		THREAD_NUMBER = eap.getTableLength();
		manager.stopThread(THREAD_NUMBER);
		}
	
	public static void main(String[] args){
		Main t = new Main();
		t.start();
		/*try{
		Thread.sleep(crawlTime);
		t.stopThread();
		}catch(InterruptedException e ){
			e.printStackTrace();
		}*/
		
	}

}