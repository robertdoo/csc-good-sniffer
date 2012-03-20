package ContentExtract;

import org.htmlparser.Parser;
import org.htmlparser.visitors.HtmlPage;

public class TitleExtract {
	private String title = null;
	private GetSourceFile getSourceFile = null;
	/**
	 * 测试函数
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TitleExtract te = new TitleExtract();
		System.out.println(te
				.getTitle(" http://bbs.news.qq.com/b-1000090417/66783.htm"));
	}

	public TitleExtract() {
		title = "";
		getSourceFile = new GetSourceFile();
	}

	/**
	 * 从指定URL对应的网页内获取标题
	 * 
	 * @param url
	 * @return
	 */
	public String getTitle(String url) {
		// 下面是函数getTitle用到的变量
		
		Parser parser = null;
		HtmlPage visitor = null;
		
		String htmlSourceFile = getSourceFile.getSourceFile(url);
		if (htmlSourceFile.equals("error"))
			return "error";
		
		try {
			// 创建一个分析器并通过传入的URL实例化它
			parser = new Parser(htmlSourceFile);
			// 设置编码方式
			parser.setEncoding("GBK");
			// 创建一个访问HTML页面的遍历器并且实例化它
			visitor = new HtmlPage(parser);
			// 用遍历器去遍历每一个节点
			parser.visitAllNodesWith(visitor);
			// 获取Title
			title = visitor.getTitle();

			/*
			 * 下面释放对象
			 */
			if (parser != null)
				parser = null;
			if (visitor != null)
				visitor = null;
			if(htmlSourceFile!=null)
				htmlSourceFile = null;
			
			return title;
		}  catch (Exception e) {
			System.err.println("提取标题时出现 异常！");
			
			if (parser != null)
				parser = null;
			if (visitor != null)
				visitor = null;
			if(htmlSourceFile!=null)
				htmlSourceFile = null;
			
			return "error";
		}
	}
}
