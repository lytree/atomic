package top.lytree.test;

import java.util.Set;
import org.junit.jupiter.api.Test;
import top.lytree.redis.RedisString;

public class RedisStringTest {

    public static RedisString redis = new RedisString("10.100.0.115", 6379);

    @Test
    public void testZRange() {
        RedisString redis = RedisStringTest.redis;
        redis.zAdd("score", "1", 1);
        redis.zAdd("score", "2", 2);
        redis.zAdd("score", "3", 3);
        redis.zAdd("score", "4", 4);
        redis.zAdd("score", "5", 5);
        redis.zAdd("score", "6", 6);
        redis.zAdd("score", "7", 7);
        redis.zAdd("score", "8", 8);
        Set<String> score = redis.zRange("score", 7, -1);
        System.out.println(score);
    }
}
