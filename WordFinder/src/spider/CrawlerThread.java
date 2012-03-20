package spider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lucene.Index.IndexProcesser;
import ContentExtract.MainBodyExtract;
import ContentExtract.TimeExtract;
import ContentExtract.TitleExtract;
import Filter.BloomFilter1;
import dataStruct.Result;
import dataStruct.SpiderData;
import database.ConvertSpiderDataToDB;

/**
 * 
 * @author Owner ���������߳�
 * 
 */
public class CrawlerThread extends Thread {
	// ���Ӹ�ʽ������
	LinkFilter filter;
	// ��ַȥ�ع�����������
	BloomFilter1 bf;
	// queue�����ҳ����
	LinkedList<String> queue;
	// �������б�־
	private boolean runningFlag;
	// ��ڵ�ַ
	private String entryUrl;
	// ��������ID
	private int id;
	// �������ݰ�װ��
	private SpiderData spiderData;
	// ���ݽ����װ��
	private Result result;
	// �ж������Ƿ�ִֹͣ�е���
	private StopThread st;
	// ������
	private IndexProcesser indexProcesser;
	// ����ͼƬ
	private ImgDownload img_download;
	// ����PDF�ļ�
	private PDFDownload pdf_download;
	// ����word�ĵ�
	private DocDownload doc_download;
	// ����xls�ĵ�
	private XlsDownload xls_download;
	// ����html��ҳ
	private HtmlDownload html_downLoader;
	// ��ʼ����ȡ�������ݵ���
	private MainBodyExtract bodyExtract;
	// ��ȡ����
	private TitleExtract titleExtract;
	// ��ȡʱ��
	private TimeExtract timeExtract;
	// �����ݿ�
	private ConvertSpiderDataToDB saveToDB;
	// ������ȡ1
	UrlParser up;
	// ������ȡ2
	CatchUrl cu;

	/**
	 * �ж������Ƿ���������
	 */
	public boolean isRunning() {
		return runningFlag;
	}

	/**
	 * �������б�־
	 * 
	 * @param flag
	 */
	public synchronized void setRunning(boolean flag) {
		runningFlag = flag;
		if (flag)
			this.notify();
	}

	/**
	 * ��ȡ���ڴ����URL
	 * 
	 * @return
	 */
	public String getUrl() {
		return this.entryUrl;
	}

	/**
	 * ���õ�ǰҪ�����URL
	 */
	public void setUrl(String url) {
		this.entryUrl = url;
	}

	/**
	 * ���캯������ʼ�����б�־��������ַ���С�������
	 * 
	 * @param threadNumber
	 * @param bf
	 * @param st
	 * @param indexProcesser
	 * @param saveToDB
	 */
	public CrawlerThread(int threadNumber, BloomFilter1 bf, StopThread st,
			IndexProcesser indexProcesser, ConvertSpiderDataToDB saveToDB) {
		// �������б�־Ϊfalse
		runningFlag = false;
		System.out.println("thread " + threadNumber + " started");
		/*
		 * ��ʼ������id����¡���������������󡢴����ݿ����ͣ������� �����̳߳�ͳһ����
		 */
		id = threadNumber;
		this.bf = bf;
		this.st = st;
		this.saveToDB = saveToDB;
		this.indexProcesser = indexProcesser;

		queue = new LinkedList<String>();

		img_download = new ImgDownload();

		html_downLoader = new HtmlDownload(SpiderConf.DEEP_CRAWLER);

		bodyExtract = new MainBodyExtract();

		titleExtract = new TitleExtract();

		timeExtract = new TimeExtract();

		up = new UrlParser();

		cu = new CatchUrl();
	}

	/**
	 * getFileNameByUrl��urlȥ��http://,���ҽ�����������?/:*|<>���ַ� ת��Ϊ_�󷵻أ�ת����Ŀ����ʹ���ܹ���Ϊ�ļ���
	 */
	public String getFileNameByUrl(String url) {
		// remove http://
		url = url.substring(7);

		url = url.replaceAll("[?/:*|<>]", "_");

		return url;

	}

	/**
	 * ���ѹ�����߳�
	 */
	public synchronized void notifyThread() {
		this.notify();
	}

	/**
	 * ���г���
	 * 
	 * @param url
	 */
	public synchronized void crawler(String url) {
		/*
		 * ����Ϊ����crawler�õ��Ķ���
		 */
		BufferedWriter bw_url = null;
		Set<String> links1 = null;
		Set<String> links2 = null;
		File htmlFile = null;

		// ��Ҫ���е���ҳ��Ŀ
		int count = 0;
		// �����е���ҳ��Ŀ
		int counter = 0;
		// ���ڴ��������
		String visitUrl = url;
		// ��ҳ��ʱ��
		String time = "";
		// ����
		String title = "";
		// ����
		String body = "";
		// ��ҳ���ر����·��
		String htmlPath = "";
		// ������ڵ�ַ��ץȡ�������ӵ��ļ���
		String urlName = url.replaceAll("[?/:*|<>]", "_");

		try {
			// ����I/O�ӿڽ���ڵ�ַ��ץȡ�������ӱ���������
			bw_url = new BufferedWriter(new FileWriter(SpiderConf
					.getPathByName(SpiderConf.SPIDER_URL_PATH)
					+ urlName + ".txt"));

			System.out.println("�߳�  " + id + " ��ʼ�������ӣ� " + visitUrl);
			// ������ڵ�ַҳ�棬����ȡ����״̬��
			int statuCode = html_downLoader.downloadFile(visitUrl);
			// ���״̬��Ϊ403��ҳ�汻��ֹ���ʣ���ֱ�ӷ��أ���������
			if (statuCode == 403) {
				System.err.println("����"+visitUrl+"����ֹ���߳�"+id+"ִֹͣ�У�");
				return;
			}

			/*
			 * ��ȡ��������ҳ��URL��UrlParser��CatchUrl�����඼ʵ�ִ˹��ܣ������һ��ʹ���ܹ� ��������ȡ��ȫ��������
			 */
			links1 = up.extracLinks(visitUrl, filter, entryUrl);
			links2 = cu.getText(visitUrl, filter, entryUrl);
			// �µ�δ���ʵ�URL����
			for (String link : links1) {
				// �����ͼƬ���ӵĻ�Ҫ��ͼƬ������ҳ����һ�𱣴棬�м���@@@����
				if (link.endsWith(".jpg") || link.endsWith(".gif")
						|| link.endsWith(".bmp") || link.endsWith(".002C")) {
					link = visitUrl + "@@@" + link;
				}
				queue.add(link);
				// ����ȡ��������д�뱾���ļ�
				bw_url.write("from HtmlpaserTool: " + link);
				bw_url.newLine();
				count++;
			}
			links1 = null;
			for (String link : links2) {

				// �����ͼƬ���ӵĻ�Ҫ��ͼƬ������ҳ����һ�𱣴棬�м���@@@����
				if (link.endsWith(".jpg") || link.endsWith(".gif")
						|| link.endsWith(".bmp") || link.endsWith(".002C")) {
					link = visitUrl + "@@@" + link;
				}
				queue.add(link);
				// ����ȡ��������д�뱾���ļ�
				bw_url.write("from testCatchUrl: " + link);
				bw_url.newLine();
				count++;
			}
			links2 = null;
			bw_url.flush();
			bw_url.close();
			// ����ץȡ��ĿΪ��ȡ��������Ŀ��ƽ�����ɸ���ʵ������������ã�
			count = count * count;
			System.out.println("�߳� " + id + "��Ҫ����" + count + "��URL");
		} catch (IOException e) {
			System.err.println("�߳�" + id + "������ڵ�ַ��" + visitUrl
					+ "ʱ����IOException�������˳���");
			return;
		}

		// ��ʼ�������������������������5���쳣����ֹ���̣߳�
		int faultNumber = 0;
		
		/*
		 * ��ʼѭ����ȡ�����ҳ�ڵ����ӣ�ѭ������Ϊ�� �������в�Ϊ��������ȡ��ҳ��С��Ԥ����ȡ��ҳ����ֹͣ���б�־Ϊfalse
		 */
		while (!queue.isEmpty() && counter < count
				&& st.isCrawlerFlag() == false) {
			// ��ͷurl����
			visitUrl = queue.removeFirst();
			// �ظ���������
			if (!bf.bloomfilter(visitUrl))
				continue;

			System.out.println("�߳� " + id + " ���ڴ������ӣ� " + visitUrl);

			/*
			 *  �����ͼƬ�����򽻸�ͼƬ���Ӵ����ദ��
			 */
			if (visitUrl.endsWith(".jpg") || visitUrl.endsWith(".gif")
					|| visitUrl.endsWith(".bmp") || visitUrl.endsWith(".002C")) {
				// ���ͼƬ����ʧ�ܣ��������������1
				if (!img_download.imgDownLoad(visitUrl))
					faultNumber++;
				// ���������������ﵽ5��������ѭ�����������߳�
				if (faultNumber >= 5)
					break;
				continue;
			}
			
			/*
			 *  �����pdf�����򽻸�pdf���Ӵ����ദ��
			 */
			else if (visitUrl.endsWith("pdf")) {
				pdf_download = new PDFDownload(SpiderConf.DEEP_CRAWLER,
						indexProcesser, saveToDB, entryUrl);
				// ����ļ�����ʧ�ܣ��������������1
				if (!pdf_download.PDFDownLoad(visitUrl))
					faultNumber++;
				// ��pdf_download�����ÿգ���Ϊ�����ã�
				pdf_download = null;
				// ���������������ﵽ5��������ѭ�����������߳�
				if (faultNumber >= 5)
					break;
				continue;
			}
			
			/*
			 *  �����word�����򽻸�word���Ӵ����ദ��
			 */
			else if (visitUrl.endsWith("doc") || visitUrl.endsWith("docx")) {
				doc_download = new DocDownload(SpiderConf.DEEP_CRAWLER,
						indexProcesser, saveToDB, entryUrl);
				// ����ļ�����ʧ�ܣ��������������1
				if (!doc_download.docDownLoad(visitUrl))
					faultNumber++;
				//��doc_download�����ÿ�
				doc_download = null;
				// ���������������ﵽ5��������ѭ�����������߳�
				if (faultNumber >= 5)
					break;
				continue;
			}
			
			/*
			 *  �����XLS�����򽻸�XLS�����ദ��
			 */
			else if (visitUrl.endsWith("xls")) {
				xls_download = new XlsDownload(SpiderConf.DEEP_CRAWLER,
						indexProcesser, saveToDB, entryUrl);
				// ����ļ�����ʧ�ܣ��������������1
				if (!xls_download.xlsDownLoad(visitUrl))
					faultNumber++;
				//��xls_download�����ÿ�
				xls_download = null;
				// ���������������ﵽ5��������ѭ�����������߳�
				if (faultNumber >= 5)
					break;
				continue;
			}
			
			/*
			 * ��������д��С�page=��������һ����Ŀ¼ҳ�棬����ҳ��ֻ����ȡ�������Ӽ���
			 */
			else if (visitUrl.indexOf("page=") != -1) {
				if (queue.size() < count) {
					Set<String> links_1 = up.extracLinks(visitUrl, filter,
							entryUrl);
					Set<String> links_2 = cu.getText(visitUrl, filter,
							entryUrl);
					// �µ�δ���ʵ�URL���У�ԭ���ǰ����ע�͵�һ��
					for (String link : links_1) {

						if (link.endsWith(".jpg") || link.endsWith(".gif")
								|| link.endsWith(".bmp")
								|| link.endsWith(".002C")) {
							link = visitUrl + "@@@" + link;
						}
						queue.add(link);
					}
					links_1 = null;
					for (String link : links_2) {

						if (link.endsWith(".jpg") || link.endsWith(".gif")
								|| link.endsWith(".bmp")
								|| link.endsWith(".002C")) {
							link = visitUrl + "@@@" + link;
						}
						queue.add(link);
					}
					links_2 = null;	
				}
				continue;
			}

			/*
			 *  ����һ�����ҳ�����Ƚ������أ�����ȡ����״̬�룬�ٽ���������ȡ
			 */
			int statuCode = html_downLoader.downloadFile(visitUrl);
			// 403������ҳ����ֹ���ʣ�ֱ������ѭ������ֹ���߳�
			if (statuCode == 403) {
				System.err.println("����" + visitUrl + "����ֹ��");
				break;
			}

			// ������������������������������1��������ڵ���5��������ѭ��, �������̣߳������������ѭ����������һ�����ӵĴ���
			else if (statuCode != 200) {
				faultNumber++;
				if (faultNumber >= 5)
					break;
				continue;
			}

			// ��ȡʱ��
			time = timeExtract.getTime(visitUrl);
			// ��ȡ����
			title = titleExtract.getTitle(visitUrl);
			// ��ȡ����
			body = bodyExtract.getMainBody(visitUrl);
			// ��ȡ�������ҳ�ļ�
			htmlFile = new File(SpiderConf
					.getPathByName(SpiderConf.SPIDER_HTML_PATH)
					+ getFileNameByUrl(visitUrl) + ".htm");
			// ��ȡ���غ���ҳ�ļ�����ľ���·��
			htmlPath = htmlFile.getAbsolutePath();
			// ���û����ȡ�����ģ���������ѭ����������һ������
			if (body.indexOf("no bodyContent") != -1)
				continue;
			// �������404�������û����ȡ�����⣬��������ѭ����������һ������
			if (title.indexOf("404") != -1 || title.trim().length() == 0)
				continue;

			/*
			 *  ��ȡʱ�䡢���⡢��������ʱ�����������������1,
			 *  ��� ������������ﵽ5�����������̣߳������������ѭ����������һ��
			 */
			if (time.equals("error") || title.equals("error")
					|| body.equals("error")) {
				faultNumber++;
				if (faultNumber >= 5)
					break;
				continue;
			}
			
			/*
			 * ����ȡ������Ϣ���з�װ�����������ʹ����ݿ�
			 */
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
			//�����ݿ�
			saveToDB.storeFile(spiderData);
			result = new Result(spiderData);
			// ��������
			indexProcesser.updateIndexByResult(result);
			
			/*
			 * ����Ķ����ÿ�
			 */
			time = null;
			htmlPath = null;
			//visitUrl = null;
			body = null;
			title = null;
			spiderData = null;
			result = null;
	
			/*
			 *  ��ȡ��������ҳ��URL
			 */
			if (queue.size() < count) {
				links1 = up.extracLinks(visitUrl, filter, entryUrl);
				links2 = cu.getText(visitUrl, filter, entryUrl);
				// �µ�δ���ʵ�URL����
				for (String link : links1) {
					if (link.endsWith(".jpg") || link.endsWith(".gif")
							|| link.endsWith(".bmp") || link.endsWith(".002C")) {
						link = visitUrl + "@@@" + link;
					}
					queue.add(link);
				}
				links1 = null;
				for (String link : links2) {

					if (link.endsWith(".jpg") || link.endsWith(".gif")
							|| link.endsWith(".bmp") || link.endsWith(".002C")) {
						link = visitUrl + "@@@" + link;
					}
					queue.add(link);
				}
				links2 = null;
			}
			
			// �Ѵ�����ҳ����1
			counter++;
			// ���������������Ϊ0			
			faultNumber = 0;
	}

		/*
		 *  ��������ַ�ÿ�
		 */
		while (!queue.isEmpty()) {
			queue.removeFirst();
		}
		
		/*
		 * ���н��������������Ϣ
		 */
		System.out.println("/////////////////////////////////////////////////////////");
		System.out.println("///�߳� " + id + "����,������  " + counter + " ����ҳ����///");
		System.out.println("////////////////////////////////////////////////////////");
	}

	/**
	 * (non-Javadoc)
	 * @see java.lang.Thread#run() ��дThread��run����
	 */
	public synchronized void run() {
		try {
			// ��������߳���Ľ�����־Ϊfalse��ִ������ѭ��
			while (st.isCrawlerFlag() == false) {
				// ���б�־λfalseʱ�̵߳ȴ�������ִ����������
				if (!runningFlag) {
					this.wait();
				} else {
					crawler(getUrl());
					//ִ����һ�������������Ϣ1����
					this.wait(60000);
				}
			}
			// �ر�ͼ��������е����ݿ�����
			img_download.getConnDB().closeConnection();
		} catch (InterruptedException e) {
			
		}
		System.out.println("�߳� " + id + "��ֹͣ��");
	}

	/**
	 *  ת��·���ַ������ѡ�\��ת��Ϊ��\\��
	 */
	public String pathStringConvert(String pathString) {
		String newString = "";
		char ch;
		for (int i = 0; i < pathString.length(); i++) {
			ch = pathString.charAt(i);
			newString += ch;
			if (ch == '\\')
				newString += '\\';
		}
		return newString;
	}

	/**
	 * ��ʼ�����Ӹ�ʽ������
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
						return true;
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
}
