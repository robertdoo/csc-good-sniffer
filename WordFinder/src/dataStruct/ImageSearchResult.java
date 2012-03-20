package dataStruct;

public class ImageSearchResult {
	private String title;
	private String url;
	private String site;
	private String localPath;
	private boolean examined;
	private String storeTime;
	private String examinedResult;
	
	public ImageSearchResult(String title, String url, String site,
			String localPath, boolean examined, String storeTime,
			String examinedResult) {
		super();
		this.title = title;
		this.url = url;
		this.site = site;
		this.localPath = localPath;
		this.examined = examined;
		this.storeTime = storeTime;
		this.examinedResult = examinedResult;
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

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public boolean isExamined() {
		return examined;
	}

	public void setExamined(boolean examined) {
		this.examined = examined;
	}

	public String getStoreTime() {
		return storeTime;
	}

	public void setStoreTime(String storeTime) {
		this.storeTime = storeTime;
	}

	public String getExaminedResult() {
		return examinedResult;
	}

	public void setExaminedResult(String examinedResult) {
		this.examinedResult = examinedResult;
	}
	
	
}
