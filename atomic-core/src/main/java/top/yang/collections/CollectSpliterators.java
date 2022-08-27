/*
 * Copyright (C) 2015 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.yang.collections;

import static java.lang.Math.max;


import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import top.yang.lang.Assert;

final class CollectSpliterators {

    private CollectSpliterators() {
    }

    /**
     * Implements the {@link Stream#flatMap} operation on spliterators.
     *
     * @param <InElementT>      the element type of the input spliterator
     * @param <OutElementT>     the element type of the output spliterators
     * @param <OutSpliteratorT> the type of the output spliterators
     */
    abstract static class FlatMapSpliterator<
            InElementT extends Object,
            OutElementT extends Object,
            OutSpliteratorT extends Spliterator<OutElementT>>
            implements Spliterator<OutElementT> {

        /**
         * Factory for constructing {@link FlatMapSpliterator} instances.
         */
        @FunctionalInterface
        interface Factory<InElementT extends Object, OutSpliteratorT extends Spliterator<?>> {

            OutSpliteratorT newFlatMapSpliterator(
                    OutSpliteratorT prefix,
                    Spliterator<InElementT> fromSplit,
                    Function<? super InElementT, OutSpliteratorT> function,
                    int splitCharacteristics,
                    long estSplitSize);
        }

        OutSpliteratorT prefix;
        final Spliterator<InElementT> from;
        final Function<? super InElementT, OutSpliteratorT> function;
        final Factory<InElementT, OutSpliteratorT> factory;
        int characteristics;
        long estimatedSize;

        FlatMapSpliterator(
                OutSpliteratorT prefix,
                Spliterator<InElementT> from,
                Function<? super InElementT, OutSpliteratorT> function,
                Factory<InElementT, OutSpliteratorT> factory,
                int characteristics,
                long estimatedSize) {
            this.prefix = prefix;
            this.from = from;
            this.function = function;
            this.factory = factory;
            this.characteristics = characteristics;
            this.estimatedSize = estimatedSize;
        }

        /*
         * The tryAdvance and forEachRemaining in FlatMapSpliteratorOfPrimitive are overloads of these
         * methods, not overrides. They are annotated @Override because they implement methods from
         * Spliterator.OfPrimitive (and override default implementations from Spliterator.OfPrimitive or
         * a subtype like Spliterator.OfInt).
         */

        @Override
        public final boolean tryAdvance(Consumer<? super OutElementT> action) {
            while (true) {
                if (prefix != null && prefix.tryAdvance(action)) {
                    if (estimatedSize != Long.MAX_VALUE) {
                        estimatedSize--;
                    }
                    return true;
                } else {
                    prefix = null;
                }
                if (!from.tryAdvance(fromElement -> prefix = function.apply(fromElement))) {
                    return false;
                }
            }
        }

        @Override
        public final void forEachRemaining(Consumer<? super OutElementT> action) {
            if (prefix != null) {
                prefix.forEachRemaining(action);
                prefix = null;
            }
            from.forEachRemaining(
                    fromElement -> {
                        Spliterator<OutElementT> elements = function.apply(fromElement);
                        if (elements != null) {
                            elements.forEachRemaining(action);
                        }
                    });
            estimatedSize = 0;
        }

        @Override

        public final OutSpliteratorT trySplit() {
            Spliterator<InElementT> fromSplit = from.trySplit();
            if (fromSplit != null) {
                int splitCharacteristics = characteristics & ~Spliterator.SIZED;
                long estSplitSize = estimateSize();
                if (estSplitSize < Long.MAX_VALUE) {
                    estSplitSize /= 2;
                    this.estimatedSize -= estSplitSize;
                    this.characteristics = splitCharacteristics;
                }
                OutSpliteratorT result =
                        factory.newFlatMapSpliterator(
                                this.prefix, fromSplit, function, splitCharacteristics, estSplitSize);
                this.prefix = null;
                return result;
            } else if (prefix != null) {
                OutSpliteratorT result = prefix;
                this.prefix = null;
                return result;
            } else {
                return null;
            }
        }

        @Override
        public final long estimateSize() {
            if (prefix != null) {
                estimatedSize = max(estimatedSize, prefix.estimateSize());
            }
            return max(estimatedSize, 0);
        }

        @Override
        public final int characteristics() {
            return characteristics;
        }
    }

    /**
     * Implementation of {@link Stream#flatMap} with an object spliterator output type.
     *
     * <p>To avoid having this type, we could use {@code FlatMapSpliterator} directly. The main
     * advantages to having the type are the ability to use its constructor reference below and the parallelism with the primitive version. In short, it makes its caller ({@code
     * flatMap}) simpler.
     *
     * @param <InElementT>  the element type of the input spliterator
     * @param <OutElementT> the element type of the output spliterators
     */
    static final class FlatMapSpliteratorOfObject<
            InElementT extends Object, OutElementT extends Object>
            extends FlatMapSpliterator<InElementT, OutElementT, Spliterator<OutElementT>> {

        FlatMapSpliteratorOfObject(
                Spliterator<OutElementT> prefix,
                Spliterator<InElementT> from,
                Function<? super InElementT, Spliterator<OutElementT>> function,
                int characteristics,
                long estimatedSize) {
            super(
                    prefix, from, function, FlatMapSpliteratorOfObject::new, characteristics, estimatedSize);
        }
    }

    /**
     * Returns a {@code Spliterator} that iterates over the elements of the spliterators generated by applying {@code function} to the elements of {@code fromSpliterator}.
     */
    static <InElementT extends Object, OutElementT extends Object>
    Spliterator<OutElementT> flatMap(
            Spliterator<InElementT> fromSpliterator,
            Function<? super InElementT, Spliterator<OutElementT>> function,
            int topCharacteristics,
            long topSize) {
        Assert.checkArgument(
                (topCharacteristics & Spliterator.SUBSIZED) == 0,
                "flatMap does not support SUBSIZED characteristic");
        Assert.checkArgument(
                (topCharacteristics & Spliterator.SORTED) == 0,
                "flatMap does not support SORTED characteristic");
        Assert.notNull(fromSpliterator);
        Assert.notNull(function);
        return new FlatMapSpliteratorOfObject<>(
                null, fromSpliterator, function, topCharacteristics, topSize);
    }


}
