package top.yang.redis.string;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisKey {

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  /**
   * 设置并获取之间的结果，要求key，value都不能为空；如果之前没有值，返回null
   *
   * @param key
   * @param value
   * @return
   */
  public void set(String key, String value) {
    stringRedisTemplate.opsForValue().set(key, value);
  }

  public void set(String key, String value, long time) {
    stringRedisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
  }

  public String get(String key, String value) {
   return stringRedisTemplate.opsForValue().get(key);
  }

  // 自增、自减方式实现计数

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
}
