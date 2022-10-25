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


import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * A class for arithmetic on values of type {@code BigInteger}.
 *
 * <p>The implementations of many methods in this class are based on material from Henry S. Warren,
 * Jr.'s <i>Hacker's Delight</i>, (Addison Wesley, 2002).
 *
 * <p>Similar functionality for {@code int} and for {@code long} can be found in {@link IntMath} and
 * {@link LongMath} respectively.
 *
 * @author Louis Wasserman
 * @since 11.0
 */
public final class BigIntegerMath {

    /**
     * Returns {@code x}, rounded to a {@code double} with the specified rounding mode. If {@code x}
     * is precisely representable as a {@code double}, its {@code double} value will be returned;
     * otherwise, the rounding will choose between the two nearest representable values with {@code
     * mode}.
     *
     * <p>For the case of {@link RoundingMode#HALF_DOWN}, {@code HALF_UP}, and {@code HALF_EVEN},
     * infinite {@code double} values are considered infinitely far away. For example, 2^2000 is not
     * representable as a double, but {@code roundToDouble(BigInteger.valueOf(2).pow(2000), HALF_UP)}
     * will return {@code Double.MAX_VALUE}, not {@code Double.POSITIVE_INFINITY}.
     *
     * <p>For the case of {@link RoundingMode#HALF_EVEN}, this implementation uses the IEEE 754
     * default rounding mode: if the two nearest representable values are equally near, the one with
     * the least significant bit zero is chosen. (In such cases, both of the nearest representable
     * values are even integers; this method returns the one that is a multiple of a greater power of
     * two.)
     *
     * @throws ArithmeticException if {@code mode} is {@link RoundingMode#UNNECESSARY} and {@code x}
     *                             is not precisely representable as a {@code double}
     * @since 30.0
     */
    public static double roundToDouble(BigInteger x, RoundingMode mode) {
        return BigIntegerToDoubleRounder.INSTANCE.roundToDouble(x, mode);
    }

    private static class BigIntegerToDoubleRounder extends ToDoubleRounder<BigInteger> {

        static final BigIntegerToDoubleRounder INSTANCE = new BigIntegerToDoubleRounder();

        private BigIntegerToDoubleRounder() {
        }

        @Override
        double roundToDoubleArbitrarily(BigInteger bigInteger) {
            return Doubles.bigToDouble(bigInteger);
        }

        @Override
        int sign(BigInteger bigInteger) {
            return bigInteger.signum();
        }

        @Override
        BigInteger toX(double d, RoundingMode mode) {
            return DoubleMath.roundToBigInteger(d, mode);
        }

        @Override
        BigInteger minus(BigInteger a, BigInteger b) {
            return a.subtract(b);
        }
    }

    private BigIntegerMath() {
    }
}
