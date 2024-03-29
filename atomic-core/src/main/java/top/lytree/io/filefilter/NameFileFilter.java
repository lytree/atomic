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
import top.lytree.io.IOCase;


/**
 * Filters file names for a certain name.
 * <p>
 * For example, to print all files and directories in the current directory whose name is {@code Test}:
 * </p>
 * <h2>Using Classic IO</h2>
 * <pre>
 * File dir = FileUtils.current();
 * String[] files = dir.list(new NameFileFilter("Test"));
 * for (String file : files) {
 *     System.out.println(file);
 * }
 * </pre>
 *
 * <h2>Using NIO</h2>
 * <pre>
 * final Path dir = PathUtils.current();
 * final AccumulatorPathVisitor visitor = AccumulatorPathVisitor.withLongCounters(new NameFileFilter("Test"));
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
 * @see FileFilterUtils#nameFileFilter(String)
 * @see FileFilterUtils#nameFileFilter(String, IOCase)
 *
 */
public class NameFileFilter extends AbstractFileFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The file names to search for
     */
    private final String[] names;

    /**
     * Whether the comparison is case sensitive.
     */
    private final IOCase ioCase;

    /**
     * Constructs a new case-sensitive name file filter for a list of names.
     *
     * @param names the names to allow, must not be null
     * @throws IllegalArgumentException if the name list is null
     * @throws ClassCastException       if the list does not contain Strings
     */
    public NameFileFilter(final List<String> names) {
        this(names, null);
    }

    /**
     * Constructs a new name file filter for a list of names specifying case-sensitivity.
     *
     * @param names  the names to allow, must not be null
     * @param ioCase how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the name list is null
     * @throws ClassCastException       if the list does not contain Strings
     */
    public NameFileFilter(final List<String> names, final IOCase ioCase) {
        requireNonNull(names, "names");
        this.names = names.toArray(EMPTY_STRING_ARRAY);
        this.ioCase = toIOCase(ioCase);
    }

    /**
     * Constructs a new case-sensitive name file filter for a single name.
     *
     * @param name the name to allow, must not be null
     * @throws IllegalArgumentException if the name is null
     */
    public NameFileFilter(final String name) {
        this(name, IOCase.SENSITIVE);
    }

    /**
     * Constructs a new case-sensitive name file filter for an array of names.
     * <p>
     * The array is not cloned, so could be changed after constructing the instance. This would be inadvisable however.
     * </p>
     *
     * @param names the names to allow, must not be null
     * @throws IllegalArgumentException if the names array is null
     */
    public NameFileFilter(final String... names) {
        this(names, IOCase.SENSITIVE);
    }

    /**
     * Constructs a new name file filter specifying case-sensitivity.
     *
     * @param name   the name to allow, must not be null
     * @param ioCase how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the name is null
     */
    public NameFileFilter(final String name, final IOCase ioCase) {
        if (name == null) {
            throw new IllegalArgumentException("The wildcard must not be null");
        }
        this.names = new String[]{name};
        this.ioCase = toIOCase(ioCase);
    }

    /**
     * Constructs a new name file filter for an array of names specifying case-sensitivity.
     *
     * @param names  the names to allow, must not be null
     * @param ioCase how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the names array is null
     */
    public NameFileFilter(final String[] names, final IOCase ioCase) {
        if (names == null) {
            throw new IllegalArgumentException("The array of names must not be null");
        }
        this.names = names.clone();
        this.ioCase = toIOCase(ioCase);
    }

    /**
     * Checks to see if the file name matches.
     *
     * @param file the File to check
     * @return true if the file name matches
     */
    @Override
    public boolean accept(final File file) {
        return acceptBaseName(file.getName());
    }

    /**
     * Checks to see if the file name matches.
     *
     * @param dir  the File directory (ignored)
     * @param name the file name
     * @return true if the file name matches
     */
    @Override
    public boolean accept(final File dir, final String name) {
        return acceptBaseName(name);
    }

    /**
     * Checks to see if the file name matches.
     *
     * @param file the File to check
     * @return true if the file name matches
     *
     */
    @Override
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return toFileVisitResult(acceptBaseName(Objects.toString(file.getFileName(), null)));
    }

    private boolean acceptBaseName(final String baseName) {
        for (final String testName : names) {
            if (ioCase.checkEquals(baseName, testName)) {
                return true;
            }
        }
        return false;
    }

    private IOCase toIOCase(final IOCase ioCase) {
        return IOCase.value(ioCase, IOCase.SENSITIVE);
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
        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append(names[i]);
            }
        }
        buffer.append(")");
        return buffer.toString();
    }

}
