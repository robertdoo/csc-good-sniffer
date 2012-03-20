package lucene.Search;

import dataStruct.SearchResult;

/**
 * ��������Ĺ�����
 * ���˵���ͬ�Ľ��
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

			//���˵�������ȫһ�����������ƶȴ���0.8�Ľ��
			//ʱ�䲻һ���ľ���Ϊ��һ��
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
			//���˵�������ȫһ��������Ҳ��ȫһ���Ľ��
			for(int j=searchResult.getCountOfResults()-1;j>i;j--)
				if(searchResult.getResult(i).equals(searchResult.getResult(j)))
					searchResult.removeResult(j);

		}
		
		return searchResult;
	}
}
