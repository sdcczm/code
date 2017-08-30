package com.wcs.ncp.http;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;





@WebServlet(name = "SendMessage", urlPatterns = {"/sendMessage"})
public class SendMessage extends HttpServlet {
 
    private static final long serialVersionUID = -1915463532411657451L;
  
 
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException
    {      	
    	String mobile="13955364011";
    	String content="测试短信";
    	sendMessage(mobile,content);
    }
 
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {       	
       	Map<String,String[]> params=request.getParameterMap();
       	String[] mobile=params.get("mobile");
      	String[] content=params.get("content");
      	int num=mobile.length;      	
        for(int i=0;i<num;i++){
        	sendMessage(mobile[i],content[i]);
        }
       	
    }

   
    //发送短信方法
    public void sendMessage(String mobile,String content){
    	try {
        	//调用短信接口地址
        	String url = "http://114.55.88.173:7862/sms";
        	//调用短信接口所需参数
        	Map<String, String> params=new HashMap<String, String>();   	
        	params.put("action", "send");  //请求动作 ,设置为固定的:send
        	params.put("account", "900039"); //用户账号
        	params.put("password", "X7HK2M");//用户账号密码
        	params.put("mobile",mobile); //被叫号码
        	params.put("content",content);//短信内容
        	params.put("extno", "106900039"); //接入号，即SP服务号（106XXXXXX）
        	params.put("rt", "json"); //响应数据类型       	  	
        	HttpClientUtils.doPost(url,params);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    
}
