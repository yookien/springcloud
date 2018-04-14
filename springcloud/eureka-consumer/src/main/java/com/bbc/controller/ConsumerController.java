package com.bbc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bbc.bean.User;
import com.bbc.server.UserInterface;

@RestController	
public class ConsumerController {
	
	@Autowired
	private UserInterface userInterface;
	
	@RequestMapping(value ="/getUser/{id}",method=RequestMethod.GET)
	public User getUser(@PathVariable int id) {
		
		System.out.println("aaaaaaaa");
		User user = userInterface.getUser(1);
		return user;
	}

}
