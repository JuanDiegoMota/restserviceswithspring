package com.servicesrest.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.github.javafaker.Faker;
import com.servicesrest.models.User;

@Service
public class UserService {
	
	@Autowired
	private Faker faker;
	
	private List<User> usersList = new ArrayList<>();

	@PostConstruct
	public void init() {
		for(int i=0; i < 10; i++) {
			usersList.add(new User(faker.funnyName().name(),
					faker.name().username(),
					faker.dragonBall().character()));			
		}
	}
	
	public List<User> getUserList(String startWith) {
		if(startWith != null) {
			return usersList.stream().filter( user -> user.getUsername().startsWith(startWith))
										.collect(Collectors.toList());
		} else {
			return usersList;
		}
	}
	
	
	public User getUserByUsername(String username) {
		return usersList.stream().filter( user -> user.getUsername().equals(username))
							.findAny()
							.orElseThrow( 
									() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
											String.format("User %s not found ", username)));
	}
	
	
	public User createUser(User user) {
		if(usersList.stream().anyMatch( u -> u.getUsername().equals(user.getUsername()))) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, 
					String.format("User %s already exists", user.getUsername()));
		}
		usersList.add(user);
		return user;
	}
	
	
	public User updateUser(User user, String username) {
		User userToBeUpdated = getUserByUsername(username);
		userToBeUpdated.setNickname(user.getNickname());
		userToBeUpdated.setPassword(user.getPassword());
		return userToBeUpdated;
	}
	
	public void deleteUser(String username) {
		User user = getUserByUsername(username);
		usersList.remove(user);
	}
	
}
