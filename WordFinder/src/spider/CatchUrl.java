package spider;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.htmlparser.beans.LinkBean;
/**
 * 
 * @author Owner
 * CatchUrl实现利用HtmlParser的LinkBean抓取网页内的链接
 */
public class CatchUrl {
	/**
	 * 测试函数
	 * @param args
	 */
	public static void main(String[] args){
		Set<String> urls = getText("http://www.xici.net/home.asp");
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
	 * 提取页面内所有链接的函数
	 * @param f 入口地址
	 * @param filter 过滤器
	 * @param queue 过滤器使用的地址列表
	 * @return
	 */
	public Set<String> getText(String f,LinkFilter filter,String entryAddress){
		  //建立LinkBean对象
		  LinkBean lb=new LinkBean();  
		  //设置连接
		  lb.setURL(f);  
		  //通过LinkBean的getLinks函数获取页面内链接
		  URL[] urls=lb.getLinks(); 
		  Set<String>links = new HashSet<String>();
		  if(urls==null)return links;
		  for (int i=0;i<urls.length;i++){
			  //当URL满足过滤条件时才保存
			  if(filter.accept(urls[i].toString(),entryAddress))
		   links.add(urls[i].toString());
		  }
		 
		  lb = null;
		  urls = null;
		  return links;
		 }
	/**
	 * 提取页面内所有链接的函数
	 * @param f 入口地址
	 * @return
	 */
	public static Set<String> getText(String f){
		  LinkBean lb=new LinkBean();
		  lb.setURL(f);
		  URL[] urls=lb.getLinks();
		  Set<String>links = new HashSet<String>();
		  for (int i=0;i<urls.length;i++){
		   links.add(urls[i].toString());
		  }
		  return links;
		 }
}

