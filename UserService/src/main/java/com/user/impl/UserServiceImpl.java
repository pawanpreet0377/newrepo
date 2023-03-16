package com.user.impl;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.user.entities.Hotel;
import com.user.entities.Rating;
import com.user.entities.User;
import com.user.exceptions.ResourceNotFoundException;
import com.user.repository.UserRepository;
import com.user.services.UserService;


@Service
public class UserServiceImpl implements UserService{
	
	private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public User saveUser(User user) {
		String randomUserID = UUID.randomUUID().toString();
		user.setUserId(randomUserID);
		return userRepository.save(user);
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public User getUser(String userId) {
		//getting user from database with the help of user repository
		User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User is not found for this id " + userId));
		//fetch rating from the above called user id from the rating service
		Rating[] ratingsOfUser = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/" + user.getUserId(), Rating[].class);
		logger.info("{} ",ratingsOfUser);
		
		List<Rating> ratings = Arrays.stream(ratingsOfUser).toList();
		
		List<Rating> ratingList = ratings.stream().map(rating-> {
			
			ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/" + rating.getHotelId() , Hotel.class);
			Hotel hotel = forEntity.getBody();
			logger.info("response status code: {} " , forEntity.getStatusCode());
			rating.setHotel(hotel);
			
			return rating;
		}).collect(Collectors.toList());
		user.setRatings(ratingList);
		return user;
	}

}
