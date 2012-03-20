package database;

import java.sql.*;

import systemConfig.SystemParameter;

public class ConnDB {

	private Connection conn = null;
	private Statement st = null;
	private PreparedStatement pst = null;

	private String DRIVER_STR = "com.mysql.jdbc.Driver";
	private String USERNAME = "root";
	private String PASSWORD = "root";
	private String DSN = "jdbc:mysql://localhost:3306/";
    
	public ConnDB(String DBName) {
		String para;
		
		para = SystemParameter.parameters.getProperty("database.drivername");
		if(para != null && !para.trim().equals(""))DRIVER_STR = para;
		para = SystemParameter.parameters.getProperty("database.username");
		if(para != null && !para.trim().equals(""))USERNAME = para;
		para = SystemParameter.parameters.getProperty("database.password");
		if(para != null && !para.trim().equals(""))PASSWORD = para;
		para = SystemParameter.parameters.getProperty("database.connectionurl");
		if(para != null && !para.trim().equals(""))DSN = para;
		
		DSN += DBName+"?useUnicode=true&characterEncoding=UTF-8";
	}
    
    public Connection getConn() {
		return conn;
	}

	public boolean connectToDB(){
    	try {
			Class.forName(DRIVER_STR);
			conn=DriverManager.getConnection(DSN, USERNAME, PASSWORD);
			st = conn.createStatement();
		} catch (ClassNotFoundException e) {
			System.err.println(e.toString());
			return false;
		} catch (SQLException e) {
			System.err.println(e.toString());
			return false;
		}
		return true;
    }
	
	public PreparedStatement getPreparedStatement(String sqlstr) {
		try {
			pst = conn.prepareStatement(sqlstr);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("�쳣SQL���Ϊ��" + sqlstr);
		}
		return pst;
	}
    
    public ResultSet executeQuery(String sqlstr){
    	ResultSet rs = null;
    	
    	try {
			rs = st.executeQuery(sqlstr);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("�쳣SQL���Ϊ��" + sqlstr);
		}
    	
    	return rs;
    }
    
    public boolean executeUpdate(String sqlstr){   	
    	try {
			st.executeUpdate(sqlstr);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("�쳣SQL���Ϊ��" + sqlstr);
			return false;
		}
		
		return true;
    }
    
    public boolean closeConnection(){
    	try {
    		if(st != null)st.close();
			if(conn != null)conn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
    }
    
    public boolean isTableExist(String tableName){
    	try{
    	ResultSet rs  = conn.getMetaData().getTables(null, null, tableName, null );
    	  if (rs.next())  {

    		  return true;
    	}else  {
    		return false;
    	 } 
    	}catch(Exception e){
    		  e.printStackTrace();
    		  return false;
    	  }
    	
    }
    
    /**
	 * �������ļ�config.ini�ж�ȡ��ʼ����Ϣ
	 * �������ݿ��û���������
	 */
    /*
	private void readInitConfig()
	{
		BufferedReader bin = null;
		try{
			bin = new BufferedReader(new FileReader("config.ini"));
		}catch (FileNotFoundException e){
			System.err.println("config.ini�����ļ�������!");
		}
		
		String line = "";
		try{
			do {
				line = bin.readLine();
			}while (!line.startsWith("User="));
			username = line.substring(line.indexOf("=")+1).trim();  //������ݿ��û���
			line = bin.readLine();
			password = line.substring(line.indexOf("=")+1).trim();//������ݿ��û�����
			line = bin.readLine();
			DRIVER_STR = line.substring(line.indexOf("=")+1).trim();//������ݿ�����·��
			line = bin.readLine();
			dsn = line.substring(line.indexOf("=")+1).trim();//������ݿ����Ӳ���
		}catch (IOException e){
			username = "root";
			password = "root";
			DRIVER_STR = "com.mysql.jdbc.Driver";
			dsn = "jdbc:mysql://localhost:3306/";
			e.printStackTrace();
		}
	}
	*/
}
