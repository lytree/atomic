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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * This filter accepts {@code File}s that are files (not directories).
 * <p>
 * For example, here is how to print out a list of the real files
 * within the current directory:
 * </p>
 * <h2>Using Classic IO</h2>
 * <pre>
 * File dir = FileUtils.current();
 * String[] files = dir.list(FileFileFilter.INSTANCE);
 * for (String file : files) {
 *     System.out.println(file);
 * }
 * </pre>
 *
 * <h2>Using NIO</h2>
 * <pre>
 * final Path dir = PathUtils.current();
 * final AccumulatorPathVisitor visitor = AccumulatorPathVisitor.withLongCounters(FileFileFilter.INSTANCE);
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
 * @see FileFilterUtils#fileFileFilter()
 */
public class FileFileFilter extends AbstractFileFilter implements Serializable {

    /**
     * Singleton instance of file filter.
     *
     *
     */
    public static final IOFileFilter INSTANCE = new FileFileFilter();

    /**
     * Singleton instance of file filter.
     *
     * @deprecated Use {@link #INSTANCE}.
     */
    @Deprecated
    public static final IOFileFilter FILE = INSTANCE;

    private static final long serialVersionUID = 1L;

    /**
     * Restrictive constructor.
     */
    protected FileFileFilter() {
    }

    /**
     * Checks to see if the file is a file.
     *
     * @param file  the File to check
     * @return true if the file is a file
     */
    @Override
    public boolean accept(final File file) {
        return file.isFile();
    }

    /**
     * Checks to see if the file is a file.
     * @param file  the File to check
     *
     * @return true if the file is a file
     *
     */
    @Override
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return toFileVisitResult(Files.isRegularFile(file));
    }

}
