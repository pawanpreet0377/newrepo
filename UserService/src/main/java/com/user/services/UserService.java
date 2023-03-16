package com.user.services;

import java.util.List;

import com.user.entities.User;

public interface UserService {
	
	// create a user
	User saveUser(User user);
	
	// get all users
	List<User> getAllUsers();
	
	// user according to id
	User getUser(String userId);

}
