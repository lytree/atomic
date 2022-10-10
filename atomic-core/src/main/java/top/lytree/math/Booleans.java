package top.lytree.math;

import java.util.Comparator;

public class Booleans {

    private Booleans() {
    }

    /** Comparators for {@code Boolean} values. */
    private enum BooleanComparator implements Comparator<Boolean> {
        TRUE_FIRST(1, "Booleans.trueFirst()"),
        FALSE_FIRST(-1, "Booleans.falseFirst()");

        private final int trueValue;
        private final String toString;

        BooleanComparator(int trueValue, String toString) {
            this.trueValue = trueValue;
            this.toString = toString;
        }

        @Override
        public int compare(Boolean a, Boolean b) {
            int aVal = a ? trueValue : 0;
            int bVal = b ? trueValue : 0;
            return bVal - aVal;
        }

        @Override
        public String toString() {
            return toString;
        }
    }

    /**
     * Compares the two specified {@code boolean} values in the standard way ({@code false} is
     * considered less than {@code true}). The sign of the value returned is the same as that of
     * {@code ((Boolean) a).compareTo(b)}.
     *
     * <p><b>Note for Java 7 and later:</b> this method should be treated as deprecated; use the
     * equivalent {@link Boolean#compare} method instead.
     *
     * @param a the first {@code boolean} to compare
     * @param b the second {@code boolean} to compare
     *
     * @return a positive number if only {@code a} is {@code true}, a negative number if only {@code
     * b} is true, or zero if {@code a == b}
     */
    public static int compare(boolean a, boolean b) {
        return (a == b) ? 0 : (a ? 1 : -1);
    }

}
