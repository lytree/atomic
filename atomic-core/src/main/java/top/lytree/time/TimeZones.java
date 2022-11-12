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

import static top.lytree.time.ZoneIdEnum.CTT;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * Helps to deal with {@link java.util.TimeZone}s.
 *
 * @author pride
 */
public class TimeZones {


    /**
     * A public version of {@link java.util.TimeZone}'s package private {@code GMT_ID} field.
     */
    public static final String GMT_ID = "GMT";
    /**
     * 系统默认时区
     */
    private static final ZoneId ZONE = ZoneId.systemDefault();

    /**
     * 上海时区ID Asia/Shanghai
     */
    public static final String SHANGHAI_ZONE_ID = CTT.getZoneIdName();

    /**
     * 上海时区  Asia/Shanghai
     */
    public static final ZoneId SHANGHAI_ZONE = ZoneId.of(SHANGHAI_ZONE_ID);

    /**
     * {@link ZoneId}转换为{@link TimeZone}，{@code null}则返回系统默认值
     *
     * @param zoneId {@link ZoneId}，{@code null}则返回系统默认值
     *
     * @return {@link TimeZone}
     */
    public static TimeZone toTimeZone(final ZoneId zoneId) {
        if (null == zoneId) {
            return TimeZone.getDefault();
        }

        return TimeZone.getTimeZone(zoneId);
    }

    /**
     * {@link TimeZone}转换为{@link ZoneId}，{@code null}则返回系统默认值
     *
     * @param timeZone {@link TimeZone}，{@code null}则返回系统默认值
     *
     * @return {@link ZoneId}
     */
    public static ZoneId toZoneId(final TimeZone timeZone) {
        if (null == timeZone) {
            return ZoneId.systemDefault();
        }

        return timeZone.toZoneId();
    }
}
