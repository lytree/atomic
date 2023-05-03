package top.lytree.lang;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author pride
 */
public class RandomStringUtils {

    private static ThreadLocalRandom random() {
        return ThreadLocalRandom.current();
    }


    /**
     * <p>{@code RandomStringUtils} 不应该在标准编程中构造实例。相反，这个类应该被用作{@code RandomStringUtils.random(5);}。</p>
     *
     * <p>这个构造函数是公共的，允许需要JavaBean实例操作的工具。</p>
     */
    public RandomStringUtils() {
    }

    // Random
    //-----------------------------------------------------------------------

    /**
     * <p>创建一个随机字符串，其长度为指定的字符数。</p>
     *
     * <p>字符将从所有字符集中选择。</p>
     *
     * @param count 要创建的随机字符串的长度
     * @return 随机字符串
     */
    public static String randomThreadLocal(final int count) {
        return randomThreadLocal(count, false, false);
    }

    /**
     * <p>创建一个随机字符串，其长度为指定的字符数。</p>
     *
     * <p>字符将从ASCII值在{@code 32}和{@code 126}(包括)之间的字符集中选择。.</p>
     *
     * @param count 要创建的随机字符串的长度
     * @return 随机字符串
     */
    public static String randomAsciiThreadLocal(final int count) {
        return randomThreadLocal(count, 32, 127, false, false);
    }

    /**
     * <p>创建一个随机字符串，其长度介于包含的最小值和独占的最大值之间。</p>
     *
     * <p>字符将从ASCII值在{@code 32}和{@code 126}(包括)之间的字符集中选择。</p>
     *
     * @param minLengthInclusive 要生成的字符串的最小长度
     * @param maxLengthExclusive 要生成的字符串的唯一最大长度
     * @return the random string
     */
    public static String randomAsciiThreadLocal(final int minLengthInclusive, final int maxLengthExclusive) {
        return randomAsciiThreadLocal(RandomUtils.randomInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * <p>创建一个随机字符串，其长度为指定的字符数。</p>
     *
     * <p>字符将从拉丁字母字符集(a-z, a-z)中选择。</p>
     *
     * @param count 要创建的随机字符串的长度
     * @return 随机字符串
     */
    public static String randomAlphabeticThreadLocal(final int count) {
        return randomThreadLocal(count, true, false);
    }

    /**
     * <p>创建一个随机字符串，其长度介于包含的最小值和独占的最大值之间。</p>
     *
     * <p>字符将从拉丁字母字符集(a-z, a-z)中选择。</p>
     *
     * @param minLengthInclusive 要生成的字符串的最小长度
     * @param maxLengthExclusive 要生成的字符串的唯一最大长度
     * @return the random string
     */
    public static String randomAlphabeticThreadLocal(final int minLengthInclusive, final int maxLengthExclusive) {
        return randomAlphabeticThreadLocal(RandomUtils.randomInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * <p>创建一个随机字符串，其长度为指定的字符数。</p>
     *
     * <p>字符将从一组拉丁字母(a-z, a-z)和数字0-9中选择。</p>
     *
     * @param count 要创建的随机字符串的长度
     * @return 随机字符串
     */
    public static String randomAlphanumericThreadLocal(final int count) {
        return randomThreadLocal(count, true, true);
    }

    /**
     * <p>创建一个随机字符串，其长度介于包含的最小值和独占的最大值之间。</p>
     *
     * <p>字符将从一组拉丁字母(a-z, a-z)和数字0-9中选择。</p>
     *
     * @param minLengthInclusive 要生成的字符串的最小长度
     * @param maxLengthExclusive 要生成的字符串的唯一最大长度
     * @return 随机字符串
     */
    public static String randomAlphanumericThreadLocal(final int minLengthInclusive, final int maxLengthExclusive) {
        return randomAlphanumericThreadLocal(org.apache.commons.lang3.RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * <p>创建一个随机字符串，其长度为指定的字符数。</p>
     *
     * <p>字符将从匹配POSIX [:graph:]正则表达式字符类的字符集中选择。该类包含所有可见的ASCII字符(即除空格和控制字符外的任何字符)。</p>
     *
     * @param count 要创建的随机字符串的长度
     * @return 随机字符串
     */
    public static String randomGraphThreadLocal(final int count) {
        return randomThreadLocal(count, 33, 126, false, false);
    }

    /**
     * <p>创建一个随机字符串，其长度介于包含的最小值和独占的最大值之间。</p>
     *
     * <p>字符将从\p{Graph}字符集中选择。</p>
     *
     * @param minLengthInclusive 要生成的字符串的最小长度
     * @param maxLengthExclusive 要生成的字符串的唯一最大长度
     * @return the random string
     */
    public static String randomGraphThreadLocal(final int minLengthInclusive, final int maxLengthExclusive) {
        return randomGraphThreadLocal(org.apache.commons.lang3.RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * <p>创建一个随机字符串，其长度为指定的字符数。</p>
     *
     * <p>字符将从数字字符集中选择。</p>
     *
     * @param count 要创建的随机字符串的长度
     * @return 随机字符串
     */
    public static String randomNumericThreadLocal(final int count) {
        return randomThreadLocal(count, false, true);
    }

    /**
     * <p>创建一个随机字符串，其长度介于包含的最小值和独占的最大值之间。</p>
     *
     * <p>字符将从\p{Digit}字符集合中选择。</p>
     *
     * @param minLengthInclusive 要生成的字符串的最小长度
     * @param maxLengthExclusive 要生成的字符串的唯一最大长度
     * @return 随机字符串
     */
    public static String randomNumericThreadLocal(final int minLengthInclusive, final int maxLengthExclusive) {
        return randomNumericThreadLocal(RandomUtils.randomInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * <p>创建一个随机字符串，其长度为指定的字符数。</p>
     *
     * <p>将从POSIX [:print:]正则表达式字符类匹配的字符集中选择字符。该类包括所有可见的ASCII字符和空格(即除控制字符外的任何字符)。</p>
     *
     * @param count 要创建的随机字符串的长度
     * @return 随机字符串
     */
    public static String randomPrintThreadLocal(final int count) {
        return randomThreadLocal(count, 32, 126, false, false);
    }

    /**
     * <p>创建一个随机字符串，其长度介于包含的最小值和独占的最大值之间。</p>
     *
     * <p>字符将从\p{Print}字符集合中选择。</p>
     *
     * @param minLengthInclusive 要生成的字符串的最小长度
     * @param maxLengthExclusive 要生成的字符串的唯一最大长度
     * @return the random string
     */
    public static String randomPrintThreadLocal(final int minLengthInclusive, final int maxLengthExclusive) {
        return randomPrintThreadLocal(RandomUtils.randomInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * <p>创建一个随机字符串，其长度为指定的字符数。</p>
     *
     * <p>字符将从参数指定的字母-数字字符集合中选择。</p>
     *
     * @param count   要创建的随机字符串的长度
     * @param letters 如果{@code true}，生成的字符串可能包含字母字符
     * @param numbers 如果{@code true}，生成的字符串可能包含数字字符
     * @return the random string
     */
    public static String randomThreadLocal(final int count, final boolean letters, final boolean numbers) {
        return randomThreadLocal(count, 0, 0, letters, numbers);
    }

    /**
     * <p>创建一个随机字符串，其长度为指定的字符数。</p>
     *
     * <p>字符将从参数指定的字母-数字字符集合中选择。</p>
     *
     * @param count   要创建的随机字符串的长度
     * @param start   字符集中开始的位置
     * @param end     字符集中前面结束的位置
     * @param letters 如果{@code true}，生成的字符串可能包含字母字符
     * @param numbers 如果{@code true}，生成的字符串可能包含数字字符
     * @return the random string
     */
    public static String randomThreadLocal(final int count, final int start, final int end, final boolean letters, final boolean numbers) {
        return randomThreadLocal(count, start, end, letters, numbers, null, random());
    }

    /**
     * <p>基于各种选项创建一个随机字符串，使用默认的随机源.</p>
     *
     * <p>该方法具有完全相同的语义，但它没有使用外部提供的随机性来源，而是使用内部静态{@link Random}实例。</p>
     *
     * @param count   要创建的随机字符串的长度
     * @param start   字符集中开始的位置
     * @param end     字符集中前面结束的位置
     * @param letters 如果{@code true}，生成的字符串可能包含字母字符
     * @param numbers 如果{@code true}，生成的字符串可能包含数字字符
     * @param chars   可以进行随机选择的字符集。如果{@code null}，那么它将使用所有字符的集合。
     * @return the random string
     * @throws ArrayIndexOutOfBoundsException 如果set数组中没有{@code (end - start) + 1}字符。
     */
    public static String randomThreadLocal(final int count, final int start, final int end, final boolean letters, final boolean numbers, final char... chars) {
        return randomThreadLocal(count, start, end, letters, numbers, chars, random());
    }

    /**
     * <p>基于各种选项创建一个随机字符串，使用提供的随机源。</p>
     *
     * <p>如果start和end都是{@code 0}， start和end设置为{@code ' '}和{@code 'z'}，将使用ASCII可打印字符，除非字母和数字都是{@code false}，在这种情况下，start和end设置为{@code 0}和{@link Character#MAX_CODE_POINT}。
     *
     * <p>如果set不是{@code null}，则选择开始和结束之间的字符。</p>
     *
     * <p>此方法接受用户提供的{@link Random}
     * 作为随机性来源的实例。通过在每个调用中使用一个带有固定种子的{@link Random}实例，可以重复地和可预测地生成相同的随机字符串序列。</p>
     *
     * @param count   要创建的随机字符串的长度
     * @param start   字符集中开始的位置(包括)
     * @param end     字符集中结束于(排他)之前的位置
     * @param letters 如果{@code true}，生成的字符串可能包含字母字符
     * @param numbers 如果{@code true}，生成的字符串可能包含数字字符
     * @param chars   要进行随机选择的字符集不能为空。如果{@code null}，那么它将使用所有字符的集合。
     * @param random  随机性的来源。
     * @return the random string
     * @throws ArrayIndexOutOfBoundsException 如果set数组中没有{@code (end - start) + 1}字符。
     * @throws IllegalArgumentException       如果{@code count} &lt;0或提供的chars数组为空。
     */
    public static String randomThreadLocal(int count, int start, int end, final boolean letters, final boolean numbers,
            final char[] chars, final Random random) {
        if (count == 0) {
            return StringUtils.EMPTY;
        } else if (count < 0) {
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
     * <p>创建一个随机字符串，其长度为指定的字符数。</p>
     *
     * <p>字符将从字符串指定的字符集合中选择，不能为空。如果为空，则使用所有字符的集合。</p>
     *
     * @param count 要创建的随机字符串的长度
     * @param chars 包含要使用的字符集的字符串可以为空，但不能为空
     * @return the random string
     * @throws IllegalArgumentException if {@code count} &lt; 0 or the string is empty.
     */
    public static String randomThreadLocal(final int count, final String chars) {
        if (chars == null) {
            return randomThreadLocal(count, 0, 0, false, false, null, random());
        }
        return randomThreadLocal(count, chars.toCharArray());
    }

    /**
     * <p>创建一个随机字符串，其长度为指定的字符数。</p>
     *
     * <p>将从指定的字符集中选择字符。</p>
     *
     * @param count 要创建的随机字符串的长度
     * @param chars 包含要使用的字符集的字符数组可以为空
     * @return the random string
     * @throws IllegalArgumentException if {@code count} &lt; 0.
     */
    public static String randomThreadLocal(final int count, final char... chars) {
        if (chars == null) {
            return randomThreadLocal(count, 0, 0, false, false, null, random());
        }
        return randomThreadLocal(count, 0, chars.length, false, false, chars, random());
    }
}
