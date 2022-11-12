package top.lytree.redis;

import java.util.Set;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

public class RedisStringSet {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisStringSet(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 新增一个  sadd
     *
     * @param key
     * @param value
     */
    public void add(String key, String value) {
        stringRedisTemplate.opsForSet().add(key, value);
    }

    /**
     * 删除集合中的值  srem
     *
     * @param key
     * @param value
     */
    public void remove(String key, String value) {
        stringRedisTemplate.opsForSet().remove(key, value);
    }

    /**
     * 判断是否包含  sismember
     *
     * @param key
     * @param value
     */
    public void contains(String key, String value) {
        stringRedisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取集合中所有的值 smembers
     *
     * @param key
     * @return
     */
    public Set<String> values(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    /**
     * 返回多个集合的并集  sunion
     *
     * @param key1
     * @param key2
     * @return
     */
    public Set<String> union(String key1, String key2) {
        return stringRedisTemplate.opsForSet().union(key1, key2);
    }

    /**
     * 返回多个集合的交集 sinter
     *
     * @param key1
     * @param key2
     * @return
     */
    public Set<String> intersect(String key1, String key2) {
        return stringRedisTemplate.opsForSet().intersect(key1, key2);
    }

    /**
     * 返回集合key1中存在，但是key2中不存在的数据集合  sdiff
     *
     * @param key1
     * @param key2
     * @return
     */
    public Set<String> diff(String key1, String key2) {
        return stringRedisTemplate.opsForSet().difference(key1, key2);
    }
}
