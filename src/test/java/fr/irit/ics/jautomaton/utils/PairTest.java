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
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

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
        Assert.assertEquals("getFirst must return the value used to build the pair", expected, result);
    }

    
    @Test
    public void testGetSecondIsSame() {
        Pair<Integer, Integer> aPair = getNewPair();
        Integer result = aPair.getSecond();
        Integer expected = DEFAULT_SECOND_ELEMENT;
        Assert.assertEquals("getSecond must return the value used to build the pair", expected, result);
    }

    
    @Test
    public void testHashCodeNotNull() {
        Pair<Integer, Integer> aPair = getNewPair();
        Integer result = aPair.hashCode();
        Integer notExpected = 0;
        Assert.assertFalse("Hashcode cannot be null", notExpected.equals(result));
    }

    
    @Test
    public void testEqualsOtherPairIsSame() {
        Pair<Integer, Integer> aPair1 = getNewPair();
        Pair<Integer, Integer> aPair2 = getNewPair();
        Boolean result1 = aPair1.equals(aPair2);
        Boolean result2 = aPair1.equals(aPair2);
        Assert.assertTrue("Two pairs with the same two elements must be equals", result1);
        Assert.assertTrue("Two pairs with the same two elements must be equals", result2);
    }

    
    @Test
    public void testEqualsOtherPairFirstElementIsDifferent() {
        Pair<Integer, Integer> aPair1 = getNewPair();
        Pair<Integer, Integer> aPair2 = getNewPair(DEFAULT_DIFFERENT_FIRST_ELEMENT, DEFAULT_SECOND_ELEMENT);
        Boolean result1 = aPair1.equals(aPair2);
        Boolean result2 = aPair1.equals(aPair2);
        Assert.assertFalse("Two pairs with two different elements must not be equals", result1);
        Assert.assertFalse("Two pairs with two different elements must not be equals", result2);
    }

    
    @Test
    public void testEqualsOtherPairSecondElementIsDifferent() {
        Pair<Integer, Integer> aPair1 = getNewPair();
        Pair<Integer, Integer> aPair2 = getNewPair(DEFAULT_FIRST_ELEMENT, DEFAULT_DIFFERENT_SECOND_ELEMENT);
        Boolean result1 = aPair1.equals(aPair2);
        Boolean result2 = aPair1.equals(aPair2);
        Assert.assertFalse("Two pairs with two different elements must not be equals", result1);
        Assert.assertFalse("Two pairs with two different elements must not be equals", result2);
    }

    
    @Test
    public void testEqualsOtherClassIsDifferent() {
        Pair<Integer, Integer> aPair1 = getNewPair();
        Object aPair2 = new Object();
        Boolean result1 = aPair1.equals(aPair2);
        Assert.assertFalse("A pair cannot be equals to an object from another class", result1);
    }

    
    @Test
    public void testEqualsNullObjectIsDifferent() {
        Pair<Integer, Integer> aPair1 = getNewPair();
        Object aPair2 = null;
        Boolean result1 = aPair1.equals(aPair2);
        Assert.assertFalse("A pair cannot be equals to a null object", result1);
    }

    
    @Test
    public void testToStringNotNull() {
        Pair<Integer, Integer> aPair = getNewPair();
        String result = aPair.toString();
        Boolean analyzed = Objects.nonNull(result);
        Assert.assertTrue("ToString cannot be null", analyzed);
    }

    
    @Test
    public void testToStringNotEmpty() {
        Pair<Integer, Integer> aPair = getNewPair();
        String result = aPair.toString();
        Boolean analyzed = !result.isEmpty();
        Assert.assertTrue("ToString cannot be empty", analyzed);
    }

}
