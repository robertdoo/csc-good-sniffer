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
 * 抓取页面内链接的类
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
			System.err.println("提取链接时出现IOException");
		}
	}

	/**
	 * 获取一个网站上的链接，filter用来过滤链接
	 * 
	 * @param url
	 * @param filter
	 * @param entryAddress
	 * @return
	 */
	public Set<String> extracLinks(String url, LinkFilter filter,
			String entryAddress) {
		/*
		 * 下面是函数用到的对象
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
		
		//链接开头
		String head = "http://" + getHead(url) + "/";
		//存放链接
		links = new HashSet<String>();
		try {
			getSourceFile = new GetSourceFile();
			// 创建一个分析器并通过传入的字符串实例化它
			parser = new Parser(getSourceFile.getSourceFile(url)); 
			parser.setEncoding("GBK");
		
			frameFilter = new AndFilter(new TagNameFilter("frame"),
					new HasAttributeFilter("src"));
			ImgFilter = new OrFilter(new TagNameFilter("img"),
					new TagNameFilter("IMG"));
			// orFilter用来设置过滤<a>标签和<frame>标签
			orFilter = new OrFilter(new NodeClassFilter(LinkTag.class),
					frameFilter);
			linkFilter = new OrFilter(orFilter, ImgFilter);
			// 得到经过过滤的所有标签
			list = parser.extractAllNodesThatMatch(linkFilter);
			for (int i = 0; i < list.size(); i++) {
				tag = list.elementAt(i);
				if (tag instanceof LinkTag) {// <a>标签
					link = (LinkTag) tag;
					String linkUrl = link.getLink();
					if (linkUrl.startsWith("/") && linkUrl.length() > 1)
						linkUrl = linkUrl.substring(1, linkUrl.length());
					if (linkUrl.indexOf("http://") == -1)
						linkUrl = head + linkUrl;
					if (filter.accept(linkUrl, entryAddress))
						links.add(linkUrl);
				} else if (tag instanceof ImageTag) {// IMG标签
					image = (ImageTag) tag;
					String imgUrl = image.getImageURL();
					if (filter.accept(imgUrl, entryAddress))
						links.add(imgUrl);
				} else {// <frame>标签
					// 提取frame里src属性的链接如<frame src="test.html"/>
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
			System.err.println("提取链接时出现异常！");
			
			/*
			 * 释放对象
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
		 * 释放对象
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
	 * 测试用函数
	 * @param url
	 * @return
	 */
	public static Set<String> extracLinks(String url) {

		String head = "http://" + getHead(url) + "/";

		Set<String> links = new HashSet<String>();
		try {
			// 建立连接
			URL myUrl = new URL(url);
			// 获取连接对象
			HttpURLConnection uc = (HttpURLConnection) myUrl.openConnection();
			// 设置连接身份
			uc.setRequestProperty("User-Agent", "vigorouswei@126.com");
			// 建立获取数据的接口
			BufferedReader br = new BufferedReader(new InputStreamReader(uc
					.getInputStream()));

			StringBuffer sb = new StringBuffer();
			String line = null;
			boolean flag = true;
			// 从接口内读入数据，判断字符编码，如果是utf-8格式的则需要进行转换！
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
			// 如果字符编码为utf-8格式，则重新读入数据，并进行编码转换
			if (flag == false) {

				URL myUrl1 = new URL(url);
				uc = (HttpURLConnection) myUrl1.openConnection();
				uc.setRequestProperty("User-Agent", "vigorouswei@126.com");
				InputStream in = uc.getInputStream();

				byte[] tempbuff = new byte[100]; // 临时数组

				byte[] buff = new byte[307200]; // 定义一下足够大的数组

				int count = 0; // 读取字节个数

				int rbyte = 0; // 每次读取的个数

				while ((rbyte = in.read(tempbuff)) != -1) {

					for (int i = 0; i < rbyte; i++)

						buff[count + i] = tempbuff[i];

					count += rbyte;

				}

				byte[] result = new byte[count];

				for (int i = 0; i < count; i++)

					result[i] = buff[i];
				// 进行字符编码转换
				String output = new String(result, "UTF-8");
				int index = output.indexOf("<");
				output = output.substring(index);
				sb = new StringBuffer();
				sb.append(output);

			}

			Parser parser = new Parser(sb.toString()); // 创建一个分析器并通过传入的字符串实例化它
			if (flag == true)
				parser.setEncoding("GBK");
			else
				parser.setEncoding("UTF-8");// 设置编码方式
			NodeFilter frameFilter = new AndFilter(new TagNameFilter("frame"),
					new HasAttributeFilter("src"));
			NodeFilter ImgFilter = new OrFilter(new TagNameFilter("img"),
					new TagNameFilter("IMG"));
			// orfilter用来设置过滤<a>标签和<frame>标签
			OrFilter orFilter = new OrFilter(
					new NodeClassFilter(LinkTag.class), frameFilter);
			OrFilter linkFilter = new OrFilter(orFilter, ImgFilter);
			// 得到经过过滤的所有标签
			NodeList list = parser.extractAllNodesThatMatch(linkFilter);
			for (int i = 0; i < list.size(); i++) {
				Node tag = list.elementAt(i);
				if (tag instanceof LinkTag) {// <a>标签
					LinkTag link = (LinkTag) tag;
					String linkUrl = link.getLink();// url
					if (linkUrl.indexOf("http://") == -1)
						linkUrl = head + linkUrl;
					links.add(linkUrl);
					// System.out.println(text+linkUrl);
				} else if (tag instanceof ImageTag) {// img标签
					ImageTag image = (ImageTag) tag;
					String imgUrl = image.getImageURL();
					// System.out.print(image.getImageURL() + "********");//图片地址
					// System.out.println(image.getText());//图片文字
					// if(filter.accept(imgUrl))
					links.add(imgUrl);
				} else {// <frame>标签
					// 提取frame里src属性的链接如<frame src="test.html"/>
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
