package spider;
/**
 * øÿ÷∆≈¿≥ÊÕ£÷πµƒ¿‡
 * @author Owner
 *
 */
public class StopThread {
	private boolean mainPageCrawlerFlag;
	private boolean crawlerFlag;
	
	public StopThread(){
		mainPageCrawlerFlag = false;
		crawlerFlag = false;
	}
	public boolean isMainPageCrawlerFlag() {
		return mainPageCrawlerFlag;
	}

	public void setMainPageCrawlerFlag(boolean mainPageCrawlerFlag) {
		this.mainPageCrawlerFlag = mainPageCrawlerFlag;
	}

	public boolean isCrawlerFlag() {
		return crawlerFlag;
	}

	public void setCrawlerFlag(boolean crawlerFlag) {
		this.crawlerFlag = crawlerFlag;
	}
	
	
}
