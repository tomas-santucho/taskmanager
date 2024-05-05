package com.alkemy.taskmanager.security.core;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtService {
    private static final String SECRET = "your-secret";
    private static final String ISSUER = "your-issuer";

    public String createToken(String userId, int minutesValid) {
        var algorithm = Algorithm.HMAC256(SECRET);
        return JWT.create()
                .withIssuer(ISSUER)
                .withClaim("userId", userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + minutesValid * 60 * 1000))
                .sign(algorithm);
    }


    public DecodedJWT verifyToken(String token) throws JWTVerificationException {
        var algorithm = Algorithm.HMAC256(SECRET);
        var verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
        return verifier.verify(token);
    }

    public String extractUserId(String token) {
        var jwt = JWT.decode(token);
        return jwt.getClaim("userId").asString();
    }

    public static void main(String[] args) {
        var jwtService = new JwtService();

        var token = jwtService.createToken("12345", 60);
        System.out.println("JWT: " + token);


        try {
            var decoded = jwtService.verifyToken(token);
            System.out.println("Token is valid. User ID: " + decoded.getClaim("userId").asString());
        } catch (JWTVerificationException exception) {
            System.out.println("Invalid token: " + exception.getMessage());
        }

        var userId = jwtService.extractUserId(token);
        System.out.println("Extracted User ID: " + userId);
    }
}
