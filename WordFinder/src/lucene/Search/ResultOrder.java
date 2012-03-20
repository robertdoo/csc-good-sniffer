package lucene.Search;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;

import systemConfig.SystemParameter;

import dataStruct.Result;
import dataStruct.SearchResult;


/**
 * ����������
 * ���ݱ���������
 */
public class ResultOrder {

	private final String preTag = "<font color='red'>";
	private final String postTag = "</font>";
	private double TITLE_SIMILARITY = 0.5;   //��������ƶ�
	
	public ResultOrder() {
		
	}

	/*
	 * ����Collections����list����ʱ�Ĳ�����ʵ����Comparator�ӿڣ���д��compare����
	 */
	//����ʱ�������������
	class timeComparator implements Comparator<Result>{
		public int compare(Result r1, Result r2){
			if(r1.getTime() == null || r2.getTime() == null){
				if(r1.getTime() == null && r2.getTime() !=null)
					return -1;
				else if(r1.getTime() != null && r2.getTime() ==null)
					return 1;
				else
					return 0;
			}
			else if(r1.getTime().after(r2.getTime()))
				return 1;
			else if(r1.getTime().before(r2.getTime()))
				return -1;
			else 
				return 0;
		}
	}
	//���ݱ�������ƶ������������
	class similarityComparator implements Comparator<Result>{
		public int compare(Result r1, Result r2){
			if(r1.getSimilarity()>r2.getSimilarity())
				return -1;
			else if(r1.getSimilarity()<r2.getSimilarity())
				return 1;
			else
				return 0;
		}
	}
	
	
	
	
	
	
	//�������������ȡ����title�����������Ľ��
	public SearchResult order(SearchResult searchResult, String title){
		SearchResult orderResult = null;
		LinkedList<Result> order_results = new LinkedList<Result>();//��������ĸ������
		long sTime = 0;
		double simi;
		
		if(searchResult == null){
			System.out.println("û���������������ʧ��~��");
			return searchResult;
		}
		
		if(SystemParameter.parameters.getProperty("order.titlesimilarity") != null)
			TITLE_SIMILARITY = Double.valueOf(SystemParameter.parameters.getProperty("order.titlesimilarity"));
		title = titleParse(title);//�ȶ�����ı�����д���
		Date begin_time = new Date();
		LinkedList<Result> search_results = searchResult.getResults();
		for(int i=0;i<search_results.size();i++){
			Result sResult = search_results.get(i);
			String sResultTitle = sResult.getTitle();
			
			sResultTitle = sResultTitle.trim().replace(preTag, "").replace(postTag, "");
			sResultTitle = titleParse(sResultTitle);//�Ա�����д���
			
			simi = LevenshteinDistance.getSimilarity(sResultTitle, title);//�������ƶ�
			sResult.setSimilarity(simi);
			System.out.println("\"" + sResultTitle + "\"��\"" + title + "\"�����ƶ��ǣ�" + simi);
			if(simi > TITLE_SIMILARITY)
				order_results.add(sResult);
		}
		//��ʱ����������
		Comparator<Result> tComp = new timeComparator();
		Collections.sort(order_results, tComp);
		
		Date end_time = new Date();
		sTime = end_time.getTime() - begin_time.getTime();//�õ�����ķѵ�ʱ��
		orderResult = new SearchResult(sTime
				                       , searchResult.getRule()
				                       , order_results);
		orderResult.setWarning(searchResult.getWarning());
		orderResult.setSensative(searchResult.isSensative());
		
		return orderResult;
	}
	
	public String titleParse(String title){
		/*
		while(title.indexOf("��") != -1 && title.indexOf("��") != -1){//��������Ĳ�Ҫ
			title = title.replaceAll(title.substring(
					title.indexOf("��"),
					title.indexOf("��")+1
					), "");
		}
		while(title.indexOf("[") != -1 && title.indexOf("]") != -1){//[]����Ĳ�Ҫ
			title = title.replaceAll(title.substring(
					title.indexOf("["),
					title.indexOf("]")+1
					), "");
		}
		*/
		title = title.replaceAll(" ", "");//�ո�Ҫ
		title = title.replaceAll("	", "");//�Ʊ����Ҫ
		title = title.replaceAll("\r|\n", "");//���з���Ҫ
		title = title.replaceAll("_.*", "");//_����Ĳ�Ҫ
		title = title.replaceAll("��.*", "");//������Ĳ�Ҫ
		title = title.replaceAll("-.*", "");//-����Ĳ�Ҫ
		
		return title;
	}
	
}
