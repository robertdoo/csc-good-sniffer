package dataStruct;

import java.util.LinkedList;

/**
 * һ��SearchResult��Ӧһ�γɹ������������ͽ���������������ѵ�ʱ�䡢�����Ĺؼ��־䡢�����Ľ��
 */
public class SearchResult {

	private long searchTime = 0;
	private Rule rule = null;
	private LinkedList<Result> results = null;
	
	//��Ԥ���йص��������ԣ�warning�����Ԥ��ֵ��isSensative���Ƿ���Ԥ������ı�־
	private double warning = 0.0;
	private boolean isSensative = false;
	
	public SearchResult(long searchTime
			           , Rule rule
			           , LinkedList<Result> results) 
	{
		this.searchTime = searchTime;
		this.rule = rule;
		this.results = results;
	}
	
	public long getSearchTime() {
		return searchTime;
	}
	
	public void setSearchTime(long searchTime) {
		this.searchTime = searchTime;
	}
	
	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public LinkedList<Result> getResults() {
		return results;
	}
	
	public void setResults(LinkedList<Result> results) {
		this.results = results;
	}

	public double getWarning() {
		return warning;
	}

	public void setWarning(double warning) {
		this.warning = warning;
	}

	public boolean isSensative() {
		return isSensative;
	}

	public void setSensative(boolean isSensative) {
		this.isSensative = isSensative;
	}
	
	/**
	 * ���һ���µĽ��
	 */
	public void addResult(Result result) {
		
		results.add(result);
	}
	
	/**
	 * ���һ���µĽ��
	 */
	public void addAllResult(LinkedList<Result> newResults) {
		
		for(Result r : newResults)
			results.add(r);
	}
	
	/**
	 * �ڿ�ͷ���һ���µĽ��
	 */
	public void addFirstResult(Result result) {
		
		results.addFirst(result);
	}

	/**
	 * ����һ�����
	 */
	public Result getResult(int index){
		
		return results.get(index);
	}
	
	/**
	 * ɾ��һ�����
	 */
	public void removeResult(int index){
		
		results.remove(index);
	}
	
	/**
	 * ���ؽ����
	 */
	public int getCountOfResults(){
		
		return results.size();
	}
	
	/**
	 * �ж��Ƿ����ĳ�����
	 */
	public boolean contains(Result result){
		
		for(Result r : results)
			if(r.equals(result))
				return true;
		
		return false;
	}
	
}
