package dataStruct;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;

import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

public class IndexOfWord {

	private String firstWord;
	private int firstWordPosition;
	private int firstWordLength;
	
	public String getFirstWord() {
		return firstWord;
	}

	public int getFirstPosition() {
		return firstWordPosition;
	}

	public int getFirstWordLength() {
		return firstWordLength;
	}

	public IndexOfWord() {

	}

	/**
	 * 在一个句子中，第一个关键词出现的位置
	 */
	public void indexOfKeyWord(String text, String keyword) {
		firstWord = null;
		firstWordPosition = -1;
		firstWordLength = 0;
		
		LinkedList<String> keywords = new LinkedList<String>();
		int p, l = -1, firstP = Integer.MAX_VALUE;

		IKSegmentation seg = new IKSegmentation(new StringReader(keyword));
		
		Lexeme lex = null;;
		try {
			while ((lex = seg.next())!=null) 
				keywords.add(lex.getLexemeText());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(int i=0;i<keywords.size();i++){
			p = text.indexOf((String)keywords.get(i));
			if(p > -1 && p < firstP){
				firstP = p;
				l = i;
			}
		}
		if (firstP != Integer.MAX_VALUE) {
			firstWordPosition = firstP;
			firstWord = (String) keywords.get(l);
			firstWordLength = firstWord.length();
		}
		
	}
	
	/**
	 * 在一个句子中，关键词出现的次数
	 */
	public int countOfKeyWord(String text, String keyword) {
		
		LinkedList<String> keywords = new LinkedList<String>();
		int p, count;

		IKSegmentation seg = new IKSegmentation(new StringReader(keyword));
		
		Lexeme lex = null;;
		try {
			while ((lex = seg.next())!=null) 
				keywords.add(lex.getLexemeText());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String word, sentence;
		count = 0;
		for(int i=0;i<keywords.size();i++){
			sentence = text;
			word = keywords.get(i);
			p = sentence.indexOf(word);
			while(p!=-1){
				count++;
				sentence = sentence.substring(0,p) + sentence.substring(p+word.length());
				p = sentence.indexOf(word);
			}
		}

		return count;
	}
}
