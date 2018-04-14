package com.bbc.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bbc.bean.User;


@RestController
public class ProvidorController {
	
	@RequestMapping(value = "/findUser/{id}",method=RequestMethod.GET,
			produces=MediaType.APPLICATION_JSON)
	public User findUser(@PathVariable int id,HttpServletRequest res) {
		System.out.println(new Date().getTime() +  "ProvidorController findUser is start-----aaaaaaaa");
		User user = new User();
		user.setId(id);
		user.setName("yookien");
		user.setMsg(res.getRequestURL().toString());
		return user;
	}
	
    public User defaultFindUser(@PathVariable int id) {
		
		System.out.println("ProvidorController defaultFindUser is start-----bbbbbbb");
		User user = new User();
		user.setId(1);
		user.setMsg("Error URL");
		user.setName("Guest");
		return user;
	}
	
	
	@Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping("/service-instances/{applicationName}")
    public List<ServiceInstance> serviceInstancesByApplicationName(
            @PathVariable String applicationName) {
        return this.discoveryClient.getInstances(applicationName);
    }

}
