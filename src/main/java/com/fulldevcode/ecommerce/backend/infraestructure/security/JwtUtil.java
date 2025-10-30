package com.fulldevcode.ecommerce.backend.infraestructure.security;

import com.fulldevcode.ecommerce.backend.infraestructure.models.UserType;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.catalina.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Objects;

@Component
public class JwtUtil {

    private static final String Secret="UnaClaveMuyLargaQueDebeTenerAlMenos256bits_1234567890JWTSECRETKEY";
    private static final long Exp_ms = 1000 * 60 * 60;

    private final Key key = Keys.hmacShaKeyFor(Secret.getBytes());

    public String GenerateToken(String email, String rol, Integer id)
    {
        return Jwts.builder()
                .setSubject(email)
                .claim("rol", rol)
                .claim("id", id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + Exp_ms))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean ValidateToken(String token) {
        try
        {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }
        catch (JwtException | IllegalArgumentException ex)
        {
            return  false;
        }
    }

    public String GetEmail(String Token)
    {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(Token).getBody().getSubject();
    }

    public String GetRol(String Token) {
        Object rol = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(Token).getBody().get("rol");
        return rol != null ? rol.toString() : null;
    }

    public Integer GetId(String Token) {
        Object id = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(Token).getBody().get("id");
        return  id != null ? Integer.valueOf(id.toString()) : null;
    }
}
