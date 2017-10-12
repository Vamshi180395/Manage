package com.example.manage;

import android.util.Base64;

import java.util.Date;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;

/**
 * Created by Rama Vamshi Krishna on 09/24/2017.
 */
public class JJWT {
    public static String createJWT(String iss, String aud, String client_secret) {
        Long iss_at = System.currentTimeMillis();
        Long exp_at = iss_at + 300000;
        String compactJws = Jwts.builder()
                .setIssuer(iss)
                .setAudience(aud)
                .setExpiration(new Date(exp_at))
                .setIssuedAt(new Date(iss_at))
                .signWith(SignatureAlgorithm.HS256, TextCodec.BASE64.encode(client_secret))
                .setHeaderParam("typ", "JWT")
                .compact();
        return compactJws;
    }
}

