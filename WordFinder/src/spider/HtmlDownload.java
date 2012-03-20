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
 * ����HTML�ļ�����
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
	 * ���Ժ���
	 */
	public static void main(String[] args) {
		String url = "http://bbs.tiexue.net/post_4542035_1.html";
		new HtmlDownload(1).downloadFile(url);
	}

	/**
	 * ���캯��
	 * 
	 * @param chose
	 *            �����ж�����ҳ����������û�����̳�������
	 */
	public HtmlDownload(int chose) {
		if (chose == SpiderConf.DEEP_CRAWLER)
			filePath = filePath1;
		else
			filePath = filePath2;
		htmlPath = filePath;
	}

	/**
	 *  ������ҳ�ֽ����鵽�����ļ�
	 */
	private boolean saveToLocal(InputStream inputStream, String filePath) {
		// filePathΪҪ�����ļ�����Ե�ַ
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
			System.err.println("������ҳʱ�����쳣��");
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
	 *  ����URLָ�����ҳ
	 */
	public synchronized int downloadFile(String url) {
		HttpClient httpClient = null;
		GetMethod getMethod = null;
		InputStream responseBody = null;
		String fullUrl = url;
		
		// 1����HttpClient�������ò���
		httpClient = new HttpClient();
		// ���ó�ʱ����30��
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(
				30000);
		httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT,
				"vigorouswei@126.com");
		// 2����GetMethod�������ò���
		getMethod = new GetMethod(url);

		getMethod.getParams().setParameter("http.protocol.cookie-policy",
				CookiePolicy.BROWSER_COMPATIBILITY);
		// ����get����ʱ30��
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
		// �����������Դ���
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());

		// 3ִ��HTTP get����
		try {
			int statuCode = httpClient.executeMethod(getMethod);
			/*
			 *  �жϷ��ʵ�״̬��,����ͷ���
			 */
			if (statuCode != HttpStatus.SC_OK) {
				System.err.println("ҳ�����ʧ��: " + getMethod.getStatusLine());
				/*
				 * �ͷŶ���
				 */
				htmlPath = null;
				httpClient = null;
				getMethod.releaseConnection();
				return statuCode;
			}
			
			// 4����HTTP��Ӧ����
			responseBody = getMethod.getResponseBodyAsStream();
			url = getFileNameByUrl(url);
			htmlPath = filePath + url + ".htm";
			boolean isSaved = saveToLocal(responseBody, htmlPath);
			responseBody.close();
			
			/*
			 * �������ʧ���򷵻ش����ź�
			 */
			if (isSaved == false)
				return 300;
		} catch (HttpException e) {
			System.err.println("check your http address: " + fullUrl);
			System.err.println("������ҳʱ����HttpException�쳣");
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
			System.out.println("������ҳʱ����IOException�쳣");
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
		 * �ͷŶ���
		 */
		getMethod.releaseConnection();
		fullUrl = null;
		httpClient = null;
		
		return HttpStatus.SC_OK;
	}
	
	/**
	 * ȥ�������еġ�?/:*|<>�����ţ�ʹ���ܳ�Ϊ�ļ���
	 */
	public String getFileNameByUrl(String url) {
		// remove http://
		url = url.substring(7);
		url = url.replaceAll("[?/:*|<>]", "_");
		return url;
	}

}
