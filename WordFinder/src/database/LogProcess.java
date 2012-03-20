package database;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogProcess {

	private ConnDB connDB = null;

	private final String DB_NAME = "log";

	public static void main(String[] args) {
		String user = "njustboy";
		String operate = "load";
		String detail = "load";
		LogProcess sl = new LogProcess();
		sl.saveLog(user, operate, detail);
	}

	public LogProcess() {
		connDB = new ConnDB(DB_NAME);
		connDB.connectToDB();
	}

	public ConnDB getConnDB() {
		return connDB;
	}

	public void saveLog(String user, String operate, String detail) {
		String tableName = new SimpleDateFormat("yyyy-MM-dd")
				.format(new Date()).toString();
		tableName = "log" + tableName.replaceAll("-", "");

		// tableName = tableName.replace("-", "y");
		// tableName = tableName.replace("-", "m");
		// tableName += "d";

		// tableName = "operate_log";

		if (!connDB.isTableExist(tableName)) {
			System.out.println(tableName);
			String sql = "CREATE TABLE "
					+ tableName
					+ "("
					+ "`id` int(10) unsigned NOT NULL auto_increment,"
					+ "`User` varchar(20) NOT NULL,"
					+ "`operate` varchar(50) NOT NULL,"
					+ " `time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,"
					+ " `detail` varchar(300) default NULL,"
					+ "PRIMARY KEY  (`id`)"
					+ ") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;";

			if (connDB.executeUpdate(sql))
				System.out.println("成功创建表：" + tableName);
		}

		String sql = "insert into " + tableName
				+ "(user,operate,detail) values('" + user + "','" + operate
				+ "','" + detail + "')";
		//System.out.println(operate + ":" + detail);
		if (connDB.executeUpdate(sql))
			System.out.println("成功保存日志！");

		connDB.closeConnection();
	}

	public boolean deleteLog(String tableName, String time) {
		String sql = "delete from "+tableName+" where time='"+time+"'";
		if (connDB.executeUpdate(sql)){
	System.out.println("成功保存日志！！");
		return true;
		}else{
			return false;
		}
	}

}
