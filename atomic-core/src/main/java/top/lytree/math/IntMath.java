/*
 * Copyright (C) 2011 The Guava Authors
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

package top.lytree.math;


import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.math.RoundingMode.HALF_EVEN;
import static java.math.RoundingMode.HALF_UP;


import static top.lytree.math.MathPreconditions.*;


import java.math.BigInteger;
import java.math.RoundingMode;
import top.lytree.base.Assert;


public final class IntMath {
    // NOTE: Whenever both tests are cheap and functional, it's faster to use &, | instead of &&, ||


    static final int MAX_SIGNED_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

    static int lessThanBranchFree(int x, int y) {
        // The double negation is optimized away by normal Java, but is necessary for GWT
        // to make sure bit twiddling works as expected.
        return ~~(x - y) >>> (Integer.SIZE - 1);
    }


    /**
     * The biggest half power of two that can fit in an unsigned int.
     */

    static final int MAX_POWER_OF_SQRT2_UNSIGNED = 0xB504F333;

    // maxLog10ForLeadingZeros[i] == floor(log10(2^(Long.SIZE - i)))

    static final byte[] maxLog10ForLeadingZeros = {
            9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0,
            0
    };


    static final int[] powersOf10 = {
            1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000
    };

    // halfPowersOf10[i] = largest int less than 10^(i + 0.5)

    static final int[] halfPowersOf10 = {
            3, 31, 316, 3162, 31622, 316227, 3162277, 31622776, 316227766, Integer.MAX_VALUE
    };

    /**
     * 返回{@code b}的{@code k}次方。即使结果溢出，它也将等于{@code BigInteger.valueOf(b).pow(k).intValue()}。该实现运行时间为{@code O(log k)}。<p>比较{@link #checkedPow}，它在溢出时抛出{@link ArithmeticException}。
     *
     * @throws IllegalArgumentException if {@code k < 0}
     */
    // failing tests
    public static int pow(int b, int k) {
        checkNonNegative("exponent", k);
        switch (b) {
            case 0:
                return (k == 0) ? 1 : 0;
            case 1:
                return 1;
            case (-1):
                return ((k & 1) == 0) ? 1 : -1;
            case 2:
                return (k < Integer.SIZE) ? (1 << k) : 0;
            case (-2):
                if (k < Integer.SIZE) {
                    return ((k & 1) == 0) ? (1 << k) : -(1 << k);
                } else {
                    return 0;
                }
            default:
                // continue below to handle the general case
        }
        for (int accum = 1; ; k >>= 1) {
            switch (k) {
                case 0:
                    return accum;
                case 1:
                    return b * accum;
                default:
                    accum *= ((k & 1) == 0) ? 1 : b;
                    b *= b;
            }
        }
    }

    /**
     * 返回 {@code x}的平方根，用指定的舍入模式四舍五入。
     *
     * @throws IllegalArgumentException if {@code x < 0}
     * @throws ArithmeticException      if {@code mode} is {@link RoundingMode#UNNECESSARY} and {@code sqrt(x)} is not an integer
     */
    // need BigIntegerMath to adequately test
    @SuppressWarnings("fallthrough")
    public static int sqrt(int x, RoundingMode mode) {
        checkNonNegative("x", x);
        int sqrtFloor = sqrtFloor(x);
        switch (mode) {
            case UNNECESSARY:
                checkRoundingUnnecessary(sqrtFloor * sqrtFloor == x); // fall through
            case FLOOR:
            case DOWN:
                return sqrtFloor;
            case CEILING:
            case UP:
                return sqrtFloor + lessThanBranchFree(sqrtFloor * sqrtFloor, x);
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN:
                int halfSquare = sqrtFloor * sqrtFloor + sqrtFloor;
                /*
                 * We wish to test whether or not x <= (sqrtFloor + 0.5)^2 = halfSquare + 0.25. Since both x
                 * and halfSquare are integers, this is equivalent to testing whether or not x <=
                 * halfSquare. (We have to deal with overflow, though.)
                 *
                 * If we treat halfSquare as an unsigned int, we know that
                 *            sqrtFloor^2 <= x < (sqrtFloor + 1)^2
                 * halfSquare - sqrtFloor <= x < halfSquare + sqrtFloor + 1
                 * so |x - halfSquare| <= sqrtFloor.  Therefore, it's safe to treat x - halfSquare as a
                 * signed int, so lessThanBranchFree is safe for use.
                 */
                return sqrtFloor + lessThanBranchFree(halfSquare, x);
            default:
                throw new AssertionError();
        }
    }

    private static int sqrtFloor(int x) {
        // There is no loss of precision in converting an int to a double, according to
        // http://java.sun.com/docs/books/jls/third_edition/html/conversions.html#5.1.2
        return (int) Math.sqrt(x);
    }

    /**
     * Returns the result of dividing {@code p} by {@code q}, rounding using the specified {@code RoundingMode}.
     *
     * @throws ArithmeticException if {@code q == 0}, or if {@code mode == UNNECESSARY} and {@code a} is not an integer multiple of {@code b}
     */
    @SuppressWarnings("fallthrough")
    public static int divide(int p, int q, RoundingMode mode) {
        Assert.notNull(mode);
        if (q == 0) {
            throw new ArithmeticException("/ by zero"); // for GWT
        }
        int div = p / q;
        int rem = p - q * div; // equal to p % q

        if (rem == 0) {
            return div;
        }

        /*
         * Normal Java division rounds towards 0, consistently with RoundingMode.DOWN. We just have to
         * deal with the cases where rounding towards 0 is wrong, which typically depends on the sign of
         * p / q.
         *
         * signum is 1 if p and q are both nonnegative or both negative, and -1 otherwise.
         */
        int signum = 1 | ((p ^ q) >> (Integer.SIZE - 1));
        boolean increment;
        switch (mode) {
            case UNNECESSARY:
                checkRoundingUnnecessary(rem == 0);
                // fall through
            case DOWN:
                increment = false;
                break;
            case UP:
                increment = true;
                break;
            case CEILING:
                increment = signum > 0;
                break;
            case FLOOR:
                increment = signum < 0;
                break;
            case HALF_EVEN:
            case HALF_DOWN:
            case HALF_UP:
                int absRem = abs(rem);
                int cmpRemToHalfDivisor = absRem - (abs(q) - absRem);
                // subtracting two nonnegative ints can't overflow
                // cmpRemToHalfDivisor has the same sign as compare(abs(rem), abs(q) / 2).
                if (cmpRemToHalfDivisor == 0) { // exactly on the half mark
                    increment = (mode == HALF_UP || (mode == HALF_EVEN & (div & 1) != 0));
                } else {
                    increment = cmpRemToHalfDivisor > 0; // closer to the UP value
                }
                break;
            default:
                throw new AssertionError();
        }
        return increment ? div + signum : div;
    }

    /**
     * 返回小于{@code m}的非负值{@code x mod m}。这不同于{@code x % m}，后者可能是负数。
     *
     * <p>For example:
     *
     * <pre>{@code
     * mod(7, 4) == 3
     * mod(-7, 4) == 1
     * mod(-1, 4) == 3
     * mod(-8, 4) == 0
     * mod(8, 4) == 0
     * }</pre>
     *
     * @throws ArithmeticException if {@code m <= 0}
     * @see <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.17.3">
     * Remainder Operator</a>
     */
    public static int mod(int x, int m) {
        if (m <= 0) {
            throw new ArithmeticException("Modulus " + m + " must be > 0");
        }
        int result = x % m;
        return (result >= 0) ? result : result + m;
    }

    /**
     * 返回{@code a, b}的最大公约数。如果{@code a == 0 && b == 0}，返回{@code 0}。
     *
     * @throws IllegalArgumentException if {@code a < 0} or {@code b < 0}
     */
    public static int gcd(int a, int b) {
        /*
         * The reason we require both arguments to be >= 0 is because otherwise, what do you return on
         * gcd(0, Integer.MIN_VALUE)? BigInteger.gcd would return positive 2^31, but positive 2^31 isn't
         * an int.
         */
        checkNonNegative("a", a);
        checkNonNegative("b", b);
        if (a == 0) {
            // 0 % b == 0, so b divides a, but the converse doesn't hold.
            // BigInteger.gcd is consistent with this decision.
            return b;
        } else if (b == 0) {
            return a; // similar logic
        }
        /*
         * Uses the binary GCD algorithm; see http://en.wikipedia.org/wiki/Binary_GCD_algorithm. This is
         * >40% faster than the Euclidean algorithm in benchmarks.
         */
        int aTwos = Integer.numberOfTrailingZeros(a);
        a >>= aTwos; // divide out all 2s
        int bTwos = Integer.numberOfTrailingZeros(b);
        b >>= bTwos; // divide out all 2s
        while (a != b) { // both a, b are odd
            // The key to the binary GCD algorithm is as follows:
            // Both a and b are odd. Assume a > b; then gcd(a - b, b) = gcd(a, b).
            // But in gcd(a - b, b), a - b is even and b is odd, so we can divide out powers of two.

            // We bend over backwards to avoid branching, adapting a technique from
            // http://graphics.stanford.edu/~seander/bithacks.html#IntegerMinOrMax

            int delta = a - b; // can't overflow, since a and b are nonnegative

            int minDeltaOrZero = delta & (delta >> (Integer.SIZE - 1));
            // equivalent to Math.min(delta, 0)

            a = delta - minDeltaOrZero - minDeltaOrZero; // sets a to Math.abs(a - b)
            // a is now nonnegative and even

            b += minDeltaOrZero; // sets b to min(old a, b)
            a >>= Integer.numberOfTrailingZeros(a); // divide out all 2s, since 2 doesn't divide b
        }
        return a << min(aTwos, bTwos);
    }

    /**
     * 如果不溢出，返回{@code a}和{@code b}的和。
     *
     * @throws ArithmeticException 如果{@code a + b}在signed {@code int}算术中溢出
     */
    public static int checkedAdd(int a, int b) {
        long result = (long) a + b;
        checkNoOverflow(result == (int) result, "checkedAdd", a, b);
        return (int) result;
    }

    /**
     * 如果不溢出，返回{@code a}和{@code b}的差值。
     *
     * @throws ArithmeticException if {@code a - b} overflows in signed {@code int} arithmetic
     */
    public static int checkedSubtract(int a, int b) {
        long result = (long) a - b;
        checkNoOverflow(result == (int) result, "checkedSubtract", a, b);
        return (int) result;
    }

    /**
     * 如果不溢出，返回{@code a}和{@code b}的乘积。
     *
     * @throws ArithmeticException if {@code a * b} overflows in signed {@code int} arithmetic
     */
    public static int checkedMultiply(int a, int b) {
        long result = (long) a * b;
        checkNoOverflow(result == (int) result, "checkedMultiply", a, b);
        return (int) result;
    }

    /**
     * 返回{@code b}的{@code k}次幂，前提是它不溢出。
     *
     * <p>{@link #pow} may be faster, but does not check for overflow.
     *
     * @throws ArithmeticException if {@code b} to the {@code k}th power overflows in signed {@code int} arithmetic
     */
    public static int checkedPow(int b, int k) {
        checkNonNegative("exponent", k);
        switch (b) {
            case 0:
                return (k == 0) ? 1 : 0;
            case 1:
                return 1;
            case (-1):
                return ((k & 1) == 0) ? 1 : -1;
            case 2:
                checkNoOverflow(k < Integer.SIZE - 1, "checkedPow", b, k);
                return 1 << k;
            case (-2):
                checkNoOverflow(k < Integer.SIZE, "checkedPow", b, k);
                return ((k & 1) == 0) ? 1 << k : -1 << k;
            default:
                // continue below to handle the general case
        }
        int accum = 1;
        while (true) {
            switch (k) {
                case 0:
                    return accum;
                case 1:
                    return checkedMultiply(accum, b);
                default:
                    if ((k & 1) != 0) {
                        accum = checkedMultiply(accum, b);
                    }
                    k >>= 1;
                    if (k > 0) {
                        checkNoOverflow(-FLOOR_SQRT_MAX_INT <= b & b <= FLOOR_SQRT_MAX_INT, "checkedPow", b, k);
                        b *= b;
                    }
            }
        }
    }

    /**
     * 返回{@code a}和{@code b}的和，除非它会溢出或下溢，在这种情况下{@code Integer。MAX_VALUE}或{@code整数。返回MIN_VALUE}, respectively.
     *
     * 
     */

    public static int saturatedAdd(int a, int b) {
        return Ints.saturatedCast((long) a + b);
    }

    /**
     * 返回{@code a}和{@code b}的差值，除非它会溢出或下溢，在这种情况下{@code Integer.MAX_VALUE}或{@code Integer.MIN_VALUE}。
     *
     * 
     */

    public static int saturatedSubtract(int a, int b) {
        return Ints.saturatedCast((long) a - b);
    }

    /**
     * 返回{@code a}和{@code b}的乘积，除非它会溢出或下溢，在这种情况下{@code Integer.MAX_VALUE}或{@code Integer.MIN_VALUE}。
     *
     * 
     */

    public static int saturatedMultiply(int a, int b) {
        return Ints.saturatedCast((long) a * b);
    }

    /**
     * 返回{@code b}的{@code k}次幂，除非它会溢出或下溢，在这种情况下{@code Integer.MAX_VALUE}或{@code Integer.MIN_VALUE}。
     *
     * 
     */

    public static int saturatedPow(int b, int k) {
        checkNonNegative("exponent", k);
        switch (b) {
            case 0:
                return (k == 0) ? 1 : 0;
            case 1:
                return 1;
            case (-1):
                return ((k & 1) == 0) ? 1 : -1;
            case 2:
                if (k >= Integer.SIZE - 1) {
                    return Integer.MAX_VALUE;
                }
                return 1 << k;
            case (-2):
                if (k >= Integer.SIZE) {
                    return Integer.MAX_VALUE + (k & 1);
                }
                return ((k & 1) == 0) ? 1 << k : -1 << k;
            default:
                // continue below to handle the general case
        }
        int accum = 1;
        // if b is negative and k is odd then the limit is MIN otherwise the limit is MAX
        int limit = Integer.MAX_VALUE + ((b >>> Integer.SIZE - 1) & (k & 1));
        while (true) {
            switch (k) {
                case 0:
                    return accum;
                case 1:
                    return saturatedMultiply(accum, b);
                default:
                    if ((k & 1) != 0) {
                        accum = saturatedMultiply(accum, b);
                    }
                    k >>= 1;
                    if (k > 0) {
                        if (-FLOOR_SQRT_MAX_INT > b | b > FLOOR_SQRT_MAX_INT) {
                            return limit;
                        }
                        b *= b;
                    }
            }
        }
    }


    static final int FLOOR_SQRT_MAX_INT = 46340;

    /**
     * 返回{@code n!}，即第一个{@code n}正整数的乘积，如果{@code n == 0}则为{@code 1}，如果结果不符合{@code int}则为{@link Integer#MAX_VALUE}。
     *
     * @throws IllegalArgumentException if {@code n < 0}
     */
    public static int factorial(int n) {
        checkNonNegative("n", n);
        return (n < factorials.length) ? factorials[n] : Integer.MAX_VALUE;
    }

    private static final int[] factorials = {
            1,
            1,
            1 * 2,
            1 * 2 * 3,
            1 * 2 * 3 * 4,
            1 * 2 * 3 * 4 * 5,
            1 * 2 * 3 * 4 * 5 * 6,
            1 * 2 * 3 * 4 * 5 * 6 * 7,
            1 * 2 * 3 * 4 * 5 * 6 * 7 * 8,
            1 * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9,
            1 * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10,
            1 * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11,
            1 * 2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12
    };

    /**
     * 返回{@code n}选择{@code k}，也称为{@code n}和{@code k}的二项式系数， 或者如果结果不适合{@code int}则返回{@link Integer#MAX_VALUE}。
     *
     * @throws IllegalArgumentException if {@code n < 0}, {@code k < 0} or {@code k > n}
     */
    public static int binomial(int n, int k) {
        checkNonNegative("n", n);
        checkNonNegative("k", k);
        Assert.checkArgument(k <= n, "k (%s) > n (%s)", k, n);
        if (k > (n >> 1)) {
            k = n - k;
        }
        if (k >= biggestBinomials.length || n > biggestBinomials[k]) {
            return Integer.MAX_VALUE;
        }
        switch (k) {
            case 0:
                return 1;
            case 1:
                return n;
            default:
                long result = 1;
                for (int i = 0; i < k; i++) {
                    result *= n - i;
                    result /= i + 1;
                }
                return (int) result;
        }
    }

    // binomial(biggestBinomials[k], k) fits in an int, but not binomial(biggestBinomials[k]+1,k).

    static int[] biggestBinomials = {
            Integer.MAX_VALUE,
            Integer.MAX_VALUE,
            65536,
            2345,
            477,
            193,
            110,
            75,
            58,
            49,
            43,
            39,
            37,
            35,
            34,
            34,
            33
    };

    /**
     * 返回{@code x}和{@code y}的算术平均值，四舍五入到负无穷。该方法具有溢出弹性。
     *
     * 
     */
    public static int mean(int x, int y) {
        // Efficient method for computing the arithmetic mean.
        // The alternative (x + y) / 2 fails for large values.
        // The alternative (x + y) >>> 1 fails for negative values.
        return (x & y) + ((x ^ y) >> 1);
    }

    /**
     * 如果{@code n}是<a href="http:mathworld. wolfram.comprimennumber .html">素数 <a>:一个整数<i>大于1 <i>不能分解为<i>小于<i>的正整数的乘积。如果{@code n}是0、1或合数(<i>可以<i>被分解成更小的正整数)，则返回{@code false}。
     *
     * <p>To test larger numbers, use {@link LongMath#isPrime} or {@link BigInteger#isProbablePrime}.
     *
     * @throws IllegalArgumentException if {@code n} is negative
     * 
     */
    // TODO
    public static boolean isPrime(int n) {
        return LongMath.isPrime(n);
    }

    private IntMath() {
    }
}
