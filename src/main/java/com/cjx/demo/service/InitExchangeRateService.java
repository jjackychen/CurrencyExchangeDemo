package com.cjx.demo.service;

public interface InitExchangeRateService {
	
	/**
	 * send a get Exchange Rate request
	 * @return
	 */
	public String getExchangeRate();
	
	/**
	 * initial exchangeRate from API when program boot
	 * @return
	 */
	public int initExchangeRate();
	
	/**
	 * scheduled update exchangeRate from API per 30 min
	 */
	public void updateExchangeRate();

}
