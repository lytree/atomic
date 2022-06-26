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

package top.yang.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import top.yang.io.function.IOBiFunction;
import top.yang.io.function.IOConsumer;
import top.yang.io.function.IOFunction;
import top.yang.io.function.IORunnable;
import top.yang.io.function.IOSupplier;
import top.yang.io.function.IOTriFunction;

/**
 * Helps use lambdas that throw {@link IOException} rethrow as {@link UncheckedIOException}.
 *
 *
 */
public class UncheckedIO {

    /**
     * Accepts an IO consumer with the given argument.
     *
     * @param <T>      the return type of the operations.
     * @param t        the consumer argument.
     * @param consumer Consumes the value.
     * @throws UncheckedIOException if an I/O error occurs.
     */
    public static <T> void accept(final IOConsumer<T> consumer, final T t) {
        try {
            consumer.accept(t);
        } catch (final IOException e) {
            throw wrap(e);
        }
    }

    /**
     * Applies an IO bi-function with the given arguments.
     *
     * @param biFunction the function.
     * @param <T>        the first function argument type.
     * @param <U>        the second function argument type.
     * @param <R>        the return type.
     * @param t          the first function argument
     * @param u          the second function argument
     * @return the function result
     * @throws UncheckedIOException if an I/O error occurs.
     */
    public static <T, U, R> R apply(final IOBiFunction<T, U, R> biFunction, final T t, final U u) {
        try {
            return biFunction.apply(t, u);
        } catch (final IOException e) {
            throw wrap(e);
        }
    }

    /**
     * Applies an IO function with the given arguments.
     *
     * @param function the function.
     * @param <T>      the first function argument type.
     * @param <R>      the return type.
     * @param t        the first function argument
     * @return the function result
     * @throws UncheckedIOException if an I/O error occurs.
     */
    public static <T, R> R apply(final IOFunction<T, R> function, final T t) {
        try {
            return function.apply(t);
        } catch (final IOException e) {
            throw wrap(e);
        }
    }

    /**
     * Applies an IO tri-function with the given arguments.
     *
     * @param triFunction the function.
     * @param <T>         the first function argument type.
     * @param <U>         the second function argument type.
     * @param <V>         the third function argument type.
     * @param <R>         the return type.
     * @param t           the first function argument
     * @param u           the second function argument
     * @param v           the third function argument
     * @return the function result
     * @throws UncheckedIOException if an I/O error occurs.
     */
    public static <T, U, V, R> R apply(final IOTriFunction<T, U, V, R> triFunction, final T t, final U u, final V v) {
        try {
            return triFunction.apply(t, u, v);
        } catch (final IOException e) {
            throw wrap(e);
        }
    }

    /**
     * Creates a new UncheckedIOException for the given detail message.
     * <p>
     * This method exists because there is no String constructor in {@link UncheckedIOException}.
     * </p>
     *
     * @param e The exception to wrap.
     * @return a new {@link UncheckedIOException}.
     */
    private static UncheckedIOException wrap(final IOException e) {
        return new UncheckedIOException(e);
    }

    /**
     * Gets the result from an IO supplier.
     *
     * @param <T>      the return type of the operations.
     * @param supplier Supplies the return value.
     * @return result from the supplier.
     * @throws UncheckedIOException if an I/O error occurs.
     */
    public static <T> T get(final IOSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (final IOException e) {
            throw wrap(e);
        }
    }

    /**
     * Runs an IO runnable.
     *
     * @param runnable The runnable to run.
     * @throws UncheckedIOException if an I/O error occurs.
     */
    public static void run(final IORunnable runnable) {
        try {
            runnable.run();
        } catch (final IOException e) {
            throw wrap(e);
        }
    }
}
