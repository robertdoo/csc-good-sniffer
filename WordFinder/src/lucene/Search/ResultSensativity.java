package lucene.Search;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

import dataStruct.Result;
import dataStruct.SearchResult;


/**
 * 计算所有结果与关键词的敏感度
 */
public class ResultSensativity {
	
	//根据相关度排序
	class sensativityComparator implements Comparator<Result>{
		public int compare(Result r1, Result r2){
			if(r1.getSensativity()>r2.getSensativity())
				return -1;
			else if(r1.getSensativity()<r2.getSensativity())
				return 1;
			else
				return 0;
		}
	}
	//根据关键词个数排序
	class countComparator implements Comparator<Result>{
		public int compare(Result r1, Result r2){
			if(r1.getCountOfKeywords()>r2.getCountOfKeywords())
				return -1;
			else if(r1.getCountOfKeywords()<r2.getCountOfKeywords())
				return 1;
			else
				return 0;
		}
	}
	
	
	
	
	
	private LinkedList<String> wordList = new LinkedList<String>();
	private double sen;
	private int count;
	private Comparator<Result> cComp = new countComparator();
	private Comparator<Result> sComp = new sensativityComparator();
	private IKSegmentation seg;
	/**
	 * 计算每条结果与关键词的相关度
	 * 返回的结果是按相关度从高到低排序的
	 */
	public SearchResult calculateSensativity(SearchResult sResult) {
		if(sResult == null)
			return sResult;
		
		String keyword = sResult.getRule().getWords() + " "
				+ sResult.getRule().getSnippet();

		wordList.clear();
		seg = new IKSegmentation(new StringReader(keyword), true);
		Lexeme lex = null;
		;
		try {
			while ((lex = seg.next()) != null)
				if (wordList.indexOf(lex.getLexemeText()) == -1)
					wordList.add(lex.getLexemeText());
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			for (int i = 0; i < sResult.getCountOfResults(); i++) {
				Result result = sResult.getResult(i);
				sen = getSensativity(wordList, result.getTitle(), result.getContent());
				result.setSensativity(sen);
				count = getCount(wordList, result.getTitle(), result.getContent());
				result.setCountOfKeywords(count);
			}

			// 按关键词个数排序，降序
			Collections.sort(sResult.getResults(), cComp);

			// 按相关度排序，降序
			Collections.sort(sResult.getResults(), sComp);
		} catch (Exception e) {
			System.err.println("结果按相关度排序时出现异常！");
			e.printStackTrace();
		}
		
		return sResult;
	}
	
	/**
	 * 计算关键词和标题、内容的相关度
	 */
	public double getSensativity(LinkedList<String> wordList, String title, String content){
		double senty;
		
		senty = countOfWords(wordList, title+content)/(wordList.size()+0.0);
		
		return senty;
	}
	
	/**
	 * 计算标题和内容中包含关键词的个数
	 */
	public int getCount(LinkedList<String> wordList, String title, String content){
		int count = 0, len;
		String str = title + content;
		
		for(String s : wordList){
			len = str.length();
			str = str.replaceAll(s, "");
			count += (len - str.length())/s.length();
		}
		return count;
	}
	
	
	/**
	 * 计算字符串中包含的不同关键词的个数
	 */
	public int countOfWords(LinkedList<String> wordList, String str){
		int count = 0;
		
		for(String s : wordList){
			if(str.indexOf(s) != -1)
				count++;
		}
		
		return count;
	}
	
	/**
	 * 根据敏感度过滤结果
	 */
	public SearchResult sensativeFilter(SearchResult searchResult){
		if(searchResult == null)return null;
		
		double sensa = searchResult.getWarning();
		
		for(int i=searchResult.getCountOfResults()-1;i>=0;i--)
			if(searchResult.getResult(i).getSensativity() < sensa)
				searchResult.removeResult(i);
		
		return searchResult;
	}
	
	/**
	 * 返回敏感信息条数
	 */
	public int sensativeCount(SearchResult searchResult){
		int count = 0;
		double sensa = searchResult.getWarning();
		
		for(Result r : searchResult.getResults())
			if(r.getSensativity() >= sensa)
				count++;
		
		return count;
	}
}
