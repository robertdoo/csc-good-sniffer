package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import dataStruct.Rule;


public class RuleProcess {

	private String userName;
	private ConnDB connDB = null;
	
	private final String DB_NAME="Rule";
	
	public ConnDB getConnDB() {
		return connDB;
	}
	
	public RuleProcess(String userName){
		this.userName = userName;
		connDB = new ConnDB(DB_NAME);
		if(!connDB.connectToDB())
			System.err.println("连接数据库异常~！");
		createNewRuleTable(userName);
	}
	
	/**
	 * 创建规则的新表
	 */
	public boolean createNewRuleTable(String userName) {
		if (!connDB.isTableExist(userName + "rule")) {
			String sqlstr = "CREATE TABLE  `rule`.`" + userName + "rule` ("
					+ "`RuleName` varchar(50) NOT NULL,"
					+ "`words` varchar(400) NOT NULL,"
					+ "`snippet` varchar(400) default NULL,"
					+ "`unwantedWords` varchar(400) default NULL,"
					+ "`language` varchar(10) NOT NULL,"
					+ "`fileType` varchar(10) default NULL,"
					+ "`site` varchar(200) default NULL,"
					+ "`searchCount` varchar(5) NOT NULL,"
					+ "`ruleMapping` varchar(50) NOT NULL,"
					+ "`categoryName` varchar(45) NOT NULL,"
					+ "`isRelative` tinyint(1) NOT NULL,"
					+ "`synonymWords` varchar(400) NOT NULL,"
					+ "PRIMARY KEY  (`RuleName`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			if (!connDB.executeUpdate(sqlstr)){
				System.err.println("建立新表" + userName + "rule失败~！");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 删除表
	 */
	public boolean deleteRuleTable(String userName) {
		if (connDB.isTableExist(userName + "rule")) {
			String sqlstr = "DROP TABLE  `rule`.`" + userName + "rule`;";
			if (!connDB.executeUpdate(sqlstr)){
				System.err.println("删除表" + userName + "rule失败~！");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param rulename
	 * @return Rule associated with rulename
	 */
	public Rule getRuleByName(String rulename)
	{
		String language, words, snippet, synonymWords;
		String unwantedWords, fileType, site, cnt, ruleMapping, category;
		boolean isRelative;
		String sqlstr = "select * from " + userName 
		+ "rule where RuleName ='"+rulename+"'";
		try {
		    ResultSet rs = connDB.executeQuery(sqlstr);//选择规则名称的响应详细内容
		    if (rs.next())
		    {
		    	words = rs.getString("words");
		    	snippet = rs.getString("snippet");
		    	unwantedWords = rs.getString("unwantedWords");
		    	language = rs.getString("language");
		    	fileType = rs.getString("fileType");
		    	site = rs.getString("site");
		    	cnt = rs.getString("searchCount");
		    	ruleMapping = rs.getString("ruleMapping");
		    	category = rs.getString("categoryName");
		    	isRelative = rs.getBoolean("isRelative");
		    	synonymWords = rs.getString("synonymWords");
		    	Rule rule = new Rule(rulename, language, words, snippet,
		    			unwantedWords, fileType, site, cnt, ruleMapping,
		    			category, isRelative, synonymWords);
		        return rule;
		    }
		    else return null;
		} catch (SQLException e1) {
			System.err.println("数据库存在异常！");
		} 
		return null;
	}
	
	/**
	 * 判断规则名称是否存在
	 */
	public boolean isRuleNameExisted(String ruleName) {
		String sqlstr = "select * from " + userName 
		              + "rule where RuleName ='"+ruleName+"'";
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
	 * @return LinkedList<String> 根据类别得到规则名称
	 */
	public LinkedList<String> getRulenameByCategory(String categoryName)
	{
		LinkedList<String> result = new LinkedList<String>();
		String sqlstr = "select RuleName from " + userName + "rule " +
		                "where categoryName = '" + categoryName +"'";
		try {
            ResultSet rs = connDB.executeQuery(sqlstr);//选择规则名称的响应详细内容
            while (rs.next()){
            	result.add(rs.getString(1));
            }
            return result;
		} catch (SQLException e1) {
			System.err.println("数据库存在异常！");
		} 
		return null;
	}
	
	/**
	 * @return LinkedList<String> 所有规则名称
	 */
	public LinkedList<String> getAllRulenames()
	{
		LinkedList<String> result = new LinkedList<String>();
		String sqlstr = "select RuleName from " + userName + "rule";
		try {
            ResultSet rs = connDB.executeQuery(sqlstr);//选择规则名称的响应详细内容
            while (rs.next()){
            	result.add(rs.getString(1));
            }
            return result;
		} catch (SQLException e1) {
			System.err.println("数据库存在异常！");
		} 
		return null;
	}
	
	/**
	 * @return String[] 所有规则名称
	 */
	public String[] getAllRulenamesAsArray()
	{
		LinkedList<String> result = getAllRulenames();
		if (result != null)
		{
			String[] array = new String[result.size()];
			for (int i = 0; i < result.size(); ++i)
			{
				array[i] = result.get(i);
			}
			return array;
		}
		return null;
	}
	
	/**
	 * 插入一条新规则
	 */
	public boolean addNewRule(String RuleName, String words, String snippet, String unwantedWords
			, String language, String fileType, String site, String searchCount, String ruleMapping
			, String categoryName, boolean isRelative, String synonymWords){
		String sqlstr = "insert into " + userName + "rule values('" + 
		                RuleName + "','" +
		                words + "','" +
		                snippet + "','" +
		                unwantedWords + "','" +
		                language + "','" +
		                fileType + "','" +
		                site + "','" +
		                searchCount + "','" +
		                ruleMapping + "','" +
		                categoryName + "'," +
		                isRelative + ",'" +
		                synonymWords + "')";
		
		if(!connDB.executeUpdate(sqlstr)){
			System.err.println("插入新规则失败~！");
			return false;
		}
		
		return true;
	}
	
	/**
	 * 删除指定规则
	 */
	public boolean deleteRule(String RuleName){
		String sqlstr = "delete from " + userName + "rule where RuleName = '" + RuleName + "'";
		
		if (!connDB.executeUpdate(sqlstr)){
			System.err.println("删除规则失败~！");
			return false;
		}
		return true;
	}
	
	/**
	 * 删除某一类别下所有规则
	 */
	public void deleteRuleByCategory(String categoryName){
		LinkedList<String> delRuleNames = getRulenameByCategory(categoryName);
		
		for(int i=0;i<delRuleNames.size();i++){
			deleteRule(delRuleNames.get(i));
		}
	}
	
	/**
	 * 修改规则
	 */
	public boolean updateRule(String RuleName, String words, String snippet, String unwantedWords
			, String language, String fileType, String site, String searchCount,
			String categoryName, boolean isRelative, String synonymWords)
	{
		String sqlstr = "update " + userName + "rule set words='" + words + "',snippet='"
				+ snippet + "',unwantedWords='" + unwantedWords
				+ "',language='" + language + "',fileType='" + fileType
				+ "',site='" + site + "',searchCount='" + searchCount
				+ "',categoryName='" + categoryName + "',isRelative=" + isRelative
				+ ",synonymWords='" + synonymWords 
				+ "' where ruleName = '" + RuleName + "'";
		if(!connDB.executeUpdate(sqlstr)){
			System.err.println("更新规则" + RuleName + "失败！");
			return false;
		}
		return true;
	}
	
	/**
	 * 修改规则类别名称
	 */
	public boolean updateRuleCategory(String newCategory,
			String oldCategory) {
		String sqlstr = "update " + userName + "rule set categoryName = '"
				+ newCategory + "' where categoryName ='" + oldCategory + "'";

		if (!connDB.executeUpdate(sqlstr)) {
			System.err.println("更新规则类别名称" + oldCategory + "失败！ ");
			return false;
		} else
			return true;
	}
	
}
