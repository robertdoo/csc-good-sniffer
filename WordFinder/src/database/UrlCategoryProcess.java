package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class UrlCategoryProcess {
private ConnDB connDB = null;
	
	private final String DB_NAME="test";
	
	public ConnDB getConnDB() {
		return connDB;
	}

	public UrlCategoryProcess(){
		connDB = new ConnDB(DB_NAME);
		if(!connDB.connectToDB())
			System.err.println("连接数据库异常~！");
		
		if (!connDB.isTableExist("urlcategory")) {
			String sqlstr = "CREATE TABLE  `test`.`urlcategory` ("
					+ "`id` int(10) unsigned NOT NULL auto_increment,"
					+ "`name` varchar(45) character set utf8 NOT NULL,"
					+ "PRIMARY KEY  (`id`)"
					+ ") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;";
			if (!connDB.executeUpdate(sqlstr))
				System.err.println("建立新表urlcategory失败~！");
			else{
				sqlstr = "insert into urlcategory(name) values('其他'),('股吧'),('论坛'),('求职');";
				connDB.executeUpdate(sqlstr);
			}	
		}
	}
	
	/**
	 * @return LinkedList<String> 所有类别名称
	 */
	public LinkedList<String> getAllCategorynames()
	{	
		LinkedList<String> category = new LinkedList<String>();
		String sqlstr = "select name from urlcategory";
		ResultSet rs = connDB.executeQuery(sqlstr);
		try {
            while (rs.next()){
            	category.add(rs.getString(1));
            }
            return category;
		} catch (SQLException e1) {
			System.err.println("数据库存在异常！");
		}
		return category;
	}
	
	/**
	 * @return String[] 以字符串返回所有类别名称
	 */
	public String[] getAllCategorynamesAsArray()
	{
		LinkedList<String> category = getAllCategorynames();
		if (category.size() != 0)
		{
			String[] array = new String[category.size()];
			for (int i = 0; i < category.size(); ++i)
			{
				array[i] = category.get(i);
			}
			return array;
		}
		return null;
	}
	
	/**
	 * 判断类别名称是否存在
	 */
	public boolean isCategoryExisted(String categoryName) {
		String sqlstr = "select * from urlcategory where name = '"
		               + categoryName + "'";
		ResultSet rs = connDB.executeQuery(sqlstr);
		try {
			if(rs.next())
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 插入新类别
	 */
	public void insertNewCategory(String name){
		String sqlstr = "insert into urlcategory(name) values('" + name + "')";
		
		if(!connDB.executeUpdate(sqlstr))
			System.err.println("插入新类别失败~！");
	}
	
	/**
	 * 修改类别名称
	 */
	public void updateCategoryName(String newName, String oldName){
		String sqlstr = "update urlcategory set name = '" 
			+ newName + "' where name = '" + oldName + "'";
		//System.out.println(sqlstr);
		if(!connDB.executeUpdate(sqlstr))
			System.err.println("修改类别名称失败~！");
	}
	
	/**
	 * 根据类别名称删除一个类别，同时删除类别中的所有入口地址
	 */
	public boolean deleteCategory(String name){
		String sqlstr = "delete url,urlcategory " +
				        "from url,urlcategory " +
				        "where url.categoryName = urlcategory.name " +
				        "and urlcategory.name = '" + name + "'";
		
		if(!connDB.executeUpdate(sqlstr)){
			System.err.println("删除类别失败~！");
			return false;
		}
		
		return true;
		
		/*
		EntryAddressProcess eap = new EntryAddressProcess();
		eap.deleteEntryAddressByCategory(name);
		eap.getConnDB().closeConnection();
		*/
	}
}
