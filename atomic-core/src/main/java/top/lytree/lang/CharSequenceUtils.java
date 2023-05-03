package top.lytree.lang;


import top.lytree.array.ArrayUtils;

/**
 * @author PrideYang
 */
public class CharSequenceUtils {

    private static final int NOT_FOUND = -1;

    /**
     * <p>{@code CharSequenceUtils}实例不应该在标准编程中构造。</p>
     *
     * <p>这个构造函数是公共的，允许需要JavaBean实例操作的工具。</p>
     */
    public CharSequenceUtils() {
    }

    //-----------------------------------------------------------------------

    /**
     * <p>返回一个新的{@code CharSequence}，它是该序列的子序列，从指定索引处的{@code char}值开始
     *
     * <p>这提供了等价于{@link String#substring(int)}的{@code CharSequence}。返回序列的长度(在{@code char}中)是{@code length() - start}，所以如果{@code start == end}，则返回一个空序列。</p>
     *
     * @param cs    指定的子序列null返回null
     * @param start 有效的起始索引
     * @return 一个新的子序列，可以为空
     * @throws IndexOutOfBoundsException 如果{@code start}为负或者{@code start}大于{@code length()}
     */
    public static CharSequence subSequence(final CharSequence cs, final int start) {
        return cs == null ? null : cs.subSequence(start, cs.length());
    }

    //-----------------------------------------------------------------------

    /**
     * 返回指定字符在{@code cs}内的第一个匹配项的索引，从指定索引开始搜索。
     * <p>
     * 如果值为{@code searchChar}的字符出现在由{@code cs}对象表示的字符序列中，索引不小于{@code start}，则返回第一个出现该字符的索引。对于{@code searchChar}在0到0xFFFF(包括)范围内的值，这是最小的值<i>k</i> such that:
     * <blockquote><pre>
     * (this.charAt(<i>k</i>) == searchChar) &amp;&amp; (<i>k</i> &gt;= start)
     * </pre></blockquote>
     * 是真的。对于{@code searchChar}的其他值，它是最小的值<i>k</i> such that:
     * <blockquote><pre>
     * (this.codePointAt(<i>k</i>) == searchChar) &amp;&amp; (<i>k</i> &gt;= start)
     * </pre></blockquote>
     * 是真的。在这两种情况下，如果在{@code cs}位置{@code start}或在{@code start}之后没有出现这样的字符，则返回{@code -1}。
     *
     * <p>
     * {@code start}的值没有限制。如果为负数，它的效果与为零一样:可以搜索整个{@code CharSequence}。如果它大于{@code cs}的长度，它的效果就相当于等于{@code cs}的长度:返回{@code -1}。
     *
     * <p>所有索引都在{@code char}值中指定
     * (Unicode code units).
     *
     * @param cs         要处理的{@code CharSequence}，不为空
     * @param searchChar 要搜索的字符
     * @param start      起始索引，负数从字符串start开始
     * @return 找到搜索字符的索引，如果没有找到-1
     */
    static int indexOf(final CharSequence cs, final int searchChar, int start) {
        if (cs instanceof String) {
            return ((String) cs).indexOf(searchChar, start);
        }
        final int sz = cs.length();
        if (start < 0) {
            start = 0;
        }
        if (searchChar < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
            for (int i = start; i < sz; i++) {
                if (cs.charAt(i) == searchChar) {
                    return i;
                }
            }
            return NOT_FOUND;
        }
        //supplementary characters (LANG1300)
        if (searchChar <= Character.MAX_CODE_POINT) {
            final char[] chars = Character.toChars(searchChar);
            for (int i = start; i < sz - 1; i++) {
                final char high = cs.charAt(i);
                final char low = cs.charAt(i + 1);
                if (high == chars[0] && low == chars[1]) {
                    return i;
                }
            }
        }
        return NOT_FOUND;
    }

    /**
     * 由indexOf(CharSequence方法)用作indexOf的绿色实现。
     *
     * @param cs         要处理的{@code CharSequence}
     * @param searchChar 要搜索的{@code CharSequence}
     * @param start      开始下标
     * @return 找到搜索序列的索引
     */
    static int indexOf(final CharSequence cs, final CharSequence searchChar, final int start) {
        if (cs instanceof String) {
            return ((String) cs).indexOf(searchChar.toString(), start);
        } else if (cs instanceof StringBuilder) {
            return ((StringBuilder) cs).indexOf(searchChar.toString(), start);
        } else if (cs instanceof StringBuffer) {
            return ((StringBuffer) cs).indexOf(searchChar.toString(), start);
        }
        return cs.toString().indexOf(searchChar.toString(), start);

    }

    /**
     * 返回指定字符在{@code cs}内最后一次出现的索引，从指定索引开始向后搜索。对于{@code searchChar}的值在0到0xFFFF(包括)范围内，返回的索引是<i>k<i>的最大值，这样:
     * <blockquote><pre>
     * (this.charAt(<i>k</i>) == searchChar) &amp;&amp; (<i>k</i> &lt;= start)
     * </pre></blockquote>
     * 是真的。对于{@code searchChar}的其他值，它是<i>k<i>的最大值，这样:
     * <blockquote><pre>
     * (this.codePointAt(<i>k</i>) == searchChar) &amp;&amp; (<i>k</i> &lt;= start)
     * </pre></blockquote>
     * 是真的。在这两种情况下，如果在{@code cs}中没有这样的字符出现在{@code start}位置或位置之前，则返回{@code -1}。
     *
     * <p>所有索引都在{@code char}值中指定
     * (Unicode code units).
     *
     * @param cs         要处理的{@code CharSequence}
     * @param searchChar 要搜索的字符
     * @param start      起始索引为负值，返回-1，长度超出从结束开始
     * @return 找到搜索字符的索引，如果没有找到-1
     */
    static int lastIndexOf(final CharSequence cs, final int searchChar, int start) {
        if (cs instanceof String) {
            return ((String) cs).lastIndexOf(searchChar, start);
        }
        final int sz = cs.length();
        if (start < 0) {
            return NOT_FOUND;
        }
        if (start >= sz) {
            start = sz - 1;
        }
        if (searchChar < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
            for (int i = start; i >= 0; --i) {
                if (cs.charAt(i) == searchChar) {
                    return i;
                }
            }
            return NOT_FOUND;
        }
        //supplementary characters (LANG1300)
        //NOTE - we must do a forward traversal for this to avoid duplicating code points
        if (searchChar <= Character.MAX_CODE_POINT) {
            final char[] chars = Character.toChars(searchChar);
            //make sure it's not the last index
            if (start == sz - 1) {
                return NOT_FOUND;
            }
            for (int i = start; i >= 0; i--) {
                final char high = cs.charAt(i);
                final char low = cs.charAt(i + 1);
                if (chars[0] == high && chars[1] == low) {
                    return i;
                }
            }
        }
        return NOT_FOUND;
    }

    static final int TO_STRING_LIMIT = 16;

    /**
     * 由lastIndexOf(CharSequence方法)作为lastIndexOf的绿色实现使用
     *
     * @param cs         要处理的{@code CharSequence}
     * @param searchChar 查找的{@code CharSequence}
     * @param start      开始下标
     * @return 找到搜索序列的索引
     */
    static int lastIndexOf(final CharSequence cs, final CharSequence searchChar, int start) {
        if (searchChar == null || cs == null) {
            return NOT_FOUND;
        }
        if (searchChar instanceof String) {
            if (cs instanceof String) {
                return ((String) cs).lastIndexOf((String) searchChar, start);
            } else if (cs instanceof StringBuilder) {
                return ((StringBuilder) cs).lastIndexOf((String) searchChar, start);
            } else if (cs instanceof StringBuffer) {
                return ((StringBuffer) cs).lastIndexOf((String) searchChar, start);
            }
        }

        final int len1 = cs.length();
        final int len2 = searchChar.length();

        if (start > len1) {
            start = len1;
        }

        if (start < 0 || len2 < 0 || len2 > len1) {
            return NOT_FOUND;
        }

        if (len2 == 0) {
            return start;
        }

        if (len2 <= TO_STRING_LIMIT) {
            if (cs instanceof String) {
                return ((String) cs).lastIndexOf(searchChar.toString(), start);
            } else if (cs instanceof StringBuilder) {
                return ((StringBuilder) cs).lastIndexOf(searchChar.toString(), start);
            } else if (cs instanceof StringBuffer) {
                return ((StringBuffer) cs).lastIndexOf(searchChar.toString(), start);
            }
        }

        if (start + len2 > len1) {
            start = len1 - len2;
        }

        final char char0 = searchChar.charAt(0);

        int i = start;
        while (true) {
            while (cs.charAt(i) != char0) {
                i--;
                if (i < 0) {
                    return NOT_FOUND;
                }
            }
            if (checkLaterThan1(cs, searchChar, len2, i)) {
                return i;
            }
            i--;
            if (i < 0) {
                return NOT_FOUND;
            }
        }
    }

    private static boolean checkLaterThan1(final CharSequence cs, final CharSequence searchChar, final int len2, final int start1) {
        for (int i = 1, j = len2 - 1; i <= j; i++, j--) {
            if (cs.charAt(start1 + i) != searchChar.charAt(i)
                    ||
                    cs.charAt(start1 + j) != searchChar.charAt(j)
            ) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将给定的CharSequence转换为char[]。
     *
     * @param source 要处理的{@code CharSequence}。
     * @return 生成的字符数组，永远不要为空。
     */
    public static char[] toCharArray(final CharSequence source) {
        final int len = StringUtils.length(source);
        if (len == 0) {
            return ArrayUtils.EMPTY_CHAR_ARRAY;
        }
        if (source instanceof String) {
            return ((String) source).toCharArray();
        }
        final char[] array = new char[len];
        for (int i = 0; i < len; i++) {
            array[i] = source.charAt(i);
        }
        return array;
    }

    /**
     * regionMatches的绿色实现。
     *
     * @param cs         要处理的{@code CharSequence}
     * @param ignoreCase 是否不区分大小写
     * @param thisStart  在{@code cs} CharSequence上启动的索引
     * @param substring  要查找的{@code CharSequence}
     * @param start      从索引开始{@code substring} 字符序列
     * @param length     区域的字符长度
     * @return 区域是否匹配
     */
    static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart,
            final CharSequence substring, final int start, final int length) {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        }
        int index1 = thisStart;
        int index2 = start;
        int tmpLen = length;

        // Extract these first so we detect NPEs the same as the java.lang.String version
        final int srcLen = cs.length() - thisStart;
        final int otherLen = substring.length() - start;

        // Check for invalid parameters
        if (thisStart < 0 || start < 0 || length < 0) {
            return false;
        }

        // Check that the regions are long enough
        if (srcLen < length || otherLen < length) {
            return false;
        }

        while (tmpLen-- > 0) {
            final char c1 = cs.charAt(index1++);
            final char c2 = substring.charAt(index2++);

            if (c1 == c2) {
                continue;
            }

            if (!ignoreCase) {
                return false;
            }

            // The real same check as in String.regionMatches():
            final char u1 = Character.toUpperCase(c1);
            final char u2 = Character.toUpperCase(c2);
            if (u1 != u2 && Character.toLowerCase(u1) != Character.toLowerCase(u2)) {
                return false;
            }
        }

        return true;
    }
}
