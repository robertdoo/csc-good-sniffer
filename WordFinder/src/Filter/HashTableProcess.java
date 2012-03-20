package Filter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import database.ConnDB;

/*
 * 用于主页面监控爬虫
 */

public class HashTableProcess {

	// 测试函数 用来建立空的哈希表
	public static void main(String[] args) {
		HashTableProcess htp = new HashTableProcess(1000000);
		long begin = new Date().getTime();
		htp.createHashTable();
		long end = new Date().getTime();
		System.out.println("建立哈希表用时:" + (end - begin) / 1000.0 + "秒");

		/*
		 * begin = new Date().getTime(); new BloomFilter(1000000); end = new
		 * Date().getTime(); System.out.println("初始化哈希表用时:" + (end - begin) /
		 * 1000.0 + "秒");
		 */
	}

	int HashTableLength;
	int[] hash = new int[8];
	private final String DB_NAME = "realtimeData";
	private ConnDB connDB = null;

	public HashTableProcess(int length) {
		HashTableLength = length;
		connDB = new ConnDB(DB_NAME);
		if (connDB.connectToDB());
			//System.out.println("数据库" + DB_NAME + "连接成功！");
	}

	// 保存哈希表的函数，分为从本地读取，更新，保存三步
	public synchronized Boolean saveHashTable(Timestamp beginTime, Timestamp endTime) {
		Set<Float> dataSet = new HashSet<Float>();
		int count;
		try {
			System.err.println("现在开始保存主页面监控爬虫哈希表操作！");
			// 读取
			DataInputStream in = new DataInputStream(new BufferedInputStream(
					new FileInputStream("HashTable.dat")));
			count = in.readInt();
			for (int i = 0; i < count; i++) {
				dataSet.add(in.readFloat());
			}
			in.close();
			// 更新
			System.err.println("主页面监控哈希表读取完毕，下面开始更新！");
			ResultSet rs = null;
			String sqlstr;

			sqlstr = "select url from file where storeTime > '" + beginTime
					+ "' and storeTime <'" + endTime + "'";
			rs = connDB.executeQuery(sqlstr);
			Filter f = new Filter(HashTableLength);
			// 通过数据库中新增加的内容更新哈希表
			while (rs.next()) {
				hash = f.BF(rs.getString(1));
				dataSet.add((float) hash[0] + 0.1f);
				dataSet.add((float) hash[1] + 0.2f);
				dataSet.add((float) hash[2] + 0.3f);
				dataSet.add((float) hash[3] + 0.4f);
				dataSet.add((float) hash[4] + 0.5f);
				dataSet.add((float) hash[5] + 0.6f);
				dataSet.add((float) hash[6] + 0.7f);
				dataSet.add((float) hash[7] + 0.8f);
			}

			// 保存
			System.err.println("主页面监控爬虫哈希表更新完毕，下面开始保存！");
			DataOutputStream out = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(
							"HashTable.dat")));
			Iterator<Float> it = dataSet.iterator();
			count = dataSet.size();
			out.writeInt(count);
			while (it.hasNext()) {
				out.writeFloat(it.next());
			}
			out.close();
			System.err.println("主页面监控爬虫哈希表保存完毕！");

		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
		return true;
	}

	// 创建哈希表操作
	public Boolean createHashTable() {
		try {
			DataOutputStream out = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(
							"HashTable.dat")));
			System.out.println("主页面监控爬虫哈希表创建完毕！下面将进行初始化！");

			ResultSet rs = null;
			String sqlstr;
			Set<Float> dataSet = new HashSet<Float>();
			int count;

			sqlstr = "select url from file";
			rs = connDB.executeQuery(sqlstr);
			Filter f = new Filter(HashTableLength);
			// 通过数据库中新增加的内容更新哈希表
			while (rs.next()) {
				hash = f.BF(rs.getString(1));
				dataSet.add((float) hash[0] + 0.1f);
				dataSet.add((float) hash[1] + 0.2f);
				dataSet.add((float) hash[2] + 0.3f);
				dataSet.add((float) hash[3] + 0.4f);
				dataSet.add((float) hash[4] + 0.5f);
				dataSet.add((float) hash[5] + 0.6f);
				dataSet.add((float) hash[6] + 0.7f);
				dataSet.add((float) hash[7] + 0.8f);
			}

			Iterator<Float> it = dataSet.iterator();
			count = dataSet.size();
			out.writeInt(count);
			while (it.hasNext()) {
				out.writeFloat(it.next());
			}
			out.close();
			System.out.println("主页面监控爬虫哈希表初始化完毕！");

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public ConnDB getConnDB() {
		return connDB;
	}
}
