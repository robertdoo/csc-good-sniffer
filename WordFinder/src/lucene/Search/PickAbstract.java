package lucene.Search;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import dataStruct.AbstractSentence;
import dataStruct.IndexOfWord;


/**
 * ��ȡ�ı�ժҪ�ķ���
 */
public class PickAbstract {
	
	private final String shengluehao = "��";
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

	//���ݹؼ��ʵ���ض�����ժҪ�ľ���
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
	//���ݾ�����ԭ���е�˳������ժҪ�ľ���
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
	
	
	
	

	//��ȡ�����ժҪ
	public String pickTitleAbstract(String keyword, String title){
		//Date begin = new Date();
		String titleAbs;
		if(title.length()>TITLE_MAX_LENGTH)
			titleAbs = title.substring(0,TITLE_MAX_LENGTH) + "��";
		else
			titleAbs = title;
		//Date end = new Date();
		//System.out.println("��ȡ����ժҪ��ʱ��"+(end.getTime()-begin.getTime())+"����~��");
		
		return titleAbs;
	}
	
	//��ȡ���ݵ�ժҪ
	public String pickContentAbstract(String keyword, String content) {
		//Date begin = new Date();
		String contentAbs = "";
		//�����и������ӵ�����
		LinkedList<AbstractSentence> sentenceList = new LinkedList<AbstractSentence>();
		//��ѡΪժҪ�ľ��ӵ�����
		LinkedList<AbstractSentence> AbstractSentenceList = new LinkedList<AbstractSentence>();
		String sentence;
		int id, correlation;
		
		IndexOfWord iow = new IndexOfWord();
		StringBuffer contentBuf = new StringBuffer(content);
		char ch, nextch;
		
		//��ȡ����������
		id = 0;
		sentence = "";
		for (int i = 0; i < contentBuf.length(); i++) {
			ch = contentBuf.charAt(i);
			if (ch != '��' && ch != '��' && ch != '��')
				sentence += ch;
			else{
				sentence += ch;//�����ʱ�������Ǿ�ţ���ô������Ҫȡ
				if(i!=contentBuf.length()-1){
					nextch = contentBuf.charAt(i+1);
					if(nextch == '��'){
						sentence += nextch;//�����ź����Ƿ����ţ���ôҲҪȡ
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
		
		//���ؼ��ʵ���ض�����
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
		//Ϊ�˾�����ȡ���ؼ��ָ�������Ϣ�����һ�����Ӳ�Ҫ�Ӿ��ӵĿ�ͷ��ʼ�أ��ӹؼ������߷ֱ��
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
		
		//��id����
		Comparator<AbstractSentence> idComp = new idComparator();
		Collections.sort(AbstractSentenceList, idComp);
		
		//��ϳ�ժҪ�ı�
		for(int i=0;i<AbstractSentenceList.size();i++){
			AbstractSentence as = AbstractSentenceList.get(i);
			contentAbs += as.getSentence();
			if((i+1)<AbstractSentenceList.size())
				if(AbstractSentenceList.get(i+1).getId()!=as.getId()+1)
					contentAbs += shengluehao;
		}
		
		//�淶������
		//contentAbs = contentAbs.substring(0,Math.min(contentAbs.length(), CONTENT_MAX_LENGTH));
		//Date end = new Date();
		//System.out.println("��ȡ����ժҪ��ʱ��"+(end.getTime()-begin.getTime())+"����~��");
		contentAbs += shengluehao;
		
		return contentAbs;
	}
	
	// ·����ժҪ
	public String pickPath(String pathString) {
		String path = null;

		pathString = pathString.replace("http://", "");
		if (pathString.length() > PATH_MAX_LENGTH)
			path = pathString.substring(0, PATH_MAX_LENGTH) + "��";
		else
			path = pathString;

		return path;
	}
	
}
