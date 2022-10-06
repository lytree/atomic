

package top.lytree.base;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;


public final class Predicates {

    private Predicates() {
    }
    // interface which specifies an accept(PredicateVisitor) method.

    /**
     * Returns a predicate that always evaluates to {@code true}.
     */

    public static <T> Predicate<T> alwaysTrue() {
        return ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
    }

    /**
     * Returns a predicate that always evaluates to {@code false}.
     */

    public static <T> Predicate<T> alwaysFalse() {
        return ObjectPredicate.ALWAYS_FALSE.withNarrowedType();
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the object reference being tested is null.
     */

    public static <T> Predicate<T> isNull() {
        return ObjectPredicate.IS_NULL.withNarrowedType();
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the object reference being tested is not null.
     */

    public static <T> Predicate<T> notNull() {
        return ObjectPredicate.NOT_NULL.withNarrowedType();
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the given predicate evaluates to {@code false}.
     */
    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return new NotPredicate<>(predicate);
    }

    /**
     * Returns a predicate that evaluates to {@code true} if each of its components evaluates to {@code true}. The components are evaluated in order, and evaluation will be
     * "short-circuited" as soon as a false predicate is found. It defensively copies the iterable passed in, so future changes to it won't alter the behavior of this predicate. If
     * {@code components} is empty, the returned predicate will always evaluate to {@code true}.
     */
    public static <T> Predicate<T> and(
            Iterable<? extends Predicate<? super T>> components) {
        return new AndPredicate<>(defensiveCopy(components));
    }

    /**
     * Returns a predicate that evaluates to {@code true} if each of its components evaluates to {@code true}. The components are evaluated in order, and evaluation will be
     * "short-circuited" as soon as a false predicate is found. It defensively copies the array passed in, so future changes to it won't alter the behavior of this predicate. If
     * {@code components} is empty, the returned predicate will always evaluate to {@code true}.
     */
    @SafeVarargs
    public static <T> Predicate<T> and(Predicate<? super T>... components) {
        return new AndPredicate<T>(defensiveCopy(components));
    }

    /**
     * Returns a predicate that evaluates to {@code true} if both of its components evaluate to {@code true}. The components are evaluated in order, and evaluation will be
     * "short-circuited" as soon as a false predicate is found.
     */
    public static <T> Predicate<T> and(
            Predicate<? super T> first, Predicate<? super T> second) {
        return new AndPredicate<>(Predicates.<T>asList(Assert.notNull(first), Assert.notNull(second)));
    }

    /**
     * Returns a predicate that evaluates to {@code true} if any one of its components evaluates to {@code true}. The components are evaluated in order, and evaluation will be
     * "short-circuited" as soon as a true predicate is found. It defensively copies the iterable passed in, so future changes to it won't alter the behavior of this predicate. If
     * {@code components} is empty, the returned predicate will always evaluate to {@code false}.
     */
    public static <T> Predicate<T> or(
            Iterable<? extends Predicate<? super T>> components) {
        return new OrPredicate<>(defensiveCopy(components));
    }

    /**
     * Returns a predicate that evaluates to {@code true} if any one of its components evaluates to {@code true}. The components are evaluated in order, and evaluation will be
     * "short-circuited" as soon as a true predicate is found. It defensively copies the array passed in, so future changes to it won't alter the behavior of this predicate. If
     * {@code components} is empty, the returned predicate will always evaluate to {@code false}.
     */
    @SafeVarargs
    public static <T> Predicate<T> or(Predicate<? super T>... components) {
        return new OrPredicate<T>(defensiveCopy(components));
    }

    /**
     * Returns a predicate that evaluates to {@code true} if either of its components evaluates to {@code true}. The components are evaluated in order, and evaluation will be
     * "short-circuited" as soon as a true predicate is found.
     */
    public static <T> Predicate<T> or(
            Predicate<? super T> first, Predicate<? super T> second) {
        return new OrPredicate<>(Predicates.<T>asList(Assert.notNull(first), Assert.notNull(second)));
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the object being tested {@code equals()} the given target or both are null.
     */
    public static <T> Predicate<T> equalTo(T target) {
        return (target == null)
                ? Predicates.<T>isNull()
                : new IsEqualToPredicate(target).withNarrowedType();
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the object being tested is an instance of the given class. If the object being tested is {@code null} this predicate
     * evaluates to {@code false}.
     *
     * <p>If you want to filter an {@code Iterable} to narrow its type, consider using {@link
     *
     * <p><b>Warning:</b> contrary to the typical assumptions about predicates (as documented at
     * {@link Predicate#apply}), the returned predicate may not be <i>consistent with equals</i>. For example, {@code instanceOf(ArrayList.class)} will yield different results for
     * the two equal instances {@code Lists.newArrayList(1)} and {@code Arrays.asList(1)}.
     */

    public static <T> Predicate<T> instanceOf(Class<?> clazz) {
        return new InstanceOfPredicate<>(clazz);
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the class being tested is assignable to (is a subtype of) {@code clazz}. Example:
     *
     * <pre>{@code
     * List<Class<?>> classes = Arrays.asList(
     *     Object.class, String.class, Number.class, Long.class);
     * return Iterables.filter(classes, subtypeOf(Number.class));
     * }</pre>
     * <p>
     * The code above returns an iterable containing {@code Number.class} and {@code Long.class}.
     *
     * 
     */


    public static Predicate<Class<?>> subtypeOf(Class<?> clazz) {
        return new SubtypeOfPredicate(clazz);
    }

    /**
     * Returns a predicate that evaluates to {@code true} if the object reference being tested is a member of the given collection. It does not defensively copy the collection
     * passed in, so future changes to it will alter the behavior of the predicate.
     *
     * <p>This method can technically accept any {@code Collection<?>}, but using a typed collection
     * helps prevent bugs. This approach doesn't block any potential users since it is always possible to use {@code Predicates.<Object>in()}.
     *
     * @param target the collection that may contain the function input
     */
    public static <T> Predicate<T> in(Collection<? extends T> target) {
        return new InPredicate<>(target);
    }

    /**
     * Returns the composition of a function and a predicate. For every {@code x}, the generated predicate returns {@code predicate(function(x))}.
     *
     * @return the composition of the provided function and predicate
     */
    public static <A, B> Predicate<A> compose(
            Predicate<B> predicate, Function<A, ? extends B> function) {
        return new CompositionPredicate<>(predicate, function);
    }

    // End public API, begin private implementation classes.

    // Package private for GWT serialization.
    enum ObjectPredicate implements Predicate<Object> {
        /**
         * @see Predicates#alwaysTrue()
         */
        ALWAYS_TRUE {
            @Override
            public boolean apply(Object o) {
                return true;
            }

            @Override
            public String toString() {
                return "Predicates.alwaysTrue()";
            }
        },
        /**
         * @see Predicates#alwaysFalse()
         */
        ALWAYS_FALSE {
            @Override
            public boolean apply(Object o) {
                return false;
            }

            @Override
            public String toString() {
                return "Predicates.alwaysFalse()";
            }
        },
        /**
         * @see Predicates#isNull()
         */
        IS_NULL {
            @Override
            public boolean apply(Object o) {
                return o == null;
            }

            @Override
            public String toString() {
                return "Predicates.isNull()";
            }
        },
        /**
         * @see Predicates#notNull()
         */
        NOT_NULL {
            @Override
            public boolean apply(Object o) {
                return o != null;
            }

            @Override
            public String toString() {
                return "Predicates.notNull()";
            }
        };

        @SuppressWarnings("unchecked")
            // safe contravariant cast
        <T> Predicate<T> withNarrowedType() {
            return (Predicate<T>) this;
        }
    }

    /**
     * @see Predicates#not(Predicate)
     */
    private static class NotPredicate<T>
            implements Predicate<T>, Serializable {

        final Predicate<T> predicate;

        NotPredicate(Predicate<T> predicate) {
            this.predicate = Assert.notNull(predicate);
        }

        @Override
        public boolean apply(T t) {
            return !predicate.apply(t);
        }

        @Override
        public int hashCode() {
            return ~predicate.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof NotPredicate) {
                NotPredicate<?> that = (NotPredicate<?>) obj;
                return predicate.equals(that.predicate);
            }
            return false;
        }

        @Override
        public String toString() {
            return "Predicates.not(" + predicate + ")";
        }

        private static final long serialVersionUID = 1L;
    }

    /**
     * @see Predicates#and(Iterable)
     */
    private static class AndPredicate<T>
            implements Predicate<T>, Serializable {

        private final List<? extends Predicate<? super T>> components;

        private AndPredicate(List<? extends Predicate<? super T>> components) {
            this.components = components;
        }

        @Override
        public boolean apply(T t) {
            // Avoid using the Iterator to avoid generating garbage (issue 820).
            for (Predicate<? super T> component : components) {
                if (!component.apply(t)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            // add a random number to avoid collisions with OrPredicate
            return components.hashCode() + 0x12472c2c;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof AndPredicate) {
                AndPredicate<?> that = (AndPredicate<?>) obj;
                return components.equals(that.components);
            }
            return false;
        }

        @Override
        public String toString() {
            return toStringHelper("and", components);
        }

        private static final long serialVersionUID = 1L;
    }

    /**
     * @see Predicates#or(Iterable)
     */
    private static class OrPredicate<T>
            implements Predicate<T>, Serializable {

        private final List<? extends Predicate<? super T>> components;

        private OrPredicate(List<? extends Predicate<? super T>> components) {
            this.components = components;
        }

        @Override
        public boolean apply(T t) {
            // Avoid using the Iterator to avoid generating garbage (issue 820).
            for (Predicate<? super T> component : components) {
                if (component.apply(t)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            // add a random number to avoid collisions with AndPredicate
            return components.hashCode() + 0x053c91cf;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof OrPredicate) {
                OrPredicate<?> that = (OrPredicate<?>) obj;
                return components.equals(that.components);
            }
            return false;
        }

        @Override
        public String toString() {
            return toStringHelper("or", components);
        }

        private static final long serialVersionUID = 1L;
    }

    private static String toStringHelper(String methodName, Iterable<?> components) {
        StringBuilder builder = new StringBuilder("Predicates.").append(methodName).append('(');
        boolean first = true;
        for (Object o : components) {
            if (!first) {
                builder.append(',');
            }
            builder.append(o);
            first = false;
        }
        return builder.append(')').toString();
    }

    /**
     * @see Predicates#equalTo(Object)
     */
    private static class IsEqualToPredicate implements Predicate<Object>, Serializable {

        private final Object target;

        private IsEqualToPredicate(Object target) {
            this.target = target;
        }

        @Override
        public boolean apply(Object o) {
            return target.equals(o);
        }

        @Override
        public int hashCode() {
            return target.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof IsEqualToPredicate) {
                IsEqualToPredicate that = (IsEqualToPredicate) obj;
                return target.equals(that.target);
            }
            return false;
        }

        @Override
        public String toString() {
            return "Predicates.equalTo(" + target + ")";
        }

        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unchecked")
            // safe contravariant cast
        <T> Predicate<T> withNarrowedType() {
            return (Predicate<T>) this;
        }
    }

    /**
     * @see Predicates#instanceOf(Class)
     */

    private static class InstanceOfPredicate<T>
            implements Predicate<T>, Serializable {

        private final Class<?> clazz;

        private InstanceOfPredicate(Class<?> clazz) {
            this.clazz = Assert.notNull(clazz);
        }

        @Override
        public boolean apply(T o) {
            return clazz.isInstance(o);
        }

        @Override
        public int hashCode() {
            return clazz.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof InstanceOfPredicate) {
                InstanceOfPredicate<?> that = (InstanceOfPredicate<?>) obj;
                return clazz == that.clazz;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Predicates.instanceOf(" + clazz.getName() + ")";
        }

        private static final long serialVersionUID = 1L;
    }

    /**
     * @see Predicates#subtypeOf(Class)
     */

    private static class SubtypeOfPredicate implements Predicate<Class<?>>, Serializable {

        private final Class<?> clazz;

        private SubtypeOfPredicate(Class<?> clazz) {
            this.clazz = Assert.notNull(clazz);
        }

        @Override
        public boolean apply(Class<?> input) {
            return clazz.isAssignableFrom(input);
        }

        @Override
        public int hashCode() {
            return clazz.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SubtypeOfPredicate) {
                SubtypeOfPredicate that = (SubtypeOfPredicate) obj;
                return clazz == that.clazz;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Predicates.subtypeOf(" + clazz.getName() + ")";
        }

        private static final long serialVersionUID = 1L;
    }

    /**
     * @see Predicates#in(Collection)
     */
    private static class InPredicate<T>
            implements Predicate<T>, Serializable {

        private final Collection<?> target;

        private InPredicate(Collection<?> target) {
            this.target = Assert.notNull(target);
        }

        @Override
        public boolean apply(T t) {
            try {
                return target.contains(t);
            } catch (NullPointerException | ClassCastException e) {
                return false;
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof InPredicate) {
                InPredicate<?> that = (InPredicate<?>) obj;
                return target.equals(that.target);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return target.hashCode();
        }

        @Override
        public String toString() {
            return "Predicates.in(" + target + ")";
        }

        private static final long serialVersionUID = 1L;
    }

    /**
     * @see Predicates#compose(Predicate, Function)
     */
    private static class CompositionPredicate<A, B>
            implements Predicate<A>, Serializable {

        final Predicate<B> p;
        final Function<A, ? extends B> f;

        private CompositionPredicate(Predicate<B> p, Function<A, ? extends B> f) {
            this.p = Assert.notNull(p);
            this.f = Assert.notNull(f);
        }

        @Override
        public boolean apply(A a) {
            return p.apply(f.apply(a));
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CompositionPredicate) {
                CompositionPredicate<?, ?> that = (CompositionPredicate<?, ?>) obj;
                return f.equals(that.f) && p.equals(that.p);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return f.hashCode() ^ p.hashCode();
        }

        @Override
        public String toString() {
            // TODO(cpovirk): maybe make this look like the method call does ("Predicates.compose(...)")
            return p + "(" + f + ")";
        }

        private static final long serialVersionUID = 1L;
    }


    private static <T> List<Predicate<? super T>> asList(
            Predicate<? super T> first, Predicate<? super T> second) {
        // TODO(kevinb): understand why we still get a warning despite @SafeVarargs!
        return Arrays.<Predicate<? super T>>asList(first, second);
    }

    private static <T> List<T> defensiveCopy(T... array) {
        return defensiveCopy(Arrays.asList(array));
    }

    static <T> List<T> defensiveCopy(Iterable<T> iterable) {
        ArrayList<T> list = new ArrayList<>();
        for (T element : iterable) {
            list.add(Assert.notNull(element));
        }
        return list;
    }
}