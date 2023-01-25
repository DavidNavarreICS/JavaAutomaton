/*
 * Copyright 2020 navarre.
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author David Navarre.
 */
public class PairTest {

    public PairTest() {
    }

    private static final int DEFAULT_FIRST_ELEMENT = 0;
    private static final int DEFAULT_DIFFERENT_FIRST_ELEMENT = 1;
    private static final int DEFAULT_SECOND_ELEMENT = 0;
    private static final int DEFAULT_DIFFERENT_SECOND_ELEMENT = 1;

    private static Pair<Integer, Integer> getNewPair() {
        return getNewPair(DEFAULT_FIRST_ELEMENT, DEFAULT_SECOND_ELEMENT);
    }

    private static Pair<Integer, Integer> getNewPair(Integer e1, Integer e2) {
        return new Pair<>(e1, e2);
    }

    @Test
    public void testGetFirstIsSame() {
        Pair<Integer, Integer> aPair = getNewPair();
        Integer result = aPair.getFirst();
        Integer expected = DEFAULT_FIRST_ELEMENT;
        Assertions.assertEquals(expected, result, "getFirst must return the value used to build the pair");
    }

    @Test
    public void testGetSecondIsSame() {
        Pair<Integer, Integer> aPair = getNewPair();
        Integer result = aPair.getSecond();
        Integer expected = DEFAULT_SECOND_ELEMENT;
        Assertions.assertEquals(expected, result, "getSecond must return the value used to build the pair");
    }

    @Test
    public void testHashCodeNotNull() {
        Pair<Integer, Integer> aPair = getNewPair();
        Integer result = aPair.hashCode();
        Integer notExpected = 0;
        Assertions.assertFalse(notExpected.equals(result), "Hashcode cannot be null");
    }

    @Test
    public void testEqualsOtherPairIsSame() {
        Pair<Integer, Integer> aPair1 = getNewPair();
        Pair<Integer, Integer> aPair2 = getNewPair();
        Boolean result1 = aPair1.equals(aPair2);
        Boolean result2 = aPair1.equals(aPair2);
        Assertions.assertTrue(result1, "Two pairs with the same two elements must be equals");
        Assertions.assertTrue(result2, "Two pairs with the same two elements must be equals");
    }

    @Test
    public void testEqualsOtherPairFirstElementIsDifferent() {
        Pair<Integer, Integer> aPair1 = getNewPair();
        Pair<Integer, Integer> aPair2 = getNewPair(DEFAULT_DIFFERENT_FIRST_ELEMENT, DEFAULT_SECOND_ELEMENT);
        Boolean result1 = aPair1.equals(aPair2);
        Boolean result2 = aPair1.equals(aPair2);
        Assertions.assertFalse(result1, "Two pairs with two different elements must not be equals");
        Assertions.assertFalse(result2, "Two pairs with two different elements must not be equals");
    }

    @Test
    public void testEqualsOtherPairSecondElementIsDifferent() {
        Pair<Integer, Integer> aPair1 = getNewPair();
        Pair<Integer, Integer> aPair2 = getNewPair(DEFAULT_FIRST_ELEMENT, DEFAULT_DIFFERENT_SECOND_ELEMENT);
        Boolean result1 = aPair1.equals(aPair2);
        Boolean result2 = aPair1.equals(aPair2);
        Assertions.assertFalse(result1, "Two pairs with two different elements must not be equals");
        Assertions.assertFalse(result2, "Two pairs with two different elements must not be equals");
    }

    @Test
    public void testEqualsOtherClassIsDifferent() {
        Pair<Integer, Integer> aPair1 = getNewPair();
        Object aPair2 = new Object();
        Boolean result1 = aPair1.equals(aPair2);
        Assertions.assertFalse(result1, "A pair cannot be equals to an object from another class");
    }

    @Test
    public void testEqualsNullObjectIsDifferent() {
        Pair<Integer, Integer> aPair1 = getNewPair();
        Object aPair2 = null;
        Boolean result1 = aPair1.equals(aPair2);
        Assertions.assertFalse(result1, "A pair cannot be equals to a null object");
    }

    @Test
    public void testToStringNotNull() {
        Pair<Integer, Integer> aPair = getNewPair();
        String result = aPair.toString();
        Boolean analyzed = Objects.nonNull(result);
        Assertions.assertTrue(analyzed, "ToString cannot be null");
    }

    @Test
    public void testToStringNotEmpty() {
        Pair<Integer, Integer> aPair = getNewPair();
        String result = aPair.toString();
        Boolean analyzed = !result.isEmpty();
        Assertions.assertTrue(analyzed, "ToString cannot be empty");
    }

}
