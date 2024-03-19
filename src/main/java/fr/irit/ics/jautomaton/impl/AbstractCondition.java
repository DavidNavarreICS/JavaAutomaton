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
import java.util.Objects;

/**
 * This class implements a comparable condition.
 *
 * @author David Navarre &lt;David.Navarre at irit.fr&gt;
 */
public abstract class AbstractCondition implements Condition {

    /**
     * Value used when comparing to a null condition object.
     */
    public static final int DEFAULT_COMPARISON_RESULT = 1;

    @Override
    public int compareTo(final Condition aCondition) {
        if (Objects.isNull(aCondition)) {
            return DEFAULT_COMPARISON_RESULT;
        } else {
            return this.hashCode() - aCondition.hashCode();
        }
    }
}
