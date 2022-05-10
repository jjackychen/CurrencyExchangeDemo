package com.cjx.demo;


import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.cjx.demo.model.Currency;
import com.cjx.demo.model.ExchangeRateRespondObj;
import com.cjx.demo.service.CurrencyExchangeService;
import com.cjx.demo.service.InitExchangeRateService;
import com.cjx.demo.service.impl.ShutDownServiceImpl;
import com.cjx.demo.util.CheckUtil;
import com.cjx.demo.util.Constants;
import com.cjx.demo.util.ExchangeSchedulingUtil;
import com.github.benmanes.caffeine.cache.Cache;

@SpringBootApplication
@EnableScheduling
public class CurrencyExchangeDemoApplication implements CommandLineRunner	{
	Logger logger = LoggerFactory.getLogger(CurrencyExchangeDemoApplication.class);
	
	@Autowired
	private ShutDownServiceImpl shutDownServiceImpl;
	
	@Autowired
	private InitExchangeRateService initExchangeRatService;
	
	@Autowired
	private CurrencyExchangeService currencyExchangeService;
	
	@Autowired
	private ExchangeSchedulingUtil exchangeSchedulingUtil;
	
	@Autowired
    Cache<String, Object> caffeineCache;
	
	private String lastCurrency;
	private Double lastAmount;
	
	private Scanner scanner;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CurrencyExchangeDemoApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		scanner = new Scanner(System.in);
		this.initExchangeRate();
		this.doBatchCurrencyExchange();
		this.doManualCurrencyExchange();
		
	}
	/**
	 * init exchange rate from api
	 */
	private void initExchangeRate() {
		System.out.println("Initializing exchange rate from 3rd party API...");
		int runCode = initExchangeRatService.initExchangeRate();
		if (runCode == 0) {
			System.out.println("Initialization exchange rate from api fail, please initialization exchange rate manually.");
			initExchangeRateManually();
		}else {
			System.out.println("Initialization exchange rate from api susscess.");
		}
	}
	
	/**
	 * init exchange rate manually when init exchange rate from api fail 
	 */
	private void initExchangeRateManually() {
		System.out.println("Please enter exchange rates for various currencies based on USD.");		
		ExchangeRateRespondObj obj = new ExchangeRateRespondObj();
		Map<String,Double> rates = new HashMap<>();
		rates.put(Currency.USD.toString(), 1d);
		Stream.of(Currency.values()).forEach(x->{
			if(!x.equals(Currency.USD)) {
				boolean noFormat = false;
				Double rate = 0d;
				do {
					System.out.print(x.toString() + ":");
					noFormat = false;
					while(scanner.hasNext()) {
						String s = scanner.nextLine();
						if(s.equals("quit")) {
							System.exit(shutDownServiceImpl.initiateShutdown(0));
							break;
						}else {
							try {
								rate = Double.parseDouble(s);
							} catch (NumberFormatException e) {
								System.out.println("Rate format error, please enter again.");
								noFormat = true;
								break;
							}
							break;
						}
					}
				}while(noFormat);
				rates.put(x.toString(), rate);
			}
		});
		obj.setRates(rates);
		caffeineCache.put(Constants.EXCHANGE_RATE_CACHE_KEY, obj);
		System.out.println("Initialization exchange rate manually susscess.");
	}
	
	/**
	 * read initial payments from a file and do batch calculate exchange process
	 */
	private void doBatchCurrencyExchange() {
		if(currencyExchangeService.hasBatchCurrencyExchange()){
			currencyExchangeService.batchCurrencyExchange();
		}
	}
	
	/**
	 * type input a payment, calculate exchange and output the result per 1 min 
	 */
	private void doManualCurrencyExchange() {
		System.out.println("Please enter a pyment and followed by hitting the enter key, e.g. USD 100");
		while(scanner.hasNext()) {
			String s = scanner.nextLine();
			if(s.equals("quit")) {
				System.exit(shutDownServiceImpl.initiateShutdown(0));
				scanner.close();
				break;
			}else {
				String currency = "";
				Double amount = 0d;
				try {
					if(s.trim().equals("")) {
						continue;
					}
					String[] inputs = s.trim().split(" ");
					currency = inputs[0];
					if(!CheckUtil.checkCurrencyInput(currency)) {
						System.out.println("Currency format error, please enter again.");
						continue;
					}
					try {
						amount = Double.parseDouble(inputs[1]);
					} catch (NumberFormatException e) {
						System.out.println("Amount format error, please enter again.");
						continue;
					}
				} catch (Exception e) {
					System.out.println("Payment format error, please enter again.");
				} 
				if(amount == 0d) {
					exchangeSchedulingUtil.stopTask();
					lastAmount = amount;
					lastCurrency = currency;
				}else if(!amount.equals(lastAmount) || !currency.equals(lastCurrency)) {
					exchangeSchedulingUtil.restartTask(currency, amount);
					lastAmount = amount;
					lastCurrency = currency;
				}else {
					exchangeSchedulingUtil.startTask(currency, amount);
				}				
			}
		}
	}
}
