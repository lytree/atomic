/*
 * Copyright (C) 2008 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package top.yang.math;


import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import top.yang.base.Converter;
import top.yang.base.Assert;


/**
 * Static utility methods pertaining to {@code long} primitives, that are not already found in either {@link Long} or {@link Arrays}.
 *
 * <p>See the Guava User Guide article on <a
 * href="https://github.com/google/guava/wiki/PrimitivesExplained">primitive utilities</a>.
 *
 * @author Kevin Bourrillion
 * @since 1.0
 */

public final class LongUtils {

    private LongUtils() {
    }

    /**
     * The number of bytes required to represent a primitive {@code long} value.
     *
     * <p><b>Java 8 users:</b> use {@link Long#BYTES} instead.
     */
    public static final int BYTES = Long.SIZE / Byte.SIZE;

    /**
     * The largest power of two that can be represented as a {@code long}.
     *
     * @since 10.0
     */
    public static final long MAX_POWER_OF_TWO = 1L << (Long.SIZE - 2);

    /**
     * Returns a hash code for {@code value}; equal to the result of invoking {@code ((Long) value).hashCode()}.
     *
     * <p>This method always return the value specified by {@link Long#hashCode()} in java, which
     * might be different from {@code ((Long) value).hashCode()} in GWT because {@link Long#hashCode()} in GWT does not obey the JRE contract.
     *
     * <p><b>Java 8 users:</b> use {@link Long#hashCode(long)} instead.
     *
     * @param value a primitive {@code long} value
     * @return a hash code for the value
     */
    public static int hashCode(long value) {
        return (int) (value ^ (value >>> 32));
    }

    /**
     * Compares the two specified {@code long} values. The sign of the value returned is the same as that of {@code ((Long) a).compareTo(b)}.
     *
     * <p><b>Note for Java 7 and later:</b> this method should be treated as deprecated; use the
     * equivalent {@link Long#compare} method instead.
     *
     * @param a the first {@code long} to compare
     * @param b the second {@code long} to compare
     * @return a negative value if {@code a} is less than {@code b}; a positive value if {@code a} is greater than {@code b}; or zero if they are equal
     */
    public static int compare(long a, long b) {
        return (a < b) ? -1 : ((a > b) ? 1 : 0);
    }

    /**
     * Returns {@code true} if {@code target} is present as an element anywhere in {@code array}.
     *
     * @param array  an array of {@code long} values, possibly empty
     * @param target a primitive {@code long} value
     * @return {@code true} if {@code array[i] == target} for some value of {@code i}
     */
    public static boolean contains(long[] array, long target) {
        for (long value : array) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the index of the first appearance of the value {@code target} in {@code array}.
     *
     * @param array  an array of {@code long} values, possibly empty
     * @param target a primitive {@code long} value
     * @return the least index {@code i} for which {@code array[i] == target}, or {@code -1} if no such index exists.
     */
    public static int indexOf(long[] array, long target) {
        return indexOf(array, target, 0, array.length);
    }

    // TODO(kevinb): consider making this public
    private static int indexOf(long[] array, long target, int start, int end) {
        for (int i = start; i < end; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the start position of the first occurrence of the specified {@code target} within {@code array}, or {@code -1} if there is no such occurrence.
     *
     * <p>More formally, returns the lowest index {@code i} such that {@code Arrays.copyOfRange(array,
     * i, i + target.length)} contains exactly the same elements as {@code target}.
     *
     * @param array  the array to search for the sequence {@code target}
     * @param target the array to search for as a sub-sequence of {@code array}
     */
    public static int indexOf(long[] array, long[] target) {
        Assert.notNull(array, "array");
        Assert.notNull(target, "target");
        if (target.length == 0) {
            return 0;
        }

        outer:
        for (int i = 0; i < array.length - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    /**
     * Returns the index of the last appearance of the value {@code target} in {@code array}.
     *
     * @param array  an array of {@code long} values, possibly empty
     * @param target a primitive {@code long} value
     * @return the greatest index {@code i} for which {@code array[i] == target}, or {@code -1} if no such index exists.
     */
    public static int lastIndexOf(long[] array, long target) {
        return lastIndexOf(array, target, 0, array.length);
    }

    // TODO(kevinb): consider making this public
    private static int lastIndexOf(long[] array, long target, int start, int end) {
        for (int i = end - 1; i >= start; i--) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 返回 {@code array} 中出现的最小值 .
     *
     * @param array a <i>nonempty</i> array of {@code long} values
     * @return the value present in {@code array} that is less than or equal to every other value in the array
     * @throws IllegalArgumentException if {@code array} is empty
     */
    public static long min(long... array) {
        Assert.checkArgument(array.length > 0);
        long min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    /**
     * 返回{@code array}中存在的最大值。
     *
     * @param array a <i>nonempty</i> array of {@code long} values
     * @return the value present in {@code array} that is greater than or equal to every other value in the array
     * @throws IllegalArgumentException if {@code array} is empty
     */
    public static long max(long... array) {
        Assert.checkArgument(array.length > 0);
        long max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    /**
     * 返回关闭范围{@code [min..max]}内最接近{@code value}的值。
     *
     * <p>如果{@code value}在{@code [min..Max]范围内}， {@code value}不变返回。
     * <p>如果{@code value}小于{@code min}则返回{@code min}，
     * <p>如果{@code value}大于{@code max}则返回{@code max}。
     *
     * @param value the {@code long} value to constrain
     * @param min   the lower bound (inclusive) of the range to constrain {@code value} to
     * @param max   the upper bound (inclusive) of the range to constrain {@code value} to
     * @throws IllegalArgumentException if {@code min > max}
     * @since 21.0
     */

    public static long constrainToRange(long value, long min, long max) {
        Assert.checkArgument(min <= max, "min (%s) must be less than or equal to max (%s)", min, max);
        return Math.min(Math.max(value, min), max);
    }

    /**
     * 返回组合成单个数组的每个提供的数组的值。例如， {@code concat(new long[] {a, b}， new long[] {}， new long[] {c}} 返回数组 {@code {a, b, c}}.
     *
     * @param arrays zero or more {@code long} arrays
     * @return a single array containing all the values from the source arrays, in order
     */
    public static long[] concat(long[]... arrays) {
        int length = 0;
        for (long[] array : arrays) {
            length += array.length;
        }
        long[] result = new long[length];
        int pos = 0;
        for (long[] array : arrays) {
            System.arraycopy(array, 0, result, pos, array.length);
            pos += array.length;
        }
        return result;
    }

    public static byte[] toByteArray(long value) {
        // Note that this code needs to stay compatible with GWT, which has known
        // bugs when narrowing byte casts of long values occur.
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (value & 0xffL);
            value >>= 8;
        }
        return result;
    }

    /**
     * Returns the {@code long} value whose big-endian representation is stored in the first 8 bytes of {@code bytes}; equivalent to {@code ByteBuffer.wrap(bytes).getLong()}. For
     * example, the input byte array {@code {0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19}} would yield the {@code long} value {@code 0x1213141516171819L}.
     *
     * <p>Arguably, it's preferable to use {@link java.nio.ByteBuffer}; that library exposes much more
     * flexibility at little cost in readability.
     *
     * @throws IllegalArgumentException if {@code bytes} has fewer than 8 elements
     */
    public static long fromByteArray(byte[] bytes) {
        Assert.checkArgument(bytes.length >= BYTES, "array too small: %s < %s", bytes.length, BYTES);
        return fromBytes(
                bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7]);
    }

    /**
     * Returns the {@code long} value whose byte representation is the given 8 bytes, in big-endian order; equivalent to {@code LongUtils.fromByteArray(new byte[] {b1, b2, b3, b4,
     * b5, b6, b7, b8})}.
     *
     * @since 7.0
     */
    public static long fromBytes(
            byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8) {
        return (b1 & 0xFFL) << 56
                | (b2 & 0xFFL) << 48
                | (b3 & 0xFFL) << 40
                | (b4 & 0xFFL) << 32
                | (b5 & 0xFFL) << 24
                | (b6 & 0xFFL) << 16
                | (b7 & 0xFFL) << 8
                | (b8 & 0xFFL);
    }

    /*
     * Moving asciiDigits into this static holder class lets ProGuard eliminate and inline the LongUtils
     * class.
     */
    static final class AsciiDigits {

        private AsciiDigits() {
        }

        private static final byte[] asciiDigits;

        static {
            byte[] result = new byte[128];
            Arrays.fill(result, (byte) -1);
            for (int i = 0; i < 10; i++) {
                result['0' + i] = (byte) i;
            }
            for (int i = 0; i < 26; i++) {
                result['A' + i] = (byte) (10 + i);
                result['a' + i] = (byte) (10 + i);
            }
            asciiDigits = result;
        }

        static int digit(char c) {
            return (c < 128) ? asciiDigits[c] : -1;
        }
    }

    /**
     * Parses the specified string as a signed decimal long value. The ASCII character {@code '-'} (
     * <code>'&#92;u002D'</code>) is recognized as the minus sign.
     *
     * <p>Unlike {@link Long#parseLong(String)}, this method returns {@code null} instead of throwing
     * an exception if parsing fails. Additionally, this method only accepts ASCII digits, and returns {@code null} if non-ASCII digits are present in the string.
     *
     * <p>Note that strings prefixed with ASCII {@code '+'} are rejected, even under JDK 7, despite
     * the change to {@link Long#parseLong(String)} for that version.
     *
     * @param string the string representation of a long value
     * @return the long value represented by {@code string}, or {@code null} if {@code string} has a length of zero or cannot be parsed as a long value
     * @throws NullPointerException if {@code string} is {@code null}
     * @since 14.0
     */


    public static Long tryParse(String string) {
        return tryParse(string, 10);
    }

    /**
     * Parses the specified string as a signed long value using the specified radix. The ASCII character {@code '-'} (<code>'&#92;u002D'</code>) is recognized as the minus sign.
     *
     * <p>Unlike {@link Long#parseLong(String, int)}, this method returns {@code null} instead of
     * throwing an exception if parsing fails. Additionally, this method only accepts ASCII digits, and returns {@code null} if non-ASCII digits are present in the string.
     *
     * <p>Note that strings prefixed with ASCII {@code '+'} are rejected, even under JDK 7, despite
     * the change to {@link Long#parseLong(String, int)} for that version.
     *
     * @param string the string representation of an long value
     * @param radix  the radix to use when parsing
     * @return the long value represented by {@code string} using {@code radix}, or {@code null} if {@code string} has a length of zero or cannot be parsed as a long value
     * @throws IllegalArgumentException if {@code radix < Character.MIN_RADIX} or {@code radix > Character.MAX_RADIX}
     * @throws NullPointerException     if {@code string} is {@code null}
     * @since 19.0
     */


    public static Long tryParse(String string, int radix) {
        if (Assert.notNull(string).isEmpty()) {
            return null;
        }
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            throw new IllegalArgumentException(
                    "radix must be between MIN_RADIX and MAX_RADIX but was " + radix);
        }
        boolean negative = string.charAt(0) == '-';
        int index = negative ? 1 : 0;
        if (index == string.length()) {
            return null;
        }
        int digit = AsciiDigits.digit(string.charAt(index++));
        if (digit < 0 || digit >= radix) {
            return null;
        }
        long accum = -digit;

        long cap = Long.MIN_VALUE / radix;

        while (index < string.length()) {
            digit = AsciiDigits.digit(string.charAt(index++));
            if (digit < 0 || digit >= radix || accum < cap) {
                return null;
            }
            accum *= radix;
            if (accum < Long.MIN_VALUE + digit) {
                return null;
            }
            accum -= digit;
        }

        if (negative) {
            return accum;
        } else if (accum == Long.MIN_VALUE) {
            return null;
        } else {
            return -accum;
        }
    }

    private static final class LongConverter extends Converter<String, Long> implements Serializable {

        static final LongConverter INSTANCE = new LongConverter();

        @Override
        protected Long doForward(String value) {
            return Long.decode(value);
        }

        @Override
        protected String doBackward(Long value) {
            return value.toString();
        }

        @Override
        public String toString() {
            return "LongUtils.stringConverter()";
        }

        private Object readResolve() {
            return INSTANCE;
        }

        private static final long serialVersionUID = 1;
    }

    /**
     * Returns a serializable converter object that converts between strings and longs using {@link Long#decode} and {@link Long#toString()}. The returned converter throws {@link
     * NumberFormatException} if the input string is invalid.
     *
     * <p><b>Warning:</b> please see {@link Long#decode} to understand exactly how strings are parsed.
     * For example, the string {@code "0123"} is treated as <i>octal</i> and converted to the value {@code 83L}.
     *
     * @since 16.0
     */

    public static Converter<String, Long> stringConverter() {
        return LongConverter.INSTANCE;
    }

    /**
     * Returns an array containing the same values as {@code array}, but guaranteed to be of a specified minimum length. If {@code array} already has a length of at least {@code
     * minLength}, it is returned directly. Otherwise, a new array of size {@code minLength + padding} is returned, containing the values of {@code array}, and zeroes in the
     * remaining places.
     *
     * @param array     the source array
     * @param minLength the minimum length the returned array must guarantee
     * @param padding   an extra amount to "grow" the array by if growth is necessary
     * @return an array containing the values of {@code array}, with guaranteed minimum length {@code minLength}
     * @throws IllegalArgumentException if {@code minLength} or {@code padding} is negative
     */
    public static long[] ensureCapacity(long[] array, int minLength, int padding) {
        Assert.checkArgument(minLength >= 0, "Invalid minLength: %s", minLength);
        Assert.checkArgument(padding >= 0, "Invalid padding: %s", padding);
        return (array.length < minLength) ? Arrays.copyOf(array, minLength + padding) : array;
    }

    /**
     * Returns a string containing the supplied {@code long} values separated by {@code separator}. For example, {@code join("-", 1L, 2L, 3L)} returns the string {@code "1-2-3"}.
     *
     * @param separator the text that should appear between consecutive values in the resulting string (but not at the start or end)
     * @param array     an array of {@code long} values, possibly empty
     */
    public static String join(String separator, long... array) {
        Assert.notNull(separator);
        if (array.length == 0) {
            return "";
        }

        // For pre-sizing a builder, just get the right order of magnitude
        StringBuilder builder = new StringBuilder(array.length * 10);
        builder.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            builder.append(separator).append(array[i]);
        }
        return builder.toString();
    }

    /**
     * Returns a comparator that compares two {@code long} arrays <a href="http://en.wikipedia.org/wiki/Lexicographical_order">lexicographically</a>. That is, it compares, using
     * {@link #compare(long, long)}), the first pair of values that follow any common prefix, or when one array is a prefix of the other, treats the shorter array as the lesser.
     * For example, {@code [] < [1L] < [1L, 2L] < [2L]}.
     *
     * <p>The returned comparator is inconsistent with {@link Object#equals(Object)} (since arrays
     * support only identity equality), but it is consistent with {@link Arrays#equals(long[], long[])}.
     *
     * @since 2.0
     */
    public static Comparator<long[]> lexicographicalComparator() {
        return LexicographicalComparator.INSTANCE;
    }

    private enum LexicographicalComparator implements Comparator<long[]> {
        INSTANCE;

        @Override
        public int compare(long[] left, long[] right) {
            int minLength = Math.min(left.length, right.length);
            for (int i = 0; i < minLength; i++) {
                int result = LongUtils.compare(left[i], right[i]);
                if (result != 0) {
                    return result;
                }
            }
            return left.length - right.length;
        }

        @Override
        public String toString() {
            return "LongUtils.lexicographicalComparator()";
        }
    }

    /**
     * Sorts the elements of {@code array} in descending order.
     *
     * @since 23.1
     */
    public static void sortDescending(long[] array) {
        Assert.notNull(array);
        sortDescending(array, 0, array.length);
    }

    /**
     * Sorts the elements of {@code array} between {@code fromIndex} inclusive and {@code toIndex} exclusive in descending order.
     *
     * @since 23.1
     */
    public static void sortDescending(long[] array, int fromIndex, int toIndex) {
        Assert.notNull(array);
        Assert.checkIndex(fromIndex, array.length);
        Assert.checkIndex(toIndex, array.length);
        Arrays.sort(array, fromIndex, toIndex);
        reverse(array, fromIndex, toIndex);
    }

    /**
     * Reverses the elements of {@code array}. This is equivalent to {@code Collections.reverse(LongUtils.asList(array))}, but is likely to be more efficient.
     *
     * @since 23.1
     */
    public static void reverse(long[] array) {
        Assert.notNull(array);
        reverse(array, 0, array.length);
    }

    /**
     * Reverses the elements of {@code array} between {@code fromIndex} inclusive and {@code toIndex} exclusive. This is equivalent to {@code
     * Collections.reverse(LongUtils.asList(array).subList(fromIndex, toIndex))}, but is likely to be more efficient.
     *
     * @throws IndexOutOfBoundsException if {@code fromIndex < 0}, {@code toIndex > array.length}, or {@code toIndex > fromIndex}
     * @since 23.1
     */
    public static void reverse(long[] array, int fromIndex, int toIndex) {
        Assert.notNull(array);
        Assert.checkIndex(fromIndex, array.length);
        Assert.checkIndex(toIndex, array.length);
        for (int i = fromIndex, j = toIndex - 1; i < j; i++, j--) {
            long tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }

    /**
     * Performs a right rotation of {@code array} of "distance" places, so that the first element is moved to index "distance", and the element at index {@code i} ends up at index
     * {@code (distance + i) mod array.length}. This is equivalent to {@code Collections.rotate(LongUtils.asList(array), distance)}, but is considerably faster and avoids
     * allocation and garbage collection.
     *
     * <p>The provided "distance" may be negative, which will rotate left.
     *
     * @since NEXT
     */
    public static void rotate(long[] array, int distance) {
        rotate(array, distance, 0, array.length);
    }

    /**
     * 在包含{@code fromIndex}和排除{@code toIndex}之间对{@code数组}进行右旋转。这相当于{@code Collections.rotate(LongUtils.asList(array))。subblist (fromIndex, toIndex)，
     * distance)}，但是速度相当快，并且避免了分配和垃圾收集。
     * <p>The provided "distance" may be negative, which will rotate left.
     *
     * @throws IndexOutOfBoundsException if {@code fromIndex < 0}, {@code toIndex > array.length}, or {@code toIndex > fromIndex}
     * @since NEXT
     */
    public static void rotate(long[] array, int distance, int fromIndex, int toIndex) {
        // See Ints.rotate for more details about possible algorithms here.
        Assert.notNull(array);
        Assert.checkIndex(fromIndex, array.length);
        Assert.checkIndex(toIndex, array.length);
        if (array.length <= 1) {
            return;
        }

        int length = toIndex - fromIndex;
        // Obtain m = (-distance mod length), a non-negative value less than "length". This is how many
        // places left to rotate.
        int m = -distance % length;
        m = (m < 0) ? m + length : m;
        // The current index of what will become the first element of the rotated section.
        int newFirstIndex = m + fromIndex;
        if (newFirstIndex == fromIndex) {
            return;
        }

        reverse(array, fromIndex, newFirstIndex);
        reverse(array, newFirstIndex, toIndex);
        reverse(array, fromIndex, toIndex);
    }

    /**
     * Returns an array containing each value of {@code collection}, converted to a {@code long} value in the manner of {@link Number#longValue}.
     *
     * <p>Elements are copied from the argument collection as if by {@code collection.toArray()}.
     * Calling this method is as thread-safe as calling that method.
     *
     * @param collection a collection of {@code Number} instances
     * @return an array containing the same values as {@code collection}, in the same order, converted to primitives
     * @throws NullPointerException if {@code collection} or any of its elements is null
     * @since 1.0 (parameter was {@code Collection<Long>} before 12.0)
     */
    public static long[] toArray(Collection<? extends Number> collection) {
        if (collection instanceof LongArrayAsList) {
            return ((LongArrayAsList) collection).toLongArray();
        }

        Object[] boxedArray = collection.toArray();
        int len = boxedArray.length;
        long[] array = new long[len];
        for (int i = 0; i < len; i++) {
            // Assert.notNull for GWT (do not optimize)
            array[i] = ((Number) Assert.notNull(boxedArray[i])).longValue();
        }
        return array;
    }


    public static List<Long> asList(long... backingArray) {
        if (backingArray.length == 0) {
            return Collections.emptyList();
        }
        return new LongArrayAsList(backingArray);
    }


    private static class LongArrayAsList extends AbstractList<Long>
            implements RandomAccess, Serializable {

        final long[] array;
        final int start;
        final int end;

        LongArrayAsList(long[] array) {
            this(array, 0, array.length);
        }

        LongArrayAsList(long[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        public int size() {
            return end - start;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Long get(int index) {
            Assert.checkIndex(index, size());
            return array[start + index];
        }

        @Override
        public Spliterator.OfLong spliterator() {
            return Spliterators.spliterator(array, start, end, 0);
        }

        @Override
        public boolean contains(Object target) {
            // Overridden to prevent a ton of boxing
            return (target instanceof Long) && LongUtils.indexOf(array, (Long) target, start, end) != -1;
        }

        @Override
        public int indexOf(Object target) {
            // Overridden to prevent a ton of boxing
            if (target instanceof Long) {
                int i = LongUtils.indexOf(array, (Long) target, start, end);
                if (i >= 0) {
                    return i - start;
                }
            }
            return -1;
        }

        @Override
        public int lastIndexOf(Object target) {
            // Overridden to prevent a ton of boxing
            if (target instanceof Long) {
                int i = LongUtils.lastIndexOf(array, (Long) target, start, end);
                if (i >= 0) {
                    return i - start;
                }
            }
            return -1;
        }

        @Override
        public Long set(int index, Long element) {
            Assert.checkIndex(index, size());
            long oldValue = array[start + index];
            // Assert.notNull for GWT (do not optimize)
            array[start + index] = Assert.notNull(element);
            return oldValue;
        }

        @Override
        public List<Long> subList(int fromIndex, int toIndex) {
            int size = size();
            Assert.checkIndex(fromIndex, size);
            Assert.checkIndex(toIndex, size);
            if (fromIndex == toIndex) {
                return Collections.emptyList();
            }
            return new LongArrayAsList(array, start + fromIndex, start + toIndex);
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof LongArrayAsList) {
                LongArrayAsList that = (LongArrayAsList) object;
                int size = size();
                if (that.size() != size) {
                    return false;
                }
                for (int i = 0; i < size; i++) {
                    if (array[start + i] != that.array[that.start + i]) {
                        return false;
                    }
                }
                return true;
            }
            return super.equals(object);
        }

        @Override
        public int hashCode() {
            int result = 1;
            for (int i = start; i < end; i++) {
                result = 31 * result + LongUtils.hashCode(array[i]);
            }
            return result;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(size() * 10);
            builder.append('[').append(array[start]);
            for (int i = start + 1; i < end; i++) {
                builder.append(", ").append(array[i]);
            }
            return builder.append(']').toString();
        }

        long[] toLongArray() {
            return Arrays.copyOfRange(array, start, end);
        }

        private static final long serialVersionUID = 1L;
    }
}
