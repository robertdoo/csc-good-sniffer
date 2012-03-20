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
 * ����������̳���沿��
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
			System.out.println("���ݿ�test���ӳɹ���");
	}

	// �����ϣ���ĺ�������Ϊ�ӱ��ض�ȡ�����£���������
	public Boolean saveHashTable(Timestamp beginTime, Timestamp endTime) {
		Set<Float> dataSet = new HashSet<Float>();
		int count;
		try {
			System.err.println("���ڿ�ʼ������̳�����ϣ��������");
			// ��ȡ
			DataInputStream in = new DataInputStream(new BufferedInputStream(
					new FileInputStream("HashTable1.dat")));
			count = in.readInt();
			System.err.println("��̳�����ϣ����ԭ��"+count+"������");
			for (int i = 0; i < count; i++) {
				dataSet.add(in.readFloat());
			}
			in.close();
			System.err.println("������̳��ϣ����ȡ��ϣ����濪ʼ���£�");
			// ����
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
			System.err.println("��̳�����ϣ��������ϣ����濪ʼ���棡");
			DataOutputStream out = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(
							"HashTable1.dat")));
			// ����
			Iterator<Float> it = dataSet.iterator();
			count = dataSet.size();
			System.err.println("������̳�����ϣ��ʱ������"+count+"������");
			out.writeInt(count);
			while (it.hasNext()) {
				out.writeFloat(it.next());
			}
			out.close();
			
			System.err.println("��̳�����ϣ��������ϣ�");

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	// ������ϣ������
	public Boolean createHashTable() {
		try {
			DataOutputStream out = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(
							"HashTable1.dat")));
			System.out.println("��̳�����ϣ��������ϣ����潫���г�ʼ����");

			ResultSet rs = null;
			String sqlstr;
			Set<Float> dataSet = new HashSet<Float>();
			int count;
			int counter = 0;
			
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
			System.out.println("����"+counter+"����¼");
			System.out.println("����"+count+"����ϣ������");
			Iterator<Float> it = dataSet.iterator();
			
			out.writeInt(count);
			while (it.hasNext()) {
				out.writeFloat(it.next());
			}
			out.close();
			System.out.println("��̳�����ϣ����ʼ����ϣ�");

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