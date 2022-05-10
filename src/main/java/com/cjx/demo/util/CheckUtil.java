package com.cjx.demo.util;

public class CheckUtil {
	
	
	/**
	 * A currency usually have 3 capital letters, e.g. USD, HKD, CNY, NZD, GBP
	 * @param currency
	 * @return
	 */
	public static boolean checkCurrencyInput(String currency) {
		
		return currency.matches("([A-Z]){3}");
		
	}

}
