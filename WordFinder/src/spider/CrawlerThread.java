package spider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lucene.Index.IndexProcesser;
import ContentExtract.MainBodyExtract;
import ContentExtract.TimeExtract;
import ContentExtract.TitleExtract;
import Filter.BloomFilter1;
import dataStruct.Result;
import dataStruct.SpiderData;
import database.ConvertSpiderDataToDB;

/**
 * 
 * @author Owner 爬虫爬行线程
 * 
 */
public class CrawlerThread extends Thread {
	// 链接格式过滤器
	LinkFilter filter;
	// 地址去重过滤器过滤器
	BloomFilter1 bf;
	// queue存放网页链接
	LinkedList<String> queue;
	// 设置爬行标志
	private boolean runningFlag;
	// 入口地址
	private String entryUrl;
	// 设置爬虫ID
	private int id;
	// 爬虫数据包装类
	private SpiderData spiderData;
	// 数据结果包装类
	private Result result;
	// 判断爬虫是否停止执行的类
	private StopThread st;
	// 索引类
	private IndexProcesser indexProcesser;
	// 下载图片
	private ImgDownload img_download;
	// 下载PDF文件
	private PDFDownload pdf_download;
	// 下载word文档
	private DocDownload doc_download;
	// 下载xls文档
	private XlsDownload xls_download;
	// 下载html网页
	private HtmlDownload html_downLoader;
	// 初始化提取正文内容的类
	private MainBodyExtract bodyExtract;
	// 提取标题
	private TitleExtract titleExtract;
	// 提取时间
	private TimeExtract timeExtract;
	// 存数据库
	private ConvertSpiderDataToDB saveToDB;
	// 链接提取1
	UrlParser up;
	// 链接提取2
	CatchUrl cu;

	/**
	 * 判断爬虫是否正在爬行
	 */
	public boolean isRunning() {
		return runningFlag;
	}

	/**
	 * 设置爬行标志
	 * 
	 * @param flag
	 */
	public synchronized void setRunning(boolean flag) {
		runningFlag = flag;
		if (flag)
			this.notify();
	}

	/**
	 * 获取正在处理的URL
	 * 
	 * @return
	 */
	public String getUrl() {
		return this.entryUrl;
	}

	/**
	 * 设置当前要处理的URL
	 */
	public void setUrl(String url) {
		this.entryUrl = url;
	}

	/**
	 * 构造函数，初始化爬行标志、待爬地址队列、过滤器
	 * 
	 * @param threadNumber
	 * @param bf
	 * @param st
	 * @param indexProcesser
	 * @param saveToDB
	 */
	public CrawlerThread(int threadNumber, BloomFilter1 bf, StopThread st,
			IndexProcesser indexProcesser, ConvertSpiderDataToDB saveToDB) {
		// 设置爬行标志为false
		runningFlag = false;
		System.out.println("thread " + threadNumber + " started");
		/*
		 * 初始化爬虫id、布隆过滤器、索引对象、存数据库对象、停爬虫对象 都由线程池统一管理
		 */
		id = threadNumber;
		this.bf = bf;
		this.st = st;
		this.saveToDB = saveToDB;
		this.indexProcesser = indexProcesser;

		queue = new LinkedList<String>();

		img_download = new ImgDownload();

		html_downLoader = new HtmlDownload(SpiderConf.DEEP_CRAWLER);

		bodyExtract = new MainBodyExtract();

		titleExtract = new TitleExtract();

		timeExtract = new TimeExtract();

		up = new UrlParser();

		cu = new CatchUrl();
	}

	/**
	 * getFileNameByUrl将url去掉http://,并且将其中所含的?/:*|<>等字符 转化为_后返回，转化的目的是使其能够作为文件名
	 */
	public String getFileNameByUrl(String url) {
		// remove http://
		url = url.substring(7);

		url = url.replaceAll("[?/:*|<>]", "_");

		return url;

	}

	/**
	 * 唤醒挂起的线程
	 */
	public synchronized void notifyThread() {
		this.notify();
	}

	/**
	 * 爬行程序
	 * 
	 * @param url
	 */
	public synchronized void crawler(String url) {
		/*
		 * 下面为函数crawler用到的对象
		 */
		BufferedWriter bw_url = null;
		Set<String> links1 = null;
		Set<String> links2 = null;
		File htmlFile = null;

		// 需要爬行的网页数目
		int count = 0;
		// 已爬行的网页数目
		int counter = 0;
		// 正在处理的链接
		String visitUrl = url;
		// 网页内时间
		String time = "";
		// 标题
		String title = "";
		// 正文
		String body = "";
		// 网页本地保存的路径
		String htmlPath = "";
		// 保存入口地址内抓取到的链接的文件名
		String urlName = url.replaceAll("[?/:*|<>]", "_");

		try {
			// 建立I/O接口将入口地址内抓取到的链接保存至本地
			bw_url = new BufferedWriter(new FileWriter(SpiderConf
					.getPathByName(SpiderConf.SPIDER_URL_PATH)
					+ urlName + ".txt"));

			System.out.println("线程  " + id + " 开始处理连接： " + visitUrl);
			// 下载入口地址页面，并获取链接状态码
			int statuCode = html_downLoader.downloadFile(visitUrl);
			// 如果状态码为403，页面被禁止访问，则直接返回，结束爬行
			if (statuCode == 403) {
				System.err.println("访问"+visitUrl+"被禁止！线程"+id+"停止执行！");
				return;
			}

			/*
			 * 提取出下载网页的URL，UrlParser，CatchUrl两个类都实现此功能，结合在一起使用能够 尽可能提取到全部的链接
			 */
			links1 = up.extracLinks(visitUrl, filter, entryUrl);
			links2 = cu.getText(visitUrl, filter, entryUrl);
			// 新的未访问的URL入列
			for (String link : links1) {
				// 如果是图片链接的话要将图片所在网页链接一起保存，中间用@@@隔开
				if (link.endsWith(".jpg") || link.endsWith(".gif")
						|| link.endsWith(".bmp") || link.endsWith(".002C")) {
					link = visitUrl + "@@@" + link;
				}
				queue.add(link);
				// 将提取到的链接写入本地文件
				bw_url.write("from HtmlpaserTool: " + link);
				bw_url.newLine();
				count++;
			}
			links1 = null;
			for (String link : links2) {

				// 如果是图片链接的话要将图片所在网页链接一起保存，中间用@@@隔开
				if (link.endsWith(".jpg") || link.endsWith(".gif")
						|| link.endsWith(".bmp") || link.endsWith(".002C")) {
					link = visitUrl + "@@@" + link;
				}
				queue.add(link);
				// 将提取到的链接写入本地文件
				bw_url.write("from testCatchUrl: " + link);
				bw_url.newLine();
				count++;
			}
			links2 = null;
			bw_url.flush();
			bw_url.close();
			// 设置抓取数目为提取到链接数目的平方（可根据实际情况进行设置）
			count = count * count;
			System.out.println("线程 " + id + "需要处理" + count + "个URL");
		} catch (IOException e) {
			System.err.println("线程" + id + "处理入口地址：" + visitUrl
					+ "时出现IOException！程序退出！");
			return;
		}

		// 初始化连续出错个数（当连续出现5次异常就终止此线程）
		int faultNumber = 0;
		
		/*
		 * 开始循环爬取入口网页内的链接，循环条件为： 待爬队列不为空且已爬取网页数小于预定爬取网页数且停止爬行标志为false
		 */
		while (!queue.isEmpty() && counter < count
				&& st.isCrawlerFlag() == false) {
			// 队头url出列
			visitUrl = queue.removeFirst();
			// 重复链接跳过
			if (!bf.bloomfilter(visitUrl))
				continue;

			System.out.println("线程 " + id + " 正在处理连接： " + visitUrl);

			/*
			 *  如果是图片链接则交给图片链接处理类处理
			 */
			if (visitUrl.endsWith(".jpg") || visitUrl.endsWith(".gif")
					|| visitUrl.endsWith(".bmp") || visitUrl.endsWith(".002C")) {
				// 如果图片下载失败，连续出错个数加1
				if (!img_download.imgDownLoad(visitUrl))
					faultNumber++;
				// 如果连续出错个数达到5个，跳出循环，结束本线程
				if (faultNumber >= 5)
					break;
				continue;
			}
			
			/*
			 *  如果是pdf链接则交给pdf链接处理类处理
			 */
			else if (visitUrl.endsWith("pdf")) {
				pdf_download = new PDFDownload(SpiderConf.DEEP_CRAWLER,
						indexProcesser, saveToDB, entryUrl);
				// 如果文件下载失败，连续出错个数加1
				if (!pdf_download.PDFDownLoad(visitUrl))
					faultNumber++;
				// 将pdf_download对象置空（因为不常用）
				pdf_download = null;
				// 如果连续出错个数达到5个，跳出循环，结束本线程
				if (faultNumber >= 5)
					break;
				continue;
			}
			
			/*
			 *  如果是word链接则交给word链接处理类处理
			 */
			else if (visitUrl.endsWith("doc") || visitUrl.endsWith("docx")) {
				doc_download = new DocDownload(SpiderConf.DEEP_CRAWLER,
						indexProcesser, saveToDB, entryUrl);
				// 如果文件下载失败，连续出错个数加1
				if (!doc_download.docDownLoad(visitUrl))
					faultNumber++;
				//将doc_download对象置空
				doc_download = null;
				// 如果连续出错个数达到5个，跳出循环，结束本线程
				if (faultNumber >= 5)
					break;
				continue;
			}
			
			/*
			 *  如果是XLS链接则交给XLS处理类处理
			 */
			else if (visitUrl.endsWith("xls")) {
				xls_download = new XlsDownload(SpiderConf.DEEP_CRAWLER,
						indexProcesser, saveToDB, entryUrl);
				// 如果文件下载失败，连续出错个数加1
				if (!xls_download.xlsDownLoad(visitUrl))
					faultNumber++;
				//将xls_download对象置空
				xls_download = null;
				// 如果连续出错个数达到5个，跳出循环，结束本线程
				if (faultNumber >= 5)
					break;
				continue;
			}
			
			/*
			 * 如果链接中带有“page=”字样，一般是目录页面，此类页面只需提取里面链接即可
			 */
			else if (visitUrl.indexOf("page=") != -1) {
				if (queue.size() < count) {
					Set<String> links_1 = up.extracLinks(visitUrl, filter,
							entryUrl);
					Set<String> links_2 = cu.getText(visitUrl, filter,
							entryUrl);
					// 新的未访问的URL入列，原理和前面所注释的一样
					for (String link : links_1) {

						if (link.endsWith(".jpg") || link.endsWith(".gif")
								|| link.endsWith(".bmp")
								|| link.endsWith(".002C")) {
							link = visitUrl + "@@@" + link;
						}
						queue.add(link);
					}
					links_1 = null;
					for (String link : links_2) {

						if (link.endsWith(".jpg") || link.endsWith(".gif")
								|| link.endsWith(".bmp")
								|| link.endsWith(".002C")) {
							link = visitUrl + "@@@" + link;
						}
						queue.add(link);
					}
					links_2 = null;	
				}
				continue;
			}

			/*
			 *  对于一般的网页则首先将其下载，并获取链接状态码，再进行内容提取
			 */
			int statuCode = html_downLoader.downloadFile(visitUrl);
			// 403错误，网页被禁止访问，直接跳出循环，终止本线程
			if (statuCode == 403) {
				System.err.println("访问" + visitUrl + "被禁止！");
				break;
			}

			// 如果发生其他错误，连续出错计数器加1，如果大于等于5，则跳出循环, 结束本线程，否则结束本次循环，进行下一个链接的处理
			else if (statuCode != 200) {
				faultNumber++;
				if (faultNumber >= 5)
					break;
				continue;
			}

			// 提取时间
			time = timeExtract.getTime(visitUrl);
			// 提取标题
			title = titleExtract.getTitle(visitUrl);
			// 提取正文
			body = bodyExtract.getMainBody(visitUrl);
			// 获取保存的网页文件
			htmlFile = new File(SpiderConf
					.getPathByName(SpiderConf.SPIDER_HTML_PATH)
					+ getFileNameByUrl(visitUrl) + ".htm");
			// 提取下载后网页文件保存的绝对路径
			htmlPath = htmlFile.getAbsolutePath();
			// 如果没有提取到正文，跳出本次循环，处理下一个链接
			if (body.indexOf("no bodyContent") != -1)
				continue;
			// 如果发生404错误或者没有提取到标题，跳出本次循环，处理下一个链接
			if (title.indexOf("404") != -1 || title.trim().length() == 0)
				continue;

			/*
			 *  提取时间、标题、或者正文时出错，连续出错个数加1,
			 *  如果 连续出错个数达到5个，结束本线程，否则结束本轮循环，进行下一个
			 */
			if (time.equals("error") || title.equals("error")
					|| body.equals("error")) {
				faultNumber++;
				if (faultNumber >= 5)
					break;
				continue;
			}
			
			/*
			 * 将提取到的信息进行封装，进行索引和存数据库
			 */
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
			//存数据库
			saveToDB.storeFile(spiderData);
			result = new Result(spiderData);
			// 加入索引
			indexProcesser.updateIndexByResult(result);
			
			/*
			 * 用完的对象置空
			 */
			time = null;
			htmlPath = null;
			//visitUrl = null;
			body = null;
			title = null;
			spiderData = null;
			result = null;
	
			/*
			 *  提取出下载网页的URL
			 */
			if (queue.size() < count) {
				links1 = up.extracLinks(visitUrl, filter, entryUrl);
				links2 = cu.getText(visitUrl, filter, entryUrl);
				// 新的未访问的URL入列
				for (String link : links1) {
					if (link.endsWith(".jpg") || link.endsWith(".gif")
							|| link.endsWith(".bmp") || link.endsWith(".002C")) {
						link = visitUrl + "@@@" + link;
					}
					queue.add(link);
				}
				links1 = null;
				for (String link : links2) {

					if (link.endsWith(".jpg") || link.endsWith(".gif")
							|| link.endsWith(".bmp") || link.endsWith(".002C")) {
						link = visitUrl + "@@@" + link;
					}
					queue.add(link);
				}
				links2 = null;
			}
			
			// 已处理网页数加1
			counter++;
			// 将连续出错个数置为0			
			faultNumber = 0;
	}

		/*
		 *  将待爬地址置空
		 */
		while (!queue.isEmpty()) {
			queue.removeFirst();
		}
		
		/*
		 * 爬行结束后输出爬行信息
		 */
		System.out.println("/////////////////////////////////////////////////////////");
		System.out.println("///线程 " + id + "结束,共处理  " + counter + " 个网页链接///");
		System.out.println("////////////////////////////////////////////////////////");
	}

	/**
	 * (non-Javadoc)
	 * @see java.lang.Thread#run() 重写Thread的run函数
	 */
	public synchronized void run() {
		try {
			// 如果结束线程类的结束标志为false则执行下面循环
			while (st.isCrawlerFlag() == false) {
				// 爬行标志位false时线程等待，否则执行爬行任务
				if (!runningFlag) {
					this.wait();
				} else {
					crawler(getUrl());
					//执行完一次爬行任务后休息1分钟
					this.wait(60000);
				}
			}
			// 关闭图像操作类中的数据库连接
			img_download.getConnDB().closeConnection();
		} catch (InterruptedException e) {
			
		}
		System.out.println("线程 " + id + "已停止！");
	}

	/**
	 *  转换路径字符串，把“\”转换为“\\”
	 */
	public String pathStringConvert(String pathString) {
		String newString = "";
		char ch;
		for (int i = 0; i < pathString.length(); i++) {
			ch = pathString.charAt(i);
			newString += ch;
			if (ch == '\\')
				newString += '\\';
		}
		return newString;
	}

	/**
	 * 初始化链接格式过滤器
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
						return true;
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
}
