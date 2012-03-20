package ContentExtract;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 *提取链接指向网页内时间的类
 */
public class TimeExtract {
	private String time = null;
	private GetSourceFile getSourceFile = null;

	/**
	 * 测试函数
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TimeExtract te = new TimeExtract();
		System.out
				.println(te
						.getTime("http://bar.sina.com.cn/thread.php?tid=2164439597&bid=2437"));
	}

	public TimeExtract() {
		time = "";
		getSourceFile = new GetSourceFile();
	}

	/**
	 * 从指定URL对应的网页内提取时间 输入参数为url，返回对象为提取到的字符串形式的时间对象
	 * 
	 * @param url
	 * @return
	 */
	public String getTime(String url) {
		// 下面是函数getTime用到的变量
		String[] lines = null;

		String htmlSourceFile = getSourceFile.getSourceFile(url);
		if (htmlSourceFile.equals("error"))
			return "error";

		try {
			/*
			 * 下面进行时间提取
			 */
			lines = htmlSourceFile.split(">");
			for (String htmlLine : lines) {
				if (htmlLine.contains("发表于") || htmlLine.contains("发表时间")) {
					time = parse(htmlLine);
					if (time != null) {
						if (lines != null)
							lines = null;
						if (htmlSourceFile != null)
							htmlSourceFile = null;

						return time;
					}
					continue;
				}
				if (htmlLine.contains("置顶"))
					continue;
				if (htmlLine.contains("<!--"))
					continue;
				if (htmlLine.contains("lastModified"))
					continue;
				if (htmlLine.contains("Last-Modified"))
					continue;
				if (htmlLine.contains("lastReply"))
					continue;
				if (htmlLine.contains("加入精华"))
					continue;
				time = parse(htmlLine);
				if (time != null) {
					if (lines != null)
						lines = null;
					if (htmlSourceFile != null)
						htmlSourceFile = null;
					
					return time;
				}
			}

			/*
			 * 下面进行对象的释放
			 */
			if (lines != null)
				lines = null;
			if (htmlSourceFile != null)
				htmlSourceFile = null;

			/*
			 * 返回信息
			 */
			if (time.length()!=0) {
				return time;
			} else {
				return "no time";
			}
		} catch (Exception e) {
			System.err.println("提取时间时出现异常！");

			// 释放对象空间
			if (lines != null)
				lines = null;
			if (htmlSourceFile != null)
				htmlSourceFile = null;

			// 返回错误信息
			return "error";
		}
	}

	// 提取时间的函数，从指定字符串中用正则表达式提取时间，将获取到的第一个时间作为发帖时间
	private static String parse(String line) {
		Pattern p1 = Pattern
				.compile("\\d{2,4}[:-]\\d{1,2}[:-]\\d{1,2}\\s{0,5}\\d{1,2}[:]\\d{1,2}[:]?\\d{0,2}");
		Pattern p2 = Pattern
				.compile("\\d{2,4}\\/\\d{1,2}\\/\\d{1,2}\\s{0,5}\\d{2}:\\d{2}:?\\d{0,2}");
		Pattern p3 = Pattern
				.compile("\\d{2,4}年\\d{1,2}月\\d{1,2}日\\s{0,5}\\d{0,2}:?\\d{0,2}:?\\d{0,2}");
		Matcher m1 = p1.matcher(line);
		Matcher m2 = p2.matcher(line);
		Matcher m3 = p3.matcher(line);

		if (m1.find()) {
			String s = m1.group();
			String[] lines = s.split(" ");
			String date = lines[0];
			String time = lines[lines.length - 1];
			date = date.replaceAll(":", "-");
			String[] dates = date.split("-");
			if (dates[0].length() == 2)
				date = "20" + date;
			String[] times = time.split(":");
			if (times.length == 2)
				time = time + ":00";
			s = date + "  " + time;
			return s;
		} else if (m2.find()) {
			String s = m2.group();
			String[] lines = s.split(" ");
			String date = lines[0];
			String time = lines[lines.length - 1];
			date = date.replaceAll("\\/", "-");
			String[] dates = date.split("-");
			if (dates[0].length() == 2)
				date = "20" + date;
			String[] times = time.split(":");
			if (times.length == 2)
				time = time + ":00";
			s = date + "  " + time;
			return s;
		} else if (m3.find()) {
			String s = m3.group();
			String[] lines = s.split("日");
			String date = lines[0];
			String time;
			if (lines.length > 1) {
				time = lines[lines.length - 1];
			} else {
				time = "00:00:00";
			}
			date = date.replaceAll("年", "-").replaceAll("月", "-").replaceAll(
					"日", "");
			String[] dates = date.split("-");
			if (dates[0].length() == 2)
				date = "20" + date;
			String[] times = time.split(":");
			if (times.length == 2)
				time = time + ":00";
			s = date + "  " + time;
			return s;
		}
		return null;
	}

}
