package com.cjx.demo.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cjx.demo.service.CurrencyExchangeService;

@Component
public class ExchangeSchedulingUtil {
	
	@Autowired
	private CurrencyExchangeService currencyExchangeService;

	final ScheduledThreadPoolExecutor EXECUTOR = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

    private ScheduledFuture<?> t;
    
    public void startTask(String currency, Double amount) {
    	t = EXECUTOR.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
            	currencyExchangeService.currencyExchange(currency, amount);
            }
        }, 0, 60, TimeUnit.SECONDS);
    }
    
    public void stopTask() {
    	if(t!=null) {
    		t.cancel(true);
    	}
    }
    
    public void restartTask(String currency, Double amount){
    	stopTask();
    	startTask(currency,amount);
    	
    }
}
