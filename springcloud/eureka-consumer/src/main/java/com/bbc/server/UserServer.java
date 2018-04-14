package com.bbc.server;

import org.springframework.stereotype.Component;

import com.bbc.bean.User;

@Component
public class UserServer implements UserInterface {

	@Override
	public User getUser(int id) {
		System.out.println("UserServer defaultFindUser is start-----ccccc");
		User user = new User();
		user.setId(1);
		user.setMsg("Error URL");
		user.setName("Guest");
		return user;
	}
	
	
	
}
