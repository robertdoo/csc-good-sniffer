package dataStruct;

import java.util.LinkedList;

/**
 * 一个SearchResult对应一次成功搜索的条件和结果，包括搜索花费的时间、搜索的关键字句、搜索的结果
 */
public class SearchResult {

	private long searchTime = 0;
	private Rule rule = null;
	private LinkedList<Result> results = null;
	
	//与预警有关的两个属性，warning是最低预警值，isSensative是是否作预警处理的标志
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
	 * 添加一个新的结果
	 */
	public void addResult(Result result) {
		
		results.add(result);
	}
	
	/**
	 * 添加一列新的结果
	 */
	public void addAllResult(LinkedList<Result> newResults) {
		
		for(Result r : newResults)
			results.add(r);
	}
	
	/**
	 * 在开头添加一个新的结果
	 */
	public void addFirstResult(Result result) {
		
		results.addFirst(result);
	}

	/**
	 * 返回一条结果
	 */
	public Result getResult(int index){
		
		return results.get(index);
	}
	
	/**
	 * 删除一条结果
	 */
	public void removeResult(int index){
		
		results.remove(index);
	}
	
	/**
	 * 返回结果数
	 */
	public int getCountOfResults(){
		
		return results.size();
	}
	
	/**
	 * 判断是否包含某条结果
	 */
	public boolean contains(Result result){
		
		for(Result r : results)
			if(r.equals(result))
				return true;
		
		return false;
	}
	
}
