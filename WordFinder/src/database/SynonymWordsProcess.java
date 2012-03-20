package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;

public class SynonymWordsProcess {
	private ConnDB connDB = null;

	private final String DB_NAME = "synonym";
	
	public ConnDB getConnDB() {
		return connDB;
	}
	
	public SynonymWordsProcess() {
		connDB = new ConnDB(DB_NAME);
		if (!connDB.connectToDB())
			System.err.println("���ݿ������쳣��");
		
		if (!connDB.isTableExist("synonymwords")) {
			String sqlstr = "CREATE TABLE  `synonym`.`synonymwords` ("
					+ "`id` int(10) unsigned NOT NULL auto_increment,"
					+ "`words` varchar(500) default NULL,"
					+ "PRIMARY KEY  (`id`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			if (!connDB.executeUpdate(sqlstr))
				System.err.println("�����±�synonymwordsʧ��~��");	
		}
	}
	
	public LinkedList<String> getAllWords() {
		LinkedList<String> words = new LinkedList<String>();
		
		String sqlstr = "select words from synonymwords";
		ResultSet rs = connDB.executeQuery(sqlstr);
		try {
			while(rs.next())
				words.add(rs.getString("words"));
		} catch (SQLException e) {
			System.err.println("ȡ�����й�����ʱ�����쳣");
		}
		
		return words;
	}
	
	public String[] getAllWordsAsString() {
		LinkedList<String> words = getAllWords();
		String[] wordStrings = new String[words.size()];
		for(int i=0;i<words.size();i++)
			wordStrings[i] = words.get(i);
		return wordStrings;
	}
	
	public String[] getWordsByWord(String word) {
		LinkedList<String> words = new LinkedList<String>();
		String sqlstr = "select words from synonymwords" +
				" where words like '%" + word + "%';";
		ResultSet rs = connDB.executeQuery(sqlstr);
		
		try {
			while(rs.next())
				words.add(rs.getString("words"));
		} catch (SQLException e) {
			System.err.println("ȡ�����й�����ʱ�����쳣");
		}
		
		String[] wordStrings = new String[words.size()];
		for(int i=0;i<words.size();i++)
			wordStrings[i] = words.get(i);
		return wordStrings;
	}
	
	public String getWordsById(String id) {
		String sqlstr = "select words from synonymwords" +
				" where id = '" + id + "';";
		ResultSet rs = connDB.executeQuery(sqlstr);
		
		try {
			if(rs.next())
				return rs.getString("words");
		} catch (SQLException e) {
			System.err.println("ȡ��id" + id + "�Ĺ�����ʱ�����쳣");
		}
		
		return null;
	}
	
	public String getIdByWords(String words) {
		String sqlstr = "select id from synonymwords" +
				" where words = '" + words + "';";
		ResultSet rs = connDB.executeQuery(sqlstr);
		
		try {
			if(rs.next())
				return rs.getString("id");
		} catch (SQLException e) {
			System.err.println("ȡ��������" + words + "�ļ�¼ʱ�����쳣");
		}
		
		return null;
	}
	
	public boolean addNewWords(String newWords) {
		PreparedStatement pstmt = connDB.getPreparedStatement(
				"insert into synonymwords(words) values(?)");
		try {
			pstmt.setString(1, newWords);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("����µĹ����ʼ�¼" + newWords + "ʱ�����쳣");
			return false;
		}
		
		return true;
	}
	
	public boolean deleteWords(String id) {
		PreparedStatement pstmt = connDB.getPreparedStatement(
		"delete from synonymwords where id=?");
		
		try {
			pstmt.setString(1, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("ɾ�������ʼ�¼" + id + "ʱ�����쳣");
			return false;
		}
		
		return true;
	}
	
	public boolean updateWords(String id, String words) {
		PreparedStatement pstmt = connDB.getPreparedStatement(
		"update synonymwords set words=? where id=?");
		
		try {
			pstmt.setString(1, words);
			pstmt.setString(2, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("�޸Ĺ����ʼ�¼" + words + "ʱ�����쳣");
			return false;
		}
		
		return true;
	}
	
	public synchronized String replaceSynonym(String oriWords) {
		if(oriWords == null || oriWords.equals(""))
			return "";
		
		String synWords = "";
		HashSet<String> synSet = new HashSet<String>();
		
		String[] words = oriWords.split(" "), syns, temp;
		for(int i=0;i<words.length;i++){
			syns = getWordsByWord(words[i]);
			if(syns.length == 0)
				synSet.add(words[i]);
			else
				for(int j=0;j<syns.length;j++){
					temp = syns[j].split(" ");
					for(int m=0;m<temp.length;m++)
						synSet.add(temp[m]);
				}
		}
		Object[] os = synSet.toArray();
		for(Object o : os)
			synWords += (String)o + " ";
		
		return synWords;
	}
}
