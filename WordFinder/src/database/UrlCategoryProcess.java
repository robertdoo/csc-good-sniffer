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
			System.err.println("�������ݿ��쳣~��");
		
		if (!connDB.isTableExist("urlcategory")) {
			String sqlstr = "CREATE TABLE  `test`.`urlcategory` ("
					+ "`id` int(10) unsigned NOT NULL auto_increment,"
					+ "`name` varchar(45) character set utf8 NOT NULL,"
					+ "PRIMARY KEY  (`id`)"
					+ ") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;";
			if (!connDB.executeUpdate(sqlstr))
				System.err.println("�����±�urlcategoryʧ��~��");
			else{
				sqlstr = "insert into urlcategory(name) values('����'),('�ɰ�'),('��̳'),('��ְ');";
				connDB.executeUpdate(sqlstr);
			}	
		}
	}
	
	/**
	 * @return LinkedList<String> �����������
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
	 * ���������
	 */
	public void insertNewCategory(String name){
		String sqlstr = "insert into urlcategory(name) values('" + name + "')";
		
		if(!connDB.executeUpdate(sqlstr))
			System.err.println("���������ʧ��~��");
	}
	
	/**
	 * �޸��������
	 */
	public void updateCategoryName(String newName, String oldName){
		String sqlstr = "update urlcategory set name = '" 
			+ newName + "' where name = '" + oldName + "'";
		//System.out.println(sqlstr);
		if(!connDB.executeUpdate(sqlstr))
			System.err.println("�޸��������ʧ��~��");
	}
	
	/**
	 * �����������ɾ��һ�����ͬʱɾ������е�������ڵ�ַ
	 */
	public boolean deleteCategory(String name){
		String sqlstr = "delete url,urlcategory " +
				        "from url,urlcategory " +
				        "where url.categoryName = urlcategory.name " +
				        "and urlcategory.name = '" + name + "'";
		
		if(!connDB.executeUpdate(sqlstr)){
			System.err.println("ɾ�����ʧ��~��");
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
