package dataStruct;

public class WarningResult {

	private String warningTime;
	private String title;
	private String url;
	private String abs;
	private String ruleName;
	private String keyword;
	private String cache;
	private boolean isNew;
	
	public WarningResult() {
	}

	public WarningResult(String warningTime, String title, String url,
			String abs, String ruleName, String keyword, String cache,
			boolean isNew) {
		super();
		this.warningTime = warningTime;
		this.title = title;
		this.url = url;
		this.abs = abs;
		this.ruleName = ruleName;
		this.keyword = keyword;
		this.cache = cache;
		this.isNew = isNew;
	}

	public String getWarningTime() {
		return warningTime;
	}

	public void setWarningTime(String warningTime) {
		this.warningTime = warningTime;
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

	public String getAbs() {
		return abs;
	}

	public void setAbs(String abs) {
		this.abs = abs;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public String getCache() {
		return cache;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	
}
