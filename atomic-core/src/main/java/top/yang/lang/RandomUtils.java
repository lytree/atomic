/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.yang.lang;

import top.yang.validator.Validate;
import top.yang.string.StringUtils;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author pride
 * @since 3.3
 */
public class RandomUtils extends org.apache.commons.lang3.RandomUtils {

    /**
     * 用于随机选的数字
     */
    public static final String BASE_NUMBER = "0123456789";
    /**
     * 用于随机选的字符
     */
    public static final String BASE_CHAR = "abcdefghijklmnopqrstuvwxyz";
    /**
     * 用于随机选的字符和数字
     */
    public static final String BASE_CHAR_NUMBER = BASE_CHAR + BASE_NUMBER;

    private static ThreadLocalRandom random() {
        return ThreadLocalRandom.current();
    }

    /**
     * 创建{@link SecureRandom}，类提供加密的强随机数生成器 (RNG)<br>
     *
     * @param seed 自定义随机种子
     * @return {@link SecureRandom}
     * @since 4.6.5
     */
    public static SecureRandom createSecureRandom(byte[] seed) {
        return (null == seed) ? new SecureRandom() : new SecureRandom(seed);
    }

    /**
     * 获取SHA1PRNG的{@link SecureRandom}，类提供加密的强随机数生成器 (RNG)<br> 注意：此方法获取的是伪随机序列发生器PRNG（pseudo-random number generator）
     *
     * <p>
     * 相关说明见：https://stackoverflow.com/questions/137212/how-to-solve-slow-java-securerandom
     *
     * @return {@link SecureRandom}
     * @since 3.1.2
     */
    public static SecureRandom getSecureRandom() {
        return getSecureRandom(null);
    }

    /**
     * 获取SHA1PRNG的{@link SecureRandom}，类提供加密的强随机数生成器 (RNG)<br> 注意：此方法获取的是伪随机序列发生器PRNG（pseudo-random number generator）
     *
     * <p>
     * 相关说明见：https://stackoverflow.com/questions/137212/how-to-solve-slow-java-securerandom
     *
     * @param seed 随机数种子
     * @return {@link SecureRandom}
     * @see #createSecureRandom(byte[])
     * @since 5.5.2
     */
    public static SecureRandom getSecureRandom(byte[] seed) {
        return createSecureRandom(seed);
    }


    /**
     * 获取随机数产生器
     *
     * @param isSecure 是否为强随机数生成器 (RNG)
     * @return {@link Random}
     * @see #getSecureRandom()
     * @since 4.1.15
     */
    public static Random randomThreadLocal(boolean isSecure) {
        return isSecure ? getSecureRandom() : random();
    }

    /**
     * 获得随机Boolean值
     *
     * @return true or false
     * @since 4.5.9
     */
    public static boolean nextBooleanThreadLocal() {
        return random().nextBoolean();
    }

    /**
     * 获得指定范围内的随机数
     *
     * @param min 最小数（包含）
     * @param max 最大数（不包含）
     * @return 随机数
     */
    public static int nextIntThreadLocal(int min, int max) {
        return random().nextInt(min, max);
    }

    /**
     * 获得随机数int值
     *
     * @return 随机数
     */
    public static int nextIntThreadLocal() {
        return random().nextInt();
    }

    /**
     * 获得指定范围内的随机数 [0,limit)
     *
     * @param limit 限制随机数的范围，不包括这个数
     * @return 随机数
     */
    public static int nextIntThreadLocal(int limit) {
        return random().nextInt(limit);
    }

    /**
     * 获得指定范围内的随机数[min, max)
     *
     * @param min 最小数（包含）
     * @param max 最大数（不包含）
     * @return 随机数
     * @since 3.3.0
     */
    public static long nextLongThreadLocal(long min, long max) {
        return random().nextLong(min, max);
    }

    /**
     * 获得随机数
     *
     * @return 随机数
     * @since 3.3.0
     */
    public static long nextLongThreadLocal() {
        return random().nextLong();
    }

    /**
     * 获得指定范围内的随机数 [0,limit)
     *
     * @param limit 限制随机数的范围，不包括这个数
     * @return 随机数
     */
    public static long nextLongThreadLocal(long limit) {
        return random().nextLong(limit);
    }

    /**
     * 获得指定范围内的随机数
     *
     * @param min 最小数（包含）
     * @param max 最大数（不包含）
     * @return 随机数
     * @since 3.3.0
     */
    public static double nextDoubleThreadLocal(double min, double max) {
        return random().nextDouble(min, max);
    }


    /**
     * 获得随机数[0, 1)
     *
     * @return 随机数
     * @since 3.3.0
     */
    public static double nextDoubleThreadLocal() {
        return random().nextDouble();
    }


    /**
     * 获得指定范围内的随机数 [0,limit)
     *
     * @param limit 限制随机数的范围，不包括这个数
     * @return 随机数
     * @since 3.3.0
     */
    public static double nextDoubleThreadLocal(double limit) {
        return random().nextDouble(limit);
    }

    /**
     * 随机bytes
     *
     * @param length 长度
     * @return bytes
     */
    public static byte[] nextBytesThreadLocal(int length) {
        byte[] bytes = new byte[length];
        random().nextBytes(bytes);
        return bytes;
    }

    /**
     * 随机数字，数字为0~9单个数字
     *
     * @return 随机数字字符
     * @since 3.1.2
     */
    public static char nextNumberThreadLocal() {
        return nextCharThreadLocal(BASE_NUMBER);
    }

    /**
     * 随机字母或数字，小写
     *
     * @return 随机字符
     * @since 3.1.2
     */
    public static char nextCharThreadLocal() {
        return nextCharThreadLocal(BASE_CHAR_NUMBER);
    }

    /**
     * 随机字符
     *
     * @param baseString 随机字符选取的样本
     * @return 随机字符
     * @since 3.1.2
     */
    public static char nextCharThreadLocal(String baseString) {
        return baseString.charAt(nextIntThreadLocal(baseString.length()));
    }

    /**
     * <p>
     * {@code RandomUtils} instances should NOT be constructed in standard programming. Instead, the class should be used as {@code RandomUtils.nextBytes(5);}.
     * </p>
     *
     * <p>
     * This constructor is public to permit tools that require a JavaBean instance to operate.
     * </p>
     */
    public RandomUtils() {
    }
}
