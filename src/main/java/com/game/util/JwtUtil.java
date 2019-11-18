package com.game.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    public static String generateJwt(String gameId, int accountId, String hex) {
        byte[] bytes = null;
        try {
            bytes = Hex.decodeHex(hex);
        } catch (DecoderException e) {
            e.printStackTrace();
            return null;
        }

        Key key = Keys.hmacShaKeyFor(bytes);
        return Jwts.builder()
                .claim("gId", gameId)
                .claim("uId", accountId)
                .claim("ct", new Date().getTime())
                .signWith(key).compact();
    }

    public static String updateTime(String headerJwt, String hex) {
        byte[] bytes = null;
        try {
            bytes = Hex.decodeHex(hex);
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        Key key = Keys.hmacShaKeyFor(bytes);
        Claims body = Jwts.parser().setSigningKey(key).parseClaimsJws(headerJwt).getBody();
        body.put("t", System.currentTimeMillis());
        return Jwts.builder().setClaims(body).signWith(key).compact();
    }

    public static Object getKey(String jwt, String key, String hex) {
        byte[] bytes = null;
        try {
            bytes = Hex.decodeHex(hex);
        } catch (DecoderException e) {
            e.printStackTrace();
            return null;
        }

        Key hkey = Keys.hmacShaKeyFor(bytes);
        try{
            Claims body = Jwts.parser().setSigningKey(hkey).parseClaimsJws(jwt).getBody();
            if(body.containsKey(key)){
                return body.get(key);
            }else{
                return null;
            }
        }catch (JwtException e){
            e.printStackTrace();
        }
        return null;
    }
    public static Claims parseJwt(String jwt, String hex) {
        byte[] bytes = null;
        try {
            bytes = Hex.decodeHex(hex);
        } catch (DecoderException e) {
            e.printStackTrace();
            return null;
        }

        Key key = Keys.hmacShaKeyFor(bytes);
        try{
            Claims body = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();
            return body;
        }catch (JwtException e){
            e.printStackTrace();
        }
        return null;
    }
    public static String makeJwt(String account, String hex) {
        byte[] bytes = null;
        try {
            bytes = Hex.decodeHex(hex);
        } catch (DecoderException e) {
            e.printStackTrace();
            return null;
        }

        Key key = Keys.hmacShaKeyFor(bytes);
        return Jwts.builder().setSubject(account).signWith(key).compact();
    }
    public static boolean validateJwt(String jwt, String hex) {
        byte[] bytes = null;
        try {
            bytes = Hex.decodeHex(hex);
        } catch (DecoderException e) {
            e.printStackTrace();
            return false;
        }

        Key key = Keys.hmacShaKeyFor(bytes);
        try{
            Jwts.parser().setSigningKey(key).parseClaimsJws(jwt);
            return true;
        }catch (JwtException e){
            return false;
        }
    }
    public static String aquireJwtSubject(String jwt, String hex){
        byte[] bytes = null;
        try {
            bytes = Hex.decodeHex(hex);
        } catch (DecoderException e) {
            e.printStackTrace();
            return null;
        }
        Key key = Keys.hmacShaKeyFor(bytes);
        try{
            Claims body = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();
            String subject = body.getSubject();
            return subject;
        }catch (JwtException e){
            e.printStackTrace();
        }
        return null;
    }
}
