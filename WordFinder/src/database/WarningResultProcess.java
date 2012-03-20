package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import org.jfree.data.general.DefaultPieDataset;

import dataStruct.WarningResult;


public class WarningResultProcess {

private ConnDB connDB = null;
	
	private final String DB_NAME="WarningInfo";
	
	public ConnDB getConnDB() {
		return connDB;
	}
	
	public WarningResultProcess(){
		connDB = new ConnDB(DB_NAME);
		if(!connDB.connectToDB())
			System.err.println("连接数据库异常~！");
		
		if (!connDB.isTableExist("WarningResult")) {
			String sqlstr = "CREATE TABLE  `WarningInfo`.`WarningResult` ("
				    + "`id` int(10) unsigned NOT NULL auto_increment,"
					+ "`warningTime` varchar(50) NOT NULL,"
					+ "`title` varchar(100) NOT NULL,"
					+ "`url` varchar(500) NOT NULL,"
					+ "`abs` varchar(500) NOT NULL,"
					+ "`ruleName` varchar(100) NOT NULL,"
					+ "`keyword` varchar(100) NOT NULL,"
					+ "`cache` varchar(500) NOT NULL,"
					+ "`isNew` tinyint(1) NOT NULL,"
					+ "PRIMARY KEY  (`id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			if (!connDB.executeUpdate(sqlstr))
				System.err.println("建立新表WarningResult失败~！");
		}
	}
	
	/**
	 * 取得所有记录
	 */
	public LinkedList<WarningResult> getAllResult(){
		LinkedList<WarningResult> results = new LinkedList<WarningResult>();
		
		String warningTime, title, url, abs, ruleName, keyword, cache;
		boolean isNew;
		String sqlstr = "select * from WarningResult";
		try {
		    ResultSet rs = connDB.executeQuery(sqlstr);
		    while (rs.next())
		    {
		    	warningTime = rs.getString(2);
		    	title = rs.getString(3);
		    	url = rs.getString(4);
		    	abs = rs.getString(5);
		    	ruleName = rs.getString(6);
		    	keyword = rs.getString(7);
		    	cache = rs.getString(8);
		    	isNew = rs.getBoolean(9);
		    	WarningResult result = new WarningResult(warningTime,
		    			                                 title,
		    			                                 url,
		    			                                 abs,
		    			                                 ruleName,
		    			                                 keyword,
		    			                                 cache,
		    			                                 isNew);
		       results.add(result);
		    }
		} catch (SQLException e1) {
			System.err.println("数据库存在异常！");
		}
		
		return results;
	}
	
	/**
	 * 取得一条记录
	 */
	public WarningResult getResultByUrl(String url)
	{
		String warningTime, title, abs, ruleName, keyword, cache;
		boolean isNew;
		String sqlstr = "select * from WarningResult where url ='"+url+"'";
		try {
		    ResultSet rs = connDB.executeQuery(sqlstr);
		    if (rs.next())
		    {
		    	warningTime = rs.getString(2);
		    	title = rs.getString(3);
		    	abs = rs.getString(5);
		    	ruleName = rs.getString(6);
		    	keyword = rs.getString(7);
		    	cache = rs.getString(8);
		    	isNew = rs.getBoolean(9);
		    	WarningResult result = new WarningResult(warningTime,
		    			                                 title,
		    			                                 url,
		    			                                 abs,
		    			                                 ruleName,
		    			                                 keyword,
		    			                                 cache,
		    			                                 isNew);
		        return result;
		    }
		    else return null;
		} catch (SQLException e1) {
			System.err.println("数据库存在异常！");
		} 
		return null;
	}
	
	public boolean addResult(WarningResult result)
	{
		if(result == null){
			System.err.println("result is null!");
			return false;
		}
		String warningTime = result.getWarningTime(),
		       title = result.getTitle(),
		       url = result.getUrl(),
		       abs = result.getAbs(),
		       ruleName = result.getRuleName(),
		       keyword = result.getKeyword(),
		       cache = result.getCache();
		int isNew = result.isNew()?1:0;
		String sqlstr = "select * from WarningResult where url ='"+url+"'";
		try {
		    ResultSet rs = connDB.executeQuery(sqlstr);
		    if (rs.next())
		    {
		    	System.err.println("记录" + url + "在数据库中已存在!");
		        return false;
		    }
		    else{
		    	sqlstr = "insert into WarningResult(warningTime,title,url" +
		    			",abs,ruleName,keyword,cache,isNew) values('" +
		    			warningTime + "','" +
		    			title + "','" +
		    			url + "','" +
		    			abs + "','" +
		    			ruleName + "','" +
		    			keyword + "','" +
		    			cache + "'," +
		    			isNew + ")";
		    	if(!connDB.executeUpdate(sqlstr)){
		    		System.err.println("插入新纪录" + url + "失败！");
		    		return false;
		    	}
		    	else
		    		return true;
		    }
		} catch (SQLException e1) {
			System.err.println("数据库存在异常！");
			return false;
		} 
	}
	
	/**
	 * 把一条结果置为查看过的状态，isNew = false
	 */
	public boolean setResultChecked(WarningResult result)
	{
		if(result == null){
			System.err.println("result is null!");
			return false;
		}
		String url = result.getUrl();
		String sqlstr = "select * from WarningResult where url ='"+url+"'";
		try {
			ResultSet rs = connDB.executeQuery(sqlstr);
			if (rs.next()) {
				sqlstr = "update WarningResult set isNew = 0 "
						+ "where url = '" + url + "'";
				if (!connDB.executeUpdate(sqlstr)) {
					System.err.println("改变纪录" + url + "的状态失败！");
					return false;
				}
				else
					return true;
			} 
			else {
				System.err.println("记录" + url + "在数据库中不存在!");
				return false;
			}
		} catch (SQLException e1) {
			System.err.println("数据库存在异常！");
			return false;
		} 
	}
	
	public boolean deleteResult(WarningResult result){
		String sqlstr = "select * from WarningResult where url ='"+result.getUrl()+"'";
		
		try {
			ResultSet rs = connDB.executeQuery(sqlstr);
			if (!rs.next()) {
				System.err.println(result.getUrl() + "没有记录!");
				return false;
			} 
			sqlstr = "delete from WarningResult where url ='"+result.getUrl()+"'";
			if(!connDB.executeUpdate(sqlstr)){
				System.err.println(result.getUrl() + "删除失败!");
				return false;
			}
			else
				return true;
		} catch (SQLException e1) {
			System.err.println("数据库存在异常！");
			return false;
		} 
	}
	
	public DefaultPieDataset getStatisticsDataset(){
		DefaultPieDataset defaultpiedataset = new DefaultPieDataset();
		double totalCount = 0;
		ResultSet rs = null;
		String sqlstr = "select count(*) from WarningResult";
		try {
			rs = connDB.executeQuery(sqlstr);
			if (rs.next()) {
				totalCount = rs.getDouble(1);
			}
			sqlstr = "SELECT ruleName,count(*) as total" +
	        " from WarningResult group by ruleName";
			rs = connDB.executeQuery(sqlstr);
			while (rs.next()) {
				defaultpiedataset.setValue(rs.getString(1), rs.getDouble(2)/totalCount);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return defaultpiedataset;
	}
}
