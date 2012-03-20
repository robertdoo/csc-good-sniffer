package spider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.StringTokenizer;

import jxl.Sheet;
import jxl.Workbook;
import lucene.Index.IndexProcesser;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import dataStruct.Result;
import dataStruct.SpiderData;
import database.ConvertSpiderDataToDB;

/**
 * ����XLS�ļ��ĺ���
 * 
 * @author Owner
 * 
 */
public class XlsDownload {
	private final String filePath1 = SpiderConf
			.getPathByName(SpiderConf.SPIDER_XLS_PATH);
	private final String filePath2 = SpiderConf
			.getPathByName(SpiderConf.MAINPAGECRAWLER_XLS_PATH);
	private String pUrl;
	private String filePath;
	private SpiderData spiderData;
	private Result result;
	private IndexProcesser indexProcesser;
	private ConvertSpiderDataToDB saveToDB;

	/**
	 * ���Ժ���
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = "http://panzhihua.gov.cn/images/smpd/rsks/2009/11/23/E7D753E310564D7186BBD869F684FDD3.xls";
		new XlsDownload(1, new IndexProcesser(1), new ConvertSpiderDataToDB(
				"test"), "").xlsDownLoad(url);
	}

	/**
	 * ���캯��
	 * 
	 * @param chose
	 * @param indexProcesser
	 * @param saveToDB
	 * @param pUrl
	 */
	public XlsDownload(int chose, IndexProcesser indexProcesser,
			ConvertSpiderDataToDB saveToDB, String pUrl) {
		if (chose == SpiderConf.DEEP_CRAWLER)
			filePath = filePath1;
		else
			filePath = filePath2;
		spiderData = new SpiderData();
		this.indexProcesser = indexProcesser;
		this.saveToDB = saveToDB;
		this.pUrl = pUrl;
	}

	public boolean xlsDownLoad(String url) {
		/*
		 * �����Ǻ���xlsDownLoad�õ��Ķ���
		 */
		HttpClient client = null;
		GetMethod httpGet = null;
		InputStream in = null;
		FileOutputStream out = null;
		File xlsFile;

		String[] urls = url.split("/");
		String name = urls[urls.length - 1];

		client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(
				10000);
		client.getParams().setParameter(HttpMethodParams.USER_AGENT,
				"vigorouswei@126.com");
		httpGet = new GetMethod(url);
		httpGet.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 10000);
		httpGet.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		httpGet.getParams().setParameter("http.protocol.cookie-policy",
				CookiePolicy.BROWSER_COMPATIBILITY);
		try {
			client.executeMethod(httpGet);
			in = httpGet.getResponseBodyAsStream();
			out = new FileOutputStream(new File(filePath + name));

			/*
			 * �������ݲ�������������
			 */
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			out.close();
			in.close();

			xlsFile = new File(filePath + name);
			String xlsFilePath = xlsFile.getAbsolutePath();

			getText(xlsFilePath, url);// ��������

			/*
			 * �ͷŶ���
			 */
			httpGet.releaseConnection();
			urls = null;
			name = null;
			client = null;
			b = null;
			xlsFile = null;
			xlsFilePath = null;

			return true;
		} catch (SocketTimeoutException exc) {
			System.err.println("�����ĵ�ʱ���� SocketTimeoutException �쳣��");
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e1) {
				return false;
			}
			/*
			 * �ͷŶ���
			 */
			httpGet.releaseConnection();
			urls = null;
			name = null;
			client = null;

			return false;
		} catch (ConnectException e) {
			System.err.println("�����ĵ�ʱ���� ConnectException �쳣��");
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e1) {
				return false;
			}
			/*
			 * �ͷŶ���
			 */
			httpGet.releaseConnection();
			urls = null;
			name = null;
			client = null;

			return false;
		} catch (HttpException e) {
			System.err.println("�����ĵ�ʱ����HttpException�쳣");
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e1) {
				return false;
			}
			/*
			 * �ͷŶ���
			 */
			httpGet.releaseConnection();
			urls = null;
			name = null;
			client = null;

			return false;
		} catch (IOException e) {
			System.err.println("�����ĵ�ʱ����IOException�쳣");
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e1) {
				return false;
			}
			/*
			 * �ͷŶ���
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
				return false;
			}
			/*
			 * �ͷŶ���
			 */
			httpGet.releaseConnection();
			urls = null;
			name = null;
			client = null;

			return false;
		}

	}

	/**
	 * ��ȡXLS�ļ���Ϣ
	 * 
	 * @param localPath
	 * @param url
	 */
	public void getText(String localPath, String url) {
		/*
		 * �����Ǻ���getText�õ��ı���
		 */
		File file = null;
		Workbook wb = null;
		Sheet[] sheets = null;
		StringBuffer sb = null;
		try {
			file = new File(localPath);
			wb = Workbook.getWorkbook(file);
			sheets = wb.getSheets();
			sb = new StringBuffer();
			String data = "";
			int columns = 0;
			int rows = 0;
			for (Sheet sheet : sheets) {
				columns = sheet.getColumns();
				rows = sheet.getRows();
				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < columns; j++)
						data += sheet.getCell(j, i).getContents() + " ";
					sb.append(data + "\r\n");
					data = "";
				}
			}

			String text = sb.toString();
			// ��ȡ���ݵı���
			String title = getTitle(text);

			spiderData.setType("xls");
			spiderData.setUrl(url);
			spiderData.setTitle(title);
			spiderData.setTime("no time");
			spiderData.setPageMirrorPath(localPath);
			spiderData.setContent(text);
			spiderData.setPurl(SpiderConf.getNameByUrl(pUrl));

			// �����ݿ�
			saveToDB.storeFile(spiderData);
			result = new Result(spiderData);
			// ��������
			indexProcesser.updateIndexByResult(result);

			/*
			 * �ͷŶ���
			 */
			file = null;
			wb = null;
			sheets = null;
			sb = null;
			data = null;
			text = null;
			title = null;
			spiderData = null;
			result = null;
		} catch (Exception e) {
			System.err.println("��ȡxls��Ϣʱ����");
			/*
			 * �ͷŶ���
			 */
			file = null;
			wb = null;
			sheets = null;
			sb = null;
			spiderData = null;
			result = null;
			}
	}

	/**
	 * ��һ���ı�����ȡ����
	 * @param str
	 * @return
	 */
	public String getTitle(String str) {
		// �����Ǻ���getTitle�õ��ı���
		StringTokenizer st;
		// ��ʼ��StringTokenizer�����Իس���Ϊ���
		st = new StringTokenizer(str, "\n");
		// ��ʼ������Ϊ��
		String title = "";
		// ��ʼ������Ϊ��
		int count = 0;
		// ѭ������ȡǰ�����нϳ����ַ�����Ϊ����
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
