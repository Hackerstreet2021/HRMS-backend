package com.realcoderz.controller;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realcoderz.config.JWTTokenHelper;
import com.realcoderz.model.Authority;
import com.realcoderz.model.User;
import com.realcoderz.requests.AuthenticationRequest;
import com.realcoderz.responses.LoginResponse;
import com.realcoderz.responses.UserInfo;
import com.realcoderz.service.IUserService;


@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class AuthenticationController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	JWTTokenHelper jWTTokenHelper;
	
	@Autowired
	private IUserService userDetailsService;
	
	@PostMapping("/auth/login")
	public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) throws InvalidKeySpecException,NoSuchAlgorithmException{

		final Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUserName(), authenticationRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		User user=(User)authentication.getPrincipal();
		String jwtToken=jWTTokenHelper.generateToken(user.getUsername());		
		LoginResponse response=new LoginResponse();
		response.setToken(jwtToken);		
		return ResponseEntity.ok(response);
	}
	  
	
	
	@GetMapping("/auth/userinfo")
	public ResponseEntity<?> getUserInfo(Principal principle){
		
		User userObj=(User) userDetailsService.loadUserByUsername(principle.getName());	
		
		User userInfo=new User();		
		userInfo.setFirstName(userObj.getFirstName());
		userInfo.setLastName(userObj.getLastName());
		userInfo.setUserName(userObj.getUsername());
		userInfo.setProfilePicPath(userObj.getProfilePicPath());
		
		userInfo.setAuthorities((List<Authority>) userObj.getAuthorities());
		
		return ResponseEntity.ok(userInfo);
		
		
	}

}
