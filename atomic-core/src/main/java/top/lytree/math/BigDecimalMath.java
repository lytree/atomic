package top.lytree.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalMath {

    private BigDecimalMath() {
    }

    /**
     * 返回{@code x}，用指定的舍入模式四舍五入为{@code double}。
     * 如果{@code x}可以精确地表示为{@code double}，
     * 它的{@code double}值将被返回;否则，
     * 舍入将使用{@code mode}在两个最近的可表示值之间进行选择。
     *
     * <p>For the case of {@link RoundingMode#HALF_DOWN}, {@code HALF_UP}, and {@code HALF_EVEN},
     * infinite {@code double} values are considered infinitely far away. For example, 2^2000 is not representable as a double, but {@code
     * roundToDouble(BigDecimal.valueOf(2).pow(2000), HALF_UP)} will return {@code Double.MAX_VALUE}, not {@code Double.POSITIVE_INFINITY}.
     *
     * <p>For the case of {@link RoundingMode#HALF_EVEN}, this implementation uses the IEEE 754
     * default rounding mode: if the two nearest representable values are equally near, the one with the least significant bit zero is chosen. (In such cases, both of the nearest
     * representable values are even integers; this method returns the one that is a multiple of a greater power of two.)
     *
     * @throws ArithmeticException if {@code mode} is {@link RoundingMode#UNNECESSARY} and {@code x} is not precisely representable as a {@code double}
     * @since 30.0
     */
    public static double roundToDouble(BigDecimal x, RoundingMode mode) {
        return BigDecimalToDoubleRounder.INSTANCE.roundToDouble(x, mode);
    }

    private static class BigDecimalToDoubleRounder extends ToDoubleRounder<BigDecimal> {

        static final BigDecimalToDoubleRounder INSTANCE = new BigDecimalToDoubleRounder();

        private BigDecimalToDoubleRounder() {
        }

        @Override
        double roundToDoubleArbitrarily(BigDecimal bigDecimal) {
            return bigDecimal.doubleValue();
        }

        @Override
        int sign(BigDecimal bigDecimal) {
            return bigDecimal.signum();
        }

        @Override
        BigDecimal toX(double d, RoundingMode mode) {
            return new BigDecimal(d);
        }

        @Override
        BigDecimal minus(BigDecimal a, BigDecimal b) {
            return a.subtract(b);
        }
    }
}
