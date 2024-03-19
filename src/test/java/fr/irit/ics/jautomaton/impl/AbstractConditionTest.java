/*
 * Copyright 2024 David Navarre &lt;David.Navarre at irit.fr&gt;.
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
package fr.irit.ics.jautomaton.impl;

import fr.irit.ics.jautomaton.Condition;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author David Navarre &lt;David.Navarre at irit.fr&gt;
 */
class AbstractConditionTest {

    private static final int CONDITION1_HASHCODE = 0;
    private static final int CONDITION2_HASHCODE = 1;
    private final Condition condition1 = new ConditionImpl(CONDITION1_HASHCODE);
    private final Condition condition2 = new ConditionImpl(CONDITION2_HASHCODE);

    AbstractConditionTest() {
    }

    @Test
    void testCompareTwoNonNullShouldSuccess() {
        int expectedComparison1 = CONDITION1_HASHCODE - CONDITION2_HASHCODE;
        int expectedComparison2 = CONDITION2_HASHCODE - CONDITION1_HASHCODE;

        int comparison1 = condition1.compareTo(condition2);
        int comparison2 = condition2.compareTo(condition1);

        Assertions.assertEquals(expectedComparison1, comparison1, "The comparison should be based on hashcode value.");
        Assertions.assertEquals(expectedComparison2, comparison2, "The comparison should be based on hashcode value.");
    }

    @Test
    void testCompareWithNullShouldSuccess() {
        final int expectedComparison1 = AbstractCondition.DEFAULT_COMPARISON_RESULT;
        final Condition nullCondition = (Condition) null;
        int comparison1 = condition1.compareTo(nullCondition);

        Assertions.assertEquals(expectedComparison1, comparison1,
                "The comparison should be 1 when a null condition is used for the comparison");
    }

    public final class ConditionImpl extends AbstractCondition {

        private final int hashcode;

        public ConditionImpl(final int aHashcode) {
            this.hashcode = aHashcode;
        }

        @Override
        public boolean isVerified(final List<Object> aParameters) {
            return true;
        }

        @Override
        public int hashCode() {
            return this.hashcode;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ConditionImpl other = (ConditionImpl) obj;
            return this.hashcode == other.hashcode;
        }

    }
}
