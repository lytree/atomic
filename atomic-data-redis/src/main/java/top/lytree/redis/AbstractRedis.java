package top.lytree.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class AbstractRedis<T> {

    protected final RedisTemplate<String, T> template;


    public AbstractRedis(String host, Integer port) {
        this(host, port, null);
    }

    public AbstractRedis(RedisTemplate<String, T> redisTemplate) {
        this.template = redisTemplate;
    }

    public AbstractRedis(String host, Integer port, String password) {
        template = new RedisTemplate<>();
        init(host, port, password);

    }

    protected void init(String host, Integer port, String password) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        if (StringUtils.hasText(password)) {
            configuration.setPassword(password);
        }
        RedisSerializer serializer = getRedisSerializer();
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(configuration);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        // 默认使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        // Hash的key也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        lettuceConnectionFactory.afterPropertiesSet();
        template.setConnectionFactory(lettuceConnectionFactory);
        template.afterPropertiesSet();
    }

    /**
     * 获取redis中的key
     *
     * @return
     */
    public List<String> getAllKey(String pattern) {
        return new ArrayList<>(template.keys(pattern));
    }

    /**
     * 获取redis中的所有key
     *
     * @return
     */
    public List<String> getAllKey() {
        return new ArrayList<>(template.keys("*"));
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                template.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return template.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return template.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean delete(final String key) {
        return Boolean.TRUE.equals(template.delete(key));
    }

    protected RedisSerializer<Object> getRedisSerializer() {
        return new Jackson2JsonRedisSerializer<>(Object.class);
    }
}
