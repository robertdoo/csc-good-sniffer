package spider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.StringTokenizer;

import lucene.Index.IndexProcesser;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.textmining.text.extraction.WordExtractor;

import dataStruct.Result;
import dataStruct.SpiderData;
import database.ConvertSpiderDataToDB;
import database.FilePreprocess;

/**
 * 
 * @author Owner DocDownload实现对doc文件的下载并提取文本内容
 */
public class DocDownload {
	/**
	 * 测试函数
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		DocDownload ddl = new DocDownload(1, new IndexProcesser(1),
				new ConvertSpiderDataToDB("test"), "");
		ddl
				.docDownLoad("http://sem.njust.edu.cn/science_notice/upfiles/2005122395330187.doc");
	}

	private final String filePath1 = SpiderConf
			.getPathByName(SpiderConf.SPIDER_DOC_PATH);
	private final String filePath2 = SpiderConf
			.getPathByName(SpiderConf.MAINPAGECRAWLER_DOC_PATH);
	private String pUrl;
	private String filePath;
	private SpiderData spiderData;
	private Result result;
	private IndexProcesser indexProcesser;
	private ConvertSpiderDataToDB saveToDB;

	/**
	 * 构造函数
	 * 
	 * @param chose
	 *            分辨是主页面监控爬虫调用还是论坛爬虫调用
	 * @param indexProcesser
	 *            索引处理对象
	 * @param saveToDB
	 *            存数据库对象
	 * @param pUrl
	 *            链接父地址
	 */
	public DocDownload(int chose, IndexProcesser indexProcesser,
			ConvertSpiderDataToDB saveToDB, String pUrl) {
		if (chose == SpiderConf.DEEP_CRAWLER)
			filePath = filePath1;
		else
			filePath = filePath2;
		this.indexProcesser = indexProcesser;
		this.saveToDB = saveToDB;
		this.pUrl = pUrl;
	}

	/**
	 * 下载DOC文件的函数
	 */
	public boolean docDownLoad(String url) {
		/*
		 * 下面是函数docDownLoad用到的对象
		 */
		HttpClient client = null;
		GetMethod httpGet = null;
		InputStream in = null;
		FileOutputStream out = null;
		FileInputStream fis = null;
		WordExtractor extractor = null;
		File docFile = null;

		// 用/将url分段，取最后一段作为文件 名
		String[] urls = url.split("/");
		String name = urls[urls.length - 1];
		// 创建HttpClient对象
		client = new HttpClient();
		// 设置链接响应超时时间为5秒
		client.getHttpConnectionManager().getParams().setConnectionTimeout(
				30000);
		// 设置链接身份
		client.getParams().setParameter(HttpMethodParams.USER_AGENT,
				"vigorouswei@126.com");
		// 创建GetMethod对象
		httpGet = new GetMethod(url);
		// 设置链接时间为30秒
		httpGet.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
		// 设置请求重试处理
		httpGet.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		httpGet.getParams().setParameter("http.protocol.cookie-policy",
				CookiePolicy.BROWSER_COMPATIBILITY);

		try {
			client.executeMethod(httpGet);
			// 获取输入流
			in = httpGet.getResponseBodyAsStream();
			// 初始化保存文件接口
			out = new FileOutputStream(new File(filePath + name));
			// 定义一个字节数组，用来从输入流中读取数据
			byte[] b = new byte[1024];
			// 每次读取的个数
			int len = 0;
			/*
			 * 循环将输入流中数据读出，然后保存至本地文件中
			 */
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			out.close();
			in.close();

			// 初始化读取本地文件接口
			fis = new FileInputStream(new File(filePath + name));
			// 初始化WordExtractor对象
			extractor = new WordExtractor();
			// 从doc文件内提取内容
			String text = extractor.extractText(fis);
			// 去掉文本内容中的空格和制表符
			text = text.replaceAll(" ", "");
			text = text.replaceAll("	", "");
			// 将文本内容中的全角符号替换为半角符号
			text = FilePreprocess.replace(text);
			// 获取保存路径
			docFile = new File(filePath + name);
			String docFilePath = docFile.getAbsolutePath();
			// 获取内容的标题
			String title = getTitle(text);

			/*
			 * 封装提取到的数据
			 */
			spiderData = new SpiderData();
			spiderData.setType("doc");
			spiderData.setUrl(url);
			spiderData.setTitle(title);
			spiderData.setTime("no time");
			spiderData.setPageMirrorPath(docFilePath);
			spiderData.setContent(text);
			spiderData.setPurl(SpiderConf.getNameByUrl(pUrl));

			// 存数据库
			saveToDB.storeFile(spiderData);
			result = new Result(spiderData);
			// 加入索引
			indexProcesser.updateIndexByResult(result);
			// 释放链接
			httpGet.releaseConnection();

			/*
			 * 下面释放对象
			 */
			urls = null;
			name = null;
			client = null;
			fis = null;
			extractor = null;
			text = null;
			docFile = null;
			docFilePath = null;
			title = null;
			spiderData = null;
			result = null;

			return true;
		} catch (HttpException e) {
			System.out.println("下载文档时捕获HttpException异常");
			httpGet.releaseConnection();
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e1) {
				return false;
			}
			/*
			 * 下面释放对象
			 */
			urls = null;
			name = null;
			client = null;
			fis = null;
			extractor = null;
			docFile = null;
			spiderData = null;
			result = null;

			return false;
		} catch (SocketTimeoutException exc) {
			System.err.println("下载文档时出现 SocketTimeoutException 异常！");
			httpGet.releaseConnection();
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e1) {
				return false;
			}

			/*
			 * 下面释放对象
			 */
			urls = null;
			name = null;
			client = null;
			fis = null;
			extractor = null;
			docFile = null;
			spiderData = null;
			result = null;

			return false;
		} catch (ConnectException e) {
			System.err.println("下载文档时出现 ConnectException 异常！");
			httpGet.releaseConnection();
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e1) {
				return false;
			}

			/*
			 * 下面释放对象
			 */
			urls = null;
			name = null;
			client = null;
			fis = null;
			extractor = null;
			docFile = null;
			spiderData = null;
			result = null;

			return false;
		} catch (IOException e) {
			System.err.println("下载文档时捕获IOException异常");
			httpGet.releaseConnection();
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e1) {
				return false;
			}
			/*
			 * 下面释放对象
			 */
			urls = null;
			name = null;
			client = null;
			fis = null;
			extractor = null;
			docFile = null;
			spiderData = null;
			result = null;

			return false;
		} catch (SQLException e) {
			System.err.println("下载文档时捕获SQLException异常");
			httpGet.releaseConnection();
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e1) {
				return false;
			}

			/*
			 * 下面释放对象
			 */
			urls = null;
			name = null;
			client = null;
			fis = null;
			extractor = null;
			docFile = null;
			spiderData = null;
			result = null;

			return false;
		} catch (Exception e) {
			httpGet.releaseConnection();
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e1) {
				return false;
			}

			/*
			 * 下面释放对象
			 */
			urls = null;
			name = null;
			client = null;
			fis = null;
			extractor = null;
			docFile = null;
			spiderData = null;
			result = null;

			return false;
		}

	}

	/**
	 * 从一段字符串中获取标题的函数,原理是首先将字符串按回车符分行， 然后取前2行中最长的那段字符串作为标题
	 */
	public String getTitle(String str) {
		StringTokenizer st;
		// 初始化StringTokenizer对象，以回车符为标记
		st = new StringTokenizer(str, "\n");
		// 初始化标题为空
		String title = "";
		// 初始化行数为空
		int count = 0;
		// 循环两次取前两行中较长的字符串作为标题
		while (count < 2) {
			if (!st.hasMoreTokens()) {
				break;
			}
			String temp = st.nextToken().trim();

			if (!"".equals(temp)) {
				if (temp.length() > title.length()) {
					title = temp;
				}
				++count;
			}
		}
		st = null;
		return title.trim();
	}

}
