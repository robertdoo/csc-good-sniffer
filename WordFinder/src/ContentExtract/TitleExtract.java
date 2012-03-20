package ContentExtract;

import org.htmlparser.Parser;
import org.htmlparser.visitors.HtmlPage;

public class TitleExtract {
	private String title = null;
	private GetSourceFile getSourceFile = null;
	/**
	 * ���Ժ���
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
	 * ��ָ��URL��Ӧ����ҳ�ڻ�ȡ����
	 * 
	 * @param url
	 * @return
	 */
	public String getTitle(String url) {
		// �����Ǻ���getTitle�õ��ı���
		
		Parser parser = null;
		HtmlPage visitor = null;
		
		String htmlSourceFile = getSourceFile.getSourceFile(url);
		if (htmlSourceFile.equals("error"))
			return "error";
		
		try {
			// ����һ����������ͨ�������URLʵ������
			parser = new Parser(htmlSourceFile);
			// ���ñ��뷽ʽ
			parser.setEncoding("GBK");
			// ����һ������HTMLҳ��ı���������ʵ������
			visitor = new HtmlPage(parser);
			// �ñ�����ȥ����ÿһ���ڵ�
			parser.visitAllNodesWith(visitor);
			// ��ȡTitle
			title = visitor.getTitle();

			/*
			 * �����ͷŶ���
			 */
			if (parser != null)
				parser = null;
			if (visitor != null)
				visitor = null;
			if(htmlSourceFile!=null)
				htmlSourceFile = null;
			
			return title;
		}  catch (Exception e) {
			System.err.println("��ȡ����ʱ���� �쳣��");
			
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
