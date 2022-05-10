package com.cjx.demo.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import com.cjx.demo.model.Currency;
import com.cjx.demo.model.ExchangeRateRespondObj;
import com.cjx.demo.util.Constants;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;

@SpringBootTest(classes = CurrencyExchangeServiceImplTest.class)
public class CurrencyExchangeServiceImplTest {
	
	@InjectMocks
	private CurrencyExchangeServiceImpl currencyExchangeServiceImpl;

	@Mock
	Cache<String, Object> caffeineCache;

	private PrintStream console = null;
	private ByteArrayOutputStream bytes = null;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@BeforeEach
	void setUp() {

		MockitoAnnotations.openMocks(this);
		
		ExchangeRateRespondObj obj = new ExchangeRateRespondObj();
		Map<String,Double> rates = new HashMap<>();
		rates.put(Currency.USD.toString(), 1d);
		rates.put(Currency.CNY.toString(), 6.7211);
		rates.put(Currency.GBP.toString(), 0.81248);
		rates.put(Currency.HKD.toString(), 7.849815);
		rates.put(Currency.NZD.toString(), 1.582767);
		obj.setBase(Currency.USD.toString());
		obj.setRates(rates);
		caffeineCache = Caffeine.newBuilder()
				.maximumSize(1000)
				.expireAfter(new Expiry() {
					@Override
					public long expireAfterCreate(@NonNull Object arg0, @NonNull Object arg1, long arg2) {
						return Long.MAX_VALUE;
					}
					@Override
					public long expireAfterRead(@NonNull Object arg0, @NonNull Object arg1, long arg2,
							@NonNegative long arg3) {
						return Long.MAX_VALUE;
					}
					@Override
					public long expireAfterUpdate(@NonNull Object arg0, @NonNull Object arg1, long arg2,
							@NonNegative long arg3) {
						return Long.MAX_VALUE;
					}
				})
				.build(); 
		caffeineCache.put(Constants.EXCHANGE_RATE_CACHE_KEY, obj);
		ReflectionTestUtils.setField(currencyExchangeServiceImpl, "caffeineCache", caffeineCache);
		
		bytes = new ByteArrayOutputStream();
		console = System.out;
		System.setOut(new PrintStream(bytes));
	
	}
	
	@AfterEach
	void tearDown() {
		System.setOut(console);
	}
	
	@Test
	public void testCurrencyExchangeNormal() {
		currencyExchangeServiceImpl.currencyExchange("USD", 100d);
		String expected = new String("HKD 784.9815\n" + 
				"GBP 81.248\n" + 
				"NZD 158.2767\n" + 
				"CNY 672.11\n" + 
				"==============================");
		assertEquals(expected,bytes.toString().trim().replace("\r", ""));
	}
	
	@Test
	public void testHasBatchCurrencyExchangeNormal() {
		ReflectionTestUtils.setField(currencyExchangeServiceImpl, "batchDataFilePath", "/Users/jacky/project/currency-exchange-demo/src/main/resources/batchExchangeData.txt");
		Boolean result = currencyExchangeServiceImpl.hasBatchCurrencyExchange();
		assertEquals(true,result);
	}
	
	@Test
	public void testHasBatchCurrencyExchangeError() {
		ReflectionTestUtils.setField(currencyExchangeServiceImpl, "batchDataFilePath", "/Users/jacky/project/currency-exchange-demo/src/main/batchExchangeData.txt");
		Boolean result = currencyExchangeServiceImpl.hasBatchCurrencyExchange();
		assertEquals(false,result);
	}
	
	@Test
	public void testBatchCurrencyExchangeNormal() {
		ReflectionTestUtils.setField(currencyExchangeServiceImpl, "batchDataFilePath", "/Users/jacky/project/currency-exchange-demo/src/main/resources/batchExchangeData.txt");
		currencyExchangeServiceImpl.batchCurrencyExchange();
		String expected = new String("Batch currency exchange start\n" + 
				"USD 900\n" + 
				"CNY 2000 (CNY 297.57033819)\n" + 
				"HKD 300 (HKD 38.21746118)\n" + 
				"Batch currency exchange finish");
		assertEquals(expected,bytes.toString().trim().replace("\r", ""));
	}
	
	@Test
	public void testBatchCurrencyExchangeError() {
		ReflectionTestUtils.setField(currencyExchangeServiceImpl, "batchDataFilePath", "/Users/jacky/project/currency-exchange-demo/src/main/resources/batchExchangeDataErrorTestCase.txt");
		currencyExchangeServiceImpl.batchCurrencyExchange();
		String expected = new String("Batch currency exchange start\n" + 
				"AUD Currency is not support\n" + 
				"2..2 Amount format error\n" + 
				"HKDD Currency format error\n" + 
				"Batch currency exchange finish");
		assertEquals(expected,bytes.toString().trim().replace("\r", ""));
	}

}
