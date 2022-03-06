/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.

 * According to cos feature, we modify some class，comment, field name, etc.
 */


package top.yang.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;


public class DateUtils {

    private static final ZoneId GMT = ZoneId.of("GMT");
    private static final long MILLI_SECONDS_OF_365_DAYS = 365L * 24 * 60 * 60 * 1000;
    /**
     * RFC 822 format
     */
    protected static final DateTimeFormatter rfc822DateFormat = DateTimeFormatter.ofPattern
            ("EEE, dd MMM yyyy HH:mm:ss 'GMT'").withLocale(Locale.US).withZone(GMT);

    /**
     * ISO 8601 format
     */
    protected static final DateTimeFormatter iso8601DateFormat =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(GMT);

    /**
     * Alternate ISO 8601 format without fractional seconds
     */
    protected static final DateTimeFormatter alternateIso8601DateFormat =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(GMT);

    /**
     * Formats the specified date as an RFC 822 string.
     *
     * @param date The date to format.
     * @return The RFC 822 string representing the specified date.
     */
    public static String formatRFC822Date(LocalDateTime date) {
        return rfc822DateFormat.format(date);
    }

    /**
     * Parses the specified date string as an RFC 822 date and returns the Date object.
     *
     * @param dateString The date string to parse.
     * @return The parsed Date object.
     */
    public static LocalDateTime parseRFC822Date(String dateString) {
        return LocalDateTime.parse(dateString, rfc822DateFormat);
    }

    /**
     * Formats the specified date as an ISO 8601 string.
     *
     * @param date The date to format.
     * @return The ISO 8601 string representing the specified date.
     */
    public static String formatISO8601Date(LocalDateTime date) {
        return iso8601DateFormat.format(date);
    }

    /**
     * Parses the specified date string as an ISO 8601 date and returns the Date object.
     *
     * @param dateString The date string to parse.
     * @return The parsed Date object.
     */
    public static LocalDateTime parseISO8601Date(String dateString) {

        // For EC2 Spot Fleet.
        if (dateString.endsWith("+0000")) {
            dateString = dateString
                    .substring(0, dateString.length() - 5)
                    .concat("Z");
        }

        try {

            return LocalDateTime.parse(dateString, iso8601DateFormat);
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(dateString, alternateIso8601DateFormat);
                // If the first ISO 8601 parser didn't work, try the alternate
                // version which doesn't include fractional seconds
            } catch (Exception oops) {
                // no the alternative route doesn't work; let's bubble up the original exception
                throw e;
            }
        }
    }
}
