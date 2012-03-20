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
			System.err.println("数据库连接异常！");
		
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
				System.err.println("建立新表image失败~！");	
		}
	}
	
	/**
	 * 取出所有已处理的图片处理结果
	 */
	public LinkedList<ImageSearchResult> getAllExaminedResult() {
		LinkedList<ImageSearchResult> list = new LinkedList<ImageSearchResult>();
		String sqlstr = "select * from image where examined = '1' " +
				"and examinedResult <> '非红头文件！'" +
				" and examinedResult <> '非红头文件~！' and examinedResult <> 'xxxxx'";
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
			System.err.println("取出所有已处理的图片处理结果时出现异常");
		}
		
		return list;
	}
	
	/**
	 * 取出所有未处理的图片处理结果
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
			System.err.println("取出所有未处理的图片处理结果时出现异常");
		}
		
		return list;
	}
	
	/**
	 * 取出所有未处理图片的路径
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
			System.err.println("取出所有未处理图片的路径时出现异常");
		}
		
		return list;
	}
	
	/**
	 * 更新图片记录
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
			System.err.println("更新" + localPath + "的处理结果时出现异常");
			return false;
		}
		
		return true;
	}
}
