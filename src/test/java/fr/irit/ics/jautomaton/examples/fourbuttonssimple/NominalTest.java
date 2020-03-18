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
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author David Navarre
 */
public class NominalTest {

    private Automaton<Event, State>
            getAutomatonAfterSequence(List<Event> queue) {
        Automaton<Event, State> automaton = TestConfiguration.getNewAutomaton();
        queue.forEach((event) -> {
            automaton.acceptEvent(event);
        });
        return automaton;
    }

    @Test
    public void testInitialState() {
        Automaton<Event, State> automaton = getAutomatonAfterSequence(Collections.EMPTY_LIST);
        State state = automaton.getCurrentState();
        State expected = State.S1;
        Assert.assertEquals("Current state should be " + expected, expected, state);
        Assert.assertTrue("CB1 should be enabled", automaton.isEventEnabled(Event.CB1));
        Assert.assertFalse("CB2 should not be enabled", automaton.isEventEnabled(Event.CB2));
        Assert.assertFalse("CB3 should not be enabled", automaton.isEventEnabled(Event.CB3));
        Assert.assertFalse("CB4 should not be enabled", automaton.isEventEnabled(Event.CB4));
    }

    @Test
    public void testAfterCB1() {
        Automaton<Event, State> automaton = getAutomatonAfterSequence(Arrays.asList(Event.CB1));
        State state = automaton.getCurrentState();
        State expected = State.S2;
        Assert.assertEquals("Current state should be " + expected, expected, state);
        Assert.assertFalse("CB1 should not be enabled", automaton.isEventEnabled(Event.CB1));
        Assert.assertTrue("CB2 should be enabled", automaton.isEventEnabled(Event.CB2));
        Assert.assertFalse("CB3 should not be enabled", automaton.isEventEnabled(Event.CB3));
        Assert.assertFalse("CB4 should not be enabled", automaton.isEventEnabled(Event.CB4));
    }

    @Test
    public void testAfterCB2() {
        Automaton<Event, State> automaton = getAutomatonAfterSequence(Arrays.asList(Event.CB1, Event.CB2));
        State state = automaton.getCurrentState();
        State expected = State.S3;
        Assert.assertEquals("Current state should be " + expected, expected, state);
        Assert.assertFalse("CB1 should not be enabled", automaton.isEventEnabled(Event.CB1));
        Assert.assertFalse("CB2 should not be enabled", automaton.isEventEnabled(Event.CB2));
        Assert.assertTrue("CB3 should be enabled", automaton.isEventEnabled(Event.CB3));
        Assert.assertFalse("CB4 should not be enabled", automaton.isEventEnabled(Event.CB4));
    }

    @Test
    public void testAfterCB3() {
        Automaton<Event, State> automaton = getAutomatonAfterSequence(Arrays.asList(Event.CB1, Event.CB2, Event.CB3));
        State state = automaton.getCurrentState();
        State expected = State.S4;
        Assert.assertEquals("Current state should be " + expected, expected, state);
        Assert.assertFalse("CB1 should not be enabled", automaton.isEventEnabled(Event.CB1));
        Assert.assertFalse("CB2 should not be enabled", automaton.isEventEnabled(Event.CB2));
        Assert.assertFalse("CB3 should not be enabled", automaton.isEventEnabled(Event.CB3));
        Assert.assertTrue("CB4 should be enabled", automaton.isEventEnabled(Event.CB4));
    }

    @Test
    public void testAfterCB4() {
        Automaton<Event, State> automaton = getAutomatonAfterSequence(Arrays.asList(Event.CB1, Event.CB2, Event.CB3, Event.CB4));
        State state = automaton.getCurrentState();
        State expected = State.S1;
        Assert.assertEquals("Current state should be " + expected, expected, state);
        Assert.assertTrue("CB1 should be enabled", automaton.isEventEnabled(Event.CB1));
        Assert.assertFalse("CB2 should not be enabled", automaton.isEventEnabled(Event.CB2));
        Assert.assertFalse("CB3 should not be enabled", automaton.isEventEnabled(Event.CB3));
        Assert.assertFalse("CB4 should not be enabled", automaton.isEventEnabled(Event.CB4));
    }
}
