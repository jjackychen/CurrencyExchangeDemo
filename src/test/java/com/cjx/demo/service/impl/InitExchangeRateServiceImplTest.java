package com.cjx.demo.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import com.alibaba.fastjson.JSON;
import com.cjx.demo.model.Currency;
import com.cjx.demo.model.ExchangeRateRespondObj;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;

@SpringBootTest(classes = InitExchangeRateServiceImplTest.class)
public class InitExchangeRateServiceImplTest {

	@InjectMocks
	private InitExchangeRateServiceImpl initExchangeRatServiceImpl;

	@Mock
	Cache<String, Object> caffeineCache;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		
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
		ReflectionTestUtils.setField(initExchangeRatServiceImpl, "caffeineCache", caffeineCache);
	}

	@Test
	public void testGetExchangeRateNormalResultLength() {
		ReflectionTestUtils.setField(initExchangeRatServiceImpl, "appId", "4aa141af29344425974cb4afb1e6b532");
		ReflectionTestUtils.setField(initExchangeRatServiceImpl, "endPoint",
				"https://openexchangerates.org/api/latest.json");
		String result = initExchangeRatServiceImpl.getExchangeRate();
		assertEquals(true, result.length() > 0);
	}
	@Test
	public void testGetExchangeRateNormalBase() {
		ReflectionTestUtils.setField(initExchangeRatServiceImpl, "appId", "4aa141af29344425974cb4afb1e6b532");
		ReflectionTestUtils.setField(initExchangeRatServiceImpl, "endPoint",
				"https://openexchangerates.org/api/latest.json");
		String result = initExchangeRatServiceImpl.getExchangeRate();
		ExchangeRateRespondObj obj = JSON.parseObject(result, ExchangeRateRespondObj.class);
		assertEquals(true, obj.getBase().equals(Currency.USD.toString()));
	}
	@Test
	public void testGetExchangeRateNormalRatesSize() {
		ReflectionTestUtils.setField(initExchangeRatServiceImpl, "appId", "4aa141af29344425974cb4afb1e6b532");
		ReflectionTestUtils.setField(initExchangeRatServiceImpl, "endPoint",
				"https://openexchangerates.org/api/latest.json");
		String result = initExchangeRatServiceImpl.getExchangeRate();
		ExchangeRateRespondObj obj = JSON.parseObject(result, ExchangeRateRespondObj.class);
		assertEquals(true, obj.getRates().size() == Currency.values().length);
	}
	@Test
	public void testGetExchangeRateNormalRatesValue() {
		ReflectionTestUtils.setField(initExchangeRatServiceImpl, "appId", "4aa141af29344425974cb4afb1e6b532");
		ReflectionTestUtils.setField(initExchangeRatServiceImpl, "endPoint",
				"https://openexchangerates.org/api/latest.json");
		String result = initExchangeRatServiceImpl.getExchangeRate();
		ExchangeRateRespondObj obj = JSON.parseObject(result, ExchangeRateRespondObj.class);
		for (Double rate : obj.getRates().values()) {
			assertEquals(true, rate > 0);
		}
	}

	@Test
	public void testGetExchangeRateError() throws NoSuchFieldException, SecurityException {
		ReflectionTestUtils.setField(initExchangeRatServiceImpl, "appId", "123");
		ReflectionTestUtils.setField(initExchangeRatServiceImpl, "endPoint",
				"https://openexchangerates.org/api/latest.json");
		String result = initExchangeRatServiceImpl.getExchangeRate();
		assertEquals(true, result.length() == 0);

	}

	 @Test
	 public void testInitExchangeRateNormal() {
		 ReflectionTestUtils.setField(initExchangeRatServiceImpl, "appId", "4aa141af29344425974cb4afb1e6b532");
			ReflectionTestUtils.setField(initExchangeRatServiceImpl, "endPoint",
					"https://openexchangerates.org/api/latest.json");
			int result = initExchangeRatServiceImpl.initExchangeRate();
			assertEquals(true, result == 1);
	 }
	 
	 @Test
	 public void testInitExchangeRateError() {
		 ReflectionTestUtils.setField(initExchangeRatServiceImpl, "appId", "123");
			ReflectionTestUtils.setField(initExchangeRatServiceImpl, "endPoint",
					"https://openexchangerates.org/api/latest.json");
			int result = initExchangeRatServiceImpl.initExchangeRate();
			assertEquals(true, result == 0);
	 }

}
