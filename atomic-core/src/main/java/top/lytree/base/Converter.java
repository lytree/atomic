/*
 * Copyright (C) 2008 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package top.lytree.base;


import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;
import java.util.function.Function;


public abstract class Converter<A, B> {

    private final boolean handleNullAutomatically;

    // We lazily cache the reverse view to avoid allocating on every call to reverse().
    private transient Converter<B, A> reverse;

    /**
     * Constructor for use by subclasses.
     */
    protected Converter() {
        this(true);
    }

    /**
     * Constructor used only by {@code LegacyConverter} to suspend automatic null-handling.
     */
    Converter(boolean handleNullAutomatically) {
        this.handleNullAutomatically = handleNullAutomatically;
    }

    // SPI methods (what subclasses must implement)

    /**
     * Returns a representation of {@code a} as an instance of type {@code B}. If {@code a} cannot be converted, an unchecked exception (such as {@link IllegalArgumentException})
     * should be thrown.
     *
     * @param a the instance to convert; will never be null
     * @return the converted instance; <b>must not</b> be null
     */

    protected abstract B doForward(A a);

    /**
     * Returns a representation of {@code b} as an instance of type {@code A}. If {@code b} cannot be converted, an unchecked exception (such as {@link IllegalArgumentException})
     * should be thrown.
     *
     * @param b the instance to convert; will never be null
     * @return the converted instance; <b>must not</b> be null
     * @throws UnsupportedOperationException if backward conversion is not implemented; this should be very rare. Note that if backward conversion is not only unimplemented but
     *                                       unimplement<i>able</i> (for example, consider a {@code Converter<Chicken, ChickenNugget>}), then this is not logically a {@code
     *                                       Converter} at all, and should just implement {@link Function}.
     */

    protected abstract A doBackward(B b);

    // API (consumer-side) methods

    /**
     * Returns a representation of {@code a} as an instance of type {@code B}.
     *
     * @return the converted value; is null <i>if and only if</i> {@code a} is null
     */

    public final B convert(A a) {
        return correctedDoForward(a);
    }


    B correctedDoForward(A a) {
        if (handleNullAutomatically) {
            // TODO(kevinb): we shouldn't be checking for a null result at runtime. Assert?
            return a == null ? null : Assert.notNull(doForward(a));
        } else {
            return unsafeDoForward(a);
        }
    }


    A correctedDoBackward(B b) {
        if (handleNullAutomatically) {
            // TODO(kevinb): we shouldn't be checking for a null result at runtime. Assert?
            return b == null ? null : Assert.notNull(doBackward(b));
        } else {
            return unsafeDoBackward(b);
        }
    }


    private B unsafeDoForward(A a) {
        return doForward(a);
    }


    private A unsafeDoBackward(B b) {
        return doBackward(b);
    }

    /**
     * Returns an iterable that applies {@code convert} to each element of {@code fromIterable}. The conversion is done lazily.
     *
     * <p>The returned iterable's iterator supports {@code remove()} if the input iterator does. After
     * a successful {@code remove()} call, {@code fromIterable} no longer contains the corresponding element.
     */
    /*
     * Just as Converter could implement `Function< A,  B>` instead of `Function<A,
     * B>`, convertAll could accept and return iterables with nullable element types. In both cases,
     * we've chosen to instead use a signature that benefits existing users -- and is still safe.
     *
     * For convertAll, I haven't looked as closely at *how* much existing users benefit, so we should
     * keep an eye out for problems that new users encounter. Note also that convertAll could support
     * both use cases by using @PolyNull. (By contrast, we can't use @PolyNull for our superinterface
     * (`implements Function<@PolyNull A, @PolyNull B>`), at least as far as I know.)
     */
    public Iterable<B> convertAll(Iterable<? extends A> fromIterable) {
        Assert.notNull(fromIterable, "fromIterable");
        return () -> new Iterator<>() {
            private final Iterator<? extends A> fromIterator = fromIterable.iterator();

            @Override
            public boolean hasNext() {
                return fromIterator.hasNext();
            }

            @Override
            @SuppressWarnings("nullness") // See code comments on convertAll and Converter.apply.

            public B next() {
                return convert(fromIterator.next());
            }

            @Override
            public void remove() {
                fromIterator.remove();
            }
        };
    }

    /**
     * Returns the reversed view of this converter, which converts {@code this.convert(a)} back to a value roughly equivalent to {@code a}.
     *
     * <p>The returned converter is serializable if {@code this} converter is.
     *
     * <p><b>Note:</b> you should not override this method. It is non-final for legacy reasons.
     */
    public Converter<B, A> reverse() {
        Converter<B, A> result = reverse;
        return (result == null) ? reverse = new ReverseConverter<>(this) : result;
    }

    private static final class ReverseConverter<A, B> extends Converter<B, A>
            implements Serializable {

        final Converter<A, B> original;

        ReverseConverter(Converter<A, B> original) {
            this.original = original;
        }

        /*
         * These gymnastics are a little confusing. Basically this class has neither legacy nor
         * non-legacy behavior; it just needs to let the behavior of the backing converter shine
         * through. So, we override the correctedDo* methods, after which the do* methods should never
         * be reached.
         */

        @Override
        protected A doForward(B b) {
            throw new AssertionError();
        }

        @Override
        protected B doBackward(A a) {
            throw new AssertionError();
        }

        @Override
        A correctedDoForward(B b) {
            return original.correctedDoBackward(b);
        }

        @Override
        B correctedDoBackward(A a) {
            return original.correctedDoForward(a);
        }

        @Override
        public Converter<A, B> reverse() {
            return original;
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof ReverseConverter) {
                ReverseConverter<?, ?> that = (ReverseConverter<?, ?>) object;
                return this.original.equals(that.original);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return ~original.hashCode();
        }

        @Override
        public String toString() {
            return original + ".reverse()";
        }

        @Serial
        private static final long serialVersionUID = 1L;
    }

    /**
     * Returns a converter whose {@code convert} method applies {@code secondConverter} to the result of this converter. Its {@code reverse} method applies the converters in
     * reverse order.
     *
     * <p>The returned converter is serializable if {@code this} converter and {@code secondConverter}
     * are.
     */
    public final <C> Converter<A, C> andThen(Converter<B, C> secondConverter) {
        return doAndThen(secondConverter);
    }

    /**
     * Package-private non-final implementation of andThen() so only we can override it.
     */
    <C> Converter<A, C> doAndThen(Converter<B, C> secondConverter) {
        return new ConverterComposition<>(this, Assert.notNull(secondConverter));
    }

    private static final class ConverterComposition<A, B, C> extends Converter<A, C>
            implements Serializable {

        final Converter<A, B> first;
        final Converter<B, C> second;

        ConverterComposition(Converter<A, B> first, Converter<B, C> second) {
            this.first = first;
            this.second = second;
        }

        /*
         * These gymnastics are a little confusing. Basically this class has neither legacy nor
         * non-legacy behavior; it just needs to let the behaviors of the backing converters shine
         * through (which might even differ from each other!). So, we override the correctedDo* methods,
         * after which the do* methods should never be reached.
         */

        @Override
        protected C doForward(A a) {
            throw new AssertionError();
        }

        @Override
        protected A doBackward(C c) {
            throw new AssertionError();
        }

        @Override
        C correctedDoForward(A a) {
            return second.correctedDoForward(first.correctedDoForward(a));
        }

        @Override
        A correctedDoBackward(C c) {
            return first.correctedDoBackward(second.correctedDoBackward(c));
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof ConverterComposition<?, ?, ?> that) {
                return this.first.equals(that.first) && this.second.equals(that.second);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return 31 * first.hashCode() + second.hashCode();
        }

        @Override
        public String toString() {
            return first + ".andThen(" + second + ")";
        }

        @Serial
        private static final long serialVersionUID = 1L;
    }

    /**
     * Indicates whether another object is equal to this converter.
     *
     * <p>Most implementations will have no reason to override the behavior of {@link Object#equals}.
     * However, an implementation may also choose to return {@code true} whenever {@code object} is a {@link Converter} that it considers <i>interchangeable</i> with this one.
     * "Interchangeable"
     * <i>typically</i> means that {@code Objects.equal(this.convert(a), that.convert(a))} is true for
     * all {@code a} of type {@code A} (and similarly for {@code reverse}). Note that a {@code false} result from this method does not imply that the converters are known
     * <i>not</i> to be interchangeable.
     */
    @Override
    public boolean equals(Object object) {
        return super.equals(object);
    }

    // Static converters

    /**
     * Returns a converter based on separate forward and backward functions. This is useful if the function instances already exist, or so that you can supply lambda expressions.
     * If those circumstances don't apply, you probably don't need to use this; subclass {@code Converter} and implement its {@link #doForward} and {@link #doBackward} methods
     * directly.
     *
     * <p>These functions will never be passed {@code null} and must not under any circumstances
     * return {@code null}. If a value cannot be converted, the function should throw an unchecked exception (typically, but not necessarily, {@link IllegalArgumentException}).
     *
     * <p>The returned converter is serializable if both provided functions are.
     */
    public static <A, B> Converter<A, B> from(
            Function<? super A, ? extends B> forwardFunction,
            Function<? super B, ? extends A> backwardFunction) {
        return new FunctionBasedConverter<>(forwardFunction, backwardFunction);
    }

    private static final class FunctionBasedConverter<A, B> extends Converter<A, B>
            implements Serializable {

        private final Function<? super A, ? extends B> forwardFunction;
        private final Function<? super B, ? extends A> backwardFunction;

        private FunctionBasedConverter(
                Function<? super A, ? extends B> forwardFunction,
                Function<? super B, ? extends A> backwardFunction) {
            this.forwardFunction = Assert.notNull(forwardFunction);
            this.backwardFunction = Assert.notNull(backwardFunction);
        }

        @Override
        protected B doForward(A a) {
            return forwardFunction.apply(a);
        }

        @Override
        protected A doBackward(B b) {
            return backwardFunction.apply(b);
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof FunctionBasedConverter<?, ?> that) {
                return this.forwardFunction.equals(that.forwardFunction)
                        && this.backwardFunction.equals(that.backwardFunction);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return forwardFunction.hashCode() * 31 + backwardFunction.hashCode();
        }

        @Override
        public String toString() {
            return "Converter.from(" + forwardFunction + ", " + backwardFunction + ")";
        }
    }

    /**
     * Returns a serializable converter that always converts or reverses an object to itself.
     */
    @SuppressWarnings("unchecked") // implementation is "fully variant"
    public static <T> Converter<T, T> identity() {
        return (IdentityConverter<T>) IdentityConverter.INSTANCE;
    }

    /**
     * A converter that always converts or reverses an object to itself. Note that T is now a "pass-through type".
     */
    private static final class IdentityConverter<T> extends Converter<T, T> implements Serializable {

        static final IdentityConverter<?> INSTANCE = new IdentityConverter<>();

        @Override
        protected T doForward(T t) {
            return t;
        }

        @Override
        protected T doBackward(T t) {
            return t;
        }

        @Override
        public IdentityConverter<T> reverse() {
            return this;
        }

        @Override
        <S> Converter<T, S> doAndThen(Converter<T, S> otherConverter) {
            return Assert.notNull(otherConverter, "otherConverter");
        }

        /*
         * We *could* override convertAll() to return its input, but it's a rather pointless
         * optimization and opened up a weird type-safety problem.
         */

        @Override
        public String toString() {
            return "Converter.identity()";
        }

        @Serial
        private Object readResolve() {
            return INSTANCE;
        }

        @Serial
        private static final long serialVersionUID = 1L;
    }
}
