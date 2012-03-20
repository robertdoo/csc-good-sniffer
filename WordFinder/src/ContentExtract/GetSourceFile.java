package ContentExtract;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

public class GetSourceFile {

	/**���Ժ���
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * ��ȡ����ָ����ҳ��Դ�ļ�
	 * @param url
	 * @return
	 */
	public String getSourceFile(String url){
		URL myUrl = null;
		URLConnection conn = null;
		BufferedReader br = null;
		StringBuffer sb = null;
		URL myUrl1 = null;
		InputStream in = null;
		byte[] tempbuff = null;
		byte[] buff = null;
		byte[] result = null;
		try {
			// ������������
			myUrl = new URL(url);
			// ��ȡ���Ӷ���
			conn = myUrl.openConnection();
			// �������ӱ�ʾ
			conn.setRequestProperty("User-Agent", "vigorouswei@126.com");
			// �������ӳ�ʱ
			conn.setConnectTimeout(5000);
			// ���û�ȡ���ݵĽӿ�
			br = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			// �����洢����
			sb = new StringBuffer();
			// �ж���ҳ�����ʽ�Ƿ�Ϊgb2312
			boolean isGB2312 = true;
			// �ж���ҳ�����ʽ�Ƿ���ȷ��
			boolean isConfirmed = false;

			/*
			 * �ӽӿ��ڶ������ݣ��ж��ַ����룬�����UTF-8��ʽ������Ҫ����ת����
			 */
			String line = null;
			while (null != (line = br.readLine())) {
				sb.append(line);
				if (isConfirmed)
					continue;
				if ((line.contains("meta") || line.contains("META"))
						&& (line.contains("utf-8") || line.contains("UTF-8"))) {
					isGB2312 = false;
					isConfirmed = true;
					break;
				} else if ((line.contains("meta") || line.contains("META"))
						&& (line.contains("gbk") || line.contains("GBK") || line
								.contains("gb2312"))) {
					isGB2312 = true;
					isConfirmed = true;
				}
			}
			br.close();

			/*
			 * ������뷽ʽΪUTF-8�����¶������ݲ����ı��뷽ʽ
			 */
			if (isGB2312 == false) {
				myUrl1 = new URL(url);
				conn = myUrl1.openConnection();
				conn.setRequestProperty("User-Agent", "vigorouswei@126.com");
				conn.setConnectTimeout(5000);
				in = conn.getInputStream();
				// ��ʱ����
				tempbuff = new byte[100];
				// ����һ���㹻�������300k
				buff = new byte[307200];
				// ��ȡ�ֽڸ���
				int count = 0;
				// ÿ�ζ�ȡ�ĸ���
				int rbyte = 0;
				while ((rbyte = in.read(tempbuff)) != -1) {
					for (int i = 0; i < rbyte; i++) {
						if (count + i > 307199)
							break;
						buff[count + i] = tempbuff[i];
					}
					count += rbyte;
				}

				result = new byte[count];
				for (int i = 0; i < count; i++)
					result[i] = buff[i];

				String output = new String(result, "UTF-8");
				int index = output.indexOf("<");
				output = output.substring(index);
				sb = new StringBuffer();
				sb.append(output);
			}
			
			/*
			 * �����ͷŶ���
			 */
			if (buff != null)
				buff = null;
			if (myUrl != null)
				myUrl = null;
			if (conn != null)
				conn = null;
			if (br != null)
				br = null;
			if (line != null)
				line = null;
			if (myUrl1 != null)
				myUrl1 = null;
			if (in != null)
				in = null;
			if (tempbuff != null)
				tempbuff = null;
			if (result != null)
				result = null;
			
			return sb.toString();
		}catch (SocketTimeoutException exc) {
			System.err.println("��ȡhtmlʱ���� SocketTimeoutException �쳣��");
			if (buff != null)
				buff = null;
			if (myUrl != null)
				myUrl = null;
			if (conn != null)
				conn = null;
			if (br != null)
				br = null;
			if (sb != null)
				sb = null;
			if (myUrl1 != null)
				myUrl1 = null;
			if (in != null)
				in = null;
			if (tempbuff != null)
				tempbuff = null;
			if (result != null)
				result = null;
			return "error";
		} catch (ConnectException e) {
			System.err.println("��ȡhtmlʱ���� ConnectException �쳣��");
			if (buff != null)
				buff = null;
			if (myUrl != null)
				myUrl = null;
			if (conn != null)
				conn = null;
			if (br != null)
				br = null;
			if (sb != null)
				sb = null;
			if (myUrl1 != null)
				myUrl1 = null;
			if (in != null)
				in = null;
			if (tempbuff != null)
				tempbuff = null;
			if (result != null)
				result = null;
			return "error";
		} catch (Exception e) {
			System.err.println("��ȡhtmlʱ���ֳ��� �쳣��");
			if (buff != null)
				buff = null;
			if (myUrl != null)
				myUrl = null;
			if (conn != null)
				conn = null;
			if (br != null)
				br = null;
			if (sb != null)
				sb = null;
			if (myUrl1 != null)
				myUrl1 = null;
			if (in != null)
				in = null;
			if (tempbuff != null)
				tempbuff = null;
			if (result != null)
				result = null;
			return "error";
		}
	}

}
