package com.javacuckoo.springsecurityjwt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javacuckoo.springsecurityjwt.model.AuthenticationRequest;
import com.javacuckoo.springsecurityjwt.model.AuthenticationResponse;
import com.javacuckoo.springsecurityjwt.util.JwtUtil;

@RestController
public class AuthenticationController {

	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final JwtUtil jwtUtil;

	@Autowired
	public AuthenticationController(AuthenticationManager authenticationManager,
			UserDetailsService userDetailsService, JwtUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtUtil =jwtUtil;
	}

	@RequestMapping({ "/hello" })
	public String hello() {
		return "Hello World";
	}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authentictse(@RequestBody AuthenticationRequest request) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));
		} catch (BadCredentialsException exception) {
			throw exception;
		}
		UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUserName());
		String jwt = jwtUtil.generateToken(userDetails);
		return ResponseEntity.ok(new AuthenticationResponse(jwt));
		
	}

}
