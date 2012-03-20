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
 * CatchUrlʵ������HtmlParser��LinkBeanץȡ��ҳ�ڵ�����
 */
public class CatchUrl {
	/**
	 * ���Ժ���
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
			System.err.println("��ȡ����ʱ����IOException");
		}
	}
	
	/**
	 * ��ȡҳ�����������ӵĺ���
	 * @param f ��ڵ�ַ
	 * @param filter ������
	 * @param queue ������ʹ�õĵ�ַ�б�
	 * @return
	 */
	public Set<String> getText(String f,LinkFilter filter,String entryAddress){
		  //����LinkBean����
		  LinkBean lb=new LinkBean();  
		  //��������
		  lb.setURL(f);  
		  //ͨ��LinkBean��getLinks������ȡҳ��������
		  URL[] urls=lb.getLinks(); 
		  Set<String>links = new HashSet<String>();
		  if(urls==null)return links;
		  for (int i=0;i<urls.length;i++){
			  //��URL�����������ʱ�ű���
			  if(filter.accept(urls[i].toString(),entryAddress))
		   links.add(urls[i].toString());
		  }
		 
		  lb = null;
		  urls = null;
		  return links;
		 }
	/**
	 * ��ȡҳ�����������ӵĺ���
	 * @param f ��ڵ�ַ
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

