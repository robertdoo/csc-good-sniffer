package systemConfig;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Properties;

public class SystemParameter {
	public static Properties parameters = new Properties();
	
	public static void loadFromFile() {
		BufferedReader br = null;
		String line = "";
		String[] map = null;
		try{
			br = new BufferedReader(new FileReader("config.ini"));
		}catch (FileNotFoundException e){
			System.err.println("config.ini配置文件不存在!");
		}
		
		try {
			while ((line = br.readLine()) != null)
				if (line != null && !line.trim().equals("")) {
					map = line.split("=");
					parameters.setProperty(map[0], map[1]);
				}
		} catch (Exception e) {
			System.err.println("读取配置文件config.ini时出现异常!");
			loadAsDefault();
		}
	}
	
	public static void loadAsDefault() {
		parameters.setProperty("database.user", "root");
		parameters.setProperty("database.password", "root");
		parameters.setProperty("database.drivername", "com.mysql.jdbc.Driver");
		parameters.setProperty("database.connectionurl", "jdbc:mysql://localhost:3306/");
		
		parameters.setProperty("order.titlesimilarity", "0.5");
		
		parameters.setProperty("output.titleLength", "30");
		parameters.setProperty("output.contentLength", "90");
		parameters.setProperty("output.urlLength", "40");
	}

}
