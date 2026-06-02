package ai.assistance.helpers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtHelper {

    @Value("${jwt.secretKey}")
    private String jwtKey;

    //convert secret key to the compatible jwt
    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(jwtKey.getBytes());
    }

    //generate token  for 1 hour
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(secretKey()).compact();
    }



    //validate toke Signature,Expiration time,Token structure,Token tampering
    //if anything is wrong it will throw error
    //ExpiredJwtException,MalformedJwtException,SignatureException
    public Claims claims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(secretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
