package com.cjx.demo.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cjx.demo.model.Currency;
import com.cjx.demo.model.ExchangeRateRespondObj;
import com.cjx.demo.service.CurrencyExchangeService;
import com.cjx.demo.util.CheckUtil;
import com.cjx.demo.util.Constants;
import com.github.benmanes.caffeine.cache.Cache;

@Service
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService{
	Logger logger = LoggerFactory.getLogger(InitExchangeRateServiceImpl.class);
	
	@Autowired
    private Cache<String, Object> caffeineCache;
	
	@Value("${batchDataFilePath:}")
	private String batchDataFilePath;
	
	@Override
	public void currencyExchange(String currency, Double amount) {
		ExchangeRateRespondObj obj = (ExchangeRateRespondObj)caffeineCache.getIfPresent(Constants.EXCHANGE_RATE_CACHE_KEY);
		Map<String,Double> rates = obj.getRates();
		if(currency.equals(Currency.USD.toString())) {
			rates.forEach((k,v)->{
				if(!k.toString().equals(Currency.USD.toString())) {
					String currencyStr = k;
					Double rate = v;
					Double calcAmount = new BigDecimal(String.valueOf(amount)).multiply(new BigDecimal(String.valueOf(rate))).setScale(8,BigDecimal.ROUND_HALF_UP).doubleValue();
					System.out.println(currencyStr+" "+ calcAmount);
				}
			});
			System.out.println(Constants.DIVIDING_LINE);
		} else {
			if(!rates.containsKey(currency)) {
				System.out.println(String.format("%s is not support, please enter again.", currency));
			}else {
				Double curRate = rates.get(currency).doubleValue(); 
				Double baseAmount =  new BigDecimal(String.valueOf(amount)).divide(new BigDecimal(String.valueOf(curRate)), 8, BigDecimal.ROUND_HALF_UP).doubleValue();
				final Double baseAmountFinal = baseAmount;
				Stream.of(Currency.values()).forEach(x->{
					if(!x.toString().equals(currency)) {
						Double rate = rates.get(x.toString()).doubleValue(); 
						Double calcAmount = new BigDecimal(String.valueOf(baseAmountFinal)).multiply(new BigDecimal(String.valueOf(rate))).setScale(8,BigDecimal.ROUND_HALF_UP).doubleValue();
						System.out.println(x + " " + calcAmount);
					}
				});
				System.out.println(Constants.DIVIDING_LINE);
			}
		}
	}

	@Override
	public boolean hasBatchCurrencyExchange() {
		if(batchDataFilePath == null || batchDataFilePath.length() == 0) {
			return false;
		}
		File file = FileUtils.getFile(batchDataFilePath);
		if(!file.exists()) {
			System.out.println(String.format("Batch data File %s is not exists", batchDataFilePath));
			return false;
		}
		return true;
	}

	@Override
	public void batchCurrencyExchange() {
		System.out.println("Batch currency exchange start");
		ExchangeRateRespondObj obj = (ExchangeRateRespondObj)caffeineCache.getIfPresent(Constants.EXCHANGE_RATE_CACHE_KEY);
		Map<String,Double> rates = obj.getRates();
		
		File file = FileUtils.getFile(batchDataFilePath);
		List<String> lines;
		try {
			lines = FileUtils.readLines(file, "UTF-8");
			lines.forEach(s->{
				String[] inputs = s.trim().split(" ");
				String currency = inputs[0];
				if(!CheckUtil.checkCurrencyInput(currency)) {
					System.out.println(String.format("%s Currency format error",currency));
					return;
				}
				Double amount = 0d;
				try {
					amount = Double.parseDouble(inputs[1]);
				} catch (NumberFormatException e) {
					System.out.println(String.format("%s Amount format error",inputs[1]));
					return;
				} 
				if(!rates.containsKey(currency)) {
					System.out.println(String.format("%s Currency is not support", currency));
					return;
				}
				
				if(currency.equals(Currency.USD.toString())) {
					System.out.println(s);
				}else {
					Double curRate = rates.get(currency).doubleValue(); 
					Double baseAmount =  new BigDecimal(String.valueOf(amount)).divide(new BigDecimal(String.valueOf(curRate)), 8, BigDecimal.ROUND_HALF_UP).doubleValue();
					System.out.println(s + " " + String.format("(%s %s)",currency,baseAmount));
				}
				
			});
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Batch currency exchange error");
		}finally {
			System.out.println("Batch currency exchange finish");
		}
		
	}

}
