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
package top.lytree.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

/**
 * Access modes and factory methods for {@link RandomAccessFile}.
 *
 *
 */
public enum RandomAccessFileMode {

    /**
     * Mode "r" opens for reading only.
     */
    READ_ONLY("r"),

    /**
     * Mode "rw" opens for reading and writing.
     */
    READ_WRITE("rw"),

    /**
     * Mode "rws" opens for reading and writing, as with "rw", and also require that every update to the file's content or metadata be written synchronously to the underlying
     * storage device.
     */
    READ_WRITE_SYNC_ALL("rws"),

    /**
     * Mode "rwd" open for reading and writing, as with "rw", and also require that every update to the file's content be written synchronously to the underlying storage device.
     */
    READ_WRITE_SYNC_CONTENT("rwd");

    private final String mode;

    RandomAccessFileMode(final String mode) {
        this.mode = mode;
    }

    /**
     * Creates a random access file stream to read from, and optionally to write to, the file specified by the {@link File} argument.
     *
     * @param file the file object
     * @return a random access file stream
     * @throws FileNotFoundException See {@link RandomAccessFile#RandomAccessFile(File, String)}.
     */
    public RandomAccessFile create(final File file) throws FileNotFoundException {
        return new RandomAccessFile(file, mode);
    }

    /**
     * Creates a random access file stream to read from, and optionally to write to, the file specified by the {@link File} argument.
     *
     * @param file the file object
     * @return a random access file stream
     * @throws FileNotFoundException See {@link RandomAccessFile#RandomAccessFile(File, String)}.
     */
    public RandomAccessFile create(final Path file) throws FileNotFoundException {
        return create(file.toFile());
    }

    /**
     * 创建一个随机访问文件流，从{@link file}参数指定的文件中读取或可选地写入。
     *
     * @param file the file object
     * @return a random access file stream
     * @throws FileNotFoundException See {@link RandomAccessFile#RandomAccessFile(File, String)}.
     */
    public RandomAccessFile create(final String file) throws FileNotFoundException {
        return new RandomAccessFile(file, mode);
    }

    @Override
    public String toString() {
        return mode;
    }

}
