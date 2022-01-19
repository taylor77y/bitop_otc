package com.bitop.otcapi.security;

import com.alibaba.druid.util.FnvHash;
import com.bitop.otcapi.constant.Constants;
import com.bitop.otcapi.constant.JWTConstants;
import com.bitop.otcapi.constant.RedisConstants;
import com.bitop.otcapi.exception.TokenException;
import com.bitop.otcapi.redis.RedisCache;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JWTHelper {

    @Autowired
    private TokenProperties tokenProperties;


    @Autowired
    private RedisCache redisCache;

    public IJWTInfo jwtInfo(HttpServletRequest request) throws Exception {
        String token = request.getHeader(tokenProperties.getHeader());
        if (StringUtils.hasLength(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return getInfoFromToken(token);
    }

    /**
     * 获取token中的用户信息
     *
     * @param token
     * @return
     * @throws Exception
     */
    public IJWTInfo getInfoFromToken(String token) throws Exception {
        Jws<Claims> claimsJws = parserToken(token);
        if (null == claimsJws) {
            return null;
        }
        Claims body = claimsJws.getBody();
        return new JWTInfo(body.getSubject(), StringHelper.getObjectValue(body.get(JWTConstants.JWT_KEY_USER_ID)), StringHelper.getObjectValue(body.get(JWTConstants.JWT_KEY_USER_TYPE)));
    }


    /**
     * 公钥解析token
     *
     * @param token
     * @return
     * @throws Exception
     */
    public Jws<Claims> parserToken(String token) throws Exception {
        Jws<Claims> claimsJws = null;
        try {
            claimsJws = Jwts.parser().setSigningKey(tokenProperties.getSecret()).parseClaimsJws(token);
        } catch (Exception e) {
            throw new TokenException();
        }
        return claimsJws;
    }

    /**
     * 将用户数据存入数据库
     */
    public String createToken(IJWTInfo jwtInfo) {
        String token = generateToken(jwtInfo, null);
        redisCache.setCacheObject(RedisConstants.LOGIN_USER_KEY + jwtInfo.getUserId() + "_" + jwtInfo.getUserType(),
                token, tokenProperties.getExpireTime(),
                TimeUnit.MINUTES);
        return token;
    }

    /**
     * 密钥加密token
     *
     * @param jwtInfo
     * @param priKeyPath
     * @return
     * @throws Exception
     */
    private String generateToken(IJWTInfo jwtInfo, String priKeyPath) {
        String compactJws = Jwts.builder()
                .setSubject(jwtInfo.getUserName())
                .claim(JWTConstants.JWT_KEY_USER_ID, jwtInfo.getUserId())
                .claim(JWTConstants.JWT_KEY_USER_TYPE, jwtInfo.getUserType())
                .setExpiration(new Date(System.currentTimeMillis() + (long) tokenProperties.getExpireTime() * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.HS256, tokenProperties.getSecret())
                .compact();
        return compactJws;
    }


    public String getToken(HttpServletRequest request) {
        String token = request.getHeader(tokenProperties.getHeader());
        if (StringUtils.hasLength(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return token;
    }

    /**
     * 验证token是否过期
     */
    public boolean verifyToken(IJWTInfo jwtInfo,String token) {
        String redisToken= redisCache.getCacheObject(RedisConstants.LOGIN_USER_KEY + jwtInfo.getUserId() + "_" + jwtInfo.getUserType());
        if (StringUtils.isEmpty(redisToken)) {
            return false;
        }
        return token.equals(redisToken);
    }
}
