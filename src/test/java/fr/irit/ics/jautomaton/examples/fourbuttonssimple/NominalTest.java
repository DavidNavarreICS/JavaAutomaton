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
import fr.irit.ics.jautomaton.examples.fourbuttonssimple.TestConfiguration.Event;
import fr.irit.ics.jautomaton.examples.fourbuttonssimple.TestConfiguration.State;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author David Navarre
 */
class NominalTest {

    private Automaton<Event, State>
            getAutomatonAfterSequence(List<Event> queue) {
        Automaton<Event, State> automaton = TestConfiguration.getNewAutomaton();
        queue.forEach((event) -> {
            automaton.acceptEvent(event);
        });
        return automaton;
    }

    @Test
    void testInitialState() {
        Automaton<Event, State> automaton = getAutomatonAfterSequence(Collections.EMPTY_LIST);
        State state = automaton.getCurrentState();
        State expected = State.S1;
        verifyAssertions(automaton, expected, state, true, false, false, false);
    }

    @Test
    void testAfterCB1() {
        Automaton<Event, State> automaton = getAutomatonAfterSequence(Arrays.asList(Event.CB1));
        State state = automaton.getCurrentState();
        State expected = State.S2;
        verifyAssertions(automaton, expected, state, false, true, false, false);
    }

    @Test
    void testAfterCB2() {
        Automaton<Event, State> automaton = getAutomatonAfterSequence(Arrays.asList(Event.CB1, Event.CB2));
        State state = automaton.getCurrentState();
        State expected = State.S3;
        verifyAssertions(automaton, expected, state, false, false, true, false);
    }

    @Test
    void testAfterCB3() {
        Automaton<Event, State> automaton = getAutomatonAfterSequence(Arrays.asList(Event.CB1, Event.CB2, Event.CB3));
        State state = automaton.getCurrentState();
        State expected = State.S4;
        verifyAssertions(automaton, expected, state, false, false, false, true);
    }

    @Test
    void testAfterCB4() {
        Automaton<Event, State> automaton = getAutomatonAfterSequence(Arrays.asList(Event.CB1, Event.CB2, Event.CB3,
                Event.CB4));
        State state = automaton.getCurrentState();
        State expected = State.S1;
        verifyAssertions(automaton, expected, state, true, false, false, false);
    }

    private void verifyAssertions(
            final Automaton<Event, State> aAutomaton,
            final State aExpected,
            final State aState,
            final boolean CB1Enable,
            final boolean CB2Enable,
            final boolean CB3Enable,
            final boolean CB4Enable) {
        Assertions.assertEquals(aExpected, aState, "Current state should be " + aExpected);
        assertEnabling(aAutomaton, Event.CB1, CB1Enable);
        assertEnabling(aAutomaton, Event.CB2, CB2Enable);
        assertEnabling(aAutomaton, Event.CB3, CB3Enable);
        assertEnabling(aAutomaton, Event.CB4, CB4Enable);
    }

    private void assertEnabling(final Automaton<Event, State> aAutomaton, final Event aEvent, boolean aEventEnable) {
        if (aEventEnable) {
            Assertions.assertTrue(aAutomaton.isEventEnabled(aEvent), aEvent.toString() + " should be enabled");
        } else {
            Assertions.assertFalse(aAutomaton.isEventEnabled(aEvent), aEvent.toString() + " should not be enabled");
        }
    }
}
