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
package top.lytree.exception;

import java.io.File;
import java.io.IOException;
import java.io.Serial;

/**
 * 文件不存在异常
 */
public class FileExistsException extends IOException {

    /**
     * Defines the serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Default Constructor.
     */
    public FileExistsException() {
    }

    /**
     * Constructs an instance with the specified file.
     *
     * @param file The file that exists
     */
    public FileExistsException(final File file) {
        super("File " + file + " exists");
    }

    /**
     * Constructs an instance with the specified message.
     *
     * @param message The error message
     */
    public FileExistsException(final String message) {
        super(message);
    }

}
