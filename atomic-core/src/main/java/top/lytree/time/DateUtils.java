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
package top.lytree.time;


import java.util.Calendar;
import java.util.Date;


/**
 * <p>A suite of utilities surrounding the use of the
 * {@link Calendar} and {@link Date} object.</p>
 *
 * <p>DateUtils contains a lot of common methods considering manipulations
 * of Dates or Calendars. Some methods require some extra explanation. The truncate, ceiling and round methods could be considered the Math.floor(), Math.ceil() or Math.round
 * versions for dates This way date-fields will be ignored in bottom-up order. As a complement to these methods we've introduced some fragment-methods. With these methods the
 * Date-fields will be ignored in top-down order. Since a date without a year is not a valid date, you have to decide in what kind of date-field you want your result, for instance
 * milliseconds or days.
 * </p>
 * <p>
 * Several methods are provided for adding to {@code Date} objects, of the form {@code addXXX(Date date, int amount)}. It is important to note these methods use a {@code Calendar}
 * internally (with default time zone and locale) and may be affected by changes to daylight saving time (DST).
 * </p>
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {


    /**
     * 当前时间的时间戳
     *
     * @return 时间
     */
    public static long current() {
        return System.currentTimeMillis();
    }

    /**
     * 当前时间的时间戳（秒）
     *
     * @return 当前时间秒数
     */
    public static long currentSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * Calls {@link System#nanoTime()}.
     */
    @SuppressWarnings("GoodTime") // reading system time without TimeSource
    public static long systemNanoTime() {
        return System.nanoTime();
    }
}
