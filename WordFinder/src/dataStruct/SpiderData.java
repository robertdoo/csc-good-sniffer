package dataStruct;

public class SpiderData {
	private String type = "";
	private String title = "";
	private String url = "";
	private String purl = "";
	public String getPurl() {
		return purl;
	}
	public void setPurl(String purl) {
		this.purl = purl;
	}
	private String time = "";
	private String pageMirrorPath = "";
	private String content = "";
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getPageMirrorPath() {
		return pageMirrorPath;
	}
	public void setPageMirrorPath(String pageMirrorPath) {
		this.pageMirrorPath = pageMirrorPath;
	}
	
}
