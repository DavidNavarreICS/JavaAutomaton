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
package fr.irit.ics.jautomaton.examples.fourbuttonsalternate;

import fr.irit.ics.jautomaton.Automaton;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 *
 * @author David Navarre
 */
public abstract class AbstractCornerCasesTest {

    public static Object[] getData(final List<TestConfiguration.Event> exclusions) {
        List answer = new ArrayList(TestConfiguration.State.values().length);
        for (TestConfiguration.Event event : TestConfiguration.Event.values()) {
            if (!exclusions.contains(event)) {
                answer.add(event);
            }
        }
        return answer.toArray();
    }
    protected Automaton<TestConfiguration.Event, TestConfiguration.State> automaton;

    public abstract void putInCorrectState();

    @ParameterizedTest
    @MethodSource("data")
    public void testAcceptEventEventNotAllowed(final TestConfiguration.Event event) {
        setUp();
        final IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            automaton.acceptEvent(event);
        });
        Assertions.assertNotNull(exception);
    }

    protected final void setUp() {
        automaton = TestConfiguration.getNewAutomaton();
        putInCorrectState();
    }
}
