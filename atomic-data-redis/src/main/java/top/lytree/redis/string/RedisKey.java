package top.lytree.redis.string;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RedisKey {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisKey(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 设置值
     *
     * @param key   名
     * @param value 值
     */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置值
     *
     * @param key   名
     * @param value 值
     * @param time  失效实现（秒）
     */
    public void set(String key, String value, long time) {
        stringRedisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 获取值
     *
     * @param key 名
     * @return value
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 获取值 获取为空 返回默认值
     *
     * @param key 名
     * @return value
     */
    public String getOrDefault(String key, String defaultValue) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.hasText(value)) {
            return value;
        }
        return defaultValue;
    }

    /**
     * 实现计数的加/减（ value为负数表示减）
     *
     * @param key
     * @param value
     * @return 返回redis中的值
     */
    public Long incr(String key, long value) {
        return stringRedisTemplate.execute((RedisCallback<Long>) con -> con.incrBy(key.getBytes(), value));
    }

    public Long decr(String key, long value) {
        return stringRedisTemplate.execute((RedisCallback<Long>) con -> con.decrBy(key.getBytes(), value));
    }

    /**
     * 实现计数的加/减1
     *
     * @param key
     * @return 返回redis中的值
     */
    public Long incr(String key) {
        return incr(key, 1);
    }

    public Long decr(String key) {
        return decr(key, 1);
    }

}
