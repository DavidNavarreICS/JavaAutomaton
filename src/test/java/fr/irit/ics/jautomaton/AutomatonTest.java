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
package fr.irit.ics.jautomaton;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EnumSet;
import java.util.Objects;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author navarre
 */
public class AutomatonTest {

    private enum State {
        S1, S2
    }

    private enum Event {
        E1, E2
    }
    private final static String INCORRECT_PROPERTY_NAME = "foo";

    public AutomatonTest() {
    }

    @Test
    public void testAcceptEvent() {
        final Automaton<Event, State> automaton = getAutomaton();
    }

    @Test
    public void testCreateRegister() {
        final Automaton<Event, State> automaton = getAutomaton();
    }

    @Test
    public void testGetRegisterValue() {
        final Automaton<Event, State> automaton = getAutomaton();
    }

    @Test
    public void testSetRegisterValue() {
        final Automaton<Event, State> automaton = getAutomaton();
    }

    @Test
    public void testRegisterInitialization_GenericType() {
        final Automaton<Event, State> automaton = getAutomaton();
    }

    @Test
    public void testRegisterInitialization_GenericType_Action() {
        final Automaton<Event, State> automaton = getAutomaton();
    }

    @Test
    public void testRegisterInitialization_3args() {
        final Automaton<Event, State> automaton = getAutomaton();
    }

    @Test
    public void testRegisterTransition_3args() {
        final Automaton<Event, State> automaton = getAutomaton();
    }

    @Test
    public void testRegisterTransition_4args() {
        final Automaton<Event, State> automaton = getAutomaton();
    }

    @Test
    public void testRegisterTransition_5args() {
        final Automaton<Event, State> automaton = getAutomaton();
    }

    @Test
    public void testInitialize() {
        final Automaton<Event, State> automaton = getAutomaton();
    }

    @Test
    public void testIsEventEnabled() {
        final Automaton<Event, State> automaton = getAutomaton();
    }

    @Test
    public void testPropertyChangeListener_InitialState() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final StatePropertyChangeListener statePropertyListener = new StatePropertyChangeListener();
        automaton.addPropertyChangeListener(Automaton.STATE_PROPERTY, statePropertyListener);
        automaton.initialize();
        State expected = State.S1;
        Assert.assertEquals("The initial state should be S1", expected, statePropertyListener.getNewValue());
    }

    @Test
    public void testPropertyChangeListener_InitialEnablingE1() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final EventPropertyChangeListener eventPropertyListener = new EventPropertyChangeListener(Event.E1);
        automaton.addPropertyChangeListener(eventPropertyListener.getPropertyName(), eventPropertyListener);
        automaton.initialize();
        Boolean expected = true;
        Assert.assertEquals("E1 should be enabled", expected, eventPropertyListener.getNewValue());
    }

    @Test
    public void testPropertyChangeListener_InitialEnablingE2() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final EventPropertyChangeListener eventPropertyListener = new EventPropertyChangeListener(Event.E2);
        automaton.addPropertyChangeListener(eventPropertyListener.getPropertyName(), eventPropertyListener);
        automaton.initialize();
        Boolean expected = false;
        Assert.assertEquals("E2 should not be enabled", expected, eventPropertyListener.getNewValue());
    }

    @Test
    public void testAddPropertyChangeListener_PropertyChangeListener() {
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(listener);
        final PropertyChangeListener[] result = automaton.getPropertyChangeListeners();
        final int expectedSize = 1;
        Assert.assertEquals("Listener list should contain only one item", expectedSize, result.length);
        Assert.assertEquals("Listener list should contain the added listener", listener, result[0]);
    }

    @Test
    public void testRemovePropertyChangeListener_PropertyChangeListener() {
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(listener);
        PropertyChangeListener[] result = automaton.getPropertyChangeListeners();
        int expectedSize = 1;
        Assert.assertEquals("Listener list should contain only one item", expectedSize, result.length);
        Assert.assertEquals("Listener list should contain the added listener", listener, result[0]);
        automaton.removePropertyChangeListener(listener);
        result = automaton.getPropertyChangeListeners();
        expectedSize = 0;
        Assert.assertEquals("Listener list should be empty", expectedSize, result.length);
    }

    @Test
    public void testAddPropertyChangeListener_String_PropertyChangeListener() {
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(Automaton.STATE_PROPERTY, listener);
        final PropertyChangeListener[] result = automaton.getPropertyChangeListeners(Automaton.STATE_PROPERTY);
        final int expectedSize = 1;
        Assert.assertEquals("Listener list should contain only one item", expectedSize, result.length);
        Assert.assertEquals("Listener list should contain the added listener", listener, result[0]);
    }

    @Test
    public void testRemovePropertyChangeListener_String_PropertyChangeListener() {
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(Automaton.STATE_PROPERTY, listener);
        PropertyChangeListener[] result = automaton.getPropertyChangeListeners(Automaton.STATE_PROPERTY);
        int expectedSize = 1;
        Assert.assertEquals("Listener list should contain only one item", expectedSize, result.length);
        Assert.assertEquals("Listener list should contain the added listener", listener, result[0]);
        automaton.removePropertyChangeListener(Automaton.STATE_PROPERTY, listener);
        result = automaton.getPropertyChangeListeners(Automaton.STATE_PROPERTY);
        expectedSize = 0;
        Assert.assertEquals("Listener list should be empty", expectedSize, result.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPropertyChangeListener_StringNotAllowed() {
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(INCORRECT_PROPERTY_NAME, listener);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPropertyChangeListener_ListenerNull() {
        final Automaton<Event, State> automaton = getAutomaton();
        automaton.addPropertyChangeListener(Automaton.STATE_PROPERTY, null);
    }

    @Test
    public void testToString() {
        final Automaton<Event, State> automaton = getAutomaton();
        String result = automaton.toString();
        Boolean isNull = Objects.isNull(result);
        Boolean isEmpty = result.isEmpty();
        Assert.assertFalse("toString cannot be null", isNull);
        Assert.assertFalse("toString cannot be empty", isEmpty);
    }

    private static Automaton<Event, State> getAutomaton() {
        final Automaton<Event, State> automaton = new Automaton(
                EnumSet.allOf(Event.class),
                EnumSet.allOf(State.class));
        return automaton;
    }

    private Automaton<Event, State> getAutomatonWithInitialState() {
        final Automaton<Event, State> automaton = getAutomaton();
        automaton.registerInitialization(State.S1);
        automaton.registerTransition(State.S1, Event.E1, State.S2);

        return automaton;
    }

    private class StatePropertyChangeListener implements PropertyChangeListener {

        private Object newValue;

        public Object getNewValue() {
            return newValue;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            newValue = evt.getNewValue();
        }
    };

    private class EventPropertyChangeListener implements PropertyChangeListener {

        private Object newValue;
        private final String propertyName;

        public EventPropertyChangeListener(Event event) {
            propertyName = event.toString() + Automaton.ENABLED_SUFFIX;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public Object getNewValue() {
            return newValue;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            newValue = evt.getNewValue();
        }
    };

    private class FooListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
        }
    }
}
