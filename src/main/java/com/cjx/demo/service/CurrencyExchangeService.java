package com.cjx.demo.service;

public interface CurrencyExchangeService {
	
	/**
	 * calculate the currency exchange and print the result
	 * @param Currency
	 * @param ammount
	 */
	public void currencyExchange(String currency, Double amount);
	
	/**
	 * check if has the batch currency exchange task
	 * @return
	 */
	public boolean hasBatchCurrencyExchange();
	
	/**
	 * execute the batch currency exchange task process
	 */
	public void batchCurrencyExchange();
	
	
}
