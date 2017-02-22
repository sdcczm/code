package com.wcs.ncp.servlet.mule;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.ejb.Stateless;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.wcs.base.exception.AccessException;
import com.wcs.base.message.ExceptionMessage;
import com.wcs.base.util.BtcLogger;

@Stateless
public class HttpClient {

	private static BtcLogger logger = new BtcLogger("sap");

	public static void doPut(String putUrl, String content, Map<String, Object> headers) throws Exception {
		HttpURLConnection connection = null;
		try {
			String serviceName= (String)headers.get("service_name");
			String biz_event=(String)headers.get("biz_event");
			String trans_id = getUUID();
			// 传输数据记录日志
			System.out.println(serviceName+" to Mule data:" + content+";\ntrans_id:"+trans_id+"\nbiz_event:"+biz_event);
			logger.info(serviceName+" to Mule data:" + content+";\ntrans_id:"+trans_id+"\nbiz_event:"+biz_event);
			// 创建连接
			URL url = new URL(putUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(3000);  //setConnectTimeout：设置连接主机超时（单位：毫秒）
			connection.setReadTimeout(3000);     //setReadTimeout：设置从主机读取数据超时（单位：毫秒）
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("PUT");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-type", "application/json");
			connection.setRequestProperty("charset", "UTF-8");
			connection.setRequestProperty("submit_time", getDateTime());
			connection.setRequestProperty("trans_id", trans_id);
			for (String key : headers.keySet()) {
				connection.setRequestProperty(key, headers.get(key).toString());
			}
			connection.connect();
			// PUT请求
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.write(content.getBytes("UTF-8"));
			// out.writeBytes(json.toString());
			out.flush();
			out.close();
			if (connection.getResponseCode() != 200) {
				System.out.println(connection.getResponseMessage());
				System.out.println(connection.getResponseCode());
				throw new AccessException(ExceptionMessage.ERROR_FROM_MULE);
			}
			BufferedReader reader = null;
			// 读取响应
			try {
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			} catch (Exception e) {
				throw new AccessException(ExceptionMessage.ERROR_FROM_MULE);
			}
			String lines;
			StringBuffer sb = new StringBuffer("");
			while ((lines = reader.readLine()) != null) {
				lines = new String(lines.getBytes(), "utf-8");
				sb.append(lines);
			}
			System.out.println(serviceName+" result from Mule:" + sb.toString());
			logger.info(serviceName+" result from Mule:" + sb.toString());
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AccessException(ExceptionMessage.EXECUTE_MULE_ERROR, e);
		} finally {
			if (connection != null) {
				// 断开连接
				connection.disconnect();
			}
		}
	}

	public static void doPost(String postUrl, String content, Map<String, Object> headers) throws Exception {
		HttpURLConnection connection = null;
		try {
			String serviceName= (String)headers.get("service_name");
			String trans_id = getUUID();
			// 传输数据记录日志
			System.out.println(serviceName+" to Mule data:" + content+";trans_id:"+trans_id);
			logger.info(serviceName+" to Mule data:" + content+";trans_id:"+trans_id);
			// 创建连接
			URL url = new URL(postUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(3000);  //setConnectTimeout：设置连接主机超时（单位：毫秒）
			connection.setReadTimeout(3000);     //setReadTimeout：设置从主机读取数据超时（单位：毫秒）
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-type", "application/json");
			connection.setRequestProperty("charset", "UTF-8");
			connection.setRequestProperty("submit_time", getDateTime());
			connection.setRequestProperty("trans_id", trans_id);
			for (String key : headers.keySet()) {
				connection.setRequestProperty(key, headers.get(key).toString());
			}
			connection.connect();
			// PUT请求
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.write(content.getBytes("UTF-8"));
			// out.writeBytes(json.toString());
			out.flush();
			out.close();
			if (connection.getResponseCode() != 200) {
				System.out.println(connection.getResponseMessage());
				System.out.println(connection.getResponseCode());
				throw new AccessException(ExceptionMessage.ERROR_FROM_MULE);
			}
			BufferedReader reader = null;
			// 读取响应
			try {
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			} catch (Exception e) {
				throw new AccessException(ExceptionMessage.ERROR_FROM_MULE);
			}
			String lines;
			StringBuffer sb = new StringBuffer("");
			while ((lines = reader.readLine()) != null) {
				lines = new String(lines.getBytes(), "utf-8");
				sb.append(lines);
			}
			System.out.println(serviceName+" result from Mule:" + sb.toString());
			logger.info(serviceName+" result from Mule:" + sb.toString());
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AccessException(ExceptionMessage.EXECUTE_MULE_ERROR, e);
		} finally {
			if (connection != null) {
				// 断开连接
				connection.disconnect();
			}
		}
	}

	/**
	 * 生成每批数据的ID
	 * 
	 * @return
	 */
	private static String getUUID() {
		String uuid = UUID.randomUUID().toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		String dateTime = sdf.format(new Date());
		String uuidFormat = uuid + dateTime;
		return uuidFormat;
	}

	/**
	 * 生成当前时间
	 * 
	 * @return
	 */
	private static String getDateTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		return sdf.format(new Date());
	}

} 
