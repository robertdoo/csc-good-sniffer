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
			System.err.println("数据库连接异常！");
		
		if (!connDB.isTableExist("registerinfo")) {
			String sqlstr = "CREATE TABLE  `userinfo`.`registerinfo` ("
					+ "`UserName` varchar(20) NOT NULL,"
					+ "`Password` varchar(20) NOT NULL,"
					+ "`Identity` varchar(20) NOT NULL,"
					+ "LastLoginTime varchar(45) NOT NULL,"
					+ "PRIMARY KEY  (`UserName`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			if (!connDB.executeUpdate(sqlstr))
				System.err.println("建立新表registerinfo失败~！");	
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
	 * 检查用户名、密码，登录身份是否存在，用于登录时的操作
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
			System.err.println("判断用户登录信息是否正确时出现异常。");
			return false;
		}
	}
	
	/**
	 * 判断用户名是否已存在 
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
			System.err.println("判断用户是否已存在时出现异常。");
			return false;
		}
	}
	
	/**
	 * 添加一名新用户
	 */
	public boolean addUser(String userName, String password, String identity) {
		String currentTime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(new Date());	
		String sqlstr = "insert into registerinfo values('" + userName + "','"
				+ password + "','" + identity + "','" + currentTime +"')";
		if (!connDB.executeUpdate(sqlstr))
			return false;
		
		//创建用户的同时创建属于用户的规则表
		RuleProcess rp = new RuleProcess(userName);
		rp.getConnDB().closeConnection();

		CategoryProcess cp = new CategoryProcess(userName);
		cp.getConnDB().closeConnection();
		
		return true;
	}
	
	/**
	 * 删除用户，同时删除用户规则表和分类表
	 */
	public boolean deleteUser(String userName) {
		String sqlstr = "delete from registerinfo where UserName = '" + userName + "'";
		if (!connDB.executeUpdate(sqlstr)){
			System.err.println("删除用户记录" + userName + "时出现异常！ ");
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
	 * 修改用户名
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
	 * 修改用户密码
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
	 * 修改用户身份
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
	 * 将当前时间置为用户的登录时间
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
	 * 取得用户的上次登录时间
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
			System.err.println("取得用户上次登录时间时出现异常。");
		}
		
		return lastLoginTime;
	}
}
