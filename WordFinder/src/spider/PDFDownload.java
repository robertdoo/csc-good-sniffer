package spider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.SQLException;
import java.util.StringTokenizer;

import lucene.Index.IndexProcesser;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import dataStruct.Result;
import dataStruct.SpiderData;
import database.ConvertSpiderDataToDB;

/**
 * 下载PDF的类
 * 
 * @author Owner
 * 
 */
public class PDFDownload {
	private final String filePath1 = SpiderConf
			.getPathByName(SpiderConf.SPIDER_PDF_PATH);
	private final String filePath2 = SpiderConf
			.getPathByName(SpiderConf.MAINPAGECRAWLER_PDF_PATH);
	private String pUrl;
	private String filePath;
	private SpiderData spiderData;
	private Result result;
	private IndexProcesser indexProcesser;
	private ConvertSpiderDataToDB saveToDB;

	/**
	 * 测试函数
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		PDFDownload PDFDl = new PDFDownload(1, new IndexProcesser(1),
				new ConvertSpiderDataToDB("test"), "");
		PDFDl
				.PDFDownLoad("http://www.ceibs.edu/pdf/execed/CEIBS_HBS_IESE_Joint_Global_CEO_Program.pdf");
	}

	/**
	 * 构造函数
	 * 
	 * @param chose
	 * @param indexProcesser
	 * @param saveToDB
	 * @param pUrl
	 */
	public PDFDownload(int chose, IndexProcesser indexProcesser,
			ConvertSpiderDataToDB saveToDB, String pUrl) {
		if (chose == SpiderConf.DEEP_CRAWLER)
			filePath = filePath1;
		else
			filePath = filePath2;
		this.indexProcesser = indexProcesser;
		this.saveToDB = saveToDB;
		this.pUrl = pUrl;
	}

	public boolean PDFDownLoad(String url) {
		/*
		 *  下面是函数PDFDownLoad用到的变量
		 */
		HttpClient client;
		GetMethod httpGet;
		InputStream in = null;
		FileOutputStream out = null;
		
		String[] urls = url.split("/");
		String name = urls[urls.length - 1];
		
		client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(
				30000);
		client.getParams().setParameter(HttpMethodParams.USER_AGENT,
				"vigorouswei@126.com");
		httpGet = new GetMethod(url);
		httpGet.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
		httpGet.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		httpGet.getParams().setParameter("http.protocol.cookie-policy",
				CookiePolicy.BROWSER_COMPATIBILITY);

		try {
			client.executeMethod(httpGet);
			in = httpGet.getResponseBodyAsStream();
			out = new FileOutputStream(new File(filePath + name));

			/*
			 * 读取数据并存入本地文件
			 */
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			out.close();
			in.close();

			File pdfFile = new File(filePath + name);
			String pdfFilePath = pdfFile.getAbsolutePath();
            
			// 正常解析
			getText(url, pdfFilePath);

			/*
			 * 释放对象
			 */
			httpGet.releaseConnection();
			urls = null;
			name = null;
			client = null;
			b = null;
			pdfFile = null;
			pdfFilePath = null;
			
			return true;
		} catch (SocketTimeoutException exc) {
			System.err.println("下载文档时出现 SocketTimeoutException 异常！");
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e1) {
				// e1.printStackTrace();
				return false;
			}
			
			/*
			 * 释放对象
			 */
			httpGet.releaseConnection();
			urls = null;
			name = null;
			client = null;
			
			return false;
		} catch (ConnectException e) {
			System.err.println("下载文档时出现 ConnectException 异常！");
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
				
			} catch (IOException e1) {
				// e1.printStackTrace();
				return false;
			}
			/*
			 * 释放对象
			 */
			httpGet.releaseConnection();
			urls = null;
			name = null;
			client = null;
			
			return false;
		} catch (HttpException e) {
			System.err.println("下载文档时捕获HttpException异常");
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
				
			} catch (IOException e1) {
				// e1.printStackTrace();
				return false;
			}
			/*
			 * 释放对象
			 */
			httpGet.releaseConnection();
			urls = null;
			name = null;
			client = null;
			
			return false;
		} catch (IOException e) {
			System.err.println("下载文档时捕获IOException异常");
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			
			} catch (IOException e1) {
				// e1.printStackTrace();
				return false;
			}
			
			/*
			 * 释放对象
			 */
			httpGet.releaseConnection();
			urls = null;
			name = null;
			client = null;
			
			return false;
		} catch (SQLException e) {
			System.err.println("下载文档时捕获SQLException异常");
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e1) {
				return false;
			}
			/*
			 * 释放对象
			 */
			httpGet.releaseConnection();
			urls = null;
			name = null;
			client = null;
			
			return false;
		} catch (Exception e) {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
			/*
			 * 释放对象
			 */
			httpGet.releaseConnection();
			urls = null;
			name = null;
			client = null;
			
			return false;
		}

	}
	
	/**
	 * 提取PDF文档的信息
	 * @param file
	 * @param pdfFilePath
	 * @throws Exception
	 */
	public void getText(String file, String pdfFilePath) throws Exception {
		/*
		 *  下面是函数getText用到的变量
		 */
		// 内存中存储的PDF DOcument
		PDDocument document = null;
		URL url = null;
		PDFTextStripper stripper = null;
		// 是否排序
		boolean sort = false;
		// PDF文件名
		String PdfFile = file;
		// 编码方式
		// String encoding="UTF-8";
		// 开始提取页数
		int startPage = 1;
		// 结束提取页数
		int endPage = Integer.MAX_VALUE;
       
		/*
         *  注意这里利用try catch块来控制程序的流转
         */
		try {
			try {
				// 首先当作一个URL来装载文件，如果得到异常再从本地文件系统去装载文件
				url = new URL(PdfFile);
				document = PDDocument.load(url);
			} catch (MalformedURLException e) {
				// 如果作为URL装载得到异常则从文件系统装载
				document = PDDocument.load(PdfFile);
			}
			// 用PDFTextStripper来提取文本
			stripper = new PDFTextStripper();
			// 设置是否排序
			stripper.setSortByPosition(sort);
			// 设置起始页
			stripper.setStartPage(startPage);
			// 设置结束页
			stripper.setEndPage(endPage);
			// 提取标题
			String title = getTitle(stripper.getText(document));
			
			
			spiderData = new SpiderData();
			spiderData.setType("pdf");
			spiderData.setUrl(file);
			spiderData.setTitle(title);
			spiderData.setTime("no time");
			spiderData.setPageMirrorPath(pdfFilePath);
			spiderData.setContent(stripper.getText(document));
			spiderData.setPurl(SpiderConf.getNameByUrl(pUrl));
			
			//加入数据库
			saveToDB.storeFile(spiderData);
			result = new Result(spiderData);
			// 加入索引
			indexProcesser.updateIndexByResult(result);
		} finally {
			if (document != null) {
				document.close();
			}
			PdfFile = null;
			url = null;
			stripper = null;
			spiderData = null;
			result = null;
			}
	}

	/**
	 * 从一段文本中提取标题
	 * @param str
	 * @return
	 */
	public String getTitle(String str) {
		// 下面是函数getTitle用到的变量
		StringTokenizer st;
		String temp;
		st = new StringTokenizer(str, "\n");
		String title = "";
		int count = 0;
		while (count < 2) {
			if (!st.hasMoreTokens()) {
				break;
			}
			temp = st.nextToken().trim();

			if (!"".equals(temp)) {
				if (temp.length() > title.length()) {
					title = temp;
				}
				++count;
			}
		}
		st = null;
		temp = null;
		return title.trim();
	}

}
