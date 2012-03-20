package Filter;

import java.sql.ResultSet;
import java.util.BitSet;
import database.ConnDB;

/*
 * 用于论坛爬虫部分
 */
public class BloomFilter1 {
	//8个BitSet对象分别对应 哈希表中8个哈希函数的对应值
	private BitSet bs0 ;
	private BitSet bs1 ;
	private BitSet bs2 ;
	private BitSet bs3 ;
	private BitSet bs4 ;
	private BitSet bs5 ;
	private BitSet bs6 ;
	private BitSet bs7 ;
	
	private String dbName = "test";
	
	
	//建立filter对象
	Filter bf;
	int HashTableLength;
	
	//构造函数
	public BloomFilter1(int length){
		
		//为filter对象设置参数
		bf = new Filter(length);
		
		HashTableLength = length;
		bs0 = new BitSet(length);
		bs1 = new BitSet(length);
		bs2 = new BitSet(length);
		bs3 = new BitSet(length);
		bs4 = new BitSet(length);
		bs5 = new BitSet(length);
		bs6 = new BitSet(length);
		bs7 = new BitSet(length);
		
		
		try{
			//初始化哈希表，将保存在本地的哈希表读入内存，并用数据库中数据进行修复，确保哈希表的准确
			System.out.println("正初始化论坛爬虫哈希表！");
		
		int counter = 0;
		Filter f = new Filter(HashTableLength);
		int[] hash = new int[8];
		ConnDB connDB = new ConnDB(dbName);
		if(connDB.connectToDB())
			System.out.println("数据库"+dbName+"连接成功！");
		ResultSet rs = connDB.executeQuery("select * from file");
		
		while(counter<100000&&rs.next()){
			hash = f.BF(rs.getString(3));
			//System.out.println(rs.getInt(1)+"   "+rs.getString(3));
			bs0.set(hash[0]);
			bs1.set(hash[1]);
			bs2.set(hash[2]);
			bs3.set(hash[3]);
			bs4.set(hash[4]);
			bs5.set(hash[5]);
			bs6.set(hash[6]);
			bs7.set(hash[7]);
			
	
			counter++;
		}
		
		counter = 0;
		rs = connDB.executeQuery("select * from image");
	
		while(counter<10000&&rs.next()){
			hash = f.BF(rs.getString(3));
			bs0.set(hash[0]);
			bs1.set(hash[1]);
			bs2.set(hash[2]);
			bs3.set(hash[3]);
			bs4.set(hash[4]);
			bs5.set(hash[5]);
			bs6.set(hash[6]);
			bs7.set(hash[7]);
			
			counter++;
		}
		
		connDB.closeConnection();//关闭连接
		
		System.out.println("论坛爬虫哈希表初始化完毕！");
		
		}catch(Exception e){
			System.out.println("初始化论坛爬虫哈希表出错");
			e.printStackTrace();
		}
		
		
	}
	//保存哈希表
	/*public Boolean saveHashTable(Timestamp beginTime, Timestamp endTime){
			Boolean flag ;
			HashTableProcess1 htp = new HashTableProcess1(HashTableLength);
			flag = htp.saveHashTable(beginTime, endTime);
			return flag;
		}*/
		
	
	//检验重复的函数
	boolean flag ;
	int hash[];
	public synchronized boolean bloomfilter( String line) {
		flag = false;
		
		hash = new int[8];
		hash = bf.BF(line);
		if (bs0.get(hash[0])==false||bs1.get(hash[1])==false||bs2.get(hash[2])==false||bs3.get(hash[3])==false||
				bs4.get(hash[4])==false||bs5.get(hash[5])==false||bs6.get(hash[6])==false||bs7.get(hash[7])==false) {
			bs0.set(hash[0]);
			bs1.set(hash[1]);
			bs2.set(hash[2]);
			bs3.set(hash[3]);
			bs4.set(hash[4]);
			bs5.set(hash[5]);
			bs6.set(hash[6]);
			bs7.set(hash[7]);
			
			flag = true;// 不是重复记录 进行下一步操作
		}
		else flag = false;
		return flag;
	}
}
