package database;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.LinkedList;

public class CategoryProcess {

	private String userName;
	private ConnDB connDB = null;
	
	private final String DB_NAME="Rule";
	
	public ConnDB getConnDB() {
		return connDB;
	}

	public CategoryProcess(String userName) {
		this.userName = userName;
		connDB = new ConnDB(DB_NAME);
		if(!connDB.connectToDB())
			System.err.println("连接数据库异常~！");
		createNewCategoryTable(userName);
	}
	
	/**
	 * 创建规则分类的新表
	 */
	public boolean createNewCategoryTable(String userName) {
		if (!connDB.isTableExist(userName + "category")) {
			String sqlstr = "CREATE TABLE  `rule`.`" + userName + "category` ("
					+ "`id` int(10) unsigned NOT NULL auto_increment,"
					+ "`name` varchar(45) NOT NULL,"
					+ "PRIMARY KEY  (`id`)"
					+ ") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;";
			if (!connDB.executeUpdate(sqlstr)){
				System.err.println("建立新表" + userName + "category失败~！");
				return false;
			}
			else{
				sqlstr = "insert into " + userName + "category(name) values('其他'),"
				       + "('军事'),('经济');";
				connDB.executeUpdate(sqlstr);
			}	
		}
		return true;
	}
	
	/**
	 * 删除表
	 */
	public boolean deleteCategoryTable(String userName) {
		if (connDB.isTableExist(userName + "category")) {
			String sqlstr = "DROP TABLE  `rule`.`" + userName + "category`;";
			if (!connDB.executeUpdate(sqlstr)){
				System.err.println("删除表" + userName + "category失败~！");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @return LinkedList<String> 所有类别名称
	 */
	public LinkedList<String> getAllCategorynames()
	{	
		LinkedList<String> category = new LinkedList<String>();
		String sqlstr = "select name from " + userName + "category";
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
		String sqlstr = "select * from " + userName + "category where name = '"
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
		String sqlstr = "insert into " + userName + "category(name) values('" + name + "')";
		
		if(!connDB.executeUpdate(sqlstr))
			System.err.println("插入新类别失败~！");
	}
	
	/**
	 * 根据类别名称删除一个类别，同时删除类别中的所有规则
	 */
	public boolean deleteCategory(String name){
		String sqlstr = "delete " + userName + "rule," + userName + "category"
		              + " from " + userName + "rule," + userName + "category"
                      + " where categoryName = name" 
                      + " and name = '" + name + "'";
		
		if(!connDB.executeUpdate(sqlstr)){
			System.err.println("删除类别失败~！");
			return false;
		}
		
		return true;
		/*
		RuleProcess rp = new RuleProcess(userName);
		rp.deleteRuleByCategory(name);
		rp.getConnDB().closeConnection();
		*/
	}
	
	/**
	 * 修改类别名称
	 */
	public void updateCategoryName(String newName, String oldName){
		String sqlstr = "update " + userName + "category set name = '" 
			+ newName + "' where name = '" + oldName + "'";
		//System.out.println(sqlstr);
		if(!connDB.executeUpdate(sqlstr))
			System.err.println("修改类别名称失败~！");
	}
}
