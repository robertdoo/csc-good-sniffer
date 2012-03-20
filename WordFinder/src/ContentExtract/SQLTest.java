package ContentExtract;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Date;

import Filter.Filter;
import database.ConnDB;



public class SQLTest{

	/**
	 * @param args
	 */
	private ConnDB connDB;
	PreparedStatement ps;
	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		//SQLTest st = new SQLTest();
		//new NewThread(st).start();
		//new NewThread(st).start();
		//new NewThread(st).start();
		//st.showData();
		SQLTest st = new SQLTest("test");
		//st.doInsert();
		long begin = new Date().getTime();
		st.doSelect();
		long end = new Date().getTime();
		System.out.println("共耗时"+(end-begin)+"毫秒");
	}
	public SQLTest(String dbName){
		connDB = new ConnDB(dbName);
		connDB.connectToDB();
	}
	
	public SQLTest(){
		connDB = new ConnDB("userinfo");
		connDB.connectToDB();
	}
	public void doSelect(){
		Filter filter = new Filter(1000000);
		try{
		
		String sql = "select * from file";
		ResultSet rs = connDB.executeQuery(sql);
		int num = 0;
		while(rs.next()){
			String url = rs.getString(2);
			filter.BF(url);
			num++;
		}
		System.out.println("共处理"+num+"个数据");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void doInsert(){
		try{
			String sql = "insert into file(title,url,localPath) values('" + "hello"
			+ "','" + "www" + "','" + "null" + "')";
			for(int i=0;i<100000;i++){
				connDB.executeUpdate(sql);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void add()throws Exception{//非线程安全
		String sql = "insert into test(Password,Identity) values(?,?)";
		if(ps==null)ps = connDB.getConn().prepareStatement(sql);
		for(int i=1;i<=100;i++){
			ps.setInt(1, i);
			ps.setInt(2, i);
			ps.execute();
		}
		
	}
	
	public void showData()throws Exception{
		String sql = "select * from test;";
		Statement st = connDB.getConn().createStatement();
		System.out.println(st.execute(sql));
		 ResultSet rs = st.getResultSet();
		 rs.setFetchSize(10);
		
		 DatabaseMetaData dmd = connDB.getConn().getMetaData();
		 System.out.println(dmd.getJDBCMajorVersion());
		 System.out.println(dmd.getMaxColumnsInTable());
		 System.out.println(dmd.getMaxConnections());
		 System.out.println(dmd.getMaxStatements());
		/* int columnNum = metaData.getColumnCount();
		 for(int i=1;i<=columnNum;i++){
			 if(i>1)System.out.print(", ");
			 System.out.print(metaData.getColumnLabel(i));
		 }
		System.out.println();*/
		ResultSet resultSet = dmd.getTables(null, null, null, new String[]{"TABLE"});
		 ResultSetMetaData metaData = resultSet.getMetaData();
		int columnNum = metaData.getColumnCount();
		 for(int i=1;i<=columnNum;i++){
			 if(i>1)System.out.print(", ");
			 System.out.print(metaData.getColumnLabel(i));
		 }
		System.out.println();
		 while(resultSet.next()){
			 for(int i=1;i<=columnNum;i++){
				 if(i>1)System.out.print(",");
				 System.out.print(resultSet.getString(i));
			 }
			 System.out.println();
		 }
		
	}
	
	

}
