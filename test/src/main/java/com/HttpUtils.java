package com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * http连接工具
 * 
 * @author Administrator
 * 
 */
public class HttpUtils {

	private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);

	public static String sendPost(String url) {
		return sendPost(url, "");
	}

	public static String sendPost(String url, Map<String, String> param) {

		StringBuilder sb = new StringBuilder();

		if (param != null && param.size() > 0) {

			int index = 0;
			int length = param.size();
			for (String key : param.keySet()) {
				try {
					sb.append(URLEncoder.encode(key, "utf-8") + "=" + URLEncoder.encode(param.get(key), "utf-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				index++;
				if (index < length) {
					sb.append("&");
				}
			}
		}
		return sendPost(url, sb.toString());
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 * @throws IOException
	 */
	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuilder result = new StringBuilder();
		URLConnection conn = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestProperty("contentType", "UTF-8");
			conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
			conn.setRequestProperty("Accept-Language", Locale.getDefault().toString());
			conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
			// conn.setRequestProperty("Content-Type",
			// "application/json;charset=UTF-8");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.error("发送" + url + "," + param + "请求出现异常！" + e);
			e.printStackTrace();
			try {
				in = new BufferedReader(new InputStreamReader(((HttpURLConnection) conn).getErrorStream(), "UTF-8"));
				String line;
				while ((line = in.readLine()) != null) {
					result.append(line);
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result.toString();
	}

	public static String sendGet(String url) {

		return sendGet(url, null);
	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是name1=value1&name2=value2的形式。
	 * @return URL所代表远程资源的响应
	 */
	public static String sendGet(String url, Map<String, String> param) {

		return sendGet(url, param, null);
	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是name1=value1&name2=value2的形式。
	 * @return URL所代表远程资源的响应
	 * 
	 * @return heads 请求头部分
	 */
	public static String sendGet(String url, Map<String, String> param, Map<String, String> heads) {

		StringBuilder result = new StringBuilder();
		BufferedReader in = null;
		URLConnection conn = null;
		try {
			StringBuilder urlName = new StringBuilder(url);
			if (param != null && param.size() > 0) {
				urlName.append("?");
				int count = 0;
				Set<String> keySet = param.keySet();
				for (String key : keySet) {
					urlName.append(URLEncoder.encode(key, "utf-8"));
					urlName.append("=");
					urlName.append(URLEncoder.encode(param.get(key), "utf-8"));
					count++;
					if (count < keySet.size()) {
						urlName.append("&");
					}
				}
			}
			URL realUrl = new URL(urlName.toString());
			// 打开和URL之间的连接
			conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Cookie", "Cookie:ant_stream_54afbefa06286=1513354770/2588427990; bow_stream_54afbefa06286=13");
			conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");
			conn.setRequestProperty("Content-Type", "text/html;charset=utf-8");

			if (heads != null && heads.size() > 0) {
				Set<String> keySet = heads.keySet();
				for (String key : keySet) {
					conn.addRequestProperty(key, heads.get(key));
				}
			}

			// 建立实际的连接
			conn.connect();

			String charsetName = "UTF-8";

			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charsetName));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			LOG.error("发送" + url + "," + param + "请求出现异常！" + e);
			e.printStackTrace();
			try {
				in = new BufferedReader(new InputStreamReader(((HttpURLConnection) conn).getErrorStream(), "UTF-8"));
				String line;
				while ((line = in.readLine()) != null) {
					result.append(line);
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result.toString();
	}
}
