package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserProcess {
	private ConnDB connDB = null;

	private final String DB_NAME = "userinfo";
	
	public ConnDB getConnDB() {
		return connDB;
	}

	public UserProcess() {
		connDB = new ConnDB(DB_NAME);
		if (!connDB.connectToDB())
			System.err.println("���ݿ������쳣��");
		
		if (!connDB.isTableExist("registerinfo")) {
			String sqlstr = "CREATE TABLE  `userinfo`.`registerinfo` ("
					+ "`UserName` varchar(20) NOT NULL,"
					+ "`Password` varchar(20) NOT NULL,"
					+ "`Identity` varchar(20) NOT NULL,"
					+ "LastLoginTime varchar(45) NOT NULL,"
					+ "PRIMARY KEY  (`UserName`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			if (!connDB.executeUpdate(sqlstr))
				System.err.println("�����±�registerinfoʧ��~��");	
		}
	}
	
	public String[] getAllUsers(){
		String[] users = new String[this.getTableLength()];
		int index = 0;
		String sql = "Select UserName from registerinfo";
		ResultSet rs = connDB.executeQuery(sql);
		try{
		while(rs.next()){
			users[index++] = rs.getString(1);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return users;
		
	}
	
	public int getTableLength() {
		int tableLength = 0;
		String sql = "Select * from registerinfo";
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
	 * ����û��������룬��¼����Ƿ���ڣ����ڵ�¼ʱ�Ĳ���
	 */
	public boolean isLegalUser(String username, String password, String identity) {
		String sqlstr = "select * from registerinfo where UserName = '"
				+ username + "' and Password = '" + password + "' and "
				+ "Identity = '" + identity + "';";
		ResultSet rs = connDB.executeQuery(sqlstr);
		try {
			if (rs.next())
				return true;
			else
				return false;
		} catch (SQLException e) {
			System.err.println("�ж��û���¼��Ϣ�Ƿ���ȷʱ�����쳣��");
			return false;
		}
	}
	
	/**
	 * �ж��û����Ƿ��Ѵ��� 
	 */
	public boolean isUserExisted(String username) {
		String sqlstr = "select * from registerinfo where UserName = '"
			            + username + "'";
		ResultSet rs = connDB.executeQuery(sqlstr);
		try {
			if(rs.next())
				return true;
			else
				return false;
		} catch (SQLException e) {
			System.err.println("�ж��û��Ƿ��Ѵ���ʱ�����쳣��");
			return false;
		}
	}
	
	/**
	 * ���һ�����û�
	 */
	public boolean addUser(String userName, String password, String identity) {
		String currentTime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(new Date());	
		String sqlstr = "insert into registerinfo values('" + userName + "','"
				+ password + "','" + identity + "','" + currentTime +"')";
		if (!connDB.executeUpdate(sqlstr))
			return false;
		
		//�����û���ͬʱ���������û��Ĺ����
		RuleProcess rp = new RuleProcess(userName);
		rp.getConnDB().closeConnection();

		CategoryProcess cp = new CategoryProcess(userName);
		cp.getConnDB().closeConnection();
		
		return true;
	}
	
	/**
	 * ɾ���û���ͬʱɾ���û������ͷ����
	 */
	public boolean deleteUser(String userName) {
		String sqlstr = "delete from registerinfo where UserName = '" + userName + "'";
		if (!connDB.executeUpdate(sqlstr)){
			System.err.println("ɾ���û���¼" + userName + "ʱ�����쳣�� ");
			return false;
		}
		RuleProcess rp = new RuleProcess(userName);
		if(!rp.deleteRuleTable(userName)){
			rp.getConnDB().closeConnection();
			return false;
		}
		rp.getConnDB().closeConnection();

		CategoryProcess cp = new CategoryProcess(userName);
		if(!cp.deleteCategoryTable(userName)){
			cp.getConnDB().closeConnection();
			return false;
		}
		cp.getConnDB().closeConnection();
		return true;
	}
	
	/**
	 * �޸��û���
	 */
	public boolean updateUsername(String userName, String newName){
		String sqlstr = "update registerinfo set UserName = '" + 
               newName + "' where UserName = '" + userName + "'";
		if(connDB.executeUpdate(sqlstr))
			return true;
		else
			return false;
	}
	
	/**
	 * �޸��û�����
	 */
	public boolean updatePassword(String userName, String password) {
		String sqlstr = "update registerinfo set Password = '" + password
				+ "' where UserName = '" + userName + "'";
		if (connDB.executeUpdate(sqlstr))
			return true;
		else
			return false;
	}
	
	/**
	 * �޸��û����
	 */
	public boolean updateIdentity(String userName, String identity) {
		String sqlstr = "update registerinfo set Identity = '" + identity
				+ "' where UserName = '" + userName + "'";
		if (connDB.executeUpdate(sqlstr))
			return true;
		else
			return false;
	}

	/**
	 * ����ǰʱ����Ϊ�û��ĵ�¼ʱ��
	 */
	public boolean setLoginTime(String userName) {
		String currentTime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(new Date());
		String sqlstr = "update registerinfo set LastLoginTime = '" + currentTime
				+ "' where UserName = '" + userName + "'";
		if (connDB.executeUpdate(sqlstr))
			return true;
		else
			return false;
	}
	
	
	/**
	 * ȡ���û����ϴε�¼ʱ��
	 */
	public String getLoginTime(String userName) {
		String lastLoginTime = "";

		String sqlstr = "select LastLoginTime from registerinfo where UserName = '"
				+ userName + "'";
		ResultSet rs = connDB.executeQuery(sqlstr);
		try {
			if (rs.next())
				lastLoginTime = rs.getString("LastLoginTime");
		} catch (SQLException e) {
			System.err.println("ȡ���û��ϴε�¼ʱ��ʱ�����쳣��");
		}
		
		return lastLoginTime;
	}
}
