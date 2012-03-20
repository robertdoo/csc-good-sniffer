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
 * ����ͼƬ����
 * 
 * @author Owner
 * 
 */
public class ImgDownload {
	private ConnDB connDB;
	private final String filePath = SpiderConf
			.getPathByName(SpiderConf.SPIDER_IMG_PATH);

	/**
	 * ���Ժ���
	 */
	public static void main(String[] args) {
		ImgDownload id = new ImgDownload();
		id
				.imgDownLoad("http://www.163.com/@@@http://img2.cache.netease.com/cnews/2010/11/23/201011231527016cd39.jpg");
	}

	/**
	 * ���캯�� �������ݿ�
	 */
	public ImgDownload() {
		connDB = new ConnDB("test");
		connDB.connectToDB();
	}

	/**
	 * �������ݿ����Ӷ���
	 * 
	 * @return
	 */
	public ConnDB getConnDB() {
		return connDB;
	}

	/**
	 * ����ͼƬ�ĺ���
	 * 
	 * @param url
	 * @return
	 */
	public boolean imgDownLoad(String url) {
		/*
		 * �����Ǻ���imgDownLoad�õ��Ķ���
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
			 * �������ݴ�ŵ�����
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
			 * ��ͼƬ��Ϣ�������ݿ�
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
				System.out.println("ͼƬ" + name + "�ѱ�������ݿ⣡");
			else
				System.out.println("ͼƬ" + name + "��������ݿ�ʱ����");

			/*
			 * �ͷŶ���
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
			System.err.println("����ͼƬʱ���� SocketTimeoutException �쳣��");
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
			System.err.println("����ͼƬʱ���� ConnectException �쳣��");
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
			System.err.println("����ͼƬʱ����HttpException�쳣");
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
			System.err.println("����ͼƬʱ����IOException�쳣");
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
			System.err.println("����ͼƬʱ����Exception�쳣");
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

	public String pathStringConvert(String pathString) {// ת��·���ַ������ѡ�\��ת��Ϊ��\\��
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
