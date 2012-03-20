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
			System.err.println("�������ݿ��쳣~��");
		createNewCategoryTable(userName);
	}
	
	/**
	 * �������������±�
	 */
	public boolean createNewCategoryTable(String userName) {
		if (!connDB.isTableExist(userName + "category")) {
			String sqlstr = "CREATE TABLE  `rule`.`" + userName + "category` ("
					+ "`id` int(10) unsigned NOT NULL auto_increment,"
					+ "`name` varchar(45) NOT NULL,"
					+ "PRIMARY KEY  (`id`)"
					+ ") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;";
			if (!connDB.executeUpdate(sqlstr)){
				System.err.println("�����±�" + userName + "categoryʧ��~��");
				return false;
			}
			else{
				sqlstr = "insert into " + userName + "category(name) values('����'),"
				       + "('����'),('����');";
				connDB.executeUpdate(sqlstr);
			}	
		}
		return true;
	}
	
	/**
	 * ɾ����
	 */
	public boolean deleteCategoryTable(String userName) {
		if (connDB.isTableExist(userName + "category")) {
			String sqlstr = "DROP TABLE  `rule`.`" + userName + "category`;";
			if (!connDB.executeUpdate(sqlstr)){
				System.err.println("ɾ����" + userName + "categoryʧ��~��");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @return LinkedList<String> �����������
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
			System.err.println("���ݿ�����쳣��");
		}
		return category;
	}
	
	/**
	 * @return String[] ���ַ������������������
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
	 * �ж���������Ƿ����
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
	 * ���������
	 */
	public void insertNewCategory(String name){
		String sqlstr = "insert into " + userName + "category(name) values('" + name + "')";
		
		if(!connDB.executeUpdate(sqlstr))
			System.err.println("���������ʧ��~��");
	}
	
	/**
	 * �����������ɾ��һ�����ͬʱɾ������е����й���
	 */
	public boolean deleteCategory(String name){
		String sqlstr = "delete " + userName + "rule," + userName + "category"
		              + " from " + userName + "rule," + userName + "category"
                      + " where categoryName = name" 
                      + " and name = '" + name + "'";
		
		if(!connDB.executeUpdate(sqlstr)){
			System.err.println("ɾ�����ʧ��~��");
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
	 * �޸��������
	 */
	public void updateCategoryName(String newName, String oldName){
		String sqlstr = "update " + userName + "category set name = '" 
			+ newName + "' where name = '" + oldName + "'";
		//System.out.println(sqlstr);
		if(!connDB.executeUpdate(sqlstr))
			System.err.println("�޸��������ʧ��~��");
	}
}
