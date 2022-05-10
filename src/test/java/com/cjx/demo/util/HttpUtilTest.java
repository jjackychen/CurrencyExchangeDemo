package com.cjx.demo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.alibaba.fastjson.JSON;
import com.cjx.demo.model.Currency;
import com.cjx.demo.model.ExchangeRateRespondObj;

@SpringBootTest(classes = HttpUtilTest.class)
public class HttpUtilTest {
	
	@Value("${3rdApi.endPoint}")
	private String endPoint;
	
	@Value("${3rdApi.appId}")
	private String appId;
	
	private String base = Currency.USD.name();
	
	private String symbols = Stream.of(Currency.values()).map(String::valueOf).collect(Collectors.joining(",")).toString();
	
	@Test
	public void testDoGetNormal() {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("app_id", appId);
		param.put("base", base);
		param.put("symbols", symbols);
		String respResult = HttpUtil.doGet(endPoint, param);
		assertEquals(true,respResult.length() > 0);
	}
	
	
	@Test
	public void testDoGetFailWithEndPoint() {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("app_id", appId);
		param.put("base", base);
		param.put("symbols", symbols);
		String respResult = HttpUtil.doGet(endPoint+"777", param);
		assertEquals(true,respResult.length() == 0);
		
	}
	
	@Test
	public void testDoGetFailWithAppId() {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("app_id", appId+"777");
		param.put("base", base);
		param.put("symbols", symbols);
		String respResult = HttpUtil.doGet(endPoint, param);
		assertEquals(true,respResult.length() == 0);
		
	}

	@Test
	public void testDoGetFailWithBase() {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("app_id", appId);
		param.put("base", "TTT");
		param.put("symbols", symbols);
		String respResult = HttpUtil.doGet(endPoint, param);
		assertEquals(true,respResult.length() == 0);
	}
	
	@Test
	public void testDoGetFailWithSymbols() {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("app_id", appId);
		param.put("base", base);
		param.put("symbols", symbols.replaceAll(",", ""));
		String respResult = HttpUtil.doGet(endPoint, param);
		assertEquals(true,respResult.length() == 0 || JSON.parseObject(respResult, ExchangeRateRespondObj.class).getRates().size() == 0);
	}
}
