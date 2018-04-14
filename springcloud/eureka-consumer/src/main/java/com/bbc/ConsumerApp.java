package com.bbc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@ComponentScan
@EnableCircuitBreaker  //hystrix
@EnableHystrix
public class ConsumerApp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new SpringApplicationBuilder(ConsumerApp.class).web(true).run(args);
	}

}
