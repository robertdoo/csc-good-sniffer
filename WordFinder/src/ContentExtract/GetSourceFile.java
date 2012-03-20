package ContentExtract;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

public class GetSourceFile {

	/**测试函数
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 提取链接指向网页的源文件
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
			// 建立网络连接
			myUrl = new URL(url);
			// 获取连接对象
			conn = myUrl.openConnection();
			// 设置连接标示
			conn.setRequestProperty("User-Agent", "vigorouswei@126.com");
			// 设置连接超时
			conn.setConnectTimeout(5000);
			// 设置获取数据的接口
			br = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			// 建立存储对象
			sb = new StringBuffer();
			// 判断网页编码格式是否为gb2312
			boolean isGB2312 = true;
			// 判断网页编码格式是否已确定
			boolean isConfirmed = false;

			/*
			 * 从接口内读入数据，判断字符编码，如果是UTF-8格式的则需要进行转换！
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
			 * 如果编码方式为UTF-8则重新读入数据并更改编码方式
			 */
			if (isGB2312 == false) {
				myUrl1 = new URL(url);
				conn = myUrl1.openConnection();
				conn.setRequestProperty("User-Agent", "vigorouswei@126.com");
				conn.setConnectTimeout(5000);
				in = conn.getInputStream();
				// 临时数组
				tempbuff = new byte[100];
				// 定义一下足够大的数组300k
				buff = new byte[307200];
				// 读取字节个数
				int count = 0;
				// 每次读取的个数
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
			 * 下面释放对象
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
			System.err.println("提取html时出现 SocketTimeoutException 异常！");
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
			System.err.println("提取html时出现 ConnectException 异常！");
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
			System.err.println("提取html时出现出现 异常！");
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
