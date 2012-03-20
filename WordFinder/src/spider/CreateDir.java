package spider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * 
 * @author Owner CreareDir用来创建放置爬虫爬去到的文件的文件夹
 */
public class CreateDir {
	/**
	 * 测试函数
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CreateDir cd = new CreateDir();
		cd.createDir();
	}

	/**
	 * 创建文件夹的函数
	 */
	public void createDir() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("Spider.conf"));
			String line = null;
			while ((line = br.readLine()) != null) {
				int begin = line.indexOf("=");
				if (begin == -1)
					continue;
				String path = line.substring(begin + 1);
				File dir = new File(path);
				if (!dir.isDirectory())
					dir.mkdirs();
			}
			br.close();

			File file = new File(SpiderConf
					.getPathByName(SpiderConf.SPIDER_INDEX_PATH)
					+ "write.lock");
			if (file.exists())
				file.delete();
			file = new File(SpiderConf
					.getPathByName(SpiderConf.MAINPAGECRAWLER_INDEX_PATH)
					+ "write.lock");
			if (file.exists())
				file.delete();
			file = new File(SpiderConf
					.getPathByName(SpiderConf.METASEARCH_INDEX_PATH)
					+ "write.lock");
			if (file.exists())
				file.delete();

		} catch (Exception e) {
			System.err.println("文件夹创建失败！");
			System.err.println(e.getMessage());
		}
	}

}
