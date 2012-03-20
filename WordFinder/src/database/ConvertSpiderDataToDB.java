package database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;


import dataStruct.SpiderData;

public class ConvertSpiderDataToDB {
	private ConnDB connDB = null;

	public ConvertSpiderDataToDB(String DB_NAME) {
		connDB = new ConnDB(DB_NAME);
		if (connDB.connectToDB())
			System.out.println("数据库" + DB_NAME + "连接成功！");
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
						Math.min(spiderData.getUrl().length(), 500));// 只取前500个字符
				url = pathStringConvert(url);// 为方便存入MySQL，对路径字符串进行处理
			}

			title = spiderData.getTitle();// 第三行是标题
			if (title == null)
				title = "null";
			else {
				title = title.replace(" ", "");// 去除空格
				title = title.substring(0, Math.min(title.length(), 100));// 只取前100个字符
			}

		// 存入数据库
		sqlstr = "insert into file(title,url) values('" + title
				+ "','" + url + "')";
		if (connDB.executeUpdate(sqlstr))
			System.out.println(title + "已保存进数据库！");
		else
			System.out.println(title + "保存进数据库时出错！");
		System.out.println();
	}

	
	public String pathStringConvert(String pathString) {// 转换路径字符串，把“\”转换为“\\”
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
			System.out.println("数据库连接已关闭~!");
	}

}
