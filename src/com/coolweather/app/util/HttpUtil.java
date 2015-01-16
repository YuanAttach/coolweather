package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	public static void sendHttpRequest(final String address, final HttpCallBackListener listener )
	{
		new Thread(new Runnable(){
			@Override
			public void run(){
				HttpURLConnection connection=null;
				try{
					//得到网络输入，进行反应
					URL url= new URL(address);
					connection=(HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in=connection.getInputStream();
					BufferedReader reader= new BufferedReader(new InputStreamReader(in));
					StringBuilder response=new StringBuilder();
					String line;
					while((line=reader.readLine())!=null){
						response.append(line);
					}
					//连接成功，监听回调
					if(listener!=null){
						listener.onFinish(response.toString());
					}
				}
				catch(Exception e){
					//监听错误回掉处理
					if(listener!=null){
						listener.onError(e);
					}
					}
				finally{
					//关闭连接
					if(connection!=null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}

	
}
