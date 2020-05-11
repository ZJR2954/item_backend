package com.item_backend.utils;

import com.item_backend.config.JwtConfig;
import com.item_backend.model.entity.TestUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

@Component
public class JwtTokenUtil implements Serializable {


    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";
    private static final String CLAIM_KEY_ROLES = "roles";
    private static final long serialVersionUID = 3001653071458011111L;

    @Autowired
    private JwtConfig jwtConfig;


    /**
     * 从request中获取用户名
     *
     * @param request
     * @return
     */
    public String getUsernameFromRequest(HttpServletRequest request) {
        String token = request.getHeader(jwtConfig.getHeader());
        token = token.substring(jwtConfig.getPrefix().length());

        return token == null ? null : getUsernameFromToken(token);
    }

    /**
     * 从token中获取用户名
     *
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            // key为“sub”
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }


    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = new Date((Long)claims.get(CLAIM_KEY_CREATED));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    /**
     * token是否过期
     * true 过期 false 未过期
     *
     * @param token
     * @return
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        boolean isExpired = expiration.before(new Date());
        return isExpired;
    }

    /**
     * 从token中获取过期时间
     *
     * @param token
     * @return
     */
    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    /**
     * @Description: 解密获取用户信息
     * @Author: Mt.Li
    */
    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * 生成过期时间 单位[ms]
     *
     * @return
     */
    private Date generateExpirationDate() {
        // 当前毫秒级时间 + yml中的time * 1000
        return new Date(System.currentTimeMillis() + jwtConfig.getTime() * 1000);
    }



    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }


    /**
     * 根据提供的用户详细信息生成token
     *
     * @param user
     * @return
     */
    public String generateToken(TestUser user) {
        Map<String, Object> claims = new HashMap<>(3);
        claims.put(CLAIM_KEY_USERNAME, user.getName()); // 放入用户名
        claims.put(CLAIM_KEY_CREATED, new Date()); // 放入token生成时间
        List<String> roles = new ArrayList<>();
        claims.put(CLAIM_KEY_ROLES, roles); // 放入用户权限

        return generateToken(claims);
    }

    /**
     * 生成token（JWT令牌）
     *
     * @param claims
     * @return
     */
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
                .compact();
    }

    /**
     * @Description: 判断是否要刷新token
     * @Author: Mt.Li
    */
    public Boolean canTokenBeRefreshed(String token) {
        return !isTokenExpired(token);
    }

    /**
     * @Description: 刷新token，代表当前用户活跃，重新赋予30分钟
     * @Author: Mt.Li
    */
    public String refreshToken(String token) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            claims.put(CLAIM_KEY_CREATED, new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * 校验token
     *
     * @param token
     * @param user
     * @return
     */
    public Boolean validateToken(String token, TestUser user) {
        final String username = getUsernameFromToken(token);  //从token中取出用户名
        return (username.equals(user.getName())
                &&
                !isTokenExpired(token) //校验是否过期
        );
    }

    /**
     * 从token中获取用户角色
     *
     * @param authToken
     * @return
     */
    public List<String> getRolesFromToken(String authToken) {
        List<String> roles;
        try {
            final Claims claims = getClaimsFromToken(authToken);
            roles = (List<String>)claims.get(CLAIM_KEY_ROLES);
        } catch (Exception e) {
            roles = null;
        }
        return roles;
    }
}

