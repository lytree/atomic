package top.yang.web.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JWTUtils {

    private static String secretKey = "MIIEpAIBAAKCAQEAvIuSLGxlvxLJKKIlLzzJlT0RXCCh+D6mhrCKdkltbq7w3d6QRH/iKZRHdQ6zp2aQG/nGp3qY+o56SoqwchAjkBELgiu4lw8lhk/LgI7kNd43JappwtyvqqQBZ9jdPicdnh18tbPxY9WyEQMiHkqsQSFyWdKV3s+hKU8aWaf/mH8zUlAQSdIR+etn5j8yb/nXcU/NegQAKJQ2pejSQAPtfqtB4bghdNa3Sby2oluNHTQ+dFb2j3qkPaHnfhYOYHkinu/urPS28NT13gvg7hhbb9ivphQODnXqb1VVOQw7JRKXqDUUuKiN0+KG47SGnP56F5sKyoW1Xp07ErJ0K7+BTQIDAQABAoIBAQCIsb/mSShzIRGKThQbbTzQ4Bdn7ZjAO9vLps6b95xJNLgYgzWhE/5KGuC7s074vNkaDzrBTZb/gLCUvnwyFhVSa6kmVMBddHPGLq/horc9fsAIpMZMqmcJliIWwhmU6BMHAWXa7nzB3rpcryLbR1QfXDpleNGDWpEDPABko/IhAkhkN6y4flezdYRvCS9j6krgv3Ja9AKN0BSUrpnZfCfChz94qXn81u/UL6NF6q7hX38lbopQkBX2Opxrkhs5JehpHqzY32x8rV2ucMduZ0DG7khGJW0dPbz5pbVfjp+SykRDIB+LbKkKfrbUNg1Emi+uDvhsU2bhXKYKp6od7PhBAoGBAOoUR5Sl8XwkTiQ88V78D0sU75Sn4HLBk79NE+srn3fTtgmhTyfjFbP5sZatY7BNq62BOcnEDQwdDpSKcODX4kpi6a9p91jBpcUtBOzYKg2AG9IU/uScUgjPeLLz+c+qp+RAKwxjelFMchu7xC8+KQBre101eqK6XxjqeZ8uSMIpAoGBAM4z";


    private static JwtParser jwtParser;
    private static JwtBuilder jwtBuilder;
    /**
     * rememberMe 为 false 的时候过期时间是1个小时
     */
    public static final long EXPIRATION = 60 * 60 * 60 * 60L;

    /**
     * rememberMe 为 true 的时候过期时间是7天
     */
    public static final long EXPIRATION_REMEMBER = 60 * 60 * 60 * 60 * 24 * 7L;
    // JWT token defaults
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";

    static {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
        jwtBuilder = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS512);
    }

    /**
     * 创建Token 设置永不过期，
     *
     * @param userId /
     * @return /
     */
    public static String createToken(String userId, long expiration) {
        Date exp = new Date(((new Date()).getTime()) + expiration);
        String compact = jwtBuilder
                // 加入ID确保生成的 Token 都不一致
                .setId(UUID.randomUUID().toString())
                .claim(TOKEN_HEADER, userId)
                .setHeaderParam("type", TOKEN_TYPE)
                .setSubject(userId).setExpiration(exp)
                .compact();
        return TOKEN_PREFIX + compact;
    }

    public static Claims getClaims(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }

    public static String getId(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    public static boolean isJwtExpired(String token) {
        Date expirationDate = getClaimDateFromToken(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }


    public static Date getClaimDateFromToken(String token, Function<Claims, Date> claimsResolver) {
        final Claims claims = jwtParser.parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }

    public static String getClaimStringFromToken(String token, Function<Claims, String> claimsResolver) {
        final Claims claims = jwtParser.parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }

}
