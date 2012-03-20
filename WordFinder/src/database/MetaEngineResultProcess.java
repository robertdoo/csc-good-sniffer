package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import dataStruct.Result;

public class MetaEngineResultProcess {

	private ConnDB connDB = null;
	
	private final String DB_NAME="SearchResult";
	private final String SQL_INSERT = "insert into MetaEngineResult(url,title) values(?,?)";
	
	public ConnDB getConnDB() {
		return connDB;
	}
	
	public MetaEngineResultProcess(){
		connDB = new ConnDB(DB_NAME);
		if(!connDB.connectToDB())
			System.err.println("连接数据库异常~！");
		createNewSearchResultTable();
	}
	
	/**
	 * 创建新表
	 */
	public boolean createNewSearchResultTable() {
		if (!connDB.isTableExist("MetaEngineResult")) {
			String sqlstr = "CREATE TABLE  `searchresult`.`MetaEngineResult` ("
			+ "id int(10) unsigned NOT NULL auto_increment,"
			+ "title VARCHAR(500)," + "url VARCHAR(500),"
			 +"storeTime timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,"
			+ "PRIMARY KEY  (id)"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			if (!connDB.executeUpdate(sqlstr)){
				System.err.println("建立新表MetaEngineResult失败~！");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 判断一条结果是否存在，根据URL判断
	 */
	public synchronized boolean isExisted(String url) {
		String sqlstr = "select * from MetaEngineResult where url ='"
		                + url + "';";
		ResultSet rs;
		
		try {
			rs = connDB.executeQuery(sqlstr);
			if(rs.next())
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("判断元搜索结果是否存在时出现异常！");
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println("判断元搜索结果是否存在时出现异常！");
		} finally {
			rs = null;
		}
		
		return false;
	}
	
	/**
	 * 添加一条结果
	 */
	public synchronized int addResult(Result result) {
		int n = -1;
		PreparedStatement pstmt = connDB.getPreparedStatement(SQL_INSERT);
		
		try{
			pstmt.setString(1, result.getUrl());
			pstmt.setString(2, result.getTitle());
			n = pstmt.executeUpdate();
		} catch(SQLException e) {
			//e.printStackTrace();
			System.err.println("插入一条元搜索的新结果时出现异常");
		}
		
		return n;
	}
	
	/**
	 * 添加一列结果（批处理）
	 */
	public synchronized int[] addResultList(LinkedList<Result> resultList) {
		int[] batch = null;
		PreparedStatement pstmt = connDB.getPreparedStatement(SQL_INSERT);
		try{
			for(Result r : resultList){
				pstmt.setString(1, r.getUrl());
				pstmt.setString(2, r.getTitle());
				pstmt.addBatch();
			}
			batch = pstmt.executeBatch();
		} catch(SQLException e) {
			//e.printStackTrace();
			System.err.println("插入一列元搜索的新结果时出现异常");
		}
		
		return batch;
	}
}
