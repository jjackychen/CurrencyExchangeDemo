package com.cjx.demo.util;



import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CheckUtilTest.class)
public class CheckUtilTest {
	
	//test case
	String currencyNormal = "USD";
	String currencyLowerCase = "usd";
	String currencyOverLength = "USDT";
	String currencyWithNum = "U12";
	String currencyWithSpecialChar = "US)";
	String currencyLessLength = "US";
		
	@Test
	public void testCheckCurrencyInputNormal() {
		assertEquals(true,CheckUtil.checkCurrencyInput(currencyNormal));
	}
	@Test
	public void testCheckCurrencyInputLowerCase() {
		assertEquals(false,CheckUtil.checkCurrencyInput(currencyLowerCase));
	}
	@Test
	public void testCheckCurrencyInputOverLength() {
		assertEquals(false,CheckUtil.checkCurrencyInput(currencyOverLength));
	}
	@Test
	public void testCheckCurrencyInputWithNum() {
		assertEquals(false,CheckUtil.checkCurrencyInput(currencyWithNum));
	}
	@Test
	public void testCheckCurrencyInputWithSpecialChar() {
		assertEquals(false,CheckUtil.checkCurrencyInput(currencyWithSpecialChar));
	}
	@Test
	public void testCheckCurrencyInputLessLength() {
		assertEquals(false,CheckUtil.checkCurrencyInput(currencyLessLength));
	}
}
