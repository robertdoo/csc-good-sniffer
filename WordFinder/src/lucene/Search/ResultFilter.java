package lucene.Search;

import dataStruct.SearchResult;

/**
 * 搜索结果的过滤器
 * 过滤掉相同的结果
 */
public class ResultFilter {
	
	/*
	public SearchResult filter(SearchResult searchResult){
		Result r1 = null, r2 =null;
		double titleld, contentld;
		
		for(int i=0;i<searchResult.getResults().size()-1;){
			r1 = searchResult.getResults().get(i);
			r2 = searchResult.getResults().get(i+1);
			
			titleld = LevenshteinDistance.getSimilarity(r1.getTitle(), r2.getTitle());
			contentld = LevenshteinDistance.getSimilarity(r1.getContent(), r2.getContent());

			//过滤掉标题完全一样，内容相似度大于0.8的结果
			//时间不一样的就认为不一样
			if(r1.getTime()==null && r2.getTime()==null)
				if(titleld == 1 && contentld > 0.8)
					searchResult.getResults().remove(i+1);
				else
					i++;
			else if(r1.getTime()!=null && r2.getTime()!=null)
				if(titleld == 1 && contentld > 0.8 && r1.getTime().compareTo(r2.getTime())==0)
				{
					searchResult.getResults().remove(i+1);
				}
				else
					i++;
			else
				i++;

		}
		
		return searchResult;
	}
	*/
	
	public SearchResult filter(SearchResult searchResult){
		if(searchResult == null)
			return searchResult;
		
		if(searchResult.getResults() == null)
			return searchResult;
		
		for(int i=0;i<searchResult.getCountOfResults()-1;i++){
			//过滤掉标题完全一样，链接也完全一样的结果
			for(int j=searchResult.getCountOfResults()-1;j>i;j--)
				if(searchResult.getResult(i).equals(searchResult.getResult(j)))
					searchResult.removeResult(j);

		}
		
		return searchResult;
	}
}
