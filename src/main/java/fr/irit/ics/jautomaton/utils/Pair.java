/*
 * Copyright 2020 David.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.irit.ics.jautomaton.utils;

import java.util.Objects;

/**
 * Supports the creation of typed pair of objects. As objects from this class may be used as Map keys, it implements
 * Comparable.
 *
 * @param <E1> the type of the first object of the pair
 * @param <E2> the type of the second object of the pair
 *
 * @author David Navarre
 */
public final class Pair<E1, E2> implements Comparable<Pair<E1, E2>> {

    /**
     * Default comparison value when comparing to a null value.
     */
    public static final int DEFAULT_COMPARISON_RESULT = 1;
    /**
     * Used to compute the hashcode.
     */
    private static final int HASHCODE_BASE = 5;
    /**
     * Used to compute the hashcode.
     */
    private static final int HASHCODE_MODIFIER = 53;
    /**
     * The first object.
     */
    private final E1 first;
    /**
     * The second object.
     */
    private final E2 second;

    /**
     * Build a typed pair of two objects.
     *
     * @param aFirstObject the first object
     * @param aSecondObject the second object
     */
    public Pair(final E1 aFirstObject, final E2 aSecondObject) {
        this.first = aFirstObject;
        this.second = aSecondObject;
    }

    @Override
    public int compareTo(final Pair<E1, E2> aPair) {
        if (Objects.isNull(aPair)) {
            return DEFAULT_COMPARISON_RESULT;
        } else {
            return this.hashCode() - aPair.hashCode();
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<?, ?> other = (Pair<?, ?>) obj;
        return (Objects.equals(this.first, other.first)
                && Objects.equals(this.second, other.second));
    }

    /**
     * Provides the first object of the pair.
     *
     * @return the first object
     */
    public E1 getFirst() {
        return first;
    }

    /**
     * Provides the second object of the pair.
     *
     * @return the second object
     */
    public E2 getSecond() {
        return second;
    }

    @Override
    public int hashCode() {
        int hash = HASHCODE_BASE;
        hash = HASHCODE_MODIFIER * hash + Objects.hashCode(this.first);
        hash = HASHCODE_MODIFIER * hash + Objects.hashCode(this.second);
        return hash;
    }

    @Override
    public String toString() {
        return "Pair{" + "<" + first + ", " + second + ">}";
    }

}
