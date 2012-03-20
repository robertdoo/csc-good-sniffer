package ContentExtract;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.htmlparser.beans.StringBean;

import database.FilePreprocess;

public class Charset {

	/**
	 * @param args comment
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = "http://www.google.com.hk/search?&as_q=%E5%8D%81%E5%9B%9B%E6%89%80%2B%E9%9B%B7%E8%BE%BE&as_epq=%E5%8D%97%E4%BA%AC%E5%8D%81%E5%9B%9B%E6%89%80&as_eq=%E8%88%AA%E6%AF%8D+%E4%B8%AD%E5%9B%BD&num=10&as_ft=&as_sitesearch=&ie=UTF-8&oe=gbk";
		Charset ch = new Charset();
		try{
		ch.getPage(url);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void getPage(String url) throws Exception{
		URL myUrl = new URL(url);
		HttpURLConnection uc = (HttpURLConnection) myUrl.openConnection();
		uc.setRequestProperty("User-Agent", "vigorouswei@126.com");
		uc.setConnectTimeout(5000);
		InputStream in = uc.getInputStream();
		
		
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while((line = br.readLine())!=null){
			System.out.println(line);
			sb.append(line+"\r\n");
		}
		
		byte[] tempbuff = new byte[512]; // 临时数组
		
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
		//进行字符编码转换
		String output = new String(result, "gb2312");
		int index = output.indexOf("<");
		output = output.substring(index);
		System.out.println(output);
		
		StringBean str_bean = new StringBean();
		str_bean.setLinks(false);
		str_bean.setURL(url);
		String total_txt = str_bean.getStrings();
		total_txt = total_txt.replaceAll(" ", "");
		total_txt = total_txt.replaceAll("	", "");
		total_txt = FilePreprocess.replace(total_txt);
		total_txt = total_txt.trim();
		System.out.println(total_txt);
		
	}
}
