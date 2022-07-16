package top.yang.redis.string;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisHash {


    private final StringRedisTemplate stringRedisTemplate;

    public RedisHash(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void hput(String key, String hk, String hv) {
        stringRedisTemplate.<String, String>opsForHash().put(key, hk, hv);
    }

    public void hput(String key, Map<String, String> map) {
        stringRedisTemplate.<String, String>opsForHash().putAll(key, map);
    }

    /**
     * 获取hash中field对应的值
     */
    public String hget(String key, String field) {
        return stringRedisTemplate.<String, String>opsForHash().get(key, field);
    }

    /**
     * 删除hash中field这一对kv
     */
    public void hdel(String key, String field) {
        stringRedisTemplate.opsForHash().delete(key, field);
    }

    public Map<String, String> hgetAll(String key) {
        return stringRedisTemplate.<String, String>opsForHash().entries(key);
    }

    public List<String> hgetAllValue(String key) {
        return stringRedisTemplate.<String, String>opsForHash().values(key);
    }

    public List<String> hmget(String key, List<String> fields) {
        return stringRedisTemplate.<String, String>opsForHash().multiGet(key, fields);
    }

    // hash 结构的计数
    public long hincr(String key, String field, long value) {
        return stringRedisTemplate.opsForHash().increment(key, field, value);
    }

}
