package top.lytree.redis.string;

import java.util.List;
import java.util.Map;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RedisHash {


    private final StringRedisTemplate stringRedisTemplate;

    public RedisHash(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 向hash 添加 键值对
     *
     * @param key hash 名
     * @param hk  hash中的键
     * @param hv  hash中的值
     */
    public void hPut(String key, String hk, String hv) {
        stringRedisTemplate.<String, String>opsForHash().put(key, hk, hv);
    }

    /**
     * 向hash中添加键值对
     *
     * @param key hash名
     * @param map 需要保存的Map
     */
    public void hPut(String key, Map<String, String> map) {
        stringRedisTemplate.<String, String>opsForHash().putAll(key, map);
    }

    /**
     * 获取hash中key对应的值
     *
     * @param key   hash名
     * @param field hash中的键
     * @return 返回key对应value
     */
    public String hGet(String key, String field) {
        return stringRedisTemplate.<String, String>opsForHash().get(key, field);
    }

    /**
     * 获取hash中key对应的值
     *
     * @param key   hash名
     * @param field hash中的键
     * @return 返回key对应value
     */
    public String hGetOrDefault(String key, String field, String defaultValue) {
        String value = stringRedisTemplate.<String, String>opsForHash().get(key, field);
        if (StringUtils.hasText(value)) {
            return value;
        }
        return defaultValue;
    }

    /**
     * 删除hash中field这一对kv
     */
    public void hDel(String key, String field) {
        stringRedisTemplate.opsForHash().delete(key, field);
    }

    /**
     * 获取hash的左右key value
     *
     * @param key hash 名
     * @return 所有键值对
     */
    public Map<String, String> hGetAll(String key) {
        return stringRedisTemplate.<String, String>opsForHash().entries(key);
    }

    /**
     * 获取hash的左右key value
     *
     * @param key hash 名
     * @return 所有键值对
     */
    public Map<String, String> hGetAllOrDefault(String key, Map<String, String> defaultMap) {
        Map<String, String> entries = stringRedisTemplate.<String, String>opsForHash().entries(key);
        if (null == entries || entries.isEmpty()) {
            return defaultMap;
        }
        return entries;
    }

    /**
     * 获取hash中的所有值
     *
     * @param key hash名
     * @return hash中的所有值
     */
    public List<String> hGetAllValue(String key) {
        return stringRedisTemplate.<String, String>opsForHash().values(key);
    }

    /**
     * 根据多个field 获取对应value
     *
     * @param key    hash名
     * @param fields hash中的键
     * @return 对应的value值
     */
    public List<String> hmGet(String key, List<String> fields) {
        return stringRedisTemplate.<String, String>opsForHash().multiGet(key, fields);
    }

    /**
     * hash中value的加减
     *
     * @param key   hash名
     * @param field hash中的键
     * @param value 加减值 （负值为减）
     * @return 返回加减后的值
     */
    public long hIncr(String key, String field, long value) {
        return stringRedisTemplate.opsForHash().increment(key, field, value);
    }

}
