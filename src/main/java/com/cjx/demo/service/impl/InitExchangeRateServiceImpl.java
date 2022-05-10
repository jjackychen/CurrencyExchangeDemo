package com.cjx.demo.service.impl;


import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.cjx.demo.model.Currency;
import com.cjx.demo.model.ExchangeRateRespondObj;
import com.cjx.demo.service.InitExchangeRateService;
import com.cjx.demo.util.Constants;
import com.cjx.demo.util.HttpUtil;
import com.github.benmanes.caffeine.cache.Cache;


@Service
public class InitExchangeRateServiceImpl implements InitExchangeRateService {
	Logger logger = LoggerFactory.getLogger(InitExchangeRateServiceImpl.class);
	
	@Value("${3rdApi.endPoint}")
	private String endPoint;
	
	@Value("${3rdApi.appId}")
	private String appId;
	
	@Autowired
    private Cache<String, Object> caffeineCache;
	
	/**
	 * get exchange rate from Open Exchange Rates
	 */
	@Override
	public String getExchangeRate() {
		
		String base = Currency.USD.name();
		String symbols = Stream.of(Currency.values()).map(String::valueOf).collect(Collectors.joining(",")).toString();
		
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("app_id", appId);
		param.put("base", base);
		param.put("symbols", symbols);
		String respResult = HttpUtil.doGet(endPoint, param);
		return respResult;
	}


	@Override
	public int initExchangeRate() {
		String result = getExchangeRate();
		try {
			if (result.equals("")) {
				return 0;
			}else {
				ExchangeRateRespondObj obj = JSON.parseObject(result, ExchangeRateRespondObj.class);
				caffeineCache.put(Constants.EXCHANGE_RATE_CACHE_KEY, obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}


	@Override
	@Scheduled(fixedRate = 1000*60*30)
	public void updateExchangeRate() {
		initExchangeRate();
	}
	
}
