package com.cjx.demo.config;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;

@Configuration
public class CacheConfig {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Bean
	public Cache<String, Object> caffeineCache(){
		return Caffeine.newBuilder()
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
		
	}

}
