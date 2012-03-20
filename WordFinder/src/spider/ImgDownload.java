package spider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import database.ConnDB;

/**
 * 下载图片的类
 * 
 * @author Owner
 * 
 */
public class ImgDownload {
	private ConnDB connDB;
	private final String filePath = SpiderConf
			.getPathByName(SpiderConf.SPIDER_IMG_PATH);

	/**
	 * 测试函数
	 */
	public static void main(String[] args) {
		ImgDownload id = new ImgDownload();
		id
				.imgDownLoad("http://www.163.com/@@@http://img2.cache.netease.com/cnews/2010/11/23/201011231527016cd39.jpg");
	}

	/**
	 * 构造函数 连接数据库
	 */
	public ImgDownload() {
		connDB = new ConnDB("test");
		connDB.connectToDB();
	}

	/**
	 * 返回数据库连接对象
	 * 
	 * @return
	 */
	public ConnDB getConnDB() {
		return connDB;
	}

	/**
	 * 下载图片的函数
	 * 
	 * @param url
	 * @return
	 */
	public boolean imgDownLoad(String url) {
		/*
		 * 下面是函数imgDownLoad用到的对象
		 */
		HttpClient client = null;
		GetMethod httpGet = null;
		InputStream in = null;
		FileOutputStream out = null;

		String[] urls = url.split("@@@");
		String purl = urls[0];
		String surl = urls[1];
		String name = getFileNameByUrl(surl);
		name = name.replaceAll("002C", "jpg");

		client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(
				30000);
		client.getParams().setParameter(HttpMethodParams.USER_AGENT,
				"vigorouswei@126.com");
		httpGet = new GetMethod(surl);
		httpGet.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
		httpGet.getParams().setParameter("http.protocol.cookie-policy",
				CookiePolicy.BROWSER_COMPATIBILITY);
		httpGet.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());

		try {
			client.executeMethod(httpGet);
			in = httpGet.getResponseBodyAsStream();
			out = new FileOutputStream(new File(filePath + name));

			/*
			 * 读入数据存放到本地
			 */
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			out.close();
			in.close();

			File imgFile = new File(filePath + name);
			String absoluteFilePath = imgFile.getAbsolutePath();
			absoluteFilePath = pathStringConvert(absoluteFilePath);

			/*
			 * 将图片信息存入数据库
			 */
			String sql = "insert into image(title,url,site,localPath,examined) values('"
					+ name
					+ "','"
					+ surl
					+ "','"
					+ purl
					+ "','"
					+ absoluteFilePath + "','" + "0" + "')";
			if (connDB.executeUpdate(sql))
				System.out.println("图片" + name + "已保存进数据库！");
			else
				System.out.println("图片" + name + "保存进数据库时出错！");

			/*
			 * 释放对象
			 */
			httpGet.releaseConnection();
			urls = null;
			purl = null;
			surl = null;
			name = null;
			client = null;
			b = null;
			imgFile = null;

			return true;
		} catch (SocketTimeoutException exc) {
			System.err.println("下载图片时出现 SocketTimeoutException 异常！");
			httpGet.releaseConnection();
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e1) {
				// e1.printStackTrace();
				return false;
			}
			urls = null;
			purl = null;
			surl = null;
			name = null;
			client = null;
			
			return false;
		} catch (ConnectException e) {
			System.err.println("下载图片时出现 ConnectException 异常！");
			httpGet.releaseConnection();
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e1) {
				// e1.printStackTrace();
				return false;
			}
			urls = null;
			purl = null;
			surl = null;
			name = null;
			client = null;
			
			return false;
		} catch (HttpException e) {
			System.err.println("下载图片时捕获HttpException异常");
			httpGet.releaseConnection();
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e1) {
				// e1.printStackTrace();
				return false;
			}
			urls = null;
			purl = null;
			surl = null;
			name = null;
			client = null;
			
			return false;
		} catch (IOException e) {
			// e.printStackTrace();
			System.err.println("下载图片时捕获IOException异常");
			httpGet.releaseConnection();
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();	
			} catch (IOException e1) {
				// e1.printStackTrace();
				return false;
			}
			urls = null;
			purl = null;
			surl = null;
			name = null;
			client = null;
			
			return false;
		} catch (Exception e) {
			// e.printStackTrace();
			System.err.println("下载图片时捕获Exception异常");
			httpGet.releaseConnection();
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();	
			} catch (IOException e1) {
				// e1.printStackTrace();
				return false;
			}
			urls = null;
			purl = null;
			surl = null;
			name = null;
			client = null;
			
			return false;
		}

	}

	public String getFileNameByUrl(String url) {

		url = url.substring(7);// remove http://

		url = url.replaceAll("[?/:*|<>]", "_");

		return url;

	}

	public String pathStringConvert(String pathString) {// 转换路径字符串，把“\”转换为“\\”
		String newString;
		char ch;
		newString = "";
		for (int i = 0; i < pathString.length(); i++) {
			ch = pathString.charAt(i);
			newString += ch;
			if (ch == '\\')
				newString += '\\';
		}

		return newString;
	}

}
