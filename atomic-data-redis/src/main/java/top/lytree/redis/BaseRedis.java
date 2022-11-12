package top.lytree.redis;

import org.springframework.data.redis.core.RedisTemplate;

public class BaseRedis<T> extends AbstractRedis<T> {
    public BaseRedis(String host, Integer port) {
        super(host, port);
    }

    public BaseRedis(RedisTemplate<String, T> redisTemplate) {
        super(redisTemplate);
    }

    public BaseRedis(String host, Integer port, String password) {
        super(host, port, password);
    }
}
