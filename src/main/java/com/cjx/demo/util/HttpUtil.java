package com.cjx.demo.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpUtil {
	
	static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	/**
	 * get request
	 * @param endPoint
	 * @param param
	 * @return
	 */
	public static String doGet(String endPoint, Map<String,Object> param) {
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		CloseableHttpResponse response = null;
		
		String resultStr = "";
	        try {
	        	
	            URIBuilder builder = new URIBuilder(endPoint);
	            if (param != null) {
	                for (String key : param.keySet()) {
	                    builder.addParameter(key, String.valueOf(param.get(key)));
	                }
	            }
	            URI uri = builder.build();
	            HttpGet httpGet = new HttpGet(uri);
	            response = httpClient.execute(httpGet);
	            
	            if (response!=null && response.getStatusLine().getStatusCode() == 200){
	                HttpEntity httpEntity = response.getEntity();
	                resultStr = EntityUtils.toString(httpEntity,"UTF-8");
	            }

	        } catch (URISyntaxException e) {
	            e.printStackTrace();
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }finally {
	            try {
	                if (response != null) {
	                    response.close();
	                }
	                httpClient.close();

	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return resultStr;
	}
	
	
	
}
