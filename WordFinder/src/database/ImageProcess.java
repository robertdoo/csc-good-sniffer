package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import dataStruct.ImageSearchResult;

public class ImageProcess {
	private ConnDB connDB = null;

	private final String DB_NAME = "test";
	
	public ConnDB getConnDB() {
		return connDB;
	}
	
	public ImageProcess() {
		connDB = new ConnDB(DB_NAME);
		if (!connDB.connectToDB())
			System.err.println("���ݿ������쳣��");
		
		if (!connDB.isTableExist("image")) {
			String sqlstr = "CREATE TABLE  `test`.`image` ("
					+ "`id` int(10) unsigned NOT NULL auto_increment,"
					+ "`title` varchar(500) default NULL,"
					+ "`url` varchar(500) default NULL,"
					+ "`site` varchar(500) default NULL,"
					+ "`localPath` varchar(500) default NULL,"
					+ "`examined` tinyint(1) NOT NULL default '0',"
					+ "`storeTime` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,"
					+ "`examinedResult` varchar(100) default NULL,"
					+ "PRIMARY KEY  (`id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			if (!connDB.executeUpdate(sqlstr))
				System.err.println("�����±�imageʧ��~��");	
		}
	}
	
	/**
	 * ȡ�������Ѵ����ͼƬ������
	 */
	public LinkedList<ImageSearchResult> getAllExaminedResult() {
		LinkedList<ImageSearchResult> list = new LinkedList<ImageSearchResult>();
		String sqlstr = "select * from image where examined = '1' " +
				"and examinedResult <> '�Ǻ�ͷ�ļ���'" +
				" and examinedResult <> '�Ǻ�ͷ�ļ�~��' and examinedResult <> 'xxxxx'";
		ResultSet rs = connDB.executeQuery(sqlstr);
		try {
			while(rs.next()){
				list.add(new ImageSearchResult(rs.getString("title"),
						                       rs.getString("url"),
						                       rs.getString("site"),
						                       rs.getString("localPath"),
						                       rs.getBoolean("examined"),
						                       rs.getString("storeTime"),
						                       rs.getString("examinedResult")));
			}
		} catch (SQLException e) {
			System.err.println("ȡ�������Ѵ����ͼƬ������ʱ�����쳣");
		}
		
		return list;
	}
	
	/**
	 * ȡ������δ�����ͼƬ������
	 */
	public LinkedList<ImageSearchResult> getAllUnexaminedResult() {
		LinkedList<ImageSearchResult> list = new LinkedList<ImageSearchResult>();
		String sqlstr = "select * from image where examined = '0'";
		ResultSet rs = connDB.executeQuery(sqlstr);
		try {
			while(rs.next()){
				list.add(new ImageSearchResult(rs.getString("title"),
						                       rs.getString("url"),
						                       rs.getString("site"),
						                       rs.getString("localPath"),
						                       rs.getBoolean("examined"),
						                       rs.getString("storeTime"),
						                       rs.getString("examinedResult")));
			}
		} catch (SQLException e) {
			System.err.println("ȡ������δ�����ͼƬ������ʱ�����쳣");
		}
		
		return list;
	}
	
	/**
	 * ȡ������δ����ͼƬ��·��
	 */
	public LinkedList<String> getAllUnexaminedImagePath() {
		LinkedList<String> list = new LinkedList<String>();
		String sqlstr = "select localPath from image where examined = '0'";
		ResultSet rs = connDB.executeQuery(sqlstr);
		try {
			while(rs.next()){
				list.add(rs.getString("localPath"));
			}
		} catch (SQLException e) {
			System.err.println("ȡ������δ����ͼƬ��·��ʱ�����쳣");
		}
		
		return list;
	}
	
	/**
	 * ����ͼƬ��¼
	 */
	public boolean updateExaminedResult(String localPath, String result) {
		PreparedStatement pstmt = connDB.getPreparedStatement(
		"update image set examined = '1' , " +
		"examinedResult = ? where localPath = ?");
		
		try {
			pstmt.setString(1, result);
			pstmt.setString(2, localPath);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("����" + localPath + "�Ĵ�����ʱ�����쳣");
			return false;
		}
		
		return true;
	}
}
