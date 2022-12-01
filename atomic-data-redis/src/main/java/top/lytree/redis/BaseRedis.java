package top.lytree.redis;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
    /*---------------------------------key----------------------------------------------------*/

    /**
     * 设置值
     *
     * @param key   名
     * @param value 值
     */
    public void kSet(String key, T value) {
        template.opsForValue().set(key, value);
    }

    /**
     * 设置值
     *
     * @param key   名
     * @param value 值
     * @param time  失效实现（秒）
     */
    public void kSet(String key, T value, long time) {
        template.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 获取值
     *
     * @param key 名
     * @return value
     */
    public T kGet(String key) {
        return template.opsForValue().get(key);
    }

    /**
     * 获取值 获取为空 返回默认值
     *
     * @param key 名
     * @return value
     */
    public T kGetOrDefault(String key, T defaultValue) {
        T value = template.opsForValue().get(key);
        if (ObjectUtils.isEmpty(value)) {
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
    public Long kIncr(String key, long value) {
        return template.execute((RedisCallback<Long>) con -> con.incrBy(key.getBytes(), value));
    }

    public Long kDecr(String key, long value) {
        return template.execute((RedisCallback<Long>) con -> con.decrBy(key.getBytes(), value));
    }

    /**
     * 实现计数的加/减1
     *
     * @param key
     * @return 返回redis中的值
     */
    public Long kIncr(String key) {
        return kIncr(key, 1);
    }

    public Long kDecr(String key) {
        return kDecr(key, 1);
    }
    /*---------------------------------list----------------------------------------------------*/

    /**
     * 在列表的最左边塞入一个value
     *
     * @param key
     * @param value
     */
    public void lLeftPush(String key, T value) {
        template.opsForList().leftPush(key, value);
    }

    /**
     * 在列表的最右边塞入一个value
     *
     * @param key
     * @param value
     */
    public void lRightPush(String key, T value) {
        template.opsForList().rightPush(key, value);
    }

    public void lLeftPushAll(String key, Collection<T> values) {
        template.opsForList().leftPushAll(key, values);
    }

    public void lRightPushAll(String key, Collection<T> values) {
        template.opsForList().rightPushAll(key, values);
    }

    public void lLeftPushIfPresent(String key, T value) {
        template.opsForList().leftPushIfPresent(key, value);
    }

    /**
     * 在列表的最右边塞入一个value 如果存在该value这放弃存储
     *
     * @param key
     * @param value
     */
    public void lRightPushIfPresent(String key, T value) {
        template.opsForList().rightPushIfPresent(key, value);
    }

    /**
     * 获取指定索引位置的值, index为-1时，表示返回的是最后一个；当index大于实际的列表长度时，返回null
     *
     * @param key
     * @param index
     * @return
     */
    public T lIndex(String key, int index) {
        return template.opsForList().index(key, index);
    }

    /**
     * 获取范围值，闭区间，start和end这两个下标的值都会返回; end为-1时，表示获取的是最后一个；
     * <p>
     * 如果希望返回最后两个元素，可以传入  -2, -1
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<T> lRange(String key, int start, int end) {
        return template.opsForList().range(key, start, end);
    }

    /**
     * 返回列表的长度
     *
     * @param key
     * @return
     */
    public Long lSize(String key) {
        return template.opsForList().size(key);
    }

    /**
     * 设置list中指定下标的值，采用干的是替换规则, 最左边的下标为0；-1表示最右边的一个
     *
     * @param key
     * @param index
     * @param value
     */
    public void lSet(String key, Integer index, T value) {
        template.opsForList().set(key, index, value);
    }

    /**
     * 删除列表中值为value的元素，总共删除count次；
     * <p>
     * 如原来列表为 【1， 2， 3， 4， 5， 2， 1， 2， 5】 传入参数 value=2, count=1 表示删除一个列表中value为2的元素 则执行后，列表为 【1， 3， 4， 5， 2， 1， 2， 5】
     *
     * @param key
     * @param value
     * @param count
     */
    public void lRemove(String key, T value, int count) {
        template.opsForList().remove(key, count, value);
    }

    /**
     * 删除list首尾，只保留 [start, end] 之间的值
     *
     * @param key
     * @param start
     * @param end
     */
    public void lTrim(String key, Integer start, Integer end) {
        template.opsForList().trim(key, start, end);
    }


    /*---------------------------------hash----------------------------------------------------*/

    /**
     * 向hash 添加 键值对
     *
     * @param key hash 名
     * @param hk  hash中的键
     * @param hv  hash中的值
     */
    public void hPut(String key, String hk, T hv) {
        template.<String, T>opsForHash().put(key, hk, hv);
    }

    /**
     * 向hash中添加键值对
     *
     * @param key hash名
     * @param map 需要保存的Map
     */
    public void hPut(String key, Map<String, T> map) {
        template.<String, T>opsForHash().putAll(key, map);
    }

    /**
     * 获取hash中key对应的值
     *
     * @param key   hash名
     * @param field hash中的键
     * @return 返回key对应value
     */
    public T hGet(String key, String field) {
        return template.<String, T>opsForHash().get(key, field);
    }

    /**
     * 获取hash中key对应的值
     *
     * @param key   hash名
     * @param field hash中的键
     * @return 返回key对应value
     */
    public T hGetOrDefault(String key, String field, T defaultValue) {
        T value = template.<String, T>opsForHash().get(key, field);
        if (ObjectUtils.isEmpty(value)) {
            return value;
        }
        return defaultValue;
    }

    /**
     * 删除hash中field这一对kv
     */
    public void hDel(String key, String field) {
        template.opsForHash().delete(key, field);
    }

    /**
     * 获取hash的左右key value
     *
     * @param key hash 名
     * @return 所有键值对
     */
    public Map<String, T> hGetAll(String key) {
        return template.<String, T>opsForHash().entries(key);
    }

    /**
     * 获取hash的左右key value
     *
     * @param key hash 名
     * @return 所有键值对
     */
    public Map<String, T> hGetAllOrDefault(String key, Map<String, T> defaultMap) {
        Map<String, T> entries = template.<String, T>opsForHash().entries(key);
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
    public List<T> hGetAllValue(String key) {
        return template.<String, T>opsForHash().values(key);
    }

    /**
     * 根据多个field 获取对应value
     *
     * @param key    hash名
     * @param fields hash中的键
     * @return 对应的value值
     */
    public List<T> hmGet(String key, List<String> fields) {
        return template.<String, T>opsForHash().multiGet(key, fields);
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
        return template.opsForHash().increment(key, field, value);
    }

    /*---------------------------------set----------------------------------------------------*/

    /**
     * 新增一个  sadd
     *
     * @param key
     * @param value
     */
    public long sAdd(String key, T value) {
        return template.opsForSet().add(key, value);
    }

    /**
     * 删除集合中的值  srem
     *
     * @param key
     * @param value
     */
    public void sRemove(String key, String value) {
        template.opsForSet().remove(key, value);
    }

    /**
     * 判断是否包含  sismember
     *
     * @param key
     * @param value
     */
    public void sContains(String key, String value) {
        template.opsForSet().isMember(key, value);
    }

    /**
     * 获取集合中所有的值 smembers
     *
     * @param key
     * @return
     */
    public Set<T> sValues(String key) {
        return template.opsForSet().members(key);
    }

    /**
     * 返回多个集合的并集  sunion
     *
     * @param key1
     * @param key2
     * @return
     */
    public Set<T> sUnion(String key1, String key2) {
        return template.opsForSet().union(key1, key2);
    }

    /**
     * 返回多个集合的交集 sinter
     *
     * @param key1
     * @param key2
     * @return
     */
    public Set<T> sIntersect(String key1, String key2) {
        return template.opsForSet().intersect(key1, key2);
    }

    /**
     * 返回集合key1中存在，但是key2中不存在的数据集合  sdiff
     *
     * @param key1
     * @param key2
     * @return
     */
    public Set<T> sDiff(String key1, String key2) {
        return template.opsForSet().difference(key1, key2);
    }

    /*---------------------------------zset----------------------------------------------------*/

    /**
     * 添加一个元素, zset与set最大的区别就是每个元素都有一个score，因此有个排序的辅助功能;  zadd
     *
     * @param key
     * @param value
     * @param score
     */
    public void zAdd(String key, T value, double score) {
        template.opsForZSet().add(key, value, score);
    }

    /**
     * 删除元素 zrem
     *
     * @param key
     * @param value
     */
    public void zRemove(String key, T value) {
        template.opsForZSet().remove(key, value);
    }

    /**
     * score的增加or减少 zincrby
     *
     * @param key
     * @param value
     * @param score
     */
    public Double zIncrScore(String key, T value, double score) {
        return template.opsForZSet().incrementScore(key, value, score);
    }

    /**
     * 查询value对应的score   zscore
     *
     * @param key
     * @param value
     * @return
     */
    public Double zScore(String key, T value) {
        return template.opsForZSet().score(key, value);
    }

    /**
     * 判断value在zset中的排名  zrank
     *
     * @param key
     * @param value
     * @return
     */
    public Long zRank(String key, T value) {
        return template.opsForZSet().rank(key, value);
    }

    /**
     * 返回集合的长度
     *
     * @param key
     * @return
     */
    public Long zSize(String key) {
        return template.opsForZSet().zCard(key);
    }

    /**
     * 查询集合中指定顺序的值， 0 -1 表示获取全部的集合内容  zrange
     * <p>
     * 返回有序的集合，score小的在前面
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<T> zRange(String key, int start, int end) {
        return template.opsForZSet().range(key, start, end);
    }

    /**
     * 查询集合中指定顺序的值和score，0, -1 表示获取全部的集合内容
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<T>> zRangeWithScore(String key, int start, int end) {
        return template.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * 查询集合中指定顺序的值  zrevrange
     * <p>
     * 返回有序的集合中，score大的在前面
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<T> zRevRange(String key, int start, int end) {
        return template.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 根据score的值，来获取满足条件的集合  zrangebyscore
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    /**
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<T> zSortRange(String key, int min, int max) {
        return template.opsForZSet().rangeByScore(key, min, max);
    }
}
