package ContentExtract;

import java.util.ArrayList;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.TextExtractingVisitor;

import database.FilePreprocess;

/**
 * ��ȡ���ĵ���
 * 
 * @author Owner
 * 
 */
public class MainBodyExtract {
	/*
	 * ����һЩ������̳�Ľڵ��ʽ����������ݵ��������ȡ
	 */
	// ������ʽ1
	NodeFilter body_filter_general1 = new AndFilter(new TagNameFilter("TD"),
			new HasAttributeFilter("class", "t_msgfont"));
	// ������ʽ2
	NodeFilter body_filter_general2 = new AndFilter(new TagNameFilter("div"),
			new HasAttributeFilter("class", "t_msgfont"));
	// �»���
	NodeFilter body_filter_xinhua = new AndFilter(new TagNameFilter("span"),
			new HasAttributeFilter("class", "hei14"));
	// �л���
	NodeFilter body_filter_zhonghua = new AndFilter(new TagNameFilter("DIV"),
			new HasAttributeFilter("class", "postContent"));
	// ��½
	NodeFilter body_filter_xilu = new AndFilter(new TagNameFilter("span"),
			new HasAttributeFilter("id", "bodycontent"));
	// ��Ѫ
	NodeFilter body_filter_tiexue = new AndFilter(new TagNameFilter("p"),
			new HasAttributeFilter("class", "bbsp"));
	// ����
	NodeFilter body_filter_wangyi = new AndFilter(new TagNameFilter("DIV"),
			new HasAttributeFilter("class", "articleCont"));
	// ��Ѷ
	NodeFilter body_filter_tengxun = new AndFilter(new TagNameFilter("div"),
			new HasAttributeFilter("id", "main_content_div"));
	// è��
	NodeFilter body_filter_mop = new AndFilter(new TagNameFilter("div"),
			new HasAttributeFilter("class", "mainpart"));
	// ����
	NodeFilter body_filter_tianya = new AndFilter(new TagNameFilter("div"),
			new HasAttributeFilter("class", "post"));
	// ����
	NodeFilter body_filter_xici = new AndFilter(new TagNameFilter("div"),
			new HasAttributeFilter("class", "doc_txt"));
	// javaEye
	NodeFilter body_filter_javaeye = new TagNameFilter("div");
	// �����ƾ�
	NodeFilter body_filter_eastmoney = new AndFilter(
			new AndFilter(new TagNameFilter("div"), new HasAttributeFilter(
					"class", "huifu")), new NotFilter(new HasAttributeFilter(
					"id")));
	// 51job
	NodeFilter body_filter_51job = new AndFilter(new TagNameFilter("div"),
			new HasAttributeFilter("id", "read_tpc"));
	// ���˲ƾ�
	NodeFilter body_filter_sina = new AndFilter(new TagNameFilter("div"),
			new HasAttributeFilter("class", "nr12"));
	// QQ�ƾ�
	NodeFilter body_filter_qq = new AndFilter(new TagNameFilter("div"),
			new HasAttributeFilter("id", "main_content_div"));

	private String temp_main_body = null;

	private GetSourceFile getSourceFile = null;

	/**
	 * ���Ժ���
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String file = "http://product.dangdang.com/product.aspx?product_id=20634359&ref=book-43-N1";
		MainBodyExtract mbe = new MainBodyExtract();
		System.out.println(mbe.getMainBody(file));
		System.err
				.println(mbe
						.getMainBody("http://product.dangdang.com/product.aspx?product_id=9272055&ref=book-43-N2"));

	}

	public MainBodyExtract() {
		temp_main_body = "";
		getSourceFile = new GetSourceFile();
	}

	/**
	 * ��ȡ���ĵĺ���
	 * 
	 * @param url
	 * @return
	 */
	public String getMainBody(String url) {
		/*
		 * �����Ǻ���getMainBody�õ��ı���
		 */
		StringBuffer main_body = null;
		Parser parser = null;
		NodeFilter body_filter = null;
		NodeList nodelist = null;
		String body_buffer = null;
		Node node_body = null;
		String sum_body = null;
		Parser body_parser = null;
		TextExtractingVisitor visitor = null;
		String body = null;
		String str = null;
		StringBean str_bean = null;
		String total_txt = null;
		String mainBody = null;
		main_body = new StringBuffer();

		String htmlSourceFile = getSourceFile.getSourceFile(url);
		if (htmlSourceFile.equals("error"))
			return "error";

		try {
			// ����һ����������ͨ��������ַ���ʵ������
			parser = new Parser(htmlSourceFile);
			// ���ñ��뷽ʽ
			parser.setEncoding("gb2312");

			float Threshold = 0.5f;
			if (url.indexOf("forum.home.news.cn") != -1) {
				body_filter = body_filter_xinhua;
				Threshold = 0.3f;
			} else if (url.indexOf("bbs.chnqiang.com") != -1) {
				body_filter = body_filter_general1;
				Threshold = 0.3f;
			} else if (url.indexOf("bbs.tiexue.net") != -1) {
				body_filter = body_filter_tiexue;
				Threshold = 0.1f;
			} else if (url.indexOf("club.xilu.com") != -1) {
				body_filter = body_filter_xilu;
				Threshold = 0.3f;
			} else if (url.indexOf("bbs.voc.com.cn") != -1) {
				body_filter = body_filter_general2;
				Threshold = 0.3f;
			} else if (url.indexOf("bbs.qianlong.com") != -1) {
				body_filter = body_filter_general1;
				Threshold = 0.3f;
			} else if (url.indexOf("bbs.huanqiu.com") != -1) {
				body_filter = body_filter_general2;
				Threshold = 0.3f;
			} else if (url.indexOf("club.mil.news.sina.com.cn") != -1) {
				body_filter = body_filter_general2;
				Threshold = 0.3f;
			} else if (url.indexOf("bbs.eastday.com") != -1) {
				body_filter = body_filter_general1;
				Threshold = 0.3f;
			} else if (url.indexOf("bbs.news.qq.com") != -1) {
				body_filter = new OrFilter(body_filter_general2,
						body_filter_tengxun);
				Threshold = 0.3f;
			} else if (url.indexOf("bbsmil.news.ifeng.com") != -1) {
				body_filter = body_filter_general2;
				Threshold = 0.3f;
			} else if (url.indexOf("www.iissbbs.com") != -1) {
				body_filter = body_filter_general1;
				Threshold = 0.3f;
			} else if (url.indexOf("club.china.com") != -1) {
				body_filter = body_filter_zhonghua;
				Threshold = 0.3f;
			} else if (url.indexOf("bbs.news.163.com") != -1) {
				body_filter = body_filter_wangyi;
				Threshold = 0.3f;
			} else if (url.indexOf("tt.mop.com") != -1) {
				body_filter = body_filter_mop;
				Threshold = 0.3f;
			} else if (url.indexOf("www.tianya.cn") != -1) {
				body_filter = body_filter_tianya;
				Threshold = 0.3f;
			} else if (url.indexOf("javaeye.com") != -1) {
				body_filter = body_filter_javaeye;
				Threshold = 0.3f;
			} else if (url.indexOf("www.xici.net") != -1) {
				body_filter = body_filter_xici;
				Threshold = 0.3f;
			} else if (url.indexOf("guba.eastmoney.com") != -1) {
				body_filter = body_filter_eastmoney;
				Threshold = 0.3f;
			} else if (url.indexOf("bbs.51job.com") != -1) {
				body_filter = body_filter_51job;
				Threshold = 0.3f;
			} else if (url.indexOf("bbs.bar.sina.com.cn") != -1) {
				body_filter = body_filter_sina;
				Threshold = 0.3f;
			} else if (url.indexOf("bbs.finance.qq.com") != -1) {
				body_filter = body_filter_qq;
				Threshold = 0.3f;
			} else {
				body_filter = new OrFilter(body_filter_general1,
						body_filter_general2);
			}

			// ��ȡ���Ķ�Ӧ�Ľڵ㴮
			nodelist = parser.parse(body_filter);
			body_buffer = "";
			boolean havaGetBody = false;
			int body_length = 0;

			// ��ָ���ı�ǩ����ȡ���ݣ�����ȡ������ַ�����Ϊ����
			for (int i = 0; i < nodelist.size(); i++) {
				node_body = nodelist.elementAt(i);
				sum_body = node_body.toHtml();

				body_parser = new Parser(node_body.toHtml());
				visitor = new TextExtractingVisitor();
				body_parser.visitAllNodesWith(visitor);

				body = visitor.getExtractedText();

				if ((float) body.length() / sum_body.length() > Threshold) {
					if (url.indexOf("bbs.tiexue.net") != -1) {
						body_buffer += body;
					} else if (body.length() > body_length) {
						body_buffer = body;
						body_length = body.length();
					}
					havaGetBody = true;
				}

			}
			if (havaGetBody == false) {
				body_length = 0;
				for (int i = 0; i < nodelist.size(); i++) {
					node_body = nodelist.elementAt(i);
					body_parser = new Parser(node_body.toHtml());
					visitor = new TextExtractingVisitor();
					body_parser.visitAllNodesWith(visitor);
					body = visitor.getExtractedText();

					if (url.indexOf("bbs.tiexue.net") != -1) {
						body_buffer += body;
					} else if (body.length() > body_length) {

						body_buffer = body;
						body_length = body.length();
					}
					havaGetBody = true;

				}
			}

			// ��ȡ�����ĺ�����Ľ��д����������еĿո��Ʊ��ȥ���������������
			if (havaGetBody == true) {
				int word_number = 0;
				body_buffer = body_buffer.replaceAll(" ", "");
				body_buffer = body_buffer.replaceAll("	", "");
				body_buffer = FilePreprocess.replace(body_buffer);
				body_buffer = body_buffer.trim();
				if (url.indexOf("bbs.chnqiang.com") != -1) {
					str_bean = new StringBean();
					str_bean.setLinks(false);
					str_bean.setURL(url);
					total_txt = str_bean.getStrings();
					total_txt = total_txt.replaceAll(" ", "");
					total_txt = total_txt.replaceAll("	", "");
					total_txt = FilePreprocess.replace(total_txt);
					total_txt = total_txt.trim();
					body_buffer = removeContent(body_buffer, total_txt);
				}
				while (word_number + 60 < body_buffer.length()) {
					str = body_buffer.substring(word_number, word_number + 60);
					main_body.append(str);
					main_body.append("\r\n");
					word_number += 60;
				}
				if (word_number < body_buffer.length()) {
					str = body_buffer.substring(word_number, body_buffer
							.length());

					main_body.append(str);
				}

				/*
				 * �����ͷŶ���
				 */
				if (body_filter != null)
					body_filter = null;
				if (nodelist != null)
					nodelist = null;
				if (body_buffer != null)
					body_buffer = null;
				if (node_body != null)
					node_body = null;
				if (sum_body != null)
					sum_body = null;
				if (body_parser != null)
					body_parser = null;
				if (visitor != null)
					visitor = null;
				if (body != null)
					body = null;
				if (str != null)
					str = null;
				if (str_bean != null)
					str_bean = null;
				if (total_txt != null)
					total_txt = null;
				if (parser != null)
					parser = null;
				if(htmlSourceFile!=null)
					htmlSourceFile = null;

				return main_body.toString();
			}

			else {
				// total_txtΪ��ҳ��ȥ�����б�ǩ��ʣ�µ��ַ�������ָ���ı�ǩ����ȡ��������ʱ�ͽ������������
				str_bean = new StringBean();
				str_bean.setLinks(false);
				str_bean.setURL(url);
				total_txt = str_bean.getStrings();
				total_txt = total_txt.replaceAll(" ", "");
				total_txt = total_txt.replaceAll("	", "");
				total_txt = FilePreprocess.replace(total_txt);
				total_txt = total_txt.trim();

				mainBody = total_txt;
				int nowLength = mainBody.length();
				int preLength = nowLength;
				do {
					mainBody = mainBody.replace(getLCString(mainBody,
							temp_main_body), "");
					preLength = nowLength;
					nowLength = mainBody.length();
				} while (nowLength < preLength - 10);

				temp_main_body = total_txt;
				main_body.append(mainBody);

			}
			if (main_body.length() == 0) {
				main_body.append("no bodyContent");

			}
			/*
			 * �����ͷŶ���
			 */
			if (body_filter != null)
				body_filter = null;
			if (nodelist != null)
				nodelist = null;
			if (body_buffer != null)
				body_buffer = null;
			if (node_body != null)
				node_body = null;
			if (sum_body != null)
				sum_body = null;
			if (body_parser != null)
				body_parser = null;
			if (visitor != null)
				visitor = null;
			if (body != null)
				body = null;
			if (str != null)
				str = null;
			if (str_bean != null)
				str_bean = null;
			if (total_txt != null)
				total_txt = null;
			if (parser != null)
				parser = null;
			if(htmlSourceFile!=null)
				htmlSourceFile = null;

			return main_body.toString();
		} catch (Exception e) {

			System.err.println("��ȡ:" + url + " ����ʱ����");
			/*
			 * �����ͷŶ���
			 */
			if (body_filter != null)
				body_filter = null;
			if (nodelist != null)
				nodelist = null;
			if (body_buffer != null)
				body_buffer = null;
			if (node_body != null)
				node_body = null;
			if (sum_body != null)
				sum_body = null;
			if (body_parser != null)
				body_parser = null;
			if (visitor != null)
				visitor = null;
			if (body != null)
				body = null;
			if (str != null)
				str = null;
			if (str_bean != null)
				str_bean = null;
			if (total_txt != null)
				total_txt = null;
			if (parser != null)
				parser = null;
			if(htmlSourceFile!=null)
				htmlSourceFile = null;

			return "error";
		}

	}

	/**
	 *  ��ȡ�����ַ�����������ִ�
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String getLCString(String str1, String str2) {
		String LString = "";
		String tempLString = "";
		int len1, len2;
		int maxLength = 0;
		int tempLength = 0;
		int index;

		len1 = str1.length();
		len2 = str2.length();

		for (int i = 0; i < len1; i++) {
			index = i;
			if (len1 - index < maxLength)
				break;
			for (int j = 0; j < len2; j++) {

				if (index >= len1) {
					if (tempLength > maxLength) {
						maxLength = tempLength;
						LString = tempLString;
						tempLength = 0;
						tempLString = "";
						return LString;
					} else {
						tempLength = 0;
						tempLString = "";
						break;
					}

				}

				if (str2.charAt(j) == str1.charAt(index)) {
					tempLString += str1.substring(index, index + 1);
					tempLength++;
					index++;

				} else {
					index = i;
					if (tempLength > maxLength) {
						j = j - tempLength;
						maxLength = tempLength;
						LString = tempLString;
						tempLength = 0;
						tempLString = "";
					} else {
						tempLength = 0;
						tempLString = "";
					}
					if (len1 - index < maxLength)
						break;
				}

			}
			if (tempLength > maxLength) {
				maxLength = tempLength;
				LString = tempLString;
				tempLength = 0;
				tempLString = "";
			} else {
				tempLength = 0;
				tempLString = "";
			}
		}
		return LString;
	}

	/**
	 *  ���������ַ���������ִ��ķ�������������
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String getLCString1(String str1, String str2) {
		StringBuffer str = new StringBuffer();
		int i, j;
		int len1 = str1.length();
		int len2 = str2.length();
		int maxLen = len1 > len2 ? len1 : len2;
		int[] max = new int[maxLen];
		int[] maxIndex = new int[maxLen];
		int[] c = new int[maxLen];

		for (i = 0; i < len2; i++) {
			for (j = len1 - 1; j >= 0; j--) {
				if (str2.charAt(i) == str1.charAt(j)) {
					if ((i == 0) || (j == 0))
						c[j] = 1;
					else
						c[j] = c[j - 1] + 1;
				} else {
					c[j] = 0;
				}

				if (c[j] > max[0]) {
					max[0] = c[j];
					maxIndex[0] = j;

					for (int k = 1; k < maxLen; k++) {
						max[k] = 0;
						maxIndex[k] = 0;
					}
				} else if (c[j] == max[0]) {
					for (int k = 1; k < maxLen; k++) {
						if (max[k] == 0) {
							max[k] = c[j];
							maxIndex[k] = j;
							break;
						}
					}
				}
			}
		}

		for (j = 0; j < maxLen; j++) {
			if (max[j] > 0) {
				str.append(str1.substring(maxIndex[j] - max[j] + 1,
						maxIndex[j] + 1));
			}
		}
		return str.toString().trim();
	}

	/**
	 *  ��ȡ�����ַ�����ǰ20 ��������ִ�
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String removeContent(String str1, String str2) {
		ArrayList<String> com = new ArrayList<String>(20);
		String content = "";
		String string1 = str1;
		String string2 = str2;
		for (int i = 0; i < 20; i++) {
			String commonStr = getLCString(string1, string2);
			// if(commonStr.length()<10)break;
			com.add(commonStr);
			string2 = string2.replaceAll(commonStr, "");
			string1 = string1.replaceAll(commonStr, "");
		}
		String previous = "";
		String current = "";
		for (int i = 0; i < com.size(); i++) {
			current = com.get(i);
			if (previous.equals(current))
				break;
			content += com.get(i);
			previous = current;
		}
		return content;
	}
}
