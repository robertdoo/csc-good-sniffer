package lucene.Search;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;

import systemConfig.SystemParameter;

import dataStruct.Result;
import dataStruct.SearchResult;


/**
 * 结果排序的类
 * 根据标题排序结果
 */
public class ResultOrder {

	private final String preTag = "<font color='red'>";
	private final String postTag = "</font>";
	private double TITLE_SIMILARITY = 0.5;   //标题的相似度
	
	public ResultOrder() {
		
	}

	/*
	 * 用于Collections进行list排序时的参数，实现了Comparator接口，重写了compare方法
	 */
	//根据时间排序搜索结果
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
	//根据标题的相似度排序搜索结果
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
	
	
	
	
	
	
	//从搜索结果中提取出与title具有相近标题的结果
	public SearchResult order(SearchResult searchResult, String title){
		SearchResult orderResult = null;
		LinkedList<Result> order_results = new LinkedList<Result>();//存放搜索的各条结果
		long sTime = 0;
		double simi;
		
		if(searchResult == null){
			System.out.println("没有搜索结果，排序失败~！");
			return searchResult;
		}
		
		if(SystemParameter.parameters.getProperty("order.titlesimilarity") != null)
			TITLE_SIMILARITY = Double.valueOf(SystemParameter.parameters.getProperty("order.titlesimilarity"));
		title = titleParse(title);//先对输入的标题进行处理
		Date begin_time = new Date();
		LinkedList<Result> search_results = searchResult.getResults();
		for(int i=0;i<search_results.size();i++){
			Result sResult = search_results.get(i);
			String sResultTitle = sResult.getTitle();
			
			sResultTitle = sResultTitle.trim().replace(preTag, "").replace(postTag, "");
			sResultTitle = titleParse(sResultTitle);//对标题进行处理
			
			simi = LevenshteinDistance.getSimilarity(sResultTitle, title);//计算相似度
			sResult.setSimilarity(simi);
			System.out.println("\"" + sResultTitle + "\"和\"" + title + "\"的相似度是：" + simi);
			if(simi > TITLE_SIMILARITY)
				order_results.add(sResult);
		}
		//按时间排序，升序
		Comparator<Result> tComp = new timeComparator();
		Collections.sort(order_results, tComp);
		
		Date end_time = new Date();
		sTime = end_time.getTime() - begin_time.getTime();//得到结果耗费的时间
		orderResult = new SearchResult(sTime
				                       , searchResult.getRule()
				                       , order_results);
		orderResult.setWarning(searchResult.getWarning());
		orderResult.setSensative(searchResult.isSensative());
		
		return orderResult;
	}
	
	public String titleParse(String title){
		/*
		while(title.indexOf("【") != -1 && title.indexOf("】") != -1){//【】里面的不要
			title = title.replaceAll(title.substring(
					title.indexOf("【"),
					title.indexOf("】")+1
					), "");
		}
		while(title.indexOf("[") != -1 && title.indexOf("]") != -1){//[]里面的不要
			title = title.replaceAll(title.substring(
					title.indexOf("["),
					title.indexOf("]")+1
					), "");
		}
		*/
		title = title.replaceAll(" ", "");//空格不要
		title = title.replaceAll("	", "");//制表符不要
		title = title.replaceAll("\r|\n", "");//换行符不要
		title = title.replaceAll("_.*", "");//_后面的不要
		title = title.replaceAll("—.*", "");//—后面的不要
		title = title.replaceAll("-.*", "");//-后面的不要
		
		return title;
	}
	
}
