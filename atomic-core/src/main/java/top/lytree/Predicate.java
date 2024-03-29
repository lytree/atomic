/*
 * Copyright (C) 2007 The Guava Authors
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

package top.lytree;

@FunctionalInterface
public interface Predicate<T> extends java.util.function.Predicate<T> {

    /**
     * Returns the result of applying this predicate to {@code input} (Java 8 users, see notes in the class documentation above). This method is <i>generally expected</i>, but not
     * absolutely required, to have the following properties:
     *
     * <ul>
     *   <li>Its execution does not cause any observable side effects.
     *   <li>The computation is <i>consistent with equals</i>; that is, {@link Objects#equals
     *       Objects.equal}{@code (a, b)} implies that {@code predicate.apply(a) ==
     *       predicate.apply(b))}.
     * </ul>
     *
     * @throws NullPointerException if {@code input} is null and this predicate does not accept null arguments
     */

    boolean apply(T input);

    /**
     * Indicates whether another object is equal to this predicate.
     *
     * <p>Most implementations will have no reason to override the behavior of {@link Object#equals}.
     * However, an implementation may also choose to return {@code true} whenever {@code object} is a {@link Predicate} that it considers <i>interchangeable</i> with this one.
     * "Interchangeable"
     * <i>typically</i> means that {@code this.apply(t) == that.apply(t)} for all {@code t} of type
     * {@code T}). Note that a {@code false} result from this method does not imply that the predicates are known <i>not</i> to be interchangeable.
     */
    @Override
    boolean equals(Object object);

    @Override
    default boolean test(T input) {
        return apply(input);
    }
}
