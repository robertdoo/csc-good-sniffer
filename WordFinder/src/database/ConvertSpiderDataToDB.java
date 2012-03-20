package database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;


import dataStruct.SpiderData;

public class ConvertSpiderDataToDB {
	private ConnDB connDB = null;

	public ConvertSpiderDataToDB(String DB_NAME) {
		connDB = new ConnDB(DB_NAME);
		if (connDB.connectToDB())
			System.out.println("���ݿ�" + DB_NAME + "���ӳɹ���");
	}

	public ConnDB getConnDB() {
		return connDB;
	}

	public void storeFile(SpiderData spiderData) {

		String title, url, sqlstr;
		url = "";
		title = "";

		
			if (spiderData.getUrl() == null)
				url = "null";
			else {
				url = spiderData.getUrl().substring(0,
						Math.min(spiderData.getUrl().length(), 500));// ֻȡǰ500���ַ�
				url = pathStringConvert(url);// Ϊ�������MySQL����·���ַ������д���
			}

			title = spiderData.getTitle();// �������Ǳ���
			if (title == null)
				title = "null";
			else {
				title = title.replace(" ", "");// ȥ���ո�
				title = title.substring(0, Math.min(title.length(), 100));// ֻȡǰ100���ַ�
			}

		// �������ݿ�
		sqlstr = "insert into file(title,url) values('" + title
				+ "','" + url + "')";
		if (connDB.executeUpdate(sqlstr))
			System.out.println(title + "�ѱ�������ݿ⣡");
		else
			System.out.println(title + "��������ݿ�ʱ����");
		System.out.println();
	}

	
	public String pathStringConvert(String pathString) {// ת��·���ַ������ѡ�\��ת��Ϊ��\\��
		String newString = "";
		char ch;

		for (int i = 0; i < pathString.length(); i++) {
			ch = pathString.charAt(i);
			newString += ch;
			if (ch == '\\')
				newString += '\\';
		}

		return newString;
	}

	public void printDBInfo() {
		String sqlstr = "SELECT * FROM file";
		ResultSet rs = null;
		try {
			rs = connDB.executeQuery(sqlstr);
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				for (int i = 1; i <= rsmd.getColumnCount(); i++)
					System.out.println(rsmd.getColumnName(i) + ": "
							+ rs.getString(i));
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		if (connDB.closeConnection())
			System.out.println("���ݿ������ѹر�~!");
	}

}
