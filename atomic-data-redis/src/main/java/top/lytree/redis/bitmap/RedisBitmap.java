package top.lytree.redis.bitmap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisBitmap {

    @Autowired
    private RedisTemplate redisTemplate;

    public Boolean setBit(String key, Integer index, Boolean tag) {
        return (Boolean) redisTemplate.execute((RedisCallback<Boolean>) con -> con.setBit(key.getBytes(), index, tag));
    }

    public Boolean getBit(String key, Integer index) {
        return (Boolean) redisTemplate.execute((RedisCallback<Boolean>) con -> con.getBit(key.getBytes(), index));
    }

    /**
     * 统计bitmap中，value为1的个数，非常适用于统计网站的每日活跃用户数等类似的场景
     *
     * @param key
     * @return
     */
    public Long bitCount(String key) {
        return (Long) redisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes()));
    }

    public Long bitCount(String key, int start, int end) {
        return (Long) redisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes(), start, end));
    }

    public Long bitOp(RedisStringCommands.BitOperation op, String saveKey, String... desKey) {
        byte[][] bytes = new byte[desKey.length][];
        for (int i = 0; i < desKey.length; i++) {
            bytes[i] = desKey[i].getBytes();
        }
        return (Long) redisTemplate.execute((RedisCallback<Long>) con -> con.bitOp(op, saveKey.getBytes(), bytes));
    }

    /**
     * 判断是否标记过
     *
     * @param key
     * @param offest
     * @return
     */
    public Boolean container(String key, long offest) {
        return redisTemplate.opsForValue().getBit(key, offest);
    }
}
