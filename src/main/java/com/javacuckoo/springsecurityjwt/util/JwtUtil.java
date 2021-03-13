package com.javacuckoo.springsecurityjwt.util;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtUtil {

	private final Logger loggger = LoggerFactory.getLogger(JwtUtil.class);

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
	}

	private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
		Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public Date extractExpiration(String token) {
		return extractClaims(token, Claims::getExpiration);
	}

	public String extractUserName(String token) {
		return extractClaims(token, Claims::getSubject);
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private String createToken(Map<String, Object> claims, String subject) {
		int sessionExpirationInSeconds = 300;
		String token = Jwts.builder().setClaims(claims).setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(sessionExpirationInSeconds)))
				.signWith(getKey()).compact();
		loggger.info("JWT token created {} for user {} and it will be expired in {} seconds", token, subject,
				sessionExpirationInSeconds);
		return token;
	}

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userDetails.getUsername());
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		String userName = extractUserName(token);
		return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	private Key getKey() {
		String secret = "UmV0dXJucyBhIEtleUdlbmVyYXRvciBvYmplY3QgdGhhdCBnZW5lcmF0ZXMgc2VjcmV0IGtleXMgZm9yIHRoZSBzcGVjaWZpZWQgYWxnb3JpdGht";
		return new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.getJcaName());
	}
	public static void main(String[] args) {
		JwtUtil jwtUtil =  new JwtUtil();
		UserDetails userDetails = new User("foo", "foo", new ArrayList<>());
		String jwtToken = jwtUtil.generateToken(userDetails);
		
		Claims claims = jwtUtil.extractAllClaims(jwtToken);
		System.out.println("Completed Successfully----->  "+jwtToken);
		jwtUtil.validateToken(jwtToken, userDetails);

	}
//	public static void main(String[] args) throws NoSuchAlgorithmException {
//		String secret = "Returns a KeyGenerator object that generates secret keys for the specified algorithm";
//		String encodedString = new String(Base64.getEncoder().encode(secret.getBytes()));
//		System.out.println(encodedString);
//		String decodeddString = new String(Base64.getDecoder().decode(encodedString.getBytes()));
//		System.out.println(decodeddString);
//	}

}
