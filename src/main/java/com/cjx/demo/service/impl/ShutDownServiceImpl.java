package com.cjx.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ShutDownServiceImpl {
	
	@Autowired
	private ApplicationContext appContext;
	
	public int initiateShutdown(int returnCode) {
		return SpringApplication.exit(appContext, ()-> returnCode);
	}

}
