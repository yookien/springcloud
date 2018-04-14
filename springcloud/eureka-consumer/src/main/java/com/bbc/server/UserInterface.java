package com.bbc.server;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bbc.bean.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;


@FeignClient(name = "eureka-providor" , fallback=UserServer.class)
public interface UserInterface {
	
	@RequestMapping(value = "/findUser/{id}",method=RequestMethod.GET)
	@HystrixCommand(commandProperties = {
			@HystrixProperty(name="execution.isolation.strategy",value="THREAD")
		})
	@LoadBalanced
	public User getUser(@PathVariable("id") int id) ;
	
	/**
	 * hystrix fallback 方法，要在实现类中实现
	 * @param id
	 * @return
	 *//*
	@RequestMapping(value = "/defaultGetUser/{id}",method=RequestMethod.GET)
	public User defaultGetUser(@PathVariable("id") int id) ;*/

}
