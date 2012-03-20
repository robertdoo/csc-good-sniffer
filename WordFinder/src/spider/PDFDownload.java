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
 * ����PDF����
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
	 * ���Ժ���
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
	 * ���캯��
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
		 *  �����Ǻ���PDFDownLoad�õ��ı���
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
			 * ��ȡ���ݲ����뱾���ļ�
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
            
			// ��������
			getText(url, pdfFilePath);

			/*
			 * �ͷŶ���
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
			System.err.println("�����ĵ�ʱ���� SocketTimeoutException �쳣��");
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
				// e1.printStackTrace();
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
				// e1.printStackTrace();
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
				// e1.printStackTrace();
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
		} catch (SQLException e) {
			System.err.println("�����ĵ�ʱ����SQLException�쳣");
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
				e1.printStackTrace();
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
	 * ��ȡPDF�ĵ�����Ϣ
	 * @param file
	 * @param pdfFilePath
	 * @throws Exception
	 */
	public void getText(String file, String pdfFilePath) throws Exception {
		/*
		 *  �����Ǻ���getText�õ��ı���
		 */
		// �ڴ��д洢��PDF DOcument
		PDDocument document = null;
		URL url = null;
		PDFTextStripper stripper = null;
		// �Ƿ�����
		boolean sort = false;
		// PDF�ļ���
		String PdfFile = file;
		// ���뷽ʽ
		// String encoding="UTF-8";
		// ��ʼ��ȡҳ��
		int startPage = 1;
		// ������ȡҳ��
		int endPage = Integer.MAX_VALUE;
       
		/*
         *  ע����������try catch�������Ƴ������ת
         */
		try {
			try {
				// ���ȵ���һ��URL��װ���ļ�������õ��쳣�ٴӱ����ļ�ϵͳȥװ���ļ�
				url = new URL(PdfFile);
				document = PDDocument.load(url);
			} catch (MalformedURLException e) {
				// �����ΪURLװ�صõ��쳣����ļ�ϵͳװ��
				document = PDDocument.load(PdfFile);
			}
			// ��PDFTextStripper����ȡ�ı�
			stripper = new PDFTextStripper();
			// �����Ƿ�����
			stripper.setSortByPosition(sort);
			// ������ʼҳ
			stripper.setStartPage(startPage);
			// ���ý���ҳ
			stripper.setEndPage(endPage);
			// ��ȡ����
			String title = getTitle(stripper.getText(document));
			
			
			spiderData = new SpiderData();
			spiderData.setType("pdf");
			spiderData.setUrl(file);
			spiderData.setTitle(title);
			spiderData.setTime("no time");
			spiderData.setPageMirrorPath(pdfFilePath);
			spiderData.setContent(stripper.getText(document));
			spiderData.setPurl(SpiderConf.getNameByUrl(pUrl));
			
			//�������ݿ�
			saveToDB.storeFile(spiderData);
			result = new Result(spiderData);
			// ��������
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
	 * ��һ���ı�����ȡ����
	 * @param str
	 * @return
	 */
	public String getTitle(String str) {
		// �����Ǻ���getTitle�õ��ı���
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
