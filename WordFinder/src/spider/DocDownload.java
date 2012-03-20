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
 * @author Owner DocDownloadʵ�ֶ�doc�ļ������ز���ȡ�ı�����
 */
public class DocDownload {
	/**
	 * ���Ժ���
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
	 * ���캯��
	 * 
	 * @param chose
	 *            �ֱ�����ҳ����������û�����̳�������
	 * @param indexProcesser
	 *            �����������
	 * @param saveToDB
	 *            �����ݿ����
	 * @param pUrl
	 *            ���Ӹ���ַ
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
	 * ����DOC�ļ��ĺ���
	 */
	public boolean docDownLoad(String url) {
		/*
		 * �����Ǻ���docDownLoad�õ��Ķ���
		 */
		HttpClient client = null;
		GetMethod httpGet = null;
		InputStream in = null;
		FileOutputStream out = null;
		FileInputStream fis = null;
		WordExtractor extractor = null;
		File docFile = null;

		// ��/��url�ֶΣ�ȡ���һ����Ϊ�ļ� ��
		String[] urls = url.split("/");
		String name = urls[urls.length - 1];
		// ����HttpClient����
		client = new HttpClient();
		// ����������Ӧ��ʱʱ��Ϊ5��
		client.getHttpConnectionManager().getParams().setConnectionTimeout(
				30000);
		// �����������
		client.getParams().setParameter(HttpMethodParams.USER_AGENT,
				"vigorouswei@126.com");
		// ����GetMethod����
		httpGet = new GetMethod(url);
		// ��������ʱ��Ϊ30��
		httpGet.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
		// �����������Դ���
		httpGet.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		httpGet.getParams().setParameter("http.protocol.cookie-policy",
				CookiePolicy.BROWSER_COMPATIBILITY);

		try {
			client.executeMethod(httpGet);
			// ��ȡ������
			in = httpGet.getResponseBodyAsStream();
			// ��ʼ�������ļ��ӿ�
			out = new FileOutputStream(new File(filePath + name));
			// ����һ���ֽ����飬�������������ж�ȡ����
			byte[] b = new byte[1024];
			// ÿ�ζ�ȡ�ĸ���
			int len = 0;
			/*
			 * ѭ���������������ݶ�����Ȼ�󱣴��������ļ���
			 */
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			out.close();
			in.close();

			// ��ʼ����ȡ�����ļ��ӿ�
			fis = new FileInputStream(new File(filePath + name));
			// ��ʼ��WordExtractor����
			extractor = new WordExtractor();
			// ��doc�ļ�����ȡ����
			String text = extractor.extractText(fis);
			// ȥ���ı������еĿո���Ʊ��
			text = text.replaceAll(" ", "");
			text = text.replaceAll("	", "");
			// ���ı������е�ȫ�Ƿ����滻Ϊ��Ƿ���
			text = FilePreprocess.replace(text);
			// ��ȡ����·��
			docFile = new File(filePath + name);
			String docFilePath = docFile.getAbsolutePath();
			// ��ȡ���ݵı���
			String title = getTitle(text);

			/*
			 * ��װ��ȡ��������
			 */
			spiderData = new SpiderData();
			spiderData.setType("doc");
			spiderData.setUrl(url);
			spiderData.setTitle(title);
			spiderData.setTime("no time");
			spiderData.setPageMirrorPath(docFilePath);
			spiderData.setContent(text);
			spiderData.setPurl(SpiderConf.getNameByUrl(pUrl));

			// �����ݿ�
			saveToDB.storeFile(spiderData);
			result = new Result(spiderData);
			// ��������
			indexProcesser.updateIndexByResult(result);
			// �ͷ�����
			httpGet.releaseConnection();

			/*
			 * �����ͷŶ���
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
			System.out.println("�����ĵ�ʱ����HttpException�쳣");
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
			 * �����ͷŶ���
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
			System.err.println("�����ĵ�ʱ���� SocketTimeoutException �쳣��");
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
			 * �����ͷŶ���
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
			System.err.println("�����ĵ�ʱ���� ConnectException �쳣��");
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
			 * �����ͷŶ���
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
			System.err.println("�����ĵ�ʱ����IOException�쳣");
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
			 * �����ͷŶ���
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
			System.err.println("�����ĵ�ʱ����SQLException�쳣");
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
			 * �����ͷŶ���
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
			 * �����ͷŶ���
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
	 * ��һ���ַ����л�ȡ����ĺ���,ԭ�������Ƚ��ַ������س������У� Ȼ��ȡǰ2��������Ƕ��ַ�����Ϊ����
	 */
	public String getTitle(String str) {
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
