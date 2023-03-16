package com.user.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.user.entities.User;
import com.user.services.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;


@RestController
@RequestMapping("/users")
public class UserController {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	
	@Autowired
	private UserService userService;
	
// create
	
	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user){	
		User user1 = userService.saveUser(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(user1);		
	}
// get one user
	@GetMapping("/{userId}")
	@CircuitBreaker(name = "ratingHotelBreaker", fallbackMethod = "ratingHotelFallback")
	public ResponseEntity<User> getSingleUser(@PathVariable String userId){	
		User user = userService.getUser(userId);
		logger.info("Get request of particular user");
		return ResponseEntity.ok(user);	
	}
	
// creating fallback method for circuit breaker
	public ResponseEntity<User> ratingHotelFallback(String userId, Exception ex){
	   logger.info("Fallback is executed as the one service is down",ex.getMessage());
	   User user = User.builder()
			   .email("dummy@gmail.com")
			   .name("Dummy")
			   .userId("11******")
			   .about("This user is created dummy because some service is down")
			   .build();   
	   return new ResponseEntity<>(user,HttpStatus.OK);		
	}
	
	
	@GetMapping
	public ResponseEntity<List<User>> getAllUser(){
		logger.info("Get request of all users");
		List<User> alluser = userService.getAllUsers();
		return ResponseEntity.ok(alluser);		
	}

}
