package com.onlinePharmacy.auth.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.onlinePharmacy.auth.entity.User;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.expiration}")
	private long expiration;
	
	
	private SecretKey getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	public String generateToken(User user) {
		return Jwts.builder().
				subject(user.getEmail()).
				claim("role", user.getRole().name()).
				claim("userId", user.getId()).
				claim("name",user.getName()).
				issuedAt(new Date()).
				expiration(new Date(System.currentTimeMillis() + expiration)).
				signWith(getSigningKey()).
				compact();
				
	}
	
	public String extractEmail(String token) {
		return parseClaims(token).getSubject();
	}
	
	public String extractRole(String token) {
		return parseClaims(token).get("role",String.class);
	}
	
	public Long extractuserId(String token) {
		return parseClaims(token).get("userId",Long.class);
	}
	
	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		}catch(JwtException | IllegalArgumentException e) {
			return false;
		}
	}
	
	private Claims parseClaims(String token) {
		return Jwts.parser().verifyWith(getSigningKey())
				.build().parseSignedClaims(token).
				getPayload();
	}
}
