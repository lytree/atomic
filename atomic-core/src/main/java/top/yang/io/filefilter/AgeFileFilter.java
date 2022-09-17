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
package top.yang.io.filefilter;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Date;
import top.yang.io.FileUtils;
import top.yang.io.file.PathUtils;

/**
 * Filters files based on a cutoff time, can filter either newer files or files equal to or older.
 * <p>
 * For example, to print all files and directories in the current directory older than one day:
 * </p>
 * <h2>Using Classic IO</h2>
 * <pre>
 * Path dir = PathUtils.current();
 * // We are interested in files older than one day
 * Instant cutoff = Instant.now().minus(Duration.ofDays(1));
 * String[] files = dir.list(new AgeFileFilter(cutoff));
 * for (String file : files) {
 *     System.out.println(file);
 * }
 * </pre>
 *
 * <h2>Using NIO</h2>
 * <pre>
 * Path dir = PathUtils.current();
 * // We are interested in files older than one day
 * Instant cutoff = Instant.now().minus(Duration.ofDays(1));
 * AccumulatorPathVisitor visitor = AccumulatorPathVisitor.withLongCounters(new AgeFileFilter(cutoff));
 * //
 * // Walk one dir
 * Files.<b>walkFileTree</b>(dir, Collections.emptySet(), 1, visitor);
 * System.out.println(visitor.getPathCounters());
 * System.out.println(visitor.getFileList());
 * //
 * visitor.getPathCounters().reset();
 * //
 * // Walk dir tree
 * Files.<b>walkFileTree</b>(dir, visitor);
 * System.out.println(visitor.getPathCounters());
 * System.out.println(visitor.getDirList());
 * System.out.println(visitor.getFileList());
 * </pre>
 *
 * @see FileFilterUtils#ageFileFilter(Date)
 * @see FileFilterUtils#ageFileFilter(File)
 * @see FileFilterUtils#ageFileFilter(long)
 * @see FileFilterUtils#ageFileFilter(Date, boolean)
 * @see FileFilterUtils#ageFileFilter(File, boolean)
 * @see FileFilterUtils#ageFileFilter(long, boolean)
 * 
 */
public class AgeFileFilter extends AbstractFileFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Whether the files accepted will be older or newer.
     */
    private final boolean acceptOlder;

    /**
     * The cutoff time threshold measured in milliseconds since the epoch (00:00:00 GMT, January 1, 1970).
     */
    private final Instant cutoffInstant;

    /**
     * Constructs a new age file filter for files older than (at or before) a certain cutoff date.
     *
     * @param cutoffDate the threshold age of the files
     */
    public AgeFileFilter(final Date cutoffDate) {
        this(cutoffDate, true);
    }

    /**
     * Constructs a new age file filter for files on any one side of a certain cutoff date.
     *
     * @param cutoffDate  the threshold age of the files
     * @param acceptOlder if true, older files (at or before the cutoff) are accepted, else newer ones (after the cutoff).
     */
    public AgeFileFilter(final Date cutoffDate, final boolean acceptOlder) {
        this(cutoffDate.toInstant(), acceptOlder);
    }

    /**
     * Constructs a new age file filter for files older than (at or before) a certain File (whose last modification time will be used as reference).
     *
     * @param cutoffReference the file whose last modification time is used as the threshold age of the files
     */
    public AgeFileFilter(final File cutoffReference) {
        this(cutoffReference, true);
    }

    /**
     * Constructs a new age file filter for files on any one side of a certain File (whose last modification time will be used as reference).
     *
     * @param cutoffReference the file whose last modification time is used as the threshold age of the files
     * @param acceptOlder     if true, older files (at or before the cutoff) are accepted, else newer ones (after the cutoff).
     */
    public AgeFileFilter(final File cutoffReference, final boolean acceptOlder) {
        this(FileUtils.lastModifiedUnchecked(cutoffReference), acceptOlder);
    }

    /**
     * Constructs a new age file filter for files equal to or older than a certain cutoff.
     *
     * @param cutoffInstant The cutoff time threshold since the epoch (00:00:00 GMT, January 1, 1970).
     * 
     */
    public AgeFileFilter(final Instant cutoffInstant) {
        this(cutoffInstant, true);
    }

    /**
     * Constructs a new age file filter for files on any one side of a certain cutoff.
     *
     * @param cutoffInstant The cutoff time threshold since the epoch (00:00:00 GMT, January 1, 1970).
     * @param acceptOlder   if true, older files (at or before the cutoff) are accepted, else newer ones (after the cutoff).
     * 
     */
    public AgeFileFilter(final Instant cutoffInstant, final boolean acceptOlder) {
        this.acceptOlder = acceptOlder;
        this.cutoffInstant = cutoffInstant;
    }

    /**
     * Constructs a new age file filter for files equal to or older than a certain cutoff
     *
     * @param cutoffMillis The cutoff time threshold measured in milliseconds since the epoch (00:00:00 GMT, January 1, 1970).
     */
    public AgeFileFilter(final long cutoffMillis) {
        this(Instant.ofEpochMilli(cutoffMillis), true);
    }

    /**
     * Constructs a new age file filter for files on any one side of a certain cutoff.
     *
     * @param cutoffMillis The cutoff time threshold measured in milliseconds since the epoch (00:00:00 GMT, January 1, 1970).
     * @param acceptOlder  if true, older files (at or before the cutoff) are accepted, else newer ones (after the cutoff).
     */
    public AgeFileFilter(final long cutoffMillis, final boolean acceptOlder) {
        this(Instant.ofEpochMilli(cutoffMillis), acceptOlder);
    }

    /**
     * Checks to see if the last modification of the file matches cutoff favorably.
     * <p>
     * If last modification time equals cutoff and newer files are required, file <b>IS NOT</b> selected. If last modification time equals cutoff and older files are required,
     * file
     * <b>IS</b> selected.
     * </p>
     *
     * @param file the File to check
     * @return true if the file name matches
     */
    @Override
    public boolean accept(final File file) {
        return acceptOlder != FileUtils.isFileNewer(file, cutoffInstant);
    }

    /**
     * Checks to see if the last modification of the file matches cutoff favorably.
     * <p>
     * If last modification time equals cutoff and newer files are required, file <b>IS NOT</b> selected. If last modification time equals cutoff and older files are required,
     * file
     * <b>IS</b> selected.
     * </p>
     *
     * @param file the File to check
     * @return true if the file name matches
     * 
     */
    @Override
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        try {
            return toFileVisitResult(acceptOlder != PathUtils.isNewer(file, cutoffInstant));
        } catch (final IOException e) {
            return handle(e);
        }
    }

    /**
     * Provide a String representation of this file filter.
     *
     * @return a String representation
     */
    @Override
    public String toString() {
        final String condition = acceptOlder ? "<=" : ">";
        return super.toString() + "(" + condition + cutoffInstant + ")";
    }
}
