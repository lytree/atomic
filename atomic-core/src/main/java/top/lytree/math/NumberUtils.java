/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.lytree.math;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

import top.lytree.base.Assert;
import top.lytree.lang.StringUtils;

/**
 *
 */
public class NumberUtils  {

    /**
     * 在int 范围内 2的最大次幂
     *
     * @since 10.0
     */
    public static final int MAX_POWER_OF_TWO_INT = 1 << (Integer.SIZE - 2);


    public static final ByteOrder DEFAULT_ORDER = ByteOrder.LITTLE_ENDIAN;
    /**
     * CPU的字节序
     */
    public static final ByteOrder CPU_ENDIAN = "little".equals(System.getProperty("sun.cpu.endian")) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;

    /**
     * 比较大小，值相等 返回true<br> 此方法通过调用{@link Double#doubleToLongBits(double)}方法来判断是否相等<br> 此方法判断值相等时忽略精度的，即0.00 == 0
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return 是否相等
     */
    public static boolean equals(double num1, double num2) {
        return Double.doubleToLongBits(num1) == Double.doubleToLongBits(num2);
    }

    /**
     * 比较大小，值相等 返回true<br> 此方法通过调用{@link Float#floatToIntBits(float)}方法来判断是否相等<br> 此方法判断值相等时忽略精度的，即0.00 == 0
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return 是否相等
     */
    public static boolean equals(float num1, float num2) {
        return Float.floatToIntBits(num1) == Float.floatToIntBits(num2);
    }

    /**
     * 比较大小，值相等 返回true<br> 此方法通过调用{@link BigDecimal#compareTo(BigDecimal)}方法来判断是否相等<br> 此方法判断值相等时忽略精度的，即0.00 == 0
     *
     * @param bigNum1 数字1
     * @param bigNum2 数字2
     * @return 是否相等
     */
    public static boolean equals(BigDecimal bigNum1, BigDecimal bigNum2) {
        if (bigNum1 == null || bigNum2 == null) {
            return false;
        }
        if (bigNum1.equals(bigNum2)) {
            // 如果用户传入同一对象，省略compareTo以提高性能。
            return true;
        }
        return 0 == bigNum1.compareTo(bigNum2);

    }

    // ------------------------------------------------------------------------------------------- round

    /**
     * 保留固定位数小数<br> 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br> 例如保留2位小数：123.456789 =》 123.46
     *
     * @param v     值
     * @param scale 保留小数位数
     * @return 新值
     */
    public static BigDecimal round(double v, int scale) {
        return round(v, scale, RoundingMode.HALF_UP);
    }

    /**
     * 保留固定位数小数<br> 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br> 例如保留2位小数：123.456789 =》 123.46
     *
     * @param v     值
     * @param scale 保留小数位数
     * @return 新值
     */
    public static String roundStr(double v, int scale) {
        return round(v, scale).toString();
    }

    /**
     * 保留固定位数小数<br> 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br> 例如保留2位小数：123.456789 =》 123.46
     *
     * @param numberStr 数字值的字符串表现形式
     * @param scale     保留小数位数
     * @return 新值
     */
    public static BigDecimal round(String numberStr, int scale) {
        return round(numberStr, scale, RoundingMode.HALF_UP);
    }

    /**
     * 保留固定位数小数<br> 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br> 例如保留2位小数：123.456789 =》 123.46
     *
     * @param number 数字值
     * @param scale  保留小数位数
     * @return 新值
     */
    public static BigDecimal round(BigDecimal number, int scale) {
        return round(number, scale, RoundingMode.HALF_UP);
    }

    /**
     * 保留固定位数小数<br> 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br> 例如保留2位小数：123.456789 =》 123.46
     *
     * @param numberStr 数字值的字符串表现形式
     * @param scale     保留小数位数
     * @return 新值
     */
    public static String roundStr(String numberStr, int scale) {
        return round(numberStr, scale).toString();
    }

    /**
     * 保留固定位数小数<br> 例如保留四位小数：123.456789 =》 123.4567
     *
     * @param v            值
     * @param scale        保留小数位数
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 新值
     */
    public static BigDecimal round(double v, int scale, RoundingMode roundingMode) {
        return round(Double.toString(v), scale, roundingMode);
    }

    /**
     * 保留固定位数小数<br> 例如保留四位小数：123.456789 =》 123.4567
     *
     * @param v            值
     * @param scale        保留小数位数
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 新值
     */
    public static String roundStr(double v, int scale, RoundingMode roundingMode) {
        return round(v, scale, roundingMode).toString();
    }

    /**
     * 保留固定位数小数<br> 例如保留四位小数：123.456789 =》 123.4567
     *
     * @param numberStr    数字值的字符串表现形式
     * @param scale        保留小数位数，如果传入小于0，则默认0
     * @param roundingMode 保留小数的模式 {@link RoundingMode}，如果传入null则默认四舍五入
     * @return 新值
     */
    public static BigDecimal round(String numberStr, int scale, RoundingMode roundingMode) {
        Assert.notBlank(numberStr);
        if (scale < 0) {
            scale = 0;
        }
        return round(new BigDecimal(numberStr), scale, roundingMode);
    }

    /**
     * 保留固定位数小数<br> 例如保留四位小数：123.456789 =》 123.4567
     *
     * @param number       数字值
     * @param scale        保留小数位数，如果传入小于0，则默认0
     * @param roundingMode 保留小数的模式 {@link RoundingMode}，如果传入null则默认四舍五入
     * @return 新值
     */
    public static BigDecimal round(BigDecimal number, int scale, RoundingMode roundingMode) {
        if (null == number) {
            number = BigDecimal.ZERO;
        }
        if (scale < 0) {
            scale = 0;
        }
        if (null == roundingMode) {
            roundingMode = RoundingMode.HALF_UP;
        }

        return number.setScale(scale, roundingMode);
    }

    /**
     * 保留固定位数小数<br> 例如保留四位小数：123.456789 =》 123.4567
     *
     * @param numberStr    数字值的字符串表现形式
     * @param scale        保留小数位数
     * @param roundingMode 保留小数的模式 {@link RoundingMode}
     * @return 新值
     */
    public static String roundStr(String numberStr, int scale, RoundingMode roundingMode) {
        return round(numberStr, scale, roundingMode).toString();
    }

    // ------------------------------------------------------------------------------------------- decimalFormat

    /**
     * 格式化double<br> 对 {@link DecimalFormat} 做封装<br>
     *
     * @param pattern 格式 格式中主要以 # 和 0 两种占位符号来指定数字长度。0 表示如果位数不足则以 0 填充，# 表示只要有可能就把数字拉上这个位置。<br>
     *                <ul>
     *                <li>0 =》 取一位整数</li>
     *                <li>0.00 =》 取一位整数和两位小数</li>
     *                <li>00.000 =》 取两位整数和三位小数</li>
     *                <li># =》 取所有整数部分</li>
     *                <li>#.##% =》 以百分比方式计数，并取两位小数</li>
     *                <li>#.#####E0 =》 显示为科学计数法，并取五位小数</li>
     *                <li>,### =》 每三位以逗号进行分隔，例如：299,792,458</li>
     *                <li>光速大小为每秒,###米 =》 将格式嵌入文本</li>
     *                </ul>
     * @param value   值
     * @return 格式化后的值
     */
    public static String decimalFormat(String pattern, double value) {
        Assert.isTrue(isValid(value), "value is NaN or Infinite!");
        return new DecimalFormat(pattern).format(value);
    }

    /**
     * 格式化double<br> 对 {@link DecimalFormat} 做封装<br>
     *
     * @param pattern 格式 格式中主要以 # 和 0 两种占位符号来指定数字长度。0 表示如果位数不足则以 0 填充，# 表示只要有可能就把数字拉上这个位置。<br>
     *                <ul>
     *                <li>0 =》 取一位整数</li>
     *                <li>0.00 =》 取一位整数和两位小数</li>
     *                <li>00.000 =》 取两位整数和三位小数</li>
     *                <li># =》 取所有整数部分</li>
     *                <li>#.##% =》 以百分比方式计数，并取两位小数</li>
     *                <li>#.#####E0 =》 显示为科学计数法，并取五位小数</li>
     *                <li>,### =》 每三位以逗号进行分隔，例如：299,792,458</li>
     *                <li>光速大小为每秒,###米 =》 将格式嵌入文本</li>
     *                </ul>
     * @param value   值
     * @return 格式化后的值
     */
    public static String decimalFormat(String pattern, long value) {
        return new DecimalFormat(pattern).format(value);
    }

    /**
     * 格式化double<br> 对 {@link DecimalFormat} 做封装<br>
     *
     * @param pattern 格式 格式中主要以 # 和 0 两种占位符号来指定数字长度。0 表示如果位数不足则以 0 填充，# 表示只要有可能就把数字拉上这个位置。<br>
     *                <ul>
     *                <li>0 =》 取一位整数</li>
     *                <li>0.00 =》 取一位整数和两位小数</li>
     *                <li>00.000 =》 取两位整数和三位小数</li>
     *                <li># =》 取所有整数部分</li>
     *                <li>#.##% =》 以百分比方式计数，并取两位小数</li>
     *                <li>#.#####E0 =》 显示为科学计数法，并取五位小数</li>
     *                <li>,### =》 每三位以逗号进行分隔，例如：299,792,458</li>
     *                <li>光速大小为每秒,###米 =》 将格式嵌入文本</li>
     *                </ul>
     * @param value   值，支持BigDecimal、BigInteger、Number等类型
     * @return 格式化后的值
     */
    public static String decimalFormat(String pattern, Object value) {
        return decimalFormat(pattern, value, null);
    }

    /**
     * 格式化double<br> 对 {@link DecimalFormat} 做封装<br>
     *
     * @param pattern      格式 格式中主要以 # 和 0 两种占位符号来指定数字长度。0 表示如果位数不足则以 0 填充，# 表示只要有可能就把数字拉上这个位置。<br>
     *                     <ul>
     *                     <li>0 =》 取一位整数</li>
     *                     <li>0.00 =》 取一位整数和两位小数</li>
     *                     <li>00.000 =》 取两位整数和三位小数</li>
     *                     <li># =》 取所有整数部分</li>
     *                     <li>#.##% =》 以百分比方式计数，并取两位小数</li>
     *                     <li>#.#####E0 =》 显示为科学计数法，并取五位小数</li>
     *                     <li>,### =》 每三位以逗号进行分隔，例如：299,792,458</li>
     *                     <li>光速大小为每秒,###米 =》 将格式嵌入文本</li>
     *                     </ul>
     * @param value        值，支持BigDecimal、BigInteger、Number等类型
     * @param roundingMode 保留小数的方式枚举
     * @return 格式化后的值
     */
    public static String decimalFormat(String pattern, Object value, RoundingMode roundingMode) {
        if (value instanceof Number) {
            Assert.isTrue(isValidNumber((Number) value), "value is NaN or Infinite!");
        }
        final DecimalFormat decimalFormat = new DecimalFormat(pattern);
        if (null != roundingMode) {
            decimalFormat.setRoundingMode(roundingMode);
        }
        return decimalFormat.format(value);
    }

    /**
     * 检查是否为有效的数字<br> 检查Double和Float是否为无限大，或者Not a Number<br> 非数字类型和Null将返回true
     *
     * @param number 被检查类型
     * @return 检查结果，非数字类型和Null将返回true
     */
    public static boolean isValidNumber(Number number) {
        if (number instanceof Double) {
            return (!((Double) number).isInfinite()) && (!((Double) number).isNaN());
        } else if (number instanceof Float) {
            return (!((Float) number).isInfinite()) && (!((Float) number).isNaN());
        }
        return true;
    }

    /**
     * 检查是否为有效的数字<br> 检查double否为无限大，或者Not a Number（NaN）<br>
     *
     * @param number 被检查double
     * @return 检查结果
     */
    public static boolean isValid(double number) {
        return !(Double.isNaN(number) || Double.isInfinite(number));
    }

    /**
     * 检查是否为有效的数字<br> 检查double否为无限大，或者Not a Number（NaN）<br>
     *
     * @param number 被检查double
     * @return 检查结果
     */
    public static boolean isValid(float number) {
        return !(Float.isNaN(number) || Float.isInfinite(number));
    }

    /**
     * 格式化金额输出，每三位用逗号分隔
     *
     * @param value 金额
     * @return 格式化后的值
     */
    public static String decimalFormatMoney(double value) {
        return decimalFormat(",##0.00", value);
    }

    /**
     * 格式化百分比，小数采用四舍五入方式
     *
     * @param number 值
     * @param scale  保留小数位数
     * @return 百分比
     */
    public static String formatPercent(double number, int scale) {
        final NumberFormat format = NumberFormat.getPercentInstance();
        format.setMaximumFractionDigits(scale);
        return format.format(number);
    }


    /**
     * int转byte
     *
     * @param intValue int值
     * @return byte值
     */
    public static byte intToByte(int intValue) {
        return (byte) intValue;
    }

    /**
     * byte转无符号int
     *
     * @param byteValue byte值
     * @return 无符号int值
     */
    public static int byteToUnsignedInt(byte byteValue) {
        // Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return byteValue & 0xFF;
    }

    /**
     * byte数组转short<br> 默认以小端序转换
     *
     * @param bytes byte数组
     * @return short值
     */
    public static short bytesToShort(byte[] bytes) {
        return bytesToShort(bytes, DEFAULT_ORDER);
    }

    /**
     * byte数组转short<br> 自定义端序
     *
     * @param bytes     byte数组，长度必须为2
     * @param byteOrder 端序
     * @return short值
     */
    public static short bytesToShort(byte[] bytes, ByteOrder byteOrder) {
        if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
            //小端模式，数据的高字节保存在内存的高地址中，而数据的低字节保存在内存的低地址中
            return (short) (bytes[0] & 0xff | (bytes[1] & 0xff) << Byte.SIZE);
        } else {
            return (short) (bytes[1] & 0xff | (bytes[0] & 0xff) << Byte.SIZE);
        }
    }

    /**
     * short转byte数组<br> 默认以小端序转换
     *
     * @param shortValue short值
     * @return byte数组
     */
    public static byte[] shortToBytes(short shortValue) {
        return shortToBytes(shortValue, DEFAULT_ORDER);
    }

    /**
     * short转byte数组<br> 自定义端序
     *
     * @param shortValue short值
     * @param byteOrder  端序
     * @return byte数组
     */
    public static byte[] shortToBytes(short shortValue, ByteOrder byteOrder) {
        byte[] b = new byte[Short.BYTES];
        if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
            b[0] = (byte) (shortValue & 0xff);
            b[1] = (byte) ((shortValue >> Byte.SIZE) & 0xff);
        } else {
            b[1] = (byte) (shortValue & 0xff);
            b[0] = (byte) ((shortValue >> Byte.SIZE) & 0xff);
        }
        return b;
    }

    /**
     * byte[]转int值<br> 默认以小端序转换
     *
     * @param bytes byte数组
     * @return int值
     */
    public static int bytesToInt(byte[] bytes) {
        return bytesToInt(bytes, DEFAULT_ORDER);
    }

    /**
     * byte[]转int值<br> 自定义端序
     *
     * @param bytes     byte数组
     * @param byteOrder 端序
     * @return int值
     */
    public static int bytesToInt(byte[] bytes, ByteOrder byteOrder) {
        return bytesToInt(bytes, 0, byteOrder);
    }

    /**
     * byte[]转int值<br> 自定义端序
     *
     * @param bytes     byte数组
     * @param start     开始位置（包含）
     * @param byteOrder 端序
     * @return int值
     */
    public static int bytesToInt(byte[] bytes, int start, ByteOrder byteOrder) {
        if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
            return bytes[start] & 0xFF | //
                    (bytes[1 + start] & 0xFF) << 8 | //
                    (bytes[2 + start] & 0xFF) << 16 | //
                    (bytes[3 + start] & 0xFF) << 24; //
        } else {
            return bytes[3 + start] & 0xFF | //
                    (bytes[2 + start] & 0xFF) << 8 | //
                    (bytes[1 + start] & 0xFF) << 16 | //
                    (bytes[start] & 0xFF) << 24; //
        }

    }

    /**
     * int转byte数组<br> 默认以小端序转换
     *
     * @param intValue int值
     * @return byte数组
     */
    public static byte[] intToBytes(int intValue) {
        return intToBytes(intValue, DEFAULT_ORDER);
    }

    /**
     * int转byte数组<br> 自定义端序
     *
     * @param intValue  int值
     * @param byteOrder 端序
     * @return byte数组
     */
    public static byte[] intToBytes(int intValue, ByteOrder byteOrder) {

        if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
            return new byte[]{ //
                    (byte) (intValue & 0xFF), //
                    (byte) ((intValue >> 8) & 0xFF), //
                    (byte) ((intValue >> 16) & 0xFF), //
                    (byte) ((intValue >> 24) & 0xFF) //
            };

        } else {
            return new byte[]{ //
                    (byte) ((intValue >> 24) & 0xFF), //
                    (byte) ((intValue >> 16) & 0xFF), //
                    (byte) ((intValue >> 8) & 0xFF), //
                    (byte) (intValue & 0xFF) //
            };
        }

    }

    /**
     * long转byte数组<br> 默认以小端序转换<br> from: https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
     *
     * @param longValue long值
     * @return byte数组
     */
    public static byte[] longToBytes(long longValue) {
        return longToBytes(longValue, DEFAULT_ORDER);
    }

    /**
     * long转byte数组<br> 自定义端序<br> from: https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
     *
     * @param longValue long值
     * @param byteOrder 端序
     * @return byte数组
     */
    public static byte[] longToBytes(long longValue, ByteOrder byteOrder) {
        byte[] result = new byte[Long.BYTES];
        if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
            for (int i = 0; i < result.length; i++) {
                result[i] = (byte) (longValue & 0xFF);
                longValue >>= Byte.SIZE;
            }
        } else {
            for (int i = (result.length - 1); i >= 0; i--) {
                result[i] = (byte) (longValue & 0xFF);
                longValue >>= Byte.SIZE;
            }
        }
        return result;
    }

    /**
     * byte数组转long<br> 默认以小端序转换<br> from: https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
     *
     * @param bytes byte数组
     * @return long值
     */
    public static long bytesToLong(byte[] bytes) {
        return bytesToLong(bytes, DEFAULT_ORDER);
    }

    /**
     * byte数组转long<br> 自定义端序<br> from: https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
     *
     * @param bytes     byte数组
     * @param byteOrder 端序
     * @return long值
     */
    public static long bytesToLong(byte[] bytes, ByteOrder byteOrder) {
        return bytesToLong(bytes, 0, byteOrder);
    }

    /**
     * byte数组转long<br> 自定义端序<br> from: https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
     *
     * @param bytes     byte数组
     * @param start     计算数组开始位置
     * @param byteOrder 端序
     * @return long值
     */
    public static long bytesToLong(byte[] bytes, int start, ByteOrder byteOrder) {
        long values = 0;
        if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
            for (int i = (Long.BYTES - 1); i >= 0; i--) {
                values <<= Byte.SIZE;
                values |= (bytes[i + start] & 0xff);
            }
        } else {
            for (int i = 0; i < Long.BYTES; i++) {
                values <<= Byte.SIZE;
                values |= (bytes[i + start] & 0xff);
            }
        }

        return values;
    }

    /**
     * float转byte数组，默认以小端序转换<br>
     *
     * @param floatValue float值
     * @return byte数组
     */
    public static byte[] floatToBytes(float floatValue) {
        return floatToBytes(floatValue, DEFAULT_ORDER);
    }

    /**
     * float转byte数组，自定义端序<br>
     *
     * @param floatValue float值
     * @param byteOrder  端序
     * @return byte数组
     */
    public static byte[] floatToBytes(float floatValue, ByteOrder byteOrder) {
        return intToBytes(Float.floatToIntBits(floatValue), byteOrder);
    }

    /**
     * byte数组转float<br> 默认以小端序转换<br>
     *
     * @param bytes byte数组
     * @return float值
     */
    public static double bytesToFloat(byte[] bytes) {
        return bytesToFloat(bytes, DEFAULT_ORDER);
    }

    /**
     * byte数组转float<br> 自定义端序<br>
     *
     * @param bytes     byte数组
     * @param byteOrder 端序
     * @return float值
     */
    public static float bytesToFloat(byte[] bytes, ByteOrder byteOrder) {
        return Float.intBitsToFloat(bytesToInt(bytes, byteOrder));
    }

    /**
     * double转byte数组<br> 默认以小端序转换<br>
     *
     * @param doubleValue double值
     * @return byte数组
     */
    public static byte[] doubleToBytes(double doubleValue) {
        return doubleToBytes(doubleValue, DEFAULT_ORDER);
    }

    /**
     * double转byte数组<br> 自定义端序<br> from: https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
     *
     * @param doubleValue double值
     * @param byteOrder   端序
     * @return byte数组
     */
    public static byte[] doubleToBytes(double doubleValue, ByteOrder byteOrder) {
        return longToBytes(Double.doubleToLongBits(doubleValue), byteOrder);
    }

    /**
     * byte数组转Double<br> 默认以小端序转换<br>
     *
     * @param bytes byte数组
     * @return long值
     */
    public static double bytesToDouble(byte[] bytes) {
        return bytesToDouble(bytes, DEFAULT_ORDER);
    }

    /**
     * byte数组转double<br> 自定义端序<br>
     *
     * @param bytes     byte数组
     * @param byteOrder 端序
     * @return long值
     */
    public static double bytesToDouble(byte[] bytes, ByteOrder byteOrder) {
        return Double.longBitsToDouble(bytesToLong(bytes, byteOrder));
    }

    /**
     * 将{@link Number}转换为
     *
     * @param number 数字
     * @return bytes
     */
    public static byte[] numberToBytes(Number number) {
        return numberToBytes(number, DEFAULT_ORDER);
    }

    /**
     * 将{@link Number}转换为
     *
     * @param number    数字
     * @param byteOrder 端序
     * @return bytes
     */
    public static byte[] numberToBytes(Number number, ByteOrder byteOrder) {
        if (number instanceof Double) {
            return doubleToBytes((Double) number, byteOrder);
        } else if (number instanceof Long) {
            return longToBytes((Long) number, byteOrder);
        } else if (number instanceof Integer) {
            return intToBytes((Integer) number, byteOrder);
        } else if (number instanceof Short) {
            return shortToBytes((Short) number, byteOrder);
        } else if (number instanceof Float) {
            return floatToBytes((Float) number, byteOrder);
        } else {
            return doubleToBytes(number.doubleValue(), byteOrder);
        }
    }

    /**
     * byte数组转换为指定类型数字
     *
     * @param <T>         数字类型
     * @param bytes       byte数组
     * @param targetClass 目标数字类型
     * @param byteOrder   端序
     * @return 转换后的数字
     * @throws IllegalArgumentException 不支持的数字类型，如用户自定义数字类型
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T bytesToNumber(byte[] bytes, Class<T> targetClass, ByteOrder byteOrder) throws IllegalArgumentException {
        Number number;
        if (Byte.class == targetClass) {
            number = bytes[0];
        } else if (Short.class == targetClass) {
            number = bytesToShort(bytes, byteOrder);
        } else if (Integer.class == targetClass) {
            number = bytesToInt(bytes, byteOrder);
        } else if (AtomicInteger.class == targetClass) {
            number = new AtomicInteger(bytesToInt(bytes, byteOrder));
        } else if (Long.class == targetClass) {
            number = bytesToLong(bytes, byteOrder);
        } else if (AtomicLong.class == targetClass) {
            number = new AtomicLong(bytesToLong(bytes, byteOrder));
        } else if (LongAdder.class == targetClass) {
            final LongAdder longValue = new LongAdder();
            longValue.add(bytesToLong(bytes, byteOrder));
            number = longValue;
        } else if (Float.class == targetClass) {
            number = bytesToFloat(bytes, byteOrder);
        } else if (Double.class == targetClass) {
            number = bytesToDouble(bytes, byteOrder);
        } else if (DoubleAdder.class == targetClass) {
            final DoubleAdder doubleAdder = new DoubleAdder();
            doubleAdder.add(bytesToDouble(bytes, byteOrder));
            number = doubleAdder;
        } else if (BigDecimal.class == targetClass) {
            number = BigDecimal.valueOf(bytesToDouble(bytes, byteOrder));
        } else if (BigInteger.class == targetClass) {
            number = BigInteger.valueOf(bytesToLong(bytes, byteOrder));
        } else if (Number.class == targetClass) {
            // 用户没有明确类型具体类型，默认Double
            number = bytesToDouble(bytes, byteOrder);
        } else {
            // 用户自定义类型不支持
            throw new IllegalArgumentException("Unsupported Number type: " + targetClass.getName());
        }

        return (T) number;
    }

    /**
     * Returns the {@code int} nearest in value to {@code value}.
     *
     * @param value any {@code long} value
     * @return the same value cast to {@code int} if it is in the range of the {@code int} type,
     * {@link Integer#MAX_VALUE} if it is too large, or {@link Integer#MIN_VALUE} if it is too
     * small
     */
    public static int saturatedCast(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }


    /**
     * 数字转字符串<br>
     * 调用{@link Number#toString()}，并去除尾小数点儿后多余的0
     *
     * @param number       A Number
     * @param defaultValue 如果number参数为{@code null}，返回此默认值
     * @return A String.
     * @since 3.0.9
     */
    public static String toStr(final Number number, final String defaultValue) {
        return (null == number) ? defaultValue : toStr(number);
    }

    /**
     * 数字转字符串<br>
     * 调用{@link Number#toString()}或 {@link BigDecimal#toPlainString()}，并去除尾小数点儿后多余的0
     *
     * @param number A Number
     * @return A String.
     */
    public static String toStr(final Number number) {
        return toStr(number, true);
    }

    /**
     * 数字转字符串<br>
     * 调用{@link Number#toString()}或 {@link BigDecimal#toPlainString()}，并去除尾小数点儿后多余的0
     *
     * @param number               A Number
     * @param isStripTrailingZeros 是否去除末尾多余0，例如5.0返回5
     * @return A String.
     */
    public static String toStr(final Number number, final boolean isStripTrailingZeros) {
        Assert.notNull(number, "Number is null !");

        // BigDecimal单独处理，使用非科学计数法
        if (number instanceof BigDecimal) {
            return toStr((BigDecimal) number, isStripTrailingZeros);
        }

        Assert.isTrue(isValidNumber(number), "Number is non-finite!");
        // 去掉小数点儿后多余的0
        String string = number.toString();
        if (isStripTrailingZeros) {
            if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0) {
                while (string.endsWith("0")) {
                    string = string.substring(0, string.length() - 1);
                }
                if (string.endsWith(".")) {
                    string = string.substring(0, string.length() - 1);
                }
            }
        }
        return string;
    }

    /**
     * {@link BigDecimal}数字转字符串<br>
     * 调用{@link BigDecimal#toPlainString()}，并去除尾小数点儿后多余的0
     *
     * @param bigDecimal A {@link BigDecimal}
     * @return A String.
     * @since 5.4.6
     */
    public static String toStr(final BigDecimal bigDecimal) {
        return toStr(bigDecimal, true);
    }

    /**
     * {@link BigDecimal}数字转字符串<br>
     * 调用{@link BigDecimal#toPlainString()}，可选去除尾小数点儿后多余的0
     *
     * @param bigDecimal           A {@link BigDecimal}
     * @param isStripTrailingZeros 是否去除末尾多余0，例如5.0返回5
     * @return A String.
     * @since 5.4.6
     */
    public static String toStr(BigDecimal bigDecimal, final boolean isStripTrailingZeros) {
        Assert.notNull(bigDecimal, "BigDecimal is null !");
        if (isStripTrailingZeros) {
            bigDecimal = bigDecimal.stripTrailingZeros();
        }
        return bigDecimal.toPlainString();
    }
    // endregion

    /**
     * 数字转{@link BigDecimal}<br>
     * Float、Double等有精度问题，转换为字符串后再转换<br>
     * null转换为0
     *
     * @param number 数字
     * @return {@link BigDecimal}
     * @since 4.0.9
     */
    public static BigDecimal toBigDecimal(final Number number) {
        if (null == number) {
            return BigDecimal.ZERO;
        }

        if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        } else if (number instanceof Long) {
            return new BigDecimal((Long) number);
        } else if (number instanceof Integer) {
            return new BigDecimal((Integer) number);
        } else if (number instanceof BigInteger) {
            return new BigDecimal((BigInteger) number);
        }

        // Float、Double等有精度问题，转换为字符串后再转换
        return toBigDecimal(number.toString());
    }

    /**
     * 数字转{@link BigDecimal}<br>
     * null或""或空白符转换为0
     *
     * @param numberStr 数字字符串
     * @return {@link BigDecimal}
     * @since 4.0.9
     */
    public static BigDecimal toBigDecimal(final String numberStr) {
        if (StringUtils.isBlank(numberStr)) {
            return BigDecimal.ZERO;
        }

        try {
            // 支持类似于 1,234.55 格式的数字
            final Number number = parseNumber(numberStr);
            if (number instanceof BigDecimal) {
                return (BigDecimal) number;
            } else {
                return new BigDecimal(number.toString());
            }
        } catch (final Exception ignore) {
            // 忽略解析错误
        }

        return new BigDecimal(numberStr);
    }

    /**
     * 数字转{@link BigInteger}<br>
     * null转换为0
     *
     * @param number 数字
     * @return {@link BigInteger}
     * @since 5.4.5
     */
    public static BigInteger toBigInteger(final Number number) {
        if (null == number) {
            return BigInteger.ZERO;
        }

        if (number instanceof BigInteger) {
            return (BigInteger) number;
        } else if (number instanceof Long) {
            return BigInteger.valueOf((Long) number);
        }

        return toBigInteger(number.longValue());
    }

    /**
     * 数字转{@link BigInteger}<br>
     * null或""或空白符转换为0
     *
     * @param number 数字字符串
     * @return {@link BigInteger}
     * @since 5.4.5
     */
    public static BigInteger toBigInteger(final String number) {
        return StringUtils.isBlank(number) ? BigInteger.ZERO : new BigInteger(number);
    }

    // region ----- parse

    /**
     * 解析转换数字字符串为 {@link java.lang.Integer } 规则如下：
     *
     * <pre>
     * 1、0x开头的视为16进制数字
     * 2、0开头的忽略开头的0
     * 3、其它情况按照10进制转换
     * 4、空串返回0
     * 5、.123形式返回0（按照小于0的小数对待）
     * 6、123.56截取小数点之前的数字，忽略小数部分
     * 7、解析失败返回默认值
     * </pre>
     *
     * @param numberStr    数字字符串，支持0x开头、0开头和普通十进制
     * @param defaultValue 如果解析失败, 将返回defaultValue, 允许null
     * @return Integer
     */
    public static Integer parseInt(final String numberStr, final Integer defaultValue) {
        if (StringUtils.isNotBlank(numberStr)) {
            try {
                return parseInt(numberStr);
            } catch (final NumberFormatException ignore) {
                // ignore
            }
        }
        return defaultValue;
    }

    /**
     * 解析转换数字字符串为int型数字，规则如下：
     *
     * <pre>
     * 1、0x开头的视为16进制数字
     * 2、0开头的忽略开头的0
     * 3、其它情况按照10进制转换
     * 4、空串返回0
     * 5、.123形式返回0（按照小于0的小数对待）
     * 6、123.56截取小数点之前的数字，忽略小数部分
     * 7、科学计数法抛出NumberFormatException异常
     * </pre>
     *
     * @param numberStr 数字，支持0x开头、0开头和普通十进制
     * @return int
     * @throws NumberFormatException 数字格式异常
     * @since 4.1.4
     */
    public static int parseInt(final String numberStr) throws NumberFormatException {
        return NumberParser.INSTANCE.parseInt(numberStr);
    }

    /**
     * 解析转换数字字符串为 {@link java.lang.Long } 规则如下：
     *
     * <pre>
     * 1、0x开头的视为16进制数字
     * 2、0开头的忽略开头的0
     * 3、其它情况按照10进制转换
     * 4、空串返回0
     * 5、.123形式返回0（按照小于0的小数对待）
     * 6、123.56截取小数点之前的数字，忽略小数部分
     * 7、解析失败返回默认值
     * </pre>
     *
     * @param numberStr    数字字符串，支持0x开头、0开头和普通十进制
     * @param defaultValue 如果解析失败, 将返回defaultValue, 允许null
     * @return Long
     */
    public static Long parseLong(final String numberStr, final Long defaultValue) {
        if (StringUtils.isNotBlank(numberStr)) {
            try {
                return parseLong(numberStr);
            } catch (final NumberFormatException ignore) {
                // ignore
            }
        }

        return defaultValue;
    }

    /**
     * 解析转换数字字符串为long型数字，规则如下：
     *
     * <pre>
     * 1、0x开头的视为16进制数字
     * 2、0开头的忽略开头的0
     * 3、空串返回0
     * 4、其它情况按照10进制转换
     * 5、.123形式返回0（按照小于0的小数对待）
     * 6、123.56截取小数点之前的数字，忽略小数部分
     * </pre>
     *
     * @param numberStr 数字，支持0x开头、0开头和普通十进制
     * @return long
     * @since 4.1.4
     */
    public static long parseLong(final String numberStr) {
        return NumberParser.INSTANCE.parseLong(numberStr);
    }

    /**
     * 解析转换数字字符串为 {@link java.lang.Float } 规则如下：
     *
     * <pre>
     * 1、0开头的忽略开头的0
     * 2、空串返回0
     * 3、其它情况按照10进制转换
     * 4、.123形式返回0.123（按照小于0的小数对待）
     * </pre>
     *
     * @param numberStr    数字字符串，支持0x开头、0开头和普通十进制
     * @param defaultValue 如果解析失败, 将返回defaultValue, 允许null
     * @return Float
     */
    public static Float parseFloat(final String numberStr, final Float defaultValue) {
        if (StringUtils.isNotBlank(numberStr)) {
            try {
                return parseFloat(numberStr);
            } catch (final NumberFormatException ignore) {
                // ignore
            }
        }

        return defaultValue;
    }

    /**
     * 解析转换数字字符串为long型数字，规则如下：
     *
     * <pre>
     * 1、0开头的忽略开头的0
     * 2、空串返回0
     * 3、其它情况按照10进制转换
     * 4、.123形式返回0.123（按照小于0的小数对待）
     * </pre>
     *
     * @param numberStr 数字，支持0x开头、0开头和普通十进制
     * @return long
     * @since 5.5.5
     */
    public static float parseFloat(final String numberStr) {
        return NumberParser.INSTANCE.parseFloat(numberStr);
    }

    /**
     * 解析转换数字字符串为 {@link java.lang.Double } 规则如下：
     *
     * <pre>
     * 1、0开头的忽略开头的0
     * 2、空串返回0
     * 3、其它情况按照10进制转换
     * 4、.123形式返回0.123（按照小于0的小数对待）
     * </pre>
     *
     * @param numberStr    数字字符串，支持0x开头、0开头和普通十进制
     * @param defaultValue 如果解析失败, 将返回defaultValue, 允许null
     * @return Double
     */
    public static Double parseDouble(final String numberStr, final Double defaultValue) {
        if (StringUtils.isNotBlank(numberStr)) {
            try {
                return parseDouble(numberStr);
            } catch (final NumberFormatException ignore) {
                // ignore
            }
        }
        return defaultValue;
    }

    /**
     * 解析转换数字字符串为long型数字，规则如下：
     *
     * <pre>
     * 1、0开头的忽略开头的0
     * 2、空串返回0
     * 3、其它情况按照10进制转换
     * 4、.123形式返回0.123（按照小于0的小数对待）
     * 5、NaN返回0
     * </pre>
     *
     * @param numberStr 数字，支持0x开头、0开头和普通十进制
     * @return double
     * @since 5.5.5
     */
    public static double parseDouble(final String numberStr) {
        return NumberParser.INSTANCE.parseDouble(numberStr);
    }

    /**
     * 将指定字符串转换为{@link Number }
     * 此方法不支持科学计数法
     *
     * @param numberStr    Number字符串
     * @param defaultValue 如果解析失败, 将返回defaultValue, 允许null
     * @return Number对象
     */
    public static Number parseNumber(final String numberStr, final Number defaultValue) {
        if (StringUtils.isNotBlank(numberStr)) {
            try {
                return parseNumber(numberStr);
            } catch (final NumberFormatException ignore) {
                // ignore
            }
        }
        return defaultValue;
    }

    /**
     * 将指定字符串转换为{@link Number} 对象<br>
     * 此方法不支持科学计数法
     *
     * <p>
     * 需要注意的是，在不同Locale下，数字的表示形式也是不同的，例如：<br>
     * 德国、荷兰、比利时、丹麦、意大利、罗马尼亚和欧洲大多地区使用`,`区分小数<br>
     * 也就是说，在这些国家地区，1.20表示120，而非1.2。
     * </p>
     *
     * @param numberStr Number字符串
     * @return Number对象
     * @throws NumberFormatException 包装了{@link ParseException}，当给定的数字字符串无法解析时抛出
     * @since 4.1.15
     */
    public static Number parseNumber(final String numberStr) throws NumberFormatException {
        return NumberParser.INSTANCE.parseNumber(numberStr);
    }

    /**
     * 将指定字符串转换为{@link Number} 对象<br>
     * 此方法不支持科学计数法
     *
     * <p>
     * 需要注意的是，在不同Locale下，数字的表示形式也是不同的，例如：<br>
     * 德国、荷兰、比利时、丹麦、意大利、罗马尼亚和欧洲大多地区使用`,`区分小数<br>
     * 也就是说，在这些国家地区，1.20表示120，而非1.2。
     * </p>
     *
     * @param numberStr Number字符串
     * @param locale    地区，不同地区数字表示方式不同
     * @return Number对象
     * @throws NumberFormatException 包装了{@link ParseException}，当给定的数字字符串无法解析时抛出
     */
    public static Number parseNumber(final String numberStr, final Locale locale) throws NumberFormatException {
        return NumberParser.of(locale).parseNumber(numberStr);
    }
    // endregion
    // region ----- range

    /**
     * 生成一个从0开始的数字列表<br>
     *
     * @param stopIncluded 结束的数字（不包含）
     * @return 数字列表
     */
    public static int[] range(final int stopIncluded) {
        return range(0, stopIncluded, 1);
    }

    /**
     * 生成一个数字列表<br>
     * 自动判定正序反序
     *
     * @param startInclude 开始的数字（包含）
     * @param stopIncluded 结束的数字（包含）
     * @return 数字列表
     */
    public static int[] range(final int startInclude, final int stopIncluded) {
        return range(startInclude, stopIncluded, 1);
    }

    /**
     * 生成一个数字列表<br>
     * 自动判定正序反序
     *
     * @param startInclude 开始的数字（包含）
     * @param stopIncluded 结束的数字（不包含）
     * @param step         步进
     * @return 数字列表
     */
    public static int[] range(int startInclude, int stopIncluded, int step) {
        if (startInclude > stopIncluded) {
            final int tmp = startInclude;
            startInclude = stopIncluded;
            stopIncluded = tmp;
        }

        if (step <= 0) {
            step = 1;
        }

        final int deviation = stopIncluded + 1 - startInclude;
        int length = deviation / step;
        if (deviation % step != 0) {
            length += 1;
        }
        final int[] range = new int[length];
        for (int i = 0; i < length; i++) {
            range[i] = startInclude;
            startInclude += step;
        }
        return range;
    }

    /**
     * 将给定范围内的整数添加到已有集合中，步进为1
     *
     * @param start  开始（包含）
     * @param stop   结束（包含）
     * @param values 集合
     * @return 集合
     */
    public static Collection<Integer> appendRange(final int start, final int stop, final Collection<Integer> values) {
        return appendRange(start, stop, 1, values);
    }

    /**
     * 将给定范围内的整数添加到已有集合中
     *
     * @param startInclude 开始（包含）
     * @param stopInclude  结束（包含）
     * @param step         步进
     * @param values       集合
     * @return 集合
     */
    public static Collection<Integer> appendRange(final int startInclude, final int stopInclude, int step, final Collection<Integer> values) {
        if (startInclude < stopInclude) {
            step = Math.abs(step);
        } else if (startInclude > stopInclude) {
            step = -Math.abs(step);
        } else {// start == end
            values.add(startInclude);
            return values;
        }

        for (int i = startInclude; (step > 0) ? i <= stopInclude : i >= stopInclude; i += step) {
            values.add(i);
        }
        return values;
    }
    // endregion

    /**
     * Compares two {@code int} values numerically. This is the same functionality as provided in Java 7.
     *
     * @param x the first {@code int} to compare
     * @param y the second {@code int} to compare
     * @return the value {@code 0} if {@code x == y};
     *         a value less than {@code 0} if {@code x < y}; and
     *         a value greater than {@code 0} if {@code x > y}
     * @since 3.4
     */
    public static int compare(final int x, final int y) {
        if (x == y) {
            return 0;
        }
        return x < y ? -1 : 1;
    }

    /**
     * Compares to {@code long} values numerically. This is the same functionality as provided in Java 7.
     *
     * @param x the first {@code long} to compare
     * @param y the second {@code long} to compare
     * @return the value {@code 0} if {@code x == y};
     *         a value less than {@code 0} if {@code x < y}; and
     *         a value greater than {@code 0} if {@code x > y}
     * @since 3.4
     */
    public static int compare(final long x, final long y) {
        if (x == y) {
            return 0;
        }
        return x < y ? -1 : 1;
    }

    /**
     * Compares to {@code short} values numerically. This is the same functionality as provided in Java 7.
     *
     * @param x the first {@code short} to compare
     * @param y the second {@code short} to compare
     * @return the value {@code 0} if {@code x == y};
     *         a value less than {@code 0} if {@code x < y}; and
     *         a value greater than {@code 0} if {@code x > y}
     * @since 3.4
     */
    public static int compare(final short x, final short y) {
        if (x == y) {
            return 0;
        }
        return x < y ? -1 : 1;
    }

    /**
     * Compares two {@code byte} values numerically. This is the same functionality as provided in Java 7.
     *
     * @param x the first {@code byte} to compare
     * @param y the second {@code byte} to compare
     * @return the value {@code 0} if {@code x == y};
     *         a value less than {@code 0} if {@code x < y}; and
     *         a value greater than {@code 0} if {@code x > y}
     * @since 3.4
     */
    public static int compare(final byte x, final byte y) {
        return x - y;
    }
}
