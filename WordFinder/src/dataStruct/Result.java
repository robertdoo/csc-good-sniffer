package dataStruct;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * һ���������
 */
public class Result {
	
	private String title = "";//����
	private String content = "";//����
	private String url = "";//���������
	private Date time = null;//ʱ��
	private String filetype = "";//�ļ�����
	private String pageMirrorPath = "";//��ҳ��������
	private String source = "spider";//��Դ
	
	private double sensativity = 0.0;//���������ؼ��ʵ���ضȣ�����������ͬ�Ĺؼ��ʣ�
	private int countOfKeywords = 0;//�����ؼ��ʵĸ������ؼ����ܹ��м�����
	private double similarity = 0.0;//���ƶȣ����ڸ��ݱ�������ƶȽ�������ʱ
	private Rule rule = null;

	public Result() {
		
	}

	public Result(String title, String content, String url, Date time,
			String filetype, String pageMirrorPath, String source) {
		this.title = title;
		this.content = content;
		this.url = url;
		this.time = time;
		this.filetype = filetype;
		this.pageMirrorPath = pageMirrorPath;
		this.source = source;
	}
	
	public Result(SpiderData spiderData){
		SimpleDateFormat format = new SimpleDateFormat(
		"yyyy-MM-dd HH:mm:ss");
		this.title = spiderData.getTitle();
		this.content = spiderData.getContent();
		this.url = spiderData.getUrl();
		try{
		this.time = format.parse(spiderData.getTime());
		}catch(Exception e){
			try{
			this.time = new Date();
			}catch(Exception e1){}
		}
		this.filetype = spiderData.getType();
		this.pageMirrorPath = spiderData.getPageMirrorPath();
		this.source = spiderData.getPurl();
	}
	
	/*
	public Result(searchUtility.SearchResultItem searchResultItem){
		this.title = searchResultItem.getTitle();
		this.content = searchResultItem.getSnippet();
		this.url = searchResultItem.getURL();
		this.pageMirrorPath = searchResultItem.getCache();
	}
	*/

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public String getPageMirrorPath() {
		return pageMirrorPath;
	}

	public void setPageMirrorPath(String pageMirrorPath) {
		this.pageMirrorPath = pageMirrorPath;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	public int getCountOfKeywords() {
		return countOfKeywords;
	}

	public void setCountOfKeywords(int countOfKeywords) {
		this.countOfKeywords = countOfKeywords;
	}

	public double getSensativity() {
		return sensativity;
	}

	public void setSensativity(double sensativity) {
		this.sensativity = sensativity;
	}

	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public boolean equals(Result result){
		
		if(this.title.equals(result.getTitle()) &&
				this.url.equals(result.getUrl()))
			return true;
		else
			return false;
	}
}
