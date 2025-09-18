package com.cabbooking.ratingservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor jwtRequestInterceptor() {
        return requestTemplate -> {
            String jwtToken = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJxdzJlQGdtYWlsLmNvbSIsImlhdCI6MTc1NzQ0MjQ0MSwiZXhwIjoxNzU3NTI4ODQxfQ.k8i2bPoYH3bFfJP0IQBF3w_ArxgmO3Gcf_zeuK8J4k6A4KE23eduKnbMi1kC9BQW";
            requestTemplate.header("Authorization", "Bearer " + jwtToken);
        };
    }
}
