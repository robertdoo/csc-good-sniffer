package database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;

import dataStruct.EntryAddress;

import spider.Queue;

public class EntryAddressProcess {

	private ConnDB connDB = null;

	private final String DB_NAME = "test";

	public static void main(String[] args) {

		EntryAddressProcess eap = new EntryAddressProcess();
		eap.Initialization();
		eap.deleteEntryAddress("www.baidu.com");
		eap.deleteEntryAddress("www.renren.com");
		eap.deleteEntryAddress("www.mop.com");
		eap.addEntryAddress("www.renren.com", null, null);

	}

	public ConnDB getConnDB() {
		return connDB;
	}

	public EntryAddressProcess() {
		connDB = new ConnDB(DB_NAME);
		if (!connDB.connectToDB())
			System.out.println("���ݿ������쳣~��");

		if (!connDB.isTableExist("url")) {
			String sqlstr = "CREATE TABLE  `test`.`url` ("
					+ "`id` int(10) unsigned NOT NULL auto_increment,"
					+ "`url` varchar(200) default NULL,"
					+ "`name` varchar(45) default NULL,"
					+ "`categoryName` varchar(45) NOT NULL,"
					+ "PRIMARY KEY  (`id`)"
					+ ") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;";
			if (!connDB.executeUpdate(sqlstr))
				System.err.println("�����±�urlʧ��~��");
		}
	}

	public boolean Initialization() {
		String sql = "DROP TABLE IF EXISTS url;";// �h����
		connDB.executeUpdate(sql);
		sql = " CREATE TABLE  url (fileNo int(10) unsigned NOT NULL auto_increment,"
				+ "url varchar(200) default NULL,"
				+ "name varchar(45) default NULL,"
				+ "categoryName varchar(45) default NULL,PRIMARY KEY  (fileNo)) "
				+ "ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;";
		connDB.executeUpdate(sql);// ��������

		sql = "insert into url values(null,?,null,null)";
		try {

			PreparedStatement preStatement = connDB.getConn().prepareStatement(
					sql);
			BufferedReader br = new BufferedReader(new FileReader("url.txt"));
			String line = br.readLine();
			while (line != null) {
				preStatement.setString(1, line);
				preStatement.execute();
				line = br.readLine();
			}
			System.out.println("��ڵ�ַ��ʼ����ϣ�");
		} catch (Exception e) {
			// e.printStackTrace();
			System.err.println("��ʼ����ڵ�ַ����");
			return false;
		}
		return true;
	}

	/**
	 * �ж���ڵ�ַ�������Ƿ��Ѵ���
	 */
	public boolean isEntryAddressExisted(String address) { 
		String sql = "Select * from url where url='" + address + "';";
		ResultSet rs = connDB.executeQuery(sql);
		try {
			if (rs.next())
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean addEntryAddress(String address, String name, String category) {
		try {
			String sql = "insert into url(url,name,categoryName) values(" + "'"
					+ address + "'," + "'" + name + "'," + "'" + category
					+ "');";
			connDB.executeUpdate(sql);
			System.out.println("�Ѽ�����ڵ�ַ�� " + address);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ���ݵ�ַɾ����ڵ�ַ
	 */
	public boolean deleteEntryAddress(String address) {
		LinkedList<EntryAddress> addressList = new LinkedList<EntryAddress>();
		String sql = "select * from url";
		String url;
		String name;
		String categoryName;
		ResultSet resultSet = connDB.executeQuery(sql);
		try{
		while(resultSet.next()){
			url = resultSet.getString(2);
			name = resultSet.getString(3);
			categoryName = resultSet.getString(4);
			addressList.add(new EntryAddress(url,name,categoryName));
		}
		
		deleteAll();
		
		sql = "insert into url(url,name,categoryName) values(?,?,?)";
		PreparedStatement preStatement = connDB.getConn().prepareStatement(sql);
		
		Iterator<EntryAddress> it = addressList.iterator();
		while(it.hasNext()){
			EntryAddress entryAddress = it.next();
			preStatement.setString(1, entryAddress.getUrl());
			preStatement.setString(2, entryAddress.getName());
			preStatement.setString(3, entryAddress.getCategory());
			if(entryAddress.getUrl().equals(address))continue;
			preStatement.execute();
		}
		System.out.println("ɾ����ַ�� "+address+"�ɹ���");
		return true;
		}catch(SQLException e){
			System.err.println("ɾ����ַ�� "+address+"����");
			return false;
		}
		
	}

	public boolean deleteAll() {
		String sql = "truncate table url";
		if(!connDB.executeUpdate(sql))
			return false;
		else
			return true;
		
	}

	/**
	 *  �������ɾ����ڵ�ַ
	 */
	public boolean deleteEntryAddressByCategory(String category) {
		LinkedList<EntryAddress> addressList = new LinkedList<EntryAddress>();
		String sql = "select * from url";
		String url;
		String name;
		String categoryName;
		ResultSet resultSet = connDB.executeQuery(sql);
		try{
		while(resultSet.next()){
			url = resultSet.getString(2);
			name = resultSet.getString(3);
			categoryName = resultSet.getString(4);
			addressList.add(new EntryAddress(url,name,categoryName));
		}
		
		deleteAll();
		
		sql = "insert into url(url,name,categoryName) values(?,?,?)";
		PreparedStatement preStatement = connDB.getConn().prepareStatement(sql);
		
		Iterator<EntryAddress> it = addressList.iterator();
		while(it.hasNext()){
			EntryAddress entryAddress = it.next();
			preStatement.setString(1, entryAddress.getUrl());
			preStatement.setString(2, entryAddress.getName());
			preStatement.setString(3, entryAddress.getCategory());
			if(entryAddress.getCategory().equals(category))continue;
			preStatement.execute();
		}
		//System.out.println("ɾ����� "+category+"�ɹ���");
		return true;
		}catch(SQLException e){
			System.err.println("ɾ����� "+category+"����");
			return false;
		}
		
	}

	public Queue getAllEntryAddress() {
		Queue entryAddress = new Queue();
		String sql = "Select url from url";
		ResultSet rs = connDB.executeQuery(sql);
		try {
			while (rs.next()) {
				entryAddress.enQueue(rs.getString(1));
			}
			System.out.println("��ڵ�ַ��ʼ�����");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("������ڵ�ַ����");
		}
		return entryAddress;
	}

	public String[] getAllEntryAddressAsString() {
		String[] entryAddress = new String[this.getTableLength()];
		int index = 0;
		String sql = "Select url from url";
		ResultSet rs = connDB.executeQuery(sql);
		try {
			while (rs.next()) {
				entryAddress[index++] = rs.getString(1);
			}
			System.out.println("��ڵ�ַ��ʼ�����");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("��������ڵ�ַ����");
		}
		return entryAddress;

	}

	public LinkedList<EntryAddress> getAllEntryAddressAsList() {
		LinkedList<EntryAddress> entryAddress = new LinkedList<EntryAddress>();
		String sql = "Select * from url";
		ResultSet rs = connDB.executeQuery(sql);
		try {
			while (rs.next()) {
				EntryAddress e = new EntryAddress(rs.getString(2), rs
						.getString(3), rs.getString(4));
				entryAddress.add(e);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("��������ڵ�ַ����");
		}
		return entryAddress;

	}

	public int getTableLength() {
		int tableLength = 0;
		String sql = "Select * from url";
		ResultSet rs = connDB.executeQuery(sql);
		try {
			rs.last();
			tableLength = rs.getRow();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableLength;
	}

	/**
	 * �õ�һ����ڵ�ַ��¼
	 */
	public EntryAddress getEntryAddressByUrl(String url) {
		String sqlstr = "select * from url where url ='" + url + "'";
		try {
			ResultSet rs = connDB.executeQuery(sqlstr);
			if (rs.next()) {
				EntryAddress e = new EntryAddress(rs.getString(2), rs
						.getString(3), rs.getString(4));
				return e;
			} else
				return null;
		} catch (SQLException e1) {
			System.err.println("���ݿ�����쳣��");
		}
		return null;
	}

	/**
	 * �õ�һ����ڵ�ַ������
	 */
	public String getEntryAddressNameByUrl(String url) {
		String sqlstr = "select name from url where url ='" + url + "'";
		try {
			ResultSet rs = connDB.executeQuery(sqlstr);
			if (rs.next()) {
				return rs.getString(1);
			} else
				return null;
		} catch (SQLException e1) {
			System.err.println("���ݿ�����쳣��");
		}
		return null;
	}
	
	/**
	 * �õ�һ����ڵ�ַ�����
	 */
	public String getEntryAddressCategoryByUrl(String url) {
		String sqlstr = "select categoryName from url where url ='" + url + "'";
		try {
			ResultSet rs = connDB.executeQuery(sqlstr);
			if (rs.next()) {
				return rs.getString(1);
			} else
				return null;
		} catch (SQLException e1) {
			System.err.println("���ݿ�����쳣��");
		}
		return null;
	}

	/**
	 * �������õ���ڵ�ַ��¼
	 */
	public LinkedList<EntryAddress> getEntryAddressByCategory(
			String categoryName) {
		LinkedList<EntryAddress> result = new LinkedList<EntryAddress>();
		String sqlstr = "select * " + "from url where categoryName ='"
				+ categoryName + "'";
		try {
			ResultSet rs = connDB.executeQuery(sqlstr);// ѡ��������Ƶ���Ӧ��ϸ����
			while (rs.next()) {
				EntryAddress e = new EntryAddress(rs.getString(2), rs
						.getString(3), rs.getString(4));
				result.add(e);
			}
			return result;
		} catch (SQLException e1) {
			System.err.println("���ݿ�����쳣��");
		}
		return null;
	}

	/**
	 * �ж�ĳһ������Ƿ�����ĳһ�����
	 */
	public boolean isNameExisted(String category, String entryName) {
		String sqlstr = "select * from url where name ='" + entryName 
		              + "' and categoryName = '" + category + "'";
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
	 * �޸���ڵ�ַ��¼
	 */
	public boolean updateEntryAddress(String url, String name, String category) {
		String sqlstr = "update url set name = '" + name + "',categoryName = '"
				+ category + "' where url ='" + url + "'";

		if (!connDB.executeUpdate(sqlstr)) {
			System.err.println("���µ�ַ��¼" + url + "ʧ�ܣ� ");
			return false;
		} else
			return true;
	}
	
	/**
	 * �޸���ڵ�ַ�������
	 */
	public boolean updateEntryAddressCategory(String newCategory,
			String oldCategory) {
		String sqlstr = "update url set categoryName = '"
				+ newCategory + "' where categoryName ='" + oldCategory + "'";

		if (!connDB.executeUpdate(sqlstr)) {
			System.err.println("���µ�ַ��������" + oldCategory + "ʧ�ܣ� ");
			return false;
		} else
			return true;
	}
}
