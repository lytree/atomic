package top.lytree.redis;

import org.springframework.data.redis.core.RedisTemplate;

public class RedisString extends BaseRedis<String> {

    public RedisString(String host, Integer port) {
        super(host, port);
    }

    public RedisString(RedisTemplate<String, String> redisTemplate) {
        super(redisTemplate);
    }

    public RedisString(String host, Integer port, String password) {
        super(host, port, password);
    }
}
