package spider;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lucene.Index.IndexProcesser;

import ContentExtract.MainBodyExtract;
import ContentExtract.TimeExtract;
import ContentExtract.TitleExtract;
import Filter.BloomFilter;
import dataStruct.Result;
import dataStruct.SpiderData;
import database.ConvertSpiderDataToDB;

/**
 * 主页面监控爬虫
 * 
 * @author Owner
 * 
 */
public class MainPageCrawler extends Thread {
	// 待爬行队列
	private LinkedList<String> url_list;
	// 控制爬虫结束对象，由线程池统一管理
	private StopThread st;
	// 结束本线程
	private boolean stopThisThread;
	// 地址去重过滤器
	private BloomFilter bf;
	// 入口地址
	private String entryUrl;
	// 爬虫id
	private int id;
	// 标示爬虫所在线程池
	//private int flag;
	// 处理页面个数
	private int num;
	// 循环爬行次数
	private long round;
	// 爬虫是否正在爬行标志
	private boolean isRunning;
	// 爬虫数据包装类
	private SpiderData spiderData;
	// 结果显示包装类
	private Result result;
	// 索引类
	IndexProcesser indexProcesser;
	// 存数据库
	ConvertSpiderDataToDB saveToDB;
	// 地址格式过滤器
	LinkFilter filter;
	// 链接提取器1
	UrlParser up;
	// 链接提取器2
	CatchUrl cu;
	// 下载PDF
	PDFDownload pdf_download;
	// 下载word文档
	DocDownload doc_download;
	public void setRound(long round) {
		this.round = round;
	}

	public String getEntryUrl() {
		return entryUrl;
	}

	// 下载XLS文档
	XlsDownload xls_download;
	// 下载HTML网页
	HtmlDownload html_downLoader;
	// 提取标题
	TitleExtract titleExtract = new TitleExtract();
	// 提取时间
	TimeExtract timeExtract = new TimeExtract();
	// 初始化提取正文内容的类
	MainBodyExtract bodyExtract = new MainBodyExtract();

	/**
	 * 测试函数
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

	}

	public MainPageCrawler(int id, BloomFilter bf, StopThread st,
			IndexProcesser indexProcesser, ConvertSpiderDataToDB saveToDB,int flag) {
		/*
		 * 初始化爬虫id、布隆过滤器、索引对象、存数据库对象、停爬虫对象 都由线程池统一管理
		 */
		this.st = st;
		this.bf = bf;
		this.id = id;
		//this.flag = flag;
		this.saveToDB = saveToDB;
		this.indexProcesser = indexProcesser;

		round = 0;
		isRunning = false;
		stopThisThread = false;

		url_list = new LinkedList<String>();
		up = new UrlParser();
		cu = new CatchUrl();
		html_downLoader = new HtmlDownload(SpiderConf.MAINPAGE_CRAWLER);
		titleExtract = new TitleExtract();
		timeExtract = new TimeExtract();
		bodyExtract = new MainBodyExtract();
	}

	public boolean isStopThisThread() {
		return stopThisThread;
	}

	public void setStopThisThread(boolean stopThisThread) {
		this.stopThisThread = stopThisThread;
	}

	/**
	 * 爬虫爬行程序
	 * 
	 * @param url
	 */
	public void crawler(String url) {
		// 正在爬行的链接
		String visitUrl = url;

		Set<String> links1 = up.extracLinks(visitUrl, filter, entryUrl);
		Set<String> links2 = cu.getText(visitUrl, filter, entryUrl);

		/*
		 * 第一遍爬行只将页面内已有链接存入哈希表
		 */
		if (round == 0) {
			for (String link : links1)
				bf.bloomfilter(link);
			for (String link : links2)
				bf.bloomfilter(link);
			round++;
			links1.clear();
			links2.clear();
			return;
		}

		/*
		 * 新的未访问的URL入列
		 */
		for (String link : links1) {
			url_list.add(link);
		}
		for (String link : links2) {
			url_list.add(link);
		}
		links1.clear();
		links2.clear();

		long beginTime = new Date().getTime();
		while (!url_list.isEmpty() && st.isMainPageCrawlerFlag() == false) {
			// 队头URL出列
			visitUrl = url_list.removeFirst();
			// 重复链接跳过
			if (!bf.bloomfilter(visitUrl))
				continue;
			// 处理的链接数自增
			num++;
			System.out.println("入口页面监控爬虫 " + id + " 正在处理连接： " + visitUrl);

			/*
			 * 如果是PDF链接则交给PDF链接处理类处理
			 */
			if (visitUrl.endsWith("pdf") || visitUrl.endsWith("PDF")) {
				pdf_download = new PDFDownload(SpiderConf.MAINPAGE_CRAWLER,
						indexProcesser, saveToDB, entryUrl);
				pdf_download.PDFDownLoad(visitUrl);
				pdf_download = null;
				continue;
			}

			/*
			 * 如果是word链接则交给word链接处理类处理
			 */
			else if (visitUrl.endsWith("doc") || visitUrl.endsWith("docx")) {
				doc_download = new DocDownload(SpiderConf.MAINPAGE_CRAWLER,
						indexProcesser, saveToDB, entryUrl);
				doc_download.docDownLoad(visitUrl);
				doc_download = null;
				continue;
			}

			/*
			 * 如果是xls链接则交给xls处理类处理
			 */
			else if (visitUrl.endsWith("xls") || visitUrl.endsWith("XLS")) {
				xls_download = new XlsDownload(SpiderConf.MAINPAGE_CRAWLER,
						indexProcesser, saveToDB, entryUrl);
				xls_download.xlsDownLoad(visitUrl);
				xls_download = null;
				continue;
			}

			/*
			 * 对于一般的网页则首先将其下载，并获取链接状态码
			 */
			int statuCode = html_downLoader.downloadFile(visitUrl);
			// 403错误，网页被禁止访问，直接跳出循环，终止本线程
			if (statuCode == 403) {
				System.err.println("入口页面监控爬虫 " + id + "访问" + visitUrl + "被禁止！");
				break;
			}

			// 提取时间
			String time = timeExtract.getTime(visitUrl);
			// 提取标题
			String title = titleExtract.getTitle(visitUrl);
			// 提取正文
			String body = bodyExtract.getMainBody(visitUrl);
			// 获取保存的网页文件
			File htmlFile = new File(SpiderConf
					.getPathByName(SpiderConf.MAINPAGECRAWLER_HTML_PATH)
					+ getFileNameByUrl(visitUrl) + ".htm");
			// 提取下载后网页文件保存的绝对路径
			String htmlPath = htmlFile.getAbsolutePath();
			// 如果没有提取到正文，跳出本次循环，处理下一个链接
			if (body.indexOf("no bodyContent") != -1)
				continue;
			// 如果发生404错误或者没有提取到标题，跳出本次循环，处理下一个链接
			if (title.indexOf("404") != -1 || title.trim().length() == 0)
				continue;

			spiderData = new SpiderData();
			spiderData.setType("html");
			spiderData.setUrl(visitUrl);
			spiderData.setTitle(title);
			if (time.length() == 0)
				spiderData.setTime("no time");
			else
				spiderData.setTime(time);
			spiderData.setPageMirrorPath(htmlPath);
			spiderData.setContent(body);
			spiderData.setPurl(SpiderConf.getNameByUrl(entryUrl));

			// 加入数据库
			saveToDB.storeFile(spiderData);
			result = new Result(spiderData);
			// 加入索引
			indexProcesser.updateIndexByResult(result);

			/*
			 * 用完的对象置空
			 */
			time = null;
			htmlPath = null;
			visitUrl = null;
			body = null;
			title = null;
			spiderData = null;
			result = null;

		}
		long endTime = new Date().getTime();
		System.out
				.println("*****************************************************");
		System.out.println("入口页面监控爬虫 " + id + "共处理 " + num + "个链接！用时： "
				+ (endTime - beginTime) / 1000 + "秒");
		System.out
				.println("*****************************************************");
		num = 0;
		round++;
		url_list.clear();
	}

	/**
	 * getFileNameByUrl将url去掉http://,并且将其中所含的?/:*|<>等字符 转化为_后返回，转化的目的是使其能够作为文件名
	 */
	public String getFileNameByUrl(String url) {

		url = url.substring(7);// remove http://

		url = url.replaceAll("[?/:*|<>]", "_");

		return url;

	}

	/**
	 * 设置入口地址
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.entryUrl = url;
	}

	/**
	 * 查看爬虫是否正在爬行
	 * 
	 * @return
	 */
	public boolean getIsRunning() {
		return this.isRunning;
	}

	/**
	 * 设置爬行标志
	 * 
	 * @param flag
	 */
	public synchronized void setIsRunning(boolean flag) {
		this.isRunning = flag;
		if (flag)
			this.notify();
	}
	
	public void setIsRunningFalse(){
		this.isRunning = false;
	}
	public void setIsRunningTrue(){
		this.isRunning = true;
	}

	/**
	 * run函数
	 */
	public synchronized void run() {
		while (st.isMainPageCrawlerFlag() == false) {
			try {
				if (isRunning == false) {
					wait(60000);
				} else {
					long beginTime = new Date().getTime();
					crawler(entryUrl);
					long endTime = new Date().getTime();
					System.out.println("线程" + id + "耗时" + (endTime - beginTime)
							+ "毫秒！");
					if (endTime - beginTime < 60000)
						wait(60000 - (endTime - beginTime));
				}
			} catch (InterruptedException e) {
				System.err.println("爬虫出现InterruptedException异常！");
			}
		}
		System.err.println("入口页面监控爬虫  " + id + "结束");
		setIsRunning(false);
	}

	/**
	 * 初始化地址格式过滤器
	 */
	public void iniFilter() {
		filter = new LinkFilter() {
			public boolean accept(String url, String entryAddress) {
				try {
					String line = entryAddress;
					// 带有#和+号的链接往往是跳转网页，此类链接不保存
					if (url.indexOf("#") != -1 || url.indexOf("+") != -1)
						return false;
					// 带有以下字符的连接多是确认页面、注册页面
					if (url.indexOf("uid") != -1 || url.indexOf("user") != -1
							|| url.indexOf("action=") != -1
							|| url.indexOf("register") != -1
							|| url.indexOf("filter=") != -1
							|| url.indexOf("personalinfo") != -1
							|| url.indexOf("(") != -1)
						return false;
					// 带有中文字符的链接不保存
					String regEx = "[\\u4e00-\\u9fa5]";
					Pattern p = Pattern.compile(regEx);
					Matcher m = p.matcher(url);
					if (m.find())
						return false;

					if (url.indexOf("...") != -1)
						return false;

					// 对于指向图片的链接都不保存
					if (url.endsWith(".jpg") || url.endsWith(".gif")
							|| url.endsWith(".bmp") || url.endsWith(".002C"))
						return false;
					// 指向word文件的链接保存
					if ((url.endsWith(".doc") || url.endsWith(".docx"))
							&& url.startsWith("http://"))
						return true;
					// 指向pdf文件的链接保存
					if (url.endsWith(".pdf") && url.startsWith("http://"))
						return true;
					if (url.endsWith(".xls") && url.startsWith("http://"))
						return true;
					/*
					 * 对于普通网页链接过滤的原则是：以http://开头 并且和入口地址有相同的域名
					 */
					line = line.substring(7);
					if (line.indexOf("/") != -1)
						line = line.substring(0, line.indexOf("/"));
					if (url.indexOf(line) != -1 && url.startsWith("http://"))
						return true;

					return false;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}

			}
		};
	}

	/**
	 * 返回爬虫循环爬行次数
	 * 
	 * @return
	 */
	public long getRound() {
		return round;
	}
}
