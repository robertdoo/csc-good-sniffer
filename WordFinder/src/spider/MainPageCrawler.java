package spider;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lucene.Index.IndexProcesser;

import ContentExtract.MainBodyExtract;
import ContentExtract.TimeExtract;
import ContentExtract.TitleExtract;
import Filter.BloomFilter;
import dataStruct.Result;
import dataStruct.SpiderData;
import database.ConvertSpiderDataToDB;

/**
 * ��ҳ��������
 * 
 * @author Owner
 * 
 */
public class MainPageCrawler extends Thread {
	// �����ж���
	private LinkedList<String> url_list;
	// ������������������̳߳�ͳһ����
	private StopThread st;
	// �������߳�
	private boolean stopThisThread;
	// ��ַȥ�ع�����
	private BloomFilter bf;
	// ��ڵ�ַ
	private String entryUrl;
	// ����id
	private int id;
	// ��ʾ���������̳߳�
	//private int flag;
	// ����ҳ�����
	private int num;
	// ѭ�����д���
	private long round;
	// �����Ƿ��������б�־
	private boolean isRunning;
	// �������ݰ�װ��
	private SpiderData spiderData;
	// �����ʾ��װ��
	private Result result;
	// ������
	IndexProcesser indexProcesser;
	// �����ݿ�
	ConvertSpiderDataToDB saveToDB;
	// ��ַ��ʽ������
	LinkFilter filter;
	// ������ȡ��1
	UrlParser up;
	// ������ȡ��2
	CatchUrl cu;
	// ����PDF
	PDFDownload pdf_download;
	// ����word�ĵ�
	DocDownload doc_download;
	public void setRound(long round) {
		this.round = round;
	}

	public String getEntryUrl() {
		return entryUrl;
	}

	// ����XLS�ĵ�
	XlsDownload xls_download;
	// ����HTML��ҳ
	HtmlDownload html_downLoader;
	// ��ȡ����
	TitleExtract titleExtract = new TitleExtract();
	// ��ȡʱ��
	TimeExtract timeExtract = new TimeExtract();
	// ��ʼ����ȡ�������ݵ���
	MainBodyExtract bodyExtract = new MainBodyExtract();

	/**
	 * ���Ժ���
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

	}

	public MainPageCrawler(int id, BloomFilter bf, StopThread st,
			IndexProcesser indexProcesser, ConvertSpiderDataToDB saveToDB,int flag) {
		/*
		 * ��ʼ������id����¡���������������󡢴����ݿ����ͣ������� �����̳߳�ͳһ����
		 */
		this.st = st;
		this.bf = bf;
		this.id = id;
		//this.flag = flag;
		this.saveToDB = saveToDB;
		this.indexProcesser = indexProcesser;

		round = 0;
		isRunning = false;
		stopThisThread = false;

		url_list = new LinkedList<String>();
		up = new UrlParser();
		cu = new CatchUrl();
		html_downLoader = new HtmlDownload(SpiderConf.MAINPAGE_CRAWLER);
		titleExtract = new TitleExtract();
		timeExtract = new TimeExtract();
		bodyExtract = new MainBodyExtract();
	}

	public boolean isStopThisThread() {
		return stopThisThread;
	}

	public void setStopThisThread(boolean stopThisThread) {
		this.stopThisThread = stopThisThread;
	}

	/**
	 * �������г���
	 * 
	 * @param url
	 */
	public void crawler(String url) {
		// �������е�����
		String visitUrl = url;

		Set<String> links1 = up.extracLinks(visitUrl, filter, entryUrl);
		Set<String> links2 = cu.getText(visitUrl, filter, entryUrl);

		/*
		 * ��һ������ֻ��ҳ�����������Ӵ����ϣ��
		 */
		if (round == 0) {
			for (String link : links1)
				bf.bloomfilter(link);
			for (String link : links2)
				bf.bloomfilter(link);
			round++;
			links1.clear();
			links2.clear();
			return;
		}

		/*
		 * �µ�δ���ʵ�URL����
		 */
		for (String link : links1) {
			url_list.add(link);
		}
		for (String link : links2) {
			url_list.add(link);
		}
		links1.clear();
		links2.clear();

		long beginTime = new Date().getTime();
		while (!url_list.isEmpty() && st.isMainPageCrawlerFlag() == false) {
			// ��ͷURL����
			visitUrl = url_list.removeFirst();
			// �ظ���������
			if (!bf.bloomfilter(visitUrl))
				continue;
			// ���������������
			num++;
			System.out.println("���ҳ�������� " + id + " ���ڴ������ӣ� " + visitUrl);

			/*
			 * �����PDF�����򽻸�PDF���Ӵ����ദ��
			 */
			if (visitUrl.endsWith("pdf") || visitUrl.endsWith("PDF")) {
				pdf_download = new PDFDownload(SpiderConf.MAINPAGE_CRAWLER,
						indexProcesser, saveToDB, entryUrl);
				pdf_download.PDFDownLoad(visitUrl);
				pdf_download = null;
				continue;
			}

			/*
			 * �����word�����򽻸�word���Ӵ����ദ��
			 */
			else if (visitUrl.endsWith("doc") || visitUrl.endsWith("docx")) {
				doc_download = new DocDownload(SpiderConf.MAINPAGE_CRAWLER,
						indexProcesser, saveToDB, entryUrl);
				doc_download.docDownLoad(visitUrl);
				doc_download = null;
				continue;
			}

			/*
			 * �����xls�����򽻸�xls�����ദ��
			 */
			else if (visitUrl.endsWith("xls") || visitUrl.endsWith("XLS")) {
				xls_download = new XlsDownload(SpiderConf.MAINPAGE_CRAWLER,
						indexProcesser, saveToDB, entryUrl);
				xls_download.xlsDownLoad(visitUrl);
				xls_download = null;
				continue;
			}

			/*
			 * ����һ�����ҳ�����Ƚ������أ�����ȡ����״̬��
			 */
			int statuCode = html_downLoader.downloadFile(visitUrl);
			// 403������ҳ����ֹ���ʣ�ֱ������ѭ������ֹ���߳�
			if (statuCode == 403) {
				System.err.println("���ҳ�������� " + id + "����" + visitUrl + "����ֹ��");
				break;
			}

			// ��ȡʱ��
			String time = timeExtract.getTime(visitUrl);
			// ��ȡ����
			String title = titleExtract.getTitle(visitUrl);
			// ��ȡ����
			String body = bodyExtract.getMainBody(visitUrl);
			// ��ȡ�������ҳ�ļ�
			File htmlFile = new File(SpiderConf
					.getPathByName(SpiderConf.MAINPAGECRAWLER_HTML_PATH)
					+ getFileNameByUrl(visitUrl) + ".htm");
			// ��ȡ���غ���ҳ�ļ�����ľ���·��
			String htmlPath = htmlFile.getAbsolutePath();
			// ���û����ȡ�����ģ���������ѭ����������һ������
			if (body.indexOf("no bodyContent") != -1)
				continue;
			// �������404�������û����ȡ�����⣬��������ѭ����������һ������
			if (title.indexOf("404") != -1 || title.trim().length() == 0)
				continue;

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

			// �������ݿ�
			saveToDB.storeFile(spiderData);
			result = new Result(spiderData);
			// ��������
			indexProcesser.updateIndexByResult(result);

			/*
			 * ����Ķ����ÿ�
			 */
			time = null;
			htmlPath = null;
			visitUrl = null;
			body = null;
			title = null;
			spiderData = null;
			result = null;

		}
		long endTime = new Date().getTime();
		System.out
				.println("*****************************************************");
		System.out.println("���ҳ�������� " + id + "������ " + num + "�����ӣ���ʱ�� "
				+ (endTime - beginTime) / 1000 + "��");
		System.out
				.println("*****************************************************");
		num = 0;
		round++;
		url_list.clear();
	}

	/**
	 * getFileNameByUrl��urlȥ��http://,���ҽ�����������?/:*|<>���ַ� ת��Ϊ_�󷵻أ�ת����Ŀ����ʹ���ܹ���Ϊ�ļ���
	 */
	public String getFileNameByUrl(String url) {

		url = url.substring(7);// remove http://

		url = url.replaceAll("[?/:*|<>]", "_");

		return url;

	}

	/**
	 * ������ڵ�ַ
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.entryUrl = url;
	}

	/**
	 * �鿴�����Ƿ���������
	 * 
	 * @return
	 */
	public boolean getIsRunning() {
		return this.isRunning;
	}

	/**
	 * �������б�־
	 * 
	 * @param flag
	 */
	public synchronized void setIsRunning(boolean flag) {
		this.isRunning = flag;
		if (flag)
			this.notify();
	}
	
	public void setIsRunningFalse(){
		this.isRunning = false;
	}
	public void setIsRunningTrue(){
		this.isRunning = true;
	}

	/**
	 * run����
	 */
	public synchronized void run() {
		while (st.isMainPageCrawlerFlag() == false) {
			try {
				if (isRunning == false) {
					wait(60000);
				} else {
					long beginTime = new Date().getTime();
					crawler(entryUrl);
					long endTime = new Date().getTime();
					System.out.println("�߳�" + id + "��ʱ" + (endTime - beginTime)
							+ "���룡");
					if (endTime - beginTime < 60000)
						wait(60000 - (endTime - beginTime));
				}
			} catch (InterruptedException e) {
				System.err.println("�������InterruptedException�쳣��");
			}
		}
		System.err.println("���ҳ��������  " + id + "����");
		setIsRunning(false);
	}

	/**
	 * ��ʼ����ַ��ʽ������
	 */
	public void iniFilter() {
		filter = new LinkFilter() {
			public boolean accept(String url, String entryAddress) {
				try {
					String line = entryAddress;
					// ����#��+�ŵ�������������ת��ҳ���������Ӳ�����
					if (url.indexOf("#") != -1 || url.indexOf("+") != -1)
						return false;
					// ���������ַ������Ӷ���ȷ��ҳ�桢ע��ҳ��
					if (url.indexOf("uid") != -1 || url.indexOf("user") != -1
							|| url.indexOf("action=") != -1
							|| url.indexOf("register") != -1
							|| url.indexOf("filter=") != -1
							|| url.indexOf("personalinfo") != -1
							|| url.indexOf("(") != -1)
						return false;
					// ���������ַ������Ӳ�����
					String regEx = "[\\u4e00-\\u9fa5]";
					Pattern p = Pattern.compile(regEx);
					Matcher m = p.matcher(url);
					if (m.find())
						return false;

					if (url.indexOf("...") != -1)
						return false;

					// ����ָ��ͼƬ�����Ӷ�������
					if (url.endsWith(".jpg") || url.endsWith(".gif")
							|| url.endsWith(".bmp") || url.endsWith(".002C"))
						return false;
					// ָ��word�ļ������ӱ���
					if ((url.endsWith(".doc") || url.endsWith(".docx"))
							&& url.startsWith("http://"))
						return true;
					// ָ��pdf�ļ������ӱ���
					if (url.endsWith(".pdf") && url.startsWith("http://"))
						return true;
					if (url.endsWith(".xls") && url.startsWith("http://"))
						return true;
					/*
					 * ������ͨ��ҳ���ӹ��˵�ԭ���ǣ���http://��ͷ ���Һ���ڵ�ַ����ͬ������
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

	/**
	 * ��������ѭ�����д���
	 * 
	 * @return
	 */
	public long getRound() {
		return round;
	}
}
