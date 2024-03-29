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
package top.lytree.io.filefilter;

import java.io.File;
import java.io.Serializable;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;
import top.lytree.io.FilenameUtils;
import top.lytree.io.IOCase;

/**
 * Filters files using the supplied wildcards.
 * <p>
 * This filter selects files and directories based on one or more wildcards. Testing is case-sensitive by default, but this can be configured.
 * </p>
 * <p>
 * The wildcard matcher uses the characters '?' and '*' to represent a single or multiple wildcard characters. This is the same as often found on DOS/Unix command lines. The check
 * is case-sensitive by default. See {@link FilenameUtils#wildcardMatchOnSystem(String, String)} for more information.
 * </p>
 * <p>
 * For example:
 * </p>
 * <h2>Using Classic IO</h2>
 * <pre>
 * File dir = FileUtils.current();
 * FileFilter fileFilter = new WildcardFileFilter("*test*.java~*~");
 * File[] files = dir.listFiles(fileFilter);
 * for (String file : files) {
 *     System.out.println(file);
 * }
 * </pre>
 *
 * <h2>Using NIO</h2>
 * <pre>
 * final Path dir = PathUtils.current();
 * final AccumulatorPathVisitor visitor = AccumulatorPathVisitor.withLongCounters(new WildcardFileFilter("*test*.java~*~"));
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
 *
 */
public class WildcardFileFilter extends AbstractFileFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The wildcards that will be used to match file names.
     */
    private final String[] wildcards;

    /**
     * Whether the comparison is case sensitive.
     */
    private final IOCase ioCase;

    /**
     * Constructs a new case-sensitive wildcard filter for a list of wildcards.
     *
     * @param wildcards the list of wildcards to match, not null
     * @throws IllegalArgumentException if the pattern list is null
     * @throws ClassCastException       if the list does not contain Strings
     */
    public WildcardFileFilter(final List<String> wildcards) {
        this(wildcards, IOCase.SENSITIVE);
    }

    /**
     * Constructs a new wildcard filter for a list of wildcards specifying case-sensitivity.
     *
     * @param wildcards the list of wildcards to match, not null
     * @param ioCase    how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the pattern list is null
     * @throws ClassCastException       if the list does not contain Strings
     */
    public WildcardFileFilter(final List<String> wildcards, final IOCase ioCase) {
        requireNonNull(wildcards, "wildcards");
        this.wildcards = wildcards.toArray(EMPTY_STRING_ARRAY);
        this.ioCase = IOCase.value(ioCase, IOCase.SENSITIVE);
    }

    /**
     * Constructs a new case-sensitive wildcard filter for a single wildcard.
     *
     * @param wildcard the wildcard to match
     * @throws IllegalArgumentException if the pattern is null
     */
    public WildcardFileFilter(final String wildcard) {
        this(wildcard, IOCase.SENSITIVE);
    }

    /**
     * Constructs a new case-sensitive wildcard filter for an array of wildcards.
     *
     * @param wildcards the array of wildcards to match
     * @throws IllegalArgumentException if the pattern array is null
     */
    public WildcardFileFilter(final String... wildcards) {
        this(wildcards, IOCase.SENSITIVE);
    }

    /**
     * Constructs a new wildcard filter for a single wildcard specifying case-sensitivity.
     *
     * @param wildcard the wildcard to match, not null
     * @param ioCase   how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the pattern is null
     */
    public WildcardFileFilter(final String wildcard, final IOCase ioCase) {
        requireNonNull(wildcard, "wildcard");
        this.wildcards = new String[]{wildcard};
        this.ioCase = IOCase.value(ioCase, IOCase.SENSITIVE);
    }

    /**
     * Constructs a new wildcard filter for an array of wildcards specifying case-sensitivity.
     *
     * @param wildcards the array of wildcards to match, not null
     * @param ioCase    how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the pattern array is null
     */
    public WildcardFileFilter(final String[] wildcards, final IOCase ioCase) {
        requireNonNull(wildcards, "wildcards");
        this.wildcards = wildcards.clone();
        this.ioCase = IOCase.value(ioCase, IOCase.SENSITIVE);
    }

    /**
     * Checks to see if the file name matches one of the wildcards.
     *
     * @param file the file to check
     * @return true if the file name matches one of the wildcards
     */
    @Override
    public boolean accept(final File file) {
        return accept(file.getName());
    }

    /**
     * Checks to see if the file name matches one of the wildcards.
     *
     * @param dir  the file directory (ignored)
     * @param name the file name
     * @return true if the file name matches one of the wildcards
     */
    @Override
    public boolean accept(final File dir, final String name) {
        return accept(name);
    }

    /**
     * Checks to see if the file name matches one of the wildcards.
     *
     * @param file the file to check
     * @return true if the file name matches one of the wildcards.
     *
     */
    @Override
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return toFileVisitResult(accept(Objects.toString(file.getFileName(), null)));
    }

    private boolean accept(final String name) {
        for (final String wildcard : wildcards) {
            if (FilenameUtils.wildcardMatch(name, wildcard, ioCase)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Provide a String representation of this file filter.
     *
     * @return a String representation
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("(");
        for (int i = 0; i < wildcards.length; i++) {
            if (i > 0) {
                buffer.append(",");
            }
            buffer.append(wildcards[i]);
        }
        buffer.append(")");
        return buffer.toString();
    }

}
