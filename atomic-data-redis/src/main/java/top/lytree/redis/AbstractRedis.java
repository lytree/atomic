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

    public List<String> getAllKey() {
        return new ArrayList<>(template.keys("*"));
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
