package lucene.Search;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import dataStruct.AbstractSentence;
import dataStruct.IndexOfWord;


/**
 * 提取文本摘要的方法
 */
public class PickAbstract {
	
	private final String shengluehao = "…";
	private int TITLE_MAX_LENGTH = 30;
	private int CONTENT_MAX_LENGTH = 90;
	private int PATH_MAX_LENGTH = 40;

	public PickAbstract() {
		
	}
	
	public PickAbstract(int title_max_length, int content_max_length,
			int path_max_length) {
		super();
		TITLE_MAX_LENGTH = title_max_length;
		CONTENT_MAX_LENGTH = content_max_length;
		PATH_MAX_LENGTH = path_max_length;
	}

	public int getTITLE_MAX_LENGTH() {
		return TITLE_MAX_LENGTH;
	}

	public void setTITLE_MAX_LENGTH(int title_max_length) {
		TITLE_MAX_LENGTH = title_max_length;
	}

	public int getCONTENT_MAX_LENGTH() {
		return CONTENT_MAX_LENGTH;
	}

	public void setCONTENT_MAX_LENGTH(int content_max_length) {
		CONTENT_MAX_LENGTH = content_max_length;
	}

	public int getPATH_MAX_LENGTH() {
		return PATH_MAX_LENGTH;
	}

	public void setPATH_MAX_LENGTH(int path_max_length) {
		PATH_MAX_LENGTH = path_max_length;
	}

	//根据关键词的相关度排序摘要的句子
	public class correlationComparator implements Comparator<AbstractSentence>{
		public int compare(AbstractSentence as1, AbstractSentence as2){
			if(as1.getCorrelation()>as2.getCorrelation())
				return -1;
			else if(as1.getCorrelation()<as2.getCorrelation())
				return 1;
			else
				return 0;
		}
	}
	//根据句子在原文中的顺序排序摘要的句子
	public class idComparator implements Comparator<AbstractSentence>{
		public int compare(AbstractSentence as1, AbstractSentence as2){
			if(as1.getId()>as2.getId())
				return 1;
			else if(as1.getId()<as2.getId())
				return -1;
			else
				return 0;
		}
	}
	
	
	
	

	//提取标题的摘要
	public String pickTitleAbstract(String keyword, String title){
		//Date begin = new Date();
		String titleAbs;
		if(title.length()>TITLE_MAX_LENGTH)
			titleAbs = title.substring(0,TITLE_MAX_LENGTH) + "…";
		else
			titleAbs = title;
		//Date end = new Date();
		//System.out.println("提取标题摘要用时："+(end.getTime()-begin.getTime())+"毫秒~！");
		
		return titleAbs;
	}
	
	//提取内容的摘要
	public String pickContentAbstract(String keyword, String content) {
		//Date begin = new Date();
		String contentAbs = "";
		//内容中各个句子的链表
		LinkedList<AbstractSentence> sentenceList = new LinkedList<AbstractSentence>();
		//被选为摘要的句子的链表
		LinkedList<AbstractSentence> AbstractSentenceList = new LinkedList<AbstractSentence>();
		String sentence;
		int id, correlation;
		
		IndexOfWord iow = new IndexOfWord();
		StringBuffer contentBuf = new StringBuffer(content);
		char ch, nextch;
		
		//提取出各个句子
		id = 0;
		sentence = "";
		for (int i = 0; i < contentBuf.length(); i++) {
			ch = contentBuf.charAt(i);
			if (ch != '。' && ch != '？' && ch != '！')
				sentence += ch;
			else{
				sentence += ch;//如果此时碰到的是句号，那么这个句号要取
				if(i!=contentBuf.length()-1){
					nextch = contentBuf.charAt(i+1);
					if(nextch == '”'){
						sentence += nextch;//如果句号后面是反引号，那么也要取
						i++;
					}
				}	
				if(sentence.length()>0){
					id++;
					correlation = iow.countOfKeyWord(sentence, keyword);
						
					AbstractSentence as = new AbstractSentence(id, sentence, correlation);
					sentenceList.add(as);
				}
				sentence = "";
			}
		}
		
		if(sentence.length()>0){
			id++;
			correlation = iow.countOfKeyWord(sentence, keyword);
				
			AbstractSentence as = new AbstractSentence(id, sentence, correlation);
			sentenceList.add(as);
		}
		
		//按关键词的相关度排序
		Comparator<AbstractSentence> cComp = new correlationComparator();
		Collections.sort(sentenceList, cComp);
		
		int absLength = 0;
		for(int i=0;i<sentenceList.size();i++){
			AbstractSentence as = sentenceList.get(i);
			AbstractSentenceList.add(as);
			absLength += as.getSentence().length();
			if(absLength >= CONTENT_MAX_LENGTH)
				break;
		}
		//为了尽量提取出关键字附近的信息，最后一个句子不要从句子的开头开始截，从关键字两边分别截
		int relate_begin, relate_end, availableLength;
		try{
			sentence = AbstractSentenceList.getLast().getSentence();	
		}catch(Exception e) {
			sentence = "There is Exception occurs during pick last sentence's abstract!";
		}
		iow.indexOfKeyWord(sentence, keyword);
		relate_begin = iow.getFirstPosition();
		relate_end = relate_begin + iow.getFirstWordLength();
		availableLength = CONTENT_MAX_LENGTH - absLength + sentence.length();
		//System.out.println("availableLength:" + availableLength);
		if (sentence.length() >= availableLength) {
			if(relate_begin == -1){
				sentence = sentence.substring(0,availableLength);
			}
			else{
				while (relate_end - relate_begin < availableLength) {
					if (relate_begin > 0)
						relate_begin--;
					if (relate_end < sentence.length())
						relate_end++;
				}
				sentence = sentence.substring(relate_begin, relate_end);
			}
			AbstractSentenceList.getLast().setSentence(sentence);
		}
		
		//按id排序
		Comparator<AbstractSentence> idComp = new idComparator();
		Collections.sort(AbstractSentenceList, idComp);
		
		//组合成摘要文本
		for(int i=0;i<AbstractSentenceList.size();i++){
			AbstractSentence as = AbstractSentenceList.get(i);
			contentAbs += as.getSentence();
			if((i+1)<AbstractSentenceList.size())
				if(AbstractSentenceList.get(i+1).getId()!=as.getId()+1)
					contentAbs += shengluehao;
		}
		
		//规范化长度
		//contentAbs = contentAbs.substring(0,Math.min(contentAbs.length(), CONTENT_MAX_LENGTH));
		//Date end = new Date();
		//System.out.println("提取内容摘要用时："+(end.getTime()-begin.getTime())+"毫秒~！");
		contentAbs += shengluehao;
		
		return contentAbs;
	}
	
	// 路径的摘要
	public String pickPath(String pathString) {
		String path = null;

		pathString = pathString.replace("http://", "");
		if (pathString.length() > PATH_MAX_LENGTH)
			path = pathString.substring(0, PATH_MAX_LENGTH) + "…";
		else
			path = pathString;

		return path;
	}
	
}
