package spider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

import ContentExtract.GetSourceFile;

/**
 * ץȡҳ�������ӵ���
 * 
 * @author Owner
 * 
 */
public class UrlParser {
	public static void main(String[] args) {
		/*String url = "http://www.xici.net/home.asp";
		System.out
				.println(getHead("http://tt.mop.com/topic/list_1_72_0_0.html"));
		System.out.println(url.substring(1));
		System.out.println(url.substring(1, url.length()));*/
		
		Set<String> urls = extracLinks("http://www.xici.net/home.asp");
		try{
		BufferedWriter bw = new BufferedWriter(new FileWriter("LinkBean.txt"));
		int id = 0;
		for(String url:urls){
			id++;
			System.out.println("From LinkBean: "+url);
			bw.write(id+" From LinkBean: "+url);
			bw.newLine();
		}
		bw.flush();
		bw.close();
		}catch(IOException e){
			System.err.println("��ȡ����ʱ����IOException");
		}
	}

	/**
	 * ��ȡһ����վ�ϵ����ӣ�filter������������
	 * 
	 * @param url
	 * @param filter
	 * @param entryAddress
	 * @return
	 */
	public Set<String> extracLinks(String url, LinkFilter filter,
			String entryAddress) {
		/*
		 * �����Ǻ����õ��Ķ���
		 */
		GetSourceFile getSourceFile = null;
		Set<String> links = null;
		Parser parser = null;
		NodeFilter frameFilter = null;
		NodeFilter ImgFilter = null;
		OrFilter orFilter = null;
		OrFilter linkFilter = null;
		NodeList list = null;
		Node tag = null;
		LinkTag link = null;
		ImageTag image = null;
		
		//���ӿ�ͷ
		String head = "http://" + getHead(url) + "/";
		//�������
		links = new HashSet<String>();
		try {
			getSourceFile = new GetSourceFile();
			// ����һ����������ͨ��������ַ���ʵ������
			parser = new Parser(getSourceFile.getSourceFile(url)); 
			parser.setEncoding("GBK");
		
			frameFilter = new AndFilter(new TagNameFilter("frame"),
					new HasAttributeFilter("src"));
			ImgFilter = new OrFilter(new TagNameFilter("img"),
					new TagNameFilter("IMG"));
			// orFilter�������ù���<a>��ǩ��<frame>��ǩ
			orFilter = new OrFilter(new NodeClassFilter(LinkTag.class),
					frameFilter);
			linkFilter = new OrFilter(orFilter, ImgFilter);
			// �õ��������˵����б�ǩ
			list = parser.extractAllNodesThatMatch(linkFilter);
			for (int i = 0; i < list.size(); i++) {
				tag = list.elementAt(i);
				if (tag instanceof LinkTag) {// <a>��ǩ
					link = (LinkTag) tag;
					String linkUrl = link.getLink();
					if (linkUrl.startsWith("/") && linkUrl.length() > 1)
						linkUrl = linkUrl.substring(1, linkUrl.length());
					if (linkUrl.indexOf("http://") == -1)
						linkUrl = head + linkUrl;
					if (filter.accept(linkUrl, entryAddress))
						links.add(linkUrl);
				} else if (tag instanceof ImageTag) {// IMG��ǩ
					image = (ImageTag) tag;
					String imgUrl = image.getImageURL();
					if (filter.accept(imgUrl, entryAddress))
						links.add(imgUrl);
				} else {// <frame>��ǩ
					// ��ȡframe��src���Ե�������<frame src="test.html"/>
					String frame = tag.getText();
					int start = frame.indexOf("src=");
					frame = frame.substring(start);
					int end = frame.indexOf(" ");
					if (end == -1)
						end = frame.indexOf(">");
					if (end < 6)
						continue;
					String frameUrl = frame.substring(5, end - 1);
					if (filter.accept(frameUrl, entryAddress))
						links.add(frameUrl);
				}
			}
		}  catch (Exception e) {
			System.err.println("��ȡ����ʱ�����쳣��");
			
			/*
			 * �ͷŶ���
			 */
			getSourceFile = null;
			parser = null;
			frameFilter = null;
			ImgFilter = null;
			orFilter = null;
			linkFilter = null;
			list = null;
			tag = null;
			link = null;
		
			return links;
		}
	
		/*
		 * �ͷŶ���
		 */
		getSourceFile = null;
		parser = null;
		frameFilter = null;
		ImgFilter = null;
		orFilter = null;
		linkFilter = null;
		list = null;
		tag = null;
		link = null;

		return links;
	}

	/**
	 * �����ú���
	 * @param url
	 * @return
	 */
	public static Set<String> extracLinks(String url) {

		String head = "http://" + getHead(url) + "/";

		Set<String> links = new HashSet<String>();
		try {
			// ��������
			URL myUrl = new URL(url);
			// ��ȡ���Ӷ���
			HttpURLConnection uc = (HttpURLConnection) myUrl.openConnection();
			// �����������
			uc.setRequestProperty("User-Agent", "vigorouswei@126.com");
			// ������ȡ���ݵĽӿ�
			BufferedReader br = new BufferedReader(new InputStreamReader(uc
					.getInputStream()));

			StringBuffer sb = new StringBuffer();
			String line = null;
			boolean flag = true;
			// �ӽӿ��ڶ������ݣ��ж��ַ����룬�����utf-8��ʽ������Ҫ����ת����
			while (null != (line = br.readLine())) {
				sb.append(line);
				if (line.contains("meta")
						&& (line.contains("utf-8") || line.contains("UTF-8"))) {
					flag = false;
					break;
				} else if (line.contains("meta")
						&& (line.contains("gbk") || line.contains("GBK") || line
								.contains("gb2312"))) {
					flag = true;
				}
			}
			// ����ַ�����Ϊutf-8��ʽ�������¶������ݣ������б���ת��
			if (flag == false) {

				URL myUrl1 = new URL(url);
				uc = (HttpURLConnection) myUrl1.openConnection();
				uc.setRequestProperty("User-Agent", "vigorouswei@126.com");
				InputStream in = uc.getInputStream();

				byte[] tempbuff = new byte[100]; // ��ʱ����

				byte[] buff = new byte[307200]; // ����һ���㹻�������

				int count = 0; // ��ȡ�ֽڸ���

				int rbyte = 0; // ÿ�ζ�ȡ�ĸ���

				while ((rbyte = in.read(tempbuff)) != -1) {

					for (int i = 0; i < rbyte; i++)

						buff[count + i] = tempbuff[i];

					count += rbyte;

				}

				byte[] result = new byte[count];

				for (int i = 0; i < count; i++)

					result[i] = buff[i];
				// �����ַ�����ת��
				String output = new String(result, "UTF-8");
				int index = output.indexOf("<");
				output = output.substring(index);
				sb = new StringBuffer();
				sb.append(output);

			}

			Parser parser = new Parser(sb.toString()); // ����һ����������ͨ��������ַ���ʵ������
			if (flag == true)
				parser.setEncoding("GBK");
			else
				parser.setEncoding("UTF-8");// ���ñ��뷽ʽ
			NodeFilter frameFilter = new AndFilter(new TagNameFilter("frame"),
					new HasAttributeFilter("src"));
			NodeFilter ImgFilter = new OrFilter(new TagNameFilter("img"),
					new TagNameFilter("IMG"));
			// orfilter�������ù���<a>��ǩ��<frame>��ǩ
			OrFilter orFilter = new OrFilter(
					new NodeClassFilter(LinkTag.class), frameFilter);
			OrFilter linkFilter = new OrFilter(orFilter, ImgFilter);
			// �õ��������˵����б�ǩ
			NodeList list = parser.extractAllNodesThatMatch(linkFilter);
			for (int i = 0; i < list.size(); i++) {
				Node tag = list.elementAt(i);
				if (tag instanceof LinkTag) {// <a>��ǩ
					LinkTag link = (LinkTag) tag;
					String linkUrl = link.getLink();// url
					if (linkUrl.indexOf("http://") == -1)
						linkUrl = head + linkUrl;
					links.add(linkUrl);
					// System.out.println(text+linkUrl);
				} else if (tag instanceof ImageTag) {// img��ǩ
					ImageTag image = (ImageTag) tag;
					String imgUrl = image.getImageURL();
					// System.out.print(image.getImageURL() + "********");//ͼƬ��ַ
					// System.out.println(image.getText());//ͼƬ����
					// if(filter.accept(imgUrl))
					links.add(imgUrl);
				} else {// <frame>��ǩ
					// ��ȡframe��src���Ե�������<frame src="test.html"/>
					String frame = tag.getText();
					int start = frame.indexOf("src=");
					if (start == -1)
						continue;
					frame = frame.substring(start);
					int end = frame.indexOf(" ");
					if (end == -1)
						end = frame.indexOf(">");
					String frameUrl = frame.substring(5, end - 1);
					// if(filter.accept(frameUrl))
					links.add(frameUrl);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return links;
		}
		return links;
	}

	public static Set<String> getImglinkByRE(String html, LinkFilter filter,
			String[] queue) {
		Set<String> links = new HashSet<String>();
		return links;
	}

	public static String getHead(String url) {
		String head = url.substring(7);
		if (head.indexOf("/") != -1)
			head = head.substring(0, head.indexOf("/"));
		return head;
	}
}
