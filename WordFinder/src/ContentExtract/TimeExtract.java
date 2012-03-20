package ContentExtract;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 *��ȡ����ָ����ҳ��ʱ�����
 */
public class TimeExtract {
	private String time = null;
	private GetSourceFile getSourceFile = null;

	/**
	 * ���Ժ���
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
	 * ��ָ��URL��Ӧ����ҳ����ȡʱ�� �������Ϊurl�����ض���Ϊ��ȡ�����ַ�����ʽ��ʱ�����
	 * 
	 * @param url
	 * @return
	 */
	public String getTime(String url) {
		// �����Ǻ���getTime�õ��ı���
		String[] lines = null;

		String htmlSourceFile = getSourceFile.getSourceFile(url);
		if (htmlSourceFile.equals("error"))
			return "error";

		try {
			/*
			 * �������ʱ����ȡ
			 */
			lines = htmlSourceFile.split(">");
			for (String htmlLine : lines) {
				if (htmlLine.contains("������") || htmlLine.contains("����ʱ��")) {
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
				if (htmlLine.contains("�ö�"))
					continue;
				if (htmlLine.contains("<!--"))
					continue;
				if (htmlLine.contains("lastModified"))
					continue;
				if (htmlLine.contains("Last-Modified"))
					continue;
				if (htmlLine.contains("lastReply"))
					continue;
				if (htmlLine.contains("���뾫��"))
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
			 * ������ж�����ͷ�
			 */
			if (lines != null)
				lines = null;
			if (htmlSourceFile != null)
				htmlSourceFile = null;

			/*
			 * ������Ϣ
			 */
			if (time.length()!=0) {
				return time;
			} else {
				return "no time";
			}
		} catch (Exception e) {
			System.err.println("��ȡʱ��ʱ�����쳣��");

			// �ͷŶ���ռ�
			if (lines != null)
				lines = null;
			if (htmlSourceFile != null)
				htmlSourceFile = null;

			// ���ش�����Ϣ
			return "error";
		}
	}

	// ��ȡʱ��ĺ�������ָ���ַ�������������ʽ��ȡʱ�䣬����ȡ���ĵ�һ��ʱ����Ϊ����ʱ��
	private static String parse(String line) {
		Pattern p1 = Pattern
				.compile("\\d{2,4}[:-]\\d{1,2}[:-]\\d{1,2}\\s{0,5}\\d{1,2}[:]\\d{1,2}[:]?\\d{0,2}");
		Pattern p2 = Pattern
				.compile("\\d{2,4}\\/\\d{1,2}\\/\\d{1,2}\\s{0,5}\\d{2}:\\d{2}:?\\d{0,2}");
		Pattern p3 = Pattern
				.compile("\\d{2,4}��\\d{1,2}��\\d{1,2}��\\s{0,5}\\d{0,2}:?\\d{0,2}:?\\d{0,2}");
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
			String[] lines = s.split("��");
			String date = lines[0];
			String time;
			if (lines.length > 1) {
				time = lines[lines.length - 1];
			} else {
				time = "00:00:00";
			}
			date = date.replaceAll("��", "-").replaceAll("��", "-").replaceAll(
					"��", "");
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
