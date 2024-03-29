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

package top.lytree.io.output;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import top.lytree.io.UncheckedIO;


/**
 * An {@link Appendable} implementation that throws {@link UncheckedIOException} instead of {@link IOException}.
 *
 * @see Appendable
 * @see IOException
 * @see UncheckedIOException
 *
 */
class UncheckedAppendableImpl implements UncheckedAppendable {

    private final Appendable appendable;

    UncheckedAppendableImpl(final Appendable appendable) {
        this.appendable = Objects.requireNonNull(appendable, "appendable");
    }

    @Override
    public UncheckedAppendable append(final char c) {
        UncheckedIO.apply(appendable::append, c);
        return this;
    }

    @Override
    public UncheckedAppendable append(final CharSequence csq) {
        UncheckedIO.apply(appendable::append, csq);
        return this;
    }

    @Override
    public UncheckedAppendable append(final CharSequence csq, final int start, final int end) {
        UncheckedIO.apply(appendable::append, csq, start, end);
        return this;
    }

    @Override
    public String toString() {
        return appendable.toString();
    }

}
