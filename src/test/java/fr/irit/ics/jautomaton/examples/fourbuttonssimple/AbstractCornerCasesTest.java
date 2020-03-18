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
package fr.irit.ics.jautomaton.examples.fourbuttonssimple;

import fr.irit.ics.jautomaton.Automaton;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runners.Parameterized;

/**
 *
 * @author David Navarre
 */
public abstract class AbstractCornerCasesTest {

    protected Automaton<TestConfiguration.Event, TestConfiguration.State> automaton;

    protected final void setUp() {
        automaton = TestConfiguration.getNewAutomaton();
        putInCorrectState();
    }

    public static Object[] getData(final List<TestConfiguration.Event> exclusions) {
        List answer = new ArrayList(TestConfiguration.State.values().length);
        for (TestConfiguration.Event event : TestConfiguration.Event.values()) {
            if (!exclusions.contains(event)) {
                answer.add(event);
            }
        }
        return answer.toArray();
    }

    @Parameterized.Parameter(0)
    public TestConfiguration.Event event;

    public abstract void putInCorrectState();

    @Test(expected = IllegalStateException.class)
    public void testAcceptEventEventNotAllowed() {
        setUp();
        automaton.acceptEvent(event);
    }
}
