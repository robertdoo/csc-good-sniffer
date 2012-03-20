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
 * ������ҳ��������
 */

public class HashTableProcess {

	// ���Ժ��� ���������յĹ�ϣ��
	public static void main(String[] args) {
		HashTableProcess htp = new HashTableProcess(1000000);
		long begin = new Date().getTime();
		htp.createHashTable();
		long end = new Date().getTime();
		System.out.println("������ϣ����ʱ:" + (end - begin) / 1000.0 + "��");

		/*
		 * begin = new Date().getTime(); new BloomFilter(1000000); end = new
		 * Date().getTime(); System.out.println("��ʼ����ϣ����ʱ:" + (end - begin) /
		 * 1000.0 + "��");
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
			//System.out.println("���ݿ�" + DB_NAME + "���ӳɹ���");
	}

	// �����ϣ��ĺ�������Ϊ�ӱ��ض�ȡ�����£���������
	public synchronized Boolean saveHashTable(Timestamp beginTime, Timestamp endTime) {
		Set<Float> dataSet = new HashSet<Float>();
		int count;
		try {
			System.err.println("���ڿ�ʼ������ҳ���������ϣ�������");
			// ��ȡ
			DataInputStream in = new DataInputStream(new BufferedInputStream(
					new FileInputStream("HashTable.dat")));
			count = in.readInt();
			for (int i = 0; i < count; i++) {
				dataSet.add(in.readFloat());
			}
			in.close();
			// ����
			System.err.println("��ҳ���ع�ϣ���ȡ��ϣ����濪ʼ���£�");
			ResultSet rs = null;
			String sqlstr;

			sqlstr = "select url from file where storeTime > '" + beginTime
					+ "' and storeTime <'" + endTime + "'";
			rs = connDB.executeQuery(sqlstr);
			Filter f = new Filter(HashTableLength);
			// ͨ�����ݿ��������ӵ����ݸ��¹�ϣ��
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

			// ����
			System.err.println("��ҳ���������ϣ�������ϣ����濪ʼ���棡");
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
			System.err.println("��ҳ���������ϣ������ϣ�");

		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
		return true;
	}

	// ������ϣ�����
	public Boolean createHashTable() {
		try {
			DataOutputStream out = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(
							"HashTable.dat")));
			System.out.println("��ҳ���������ϣ������ϣ����潫���г�ʼ����");

			ResultSet rs = null;
			String sqlstr;
			Set<Float> dataSet = new HashSet<Float>();
			int count;

			sqlstr = "select url from file";
			rs = connDB.executeQuery(sqlstr);
			Filter f = new Filter(HashTableLength);
			// ͨ�����ݿ��������ӵ����ݸ��¹�ϣ��
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
			System.out.println("��ҳ���������ϣ���ʼ����ϣ�");

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
