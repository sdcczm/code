package com.wcs.ncp.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.apache.http.NameValuePair;  
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.wcs.base.util.BtcLogger;

@Stateless
public class HttpClientUtils {
	
	private static BtcLogger logger = new BtcLogger();
	
	/**
	 * 带参数的post请求
	 * @param postUrl
	 * @param params
	 */
	public static void doPost(String postUrl,Map<String,String> params) {
		HttpPost request = new HttpPost(postUrl);
		try {
			// 设置请求和传输超时时间
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(5000).setConnectTimeout(5000).build();
			HttpClient httpclient = HttpClientBuilder.create().build();
			// 创建参数队列
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			for (String key : params.keySet()) {
				formparams.add(new BasicNameValuePair(key, params.get(key)));
			}
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
			request.setEntity(uefEntity);
			request.setConfig(requestConfig);
			HttpResponse response = httpclient.execute(request);
			byte[] content = EntityUtils.toByteArray(response.getEntity());
			String responseBody = new String(content);
			// 记录返回结果
			if (response.getStatusLine().getStatusCode() != 200) {
				System.out.println("调用短信接口失败,返回信息如下：" + responseBody);
				logger.info("调用短信接口失败,返回信息如下：" + responseBody);
			} else {
				System.out.println("调用短信接口成功,返回信息如下：" + responseBody);
				logger.info("调用短信接口成功,返回信息如下：" + responseBody);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			request.releaseConnection();
		}
	}
}
