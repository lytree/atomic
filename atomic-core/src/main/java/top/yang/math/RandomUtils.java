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
package top.yang.math;

import top.yang.validator.Validate;
import top.yang.string.StringUtils;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>Utility library that supplements the standard {@link Random} class.</p>
 *
 * <p>Caveat: Instances of {@link Random} are not cryptographically secure.</p>
 *
 * <p>Please note that the Apache Commons project provides a component
 * dedicated to pseudo-random number generation, namely
 * <a href="https://commons.apache.org/rng">Commons RNG</a>, that may be
 * a better choice for applications with more stringent requirements (performance and/or correctness).</p>
 *
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

    /**
     * <p>
     * Returns a random boolean value
     * </p>
     *
     * @return the random boolean
     * @since 3.5
     */
    public static boolean nextBoolean() {
        return random().nextBoolean();
    }

    /**
     * <p>
     * Creates an array of random bytes.
     * </p>
     *
     * @param count the size of the returned array
     * @return the random byte array
     * @throws IllegalArgumentException if {@code count} is negative
     */
    public static byte[] nextBytes(final int count) {
        Validate.isTrue(count >= 0, "Count cannot be negative.");

        final byte[] result = new byte[count];
        random().nextBytes(result);
        return result;
    }

    /**
     * <p> Returns a random double within 0 - Double.MAX_VALUE </p>
     *
     * @return the random double
     * @see #nextDouble(double, double)
     * @since 3.5
     */
    public static double nextDouble() {
        return nextDouble(0, Double.MAX_VALUE);
    }

    /**
     * <p>
     * Returns a random double within the specified range.
     * </p>
     *
     * @param startInclusive the smallest value that can be returned, must be non-negative
     * @param endExclusive   the upper bound (not included)
     * @return the random double
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive} or if {@code startInclusive} is negative
     */
    public static double nextDouble(final double startInclusive, final double endExclusive) {
        Validate.isTrue(endExclusive >= startInclusive,
                "Start value must be smaller or equal to end value.");
        Validate.isTrue(startInclusive >= 0, "Both range values must be non-negative.");

        if (startInclusive == endExclusive) {
            return startInclusive;
        }

        return startInclusive + ((endExclusive - startInclusive) * random().nextDouble());
    }

    /**
     * <p> Returns a random float within 0 - Float.MAX_VALUE </p>
     *
     * @return the random float
     * @see #nextFloat(float, float)
     * @since 3.5
     */
    public static float nextFloat() {
        return nextFloat(0, Float.MAX_VALUE);
    }

    /**
     * <p>
     * Returns a random float within the specified range.
     * </p>
     *
     * @param startInclusive the smallest value that can be returned, must be non-negative
     * @param endExclusive   the upper bound (not included)
     * @return the random float
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive} or if {@code startInclusive} is negative
     */
    public static float nextFloat(final float startInclusive, final float endExclusive) {
        Validate.isTrue(endExclusive >= startInclusive,
                "Start value must be smaller or equal to end value.");
        Validate.isTrue(startInclusive >= 0, "Both range values must be non-negative.");

        if (startInclusive == endExclusive) {
            return startInclusive;
        }

        return startInclusive + ((endExclusive - startInclusive) * random().nextFloat());
    }

    /**
     * <p> Returns a random int within 0 - Integer.MAX_VALUE </p>
     *
     * @return the random integer
     * @see #nextInt(int, int)
     * @since 3.5
     */
    public static int nextInt() {
        return nextInt(0, Integer.MAX_VALUE);
    }

    /**
     * <p>
     * Returns a random integer within the specified range.
     * </p>
     *
     * @param startInclusive the smallest value that can be returned, must be non-negative
     * @param endExclusive   the upper bound (not included)
     * @return the random integer
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive} or if {@code startInclusive} is negative
     */
    public static int nextInt(final int startInclusive, final int endExclusive) {
        Validate.isTrue(endExclusive >= startInclusive,
                "Start value must be smaller or equal to end value.");
        Validate.isTrue(startInclusive >= 0, "Both range values must be non-negative.");

        if (startInclusive == endExclusive) {
            return startInclusive;
        }

        return startInclusive + random().nextInt(endExclusive - startInclusive);
    }

    /**
     * <p> Returns a random long within 0 - Long.MAX_VALUE </p>
     *
     * @return the random long
     * @see #nextLong(long, long)
     * @since 3.5
     */
    public static long nextLong() {
        return nextLong(Long.MAX_VALUE);
    }

    /**
     * Generates a {@code long} value between 0 (inclusive) and the specified value (exclusive).
     *
     * @param n Bound on the random number to be returned.  Must be positive.
     * @return a random {@code long} value between 0 (inclusive) and {@code n} (exclusive).
     */
    private static long nextLong(final long n) {
        // Extracted from o.a.c.rng.core.BaseProvider.nextLong(long)
        long bits;
        long val;
        do {
            bits = random().nextLong() >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1) < 0);

        return val;
    }

    /**
     * <p>
     * Returns a random long within the specified range.
     * </p>
     *
     * @param startInclusive the smallest value that can be returned, must be non-negative
     * @param endExclusive   the upper bound (not included)
     * @return the random long
     * @throws IllegalArgumentException if {@code startInclusive > endExclusive} or if {@code startInclusive} is negative
     */
    public static long nextLong(final long startInclusive, final long endExclusive) {
        Validate.isTrue(endExclusive >= startInclusive,
                "Start value must be smaller or equal to end value.");
        Validate.isTrue(startInclusive >= 0, "Both range values must be non-negative.");

        if (startInclusive == endExclusive) {
            return startInclusive;
        }

        return startInclusive + nextLong(endExclusive - startInclusive);
    }

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
    public static Random random(boolean isSecure) {
        return isSecure ? getSecureRandom() : random();
    }

    /**
     * 获得随机Boolean值
     *
     * @return true or false
     * @since 4.5.9
     */
    public static boolean randomBoolean() {
        return 0 == randomInt(2);
    }

    /**
     * 获得指定范围内的随机数
     *
     * @param min 最小数（包含）
     * @param max 最大数（不包含）
     * @return 随机数
     */
    public static int randomInt(int min, int max) {
        return random().nextInt(min, max);
    }

    /**
     * 获得随机数int值
     *
     * @return 随机数
     */
    public static int randomInt() {
        return random().nextInt();
    }

    /**
     * 获得指定范围内的随机数 [0,limit)
     *
     * @param limit 限制随机数的范围，不包括这个数
     * @return 随机数
     */
    public static int randomInt(int limit) {
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
    public static long randomLong(long min, long max) {
        return random().nextLong(min, max);
    }

    /**
     * 获得随机数
     *
     * @return 随机数
     * @since 3.3.0
     */
    public static long randomLong() {
        return random().nextLong();
    }

    /**
     * 获得指定范围内的随机数 [0,limit)
     *
     * @param limit 限制随机数的范围，不包括这个数
     * @return 随机数
     */
    public static long randomLong(long limit) {
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
    public static double randomDouble(double min, double max) {
        return random().nextDouble(min, max);
    }


    /**
     * 获得随机数[0, 1)
     *
     * @return 随机数
     * @since 3.3.0
     */
    public static double randomDouble() {
        return random().nextDouble();
    }


    /**
     * 获得指定范围内的随机数 [0,limit)
     *
     * @param limit 限制随机数的范围，不包括这个数
     * @return 随机数
     * @since 3.3.0
     */
    public static double randomDouble(double limit) {
        return random().nextDouble(limit);
    }

    /**
     * 随机bytes
     *
     * @param length 长度
     * @return bytes
     */
    public static byte[] randomBytes(int length) {
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
    public static char randomNumber() {
        return randomChar(BASE_NUMBER);
    }

    /**
     * 随机字母或数字，小写
     *
     * @return 随机字符
     * @since 3.1.2
     */
    public static char randomChar() {
        return randomChar(BASE_CHAR_NUMBER);
    }

    /**
     * 随机字符
     *
     * @param baseString 随机字符选取的样本
     * @return 随机字符
     * @since 3.1.2
     */
    public static char randomChar(String baseString) {
        return baseString.charAt(randomInt(baseString.length()));
    }
    // Random
    //-----------------------------------------------------------------------

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     *
     * <p>Characters will be chosen from the set of all characters.</p>
     *
     * @param count the length of random string to create
     * @return the random string
     */
    public static String random(final int count) {
        return random(count, false, false);
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     *
     * <p>Characters will be chosen from the set of alpha-numeric
     * characters as indicated by the arguments.</p>
     *
     * @param count   the length of random string to create
     * @param letters if {@code true}, generated string may include alphabetic characters
     * @param numbers if {@code true}, generated string may include numeric characters
     * @return the random string
     */
    public static String random(final int count, final boolean letters, final boolean numbers) {
        return random(count, 0, 0, letters, numbers);
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     *
     * <p>Characters will be chosen from the set of characters specified.</p>
     *
     * @param count the length of random string to create
     * @param chars the character array containing the set of characters to use, may be null
     * @return the random string
     * @throws IllegalArgumentException if {@code count} &lt; 0.
     */
    public static String random(final int count, final char... chars) {
        if (chars == null) {
            return random(count, 0, 0, false, false, null, random());
        }
        return random(count, 0, chars.length, false, false, chars, random());
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     *
     * <p>Characters will be chosen from the set of alpha-numeric
     * characters as indicated by the arguments.</p>
     *
     * @param count   the length of random string to create
     * @param start   the position in set of chars to start at
     * @param end     the position in set of chars to end before
     * @param letters if {@code true}, generated string may include alphabetic characters
     * @param numbers if {@code true}, generated string may include numeric characters
     * @return the random string
     */
    public static String random(final int count, final int start, final int end, final boolean letters, final boolean numbers) {
        return random(count, start, end, letters, numbers, null, random());
    }

    /**
     * <p>Creates a random string based on a variety of options, using
     * default source of randomness.</p>
     *
     * <p>This method has exactly the same semantics as
     * {@link #random(int, int, int, boolean, boolean, char[], Random)}, but instead of using an externally supplied source of randomness, it uses the internal static {@link
     * Random} instance.</p>
     *
     * @param count   the length of random string to create
     * @param start   the position in set of chars to start at
     * @param end     the position in set of chars to end before
     * @param letters if {@code true}, generated string may include alphabetic characters
     * @param numbers if {@code true}, generated string may include numeric characters
     * @param chars   the set of chars to choose randoms from. If {@code null}, then it will use the set of all chars.
     * @return the random string
     * @throws ArrayIndexOutOfBoundsException if there are not {@code (end - start) + 1} characters in the set array.
     */
    public static String random(final int count, final int start, final int end, final boolean letters, final boolean numbers, final char... chars) {
        return random(count, start, end, letters, numbers, chars, random());
    }

    /**
     * <p>Creates a random string based on a variety of options, using
     * supplied source of randomness.</p>
     *
     * <p>If start and end are both {@code 0}, start and end are set
     * to {@code ' '} and {@code 'z'}, the ASCII printable characters, will be used, unless letters and numbers are both {@code false}, in which case, start and end are set to
     * {@code 0} and {@link Character#MAX_CODE_POINT}.
     *
     * <p>If set is not {@code null}, characters between start and
     * end are chosen.</p>
     *
     * <p>This method accepts a user-supplied {@link Random}
     * instance to use as a source of randomness. By seeding a single {@link Random} instance with a fixed seed and using it for each call, the same random sequence of strings can
     * be generated repeatedly and predictably.</p>
     *
     * @param count   the length of random string to create
     * @param start   the position in set of chars to start at (inclusive)
     * @param end     the position in set of chars to end before (exclusive)
     * @param letters if {@code true}, generated string may include alphabetic characters
     * @param numbers if {@code true}, generated string may include numeric characters
     * @param chars   the set of chars to choose randoms from, must not be empty. If {@code null}, then it will use the set of all chars.
     * @param random  a source of randomness.
     * @return the random string
     * @throws ArrayIndexOutOfBoundsException if there are not {@code (end - start) + 1} characters in the set array.
     * @throws IllegalArgumentException       if {@code count} &lt; 0 or the provided chars array is empty.
     * @since 2.0
     */
    public static String random(int count, int start, int end, final boolean letters, final boolean numbers,
            final char[] chars, final Random random) {
        if (count == 0) {
            return StringUtils.EMPTY;
        }
        if (count < 0) {
            throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
        }
        if (chars != null && chars.length == 0) {
            throw new IllegalArgumentException("The chars array must not be empty");
        }

        if (start == 0 && end == 0) {
            if (chars != null) {
                end = chars.length;
            } else if (!letters && !numbers) {
                end = Character.MAX_CODE_POINT;
            } else {
                end = 'z' + 1;
                start = ' ';
            }
        } else if (end <= start) {
            throw new IllegalArgumentException("Parameter end (" + end + ") must be greater than start (" + start + ")");
        }

        final int zero_digit_ascii = 48;
        final int first_letter_ascii = 65;

        if (chars == null && (numbers && end <= zero_digit_ascii
                || letters && end <= first_letter_ascii)) {
            throw new IllegalArgumentException("Parameter end (" + end + ") must be greater then (" + zero_digit_ascii + ") for generating digits " +
                    "or greater then (" + first_letter_ascii + ") for generating letters.");
        }

        final StringBuilder builder = new StringBuilder(count);
        final int gap = end - start;

        while (count-- != 0) {
            final int codePoint;
            if (chars == null) {
                codePoint = random.nextInt(gap) + start;

                switch (Character.getType(codePoint)) {
                    case Character.UNASSIGNED:
                    case Character.PRIVATE_USE:
                    case Character.SURROGATE:
                        count++;
                        continue;
                }

            } else {
                codePoint = chars[random.nextInt(gap) + start];
            }

            final int numberOfChars = Character.charCount(codePoint);
            if (count == 0 && numberOfChars > 1) {
                count++;
                continue;
            }

            if (letters && Character.isLetter(codePoint)
                    || numbers && Character.isDigit(codePoint)
                    || !letters && !numbers) {
                builder.appendCodePoint(codePoint);

                if (numberOfChars == 2) {
                    count--;
                }

            } else {
                count++;
            }
        }
        return builder.toString();
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     *
     * <p>Characters will be chosen from the set of characters
     * specified by the string, must not be empty. If null, the set of all characters is used.</p>
     *
     * @param count the length of random string to create
     * @param chars the String containing the set of characters to use, may be null, but must not be empty
     * @return the random string
     * @throws IllegalArgumentException if {@code count} &lt; 0 or the string is empty.
     */
    public static String random(final int count, final String chars) {
        if (chars == null) {
            return random(count, 0, 0, false, false, null, random());
        }
        return random(count, chars.toCharArray());
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     *
     * <p>Characters will be chosen from the set of Latin alphabetic
     * characters (a-z, A-Z).</p>
     *
     * @param count the length of random string to create
     * @return the random string
     */
    public static String randomAlphabetic(final int count) {
        return random(count, true, false);
    }

    /**
     * <p>Creates a random string whose length is between the inclusive minimum and
     * the exclusive maximum.</p>
     *
     * <p>Characters will be chosen from the set of Latin alphabetic characters (a-z, A-Z).</p>
     *
     * @param minLengthInclusive the inclusive minimum length of the string to generate
     * @param maxLengthExclusive the exclusive maximum length of the string to generate
     * @return the random string
     * @since 3.5
     */
    public static String randomAlphabetic(final int minLengthInclusive, final int maxLengthExclusive) {
        return randomAlphabetic(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     *
     * <p>Characters will be chosen from the set of Latin alphabetic
     * characters (a-z, A-Z) and the digits 0-9.</p>
     *
     * @param count the length of random string to create
     * @return the random string
     */
    public static String randomAlphanumeric(final int count) {
        return random(count, true, true);
    }

    /**
     * <p>Creates a random string whose length is between the inclusive minimum and
     * the exclusive maximum.</p>
     *
     * <p>Characters will be chosen from the set of Latin alphabetic
     * characters (a-z, A-Z) and the digits 0-9.</p>
     *
     * @param minLengthInclusive the inclusive minimum length of the string to generate
     * @param maxLengthExclusive the exclusive maximum length of the string to generate
     * @return the random string
     * @since 3.5
     */
    public static String randomAlphanumeric(final int minLengthInclusive, final int maxLengthExclusive) {
        return randomAlphanumeric(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     *
     * <p>Characters will be chosen from the set of characters whose
     * ASCII value is between {@code 32} and {@code 126} (inclusive).</p>
     *
     * @param count the length of random string to create
     * @return the random string
     */
    public static String randomAscii(final int count) {
        return random(count, 32, 127, false, false);
    }

    /**
     * <p>Creates a random string whose length is between the inclusive minimum and
     * the exclusive maximum.</p>
     *
     * <p>Characters will be chosen from the set of characters whose
     * ASCII value is between {@code 32} and {@code 126} (inclusive).</p>
     *
     * @param minLengthInclusive the inclusive minimum length of the string to generate
     * @param maxLengthExclusive the exclusive maximum length of the string to generate
     * @return the random string
     * @since 3.5
     */
    public static String randomAscii(final int minLengthInclusive, final int maxLengthExclusive) {
        return randomAscii(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * <p>Creates a random string whose length is the number of characters specified.</p>
     *
     * <p>Characters will be chosen from the set of characters which match the POSIX [:graph:]
     * regular expression character class. This class contains all visible ASCII characters (i.e. anything except spaces and control characters).</p>
     *
     * @param count the length of random string to create
     * @return the random string
     * @since 3.5
     */
    public static String randomGraph(final int count) {
        return random(count, 33, 126, false, false);
    }

    /**
     * <p>Creates a random string whose length is between the inclusive minimum and
     * the exclusive maximum.</p>
     *
     * <p>Characters will be chosen from the set of \p{Graph} characters.</p>
     *
     * @param minLengthInclusive the inclusive minimum length of the string to generate
     * @param maxLengthExclusive the exclusive maximum length of the string to generate
     * @return the random string
     * @since 3.5
     */
    public static String randomGraph(final int minLengthInclusive, final int maxLengthExclusive) {
        return randomGraph(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     *
     * <p>Characters will be chosen from the set of numeric
     * characters.</p>
     *
     * @param count the length of random string to create
     * @return the random string
     */
    public static String randomNumeric(final int count) {
        return random(count, false, true);
    }

    /**
     * <p>Creates a random string whose length is between the inclusive minimum and
     * the exclusive maximum.</p>
     *
     * <p>Characters will be chosen from the set of \p{Digit} characters.</p>
     *
     * @param minLengthInclusive the inclusive minimum length of the string to generate
     * @param maxLengthExclusive the exclusive maximum length of the string to generate
     * @return the random string
     * @since 3.5
     */
    public static String randomNumeric(final int minLengthInclusive, final int maxLengthExclusive) {
        return randomNumeric(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * <p>Creates a random string whose length is the number of characters specified.</p>
     *
     * <p>Characters will be chosen from the set of characters which match the POSIX [:print:]
     * regular expression character class. This class includes all visible ASCII characters and spaces (i.e. anything except control characters).</p>
     *
     * @param count the length of random string to create
     * @return the random string
     * @since 3.5
     */
    public static String randomPrint(final int count) {
        return random(count, 32, 126, false, false);
    }


    /**
     * <p>Creates a random string whose length is between the inclusive minimum and
     * the exclusive maximum.</p>
     *
     * <p>Characters will be chosen from the set of \p{Print} characters.</p>
     *
     * @param minLengthInclusive the inclusive minimum length of the string to generate
     * @param maxLengthExclusive the exclusive maximum length of the string to generate
     * @return the random string
     * @since 3.5
     */
    public static String randomPrint(final int minLengthInclusive, final int maxLengthExclusive) {
        return randomPrint(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
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
