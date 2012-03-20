package Filter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import database.ConnDB;

/*
 * 用来处理论坛爬虫部分
 */
public class HashTableProcess1 {
	int HashTableLength;
	int[] hash = new int[8];
	private final String DB_NAME = "test";
	private ConnDB connDB = null;

	public HashTableProcess1(int length) {
		HashTableLength = length;
		connDB = new ConnDB(DB_NAME);
		if (connDB.connectToDB())
			System.out.println("数据库test连接成功！");
	}

	// 保存哈希表的函数，分为从本地读取，更新，保存三步
	public Boolean saveHashTable(Timestamp beginTime, Timestamp endTime) {
		Set<Float> dataSet = new HashSet<Float>();
		int count;
		try {
			System.err.println("现在开始保存论坛爬虫哈希表操作！");
			// 读取
			DataInputStream in = new DataInputStream(new BufferedInputStream(
					new FileInputStream("HashTable1.dat")));
			count = in.readInt();
			System.err.println("论坛爬虫哈希表中原有"+count+"项数据");
			for (int i = 0; i < count; i++) {
				dataSet.add(in.readFloat());
			}
			in.close();
			System.err.println("本地论坛哈希表读取完毕，下面开始更新！");
			// 更新
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
			sqlstr = "select url from image where storeTime > '" + beginTime
					+ "' and storeTime <'" + endTime + "'";
			rs = connDB.executeQuery(sqlstr);

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
			System.err.println("论坛爬虫哈希表更新完毕，下面开始保存！");
			DataOutputStream out = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(
							"HashTable1.dat")));
			// 保存
			Iterator<Float> it = dataSet.iterator();
			count = dataSet.size();
			System.err.println("保存论坛爬虫哈希表时共保存"+count+"项数据");
			out.writeInt(count);
			while (it.hasNext()) {
				out.writeFloat(it.next());
			}
			out.close();
			
			System.err.println("论坛爬虫哈希表保存完毕！");

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	// 创建哈希表操作
	public Boolean createHashTable() {
		try {
			DataOutputStream out = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(
							"HashTable1.dat")));
			System.out.println("论坛爬虫哈希表创建完毕！下面将进行初始化！");

			ResultSet rs = null;
			String sqlstr;
			Set<Float> dataSet = new HashSet<Float>();
			int count;
			int counter = 0;
			
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
				counter++;
			}
			sqlstr = "select url from image";
			rs = connDB.executeQuery(sqlstr);
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
				counter++;
			}
			count = dataSet.size();
			System.out.println("共有"+counter+"条记录");
			System.out.println("共有"+count+"个哈希表数据");
			Iterator<Float> it = dataSet.iterator();
			
			out.writeInt(count);
			while (it.hasNext()) {
				out.writeFloat(it.next());
			}
			out.close();
			System.out.println("论坛爬虫哈希表初始化完毕！");

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
