package spider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * 下载HTML文件的类
 * 
 * @author Owner
 * 
 */
public class HtmlDownload {
	private final String filePath1 = SpiderConf
			.getPathByName(SpiderConf.SPIDER_HTML_PATH);
	private final String filePath2 = SpiderConf
			.getPathByName(SpiderConf.MAINPAGECRAWLER_HTML_PATH);
	private String filePath;
	private String htmlPath;

	/**
	 * 测试函数
	 */
	public static void main(String[] args) {
		String url = "http://bbs.tiexue.net/post_4542035_1.html";
		new HtmlDownload(1).downloadFile(url);
	}

	/**
	 * 构造函数
	 * 
	 * @param chose
	 *            用来判断是主页面监控爬虫调用还是论坛爬虫调用
	 */
	public HtmlDownload(int chose) {
		if (chose == SpiderConf.DEEP_CRAWLER)
			filePath = filePath1;
		else
			filePath = filePath2;
		htmlPath = filePath;
	}

	/**
	 *  保存网页字节数组到本地文件
	 */
	private boolean saveToLocal(InputStream inputStream, String filePath) {
		// filePath为要保存文件的相对地址
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream));
			writer = new BufferedWriter(new FileWriter(new File(filePath)));
			String data = null;
			while ((data = reader.readLine()) != null) {
				writer.write(data);
			}
			reader.close();
			writer.flush();
			writer.close();

			return true;
		} catch (IOException e) {
			System.err.println("保存网页时出现异常！");
			try {
				if (reader != null)
					reader.close();
				if (writer != null)
					writer.close();
			} catch (IOException e1) {
				return false;
			}
			return false;
		}
	}

	/**
	 *  下载URL指向的网页
	 */
	public synchronized int downloadFile(String url) {
		HttpClient httpClient = null;
		GetMethod getMethod = null;
		InputStream responseBody = null;
		String fullUrl = url;
		
		// 1生成HttpClient对象并设置参数
		httpClient = new HttpClient();
		// 设置超时链接30秒
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(
				30000);
		httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT,
				"vigorouswei@126.com");
		// 2生成GetMethod对象并设置参数
		getMethod = new GetMethod(url);

		getMethod.getParams().setParameter("http.protocol.cookie-policy",
				CookiePolicy.BROWSER_COMPATIBILITY);
		// 设置get请求超时30秒
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
		// 设置请求重试处理
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());

		// 3执行HTTP get请求
		try {
			int statuCode = httpClient.executeMethod(getMethod);
			/*
			 *  判断访问的状态码,出错就返回
			 */
			if (statuCode != HttpStatus.SC_OK) {
				System.err.println("页面访问失败: " + getMethod.getStatusLine());
				/*
				 * 释放对象
				 */
				htmlPath = null;
				httpClient = null;
				getMethod.releaseConnection();
				return statuCode;
			}
			
			// 4处理HTTP响应内容
			responseBody = getMethod.getResponseBodyAsStream();
			url = getFileNameByUrl(url);
			htmlPath = filePath + url + ".htm";
			boolean isSaved = saveToLocal(responseBody, htmlPath);
			responseBody.close();
			
			/*
			 * 如果保存失败则返回错误信号
			 */
			if (isSaved == false)
				return 300;
		} catch (HttpException e) {
			System.err.println("check your http address: " + fullUrl);
			System.err.println("下载网页时捕获HttpException异常");
			getMethod.releaseConnection();
			try {
				if (responseBody != null)
					responseBody.close();
			} catch (IOException e1) {
				return 300;
			}
			fullUrl = null;
			httpClient = null;
			
			return 300;
		} catch (IOException e) {
			System.out.println("下载网页时捕获IOException异常");
			getMethod.releaseConnection();
			try {
				if (responseBody != null)
					responseBody.close();
			} catch (IOException e1) {
				return 300;
			}
			fullUrl = null;
			httpClient = null;
			
			return 300;
		} catch (Exception e) {
			getMethod.releaseConnection();
			try {
				if (responseBody != null)
					responseBody.close();
			} catch (IOException e1) {
				return 300;
			}
			fullUrl = null;
			httpClient = null;
			
			return 300;
		}
		
		/*
		 * 释放对象
		 */
		getMethod.releaseConnection();
		fullUrl = null;
		httpClient = null;
		
		return HttpStatus.SC_OK;
	}
	
	/**
	 * 去掉链接中的”?/:*|<>“符号，使其能成为文件名
	 */
	public String getFileNameByUrl(String url) {
		// remove http://
		url = url.substring(7);
		url = url.replaceAll("[?/:*|<>]", "_");
		return url;
	}

}
