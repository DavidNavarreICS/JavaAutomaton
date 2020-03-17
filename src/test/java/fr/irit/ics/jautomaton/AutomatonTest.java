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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final static String CORRECT_REGISTER_NAME = "a1";
    private final static String INCORRECT_REGISTER_NAME = "!a1";
    private static final Logger LOG = Logger.getLogger(AutomatonTest.class.getName());
    private static final PreconditionImpl p1 = new PreconditionImpl(Boolean.TRUE);
    private static final PreconditionImpl p2 = new PreconditionImpl(Boolean.FALSE);
    private static final ActionImpl a1 = new ActionImpl(1);
    private static final ActionImpl a2 = new ActionImpl(2);

    public AutomatonTest() {
    }

    @Test
    public void testAcceptEventIsCorrect() {
        LOG.log(Level.INFO, "################ testAcceptEventIsCorrect");
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        automaton.initialize();
        automaton.acceptEvent(Event.E1);
        State finalState = automaton.getCurrentState();
        State expected = State.S2;
        Assert.assertEquals("The final state should be S2", expected, finalState);
    }

    @Test(expected = IllegalStateException.class)
    public void testAcceptEventE2Incorrect() {
        LOG.log(Level.INFO, "################ testAcceptEventE2Incorrect");
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        automaton.initialize();
        automaton.acceptEvent(Event.E2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAcceptEvent_EventNullNotAllowed() {
        LOG.log(Level.INFO, "################ testAcceptEvent_EventNullNotAllowed");
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        automaton.initialize();
        automaton.acceptEvent(null);
    }

    @Test
    public void testCreateRegister_CorrectName() {
        LOG.log(Level.INFO, "################ testCreateRegister_CorrectName");
        final Automaton<Event, State> automaton = getAutomaton();
        automaton.createRegister(CORRECT_REGISTER_NAME);
        final Integer value = 0;
        automaton.setRegisterValue(CORRECT_REGISTER_NAME, value);
        Integer result = automaton.getRegisterValue(CORRECT_REGISTER_NAME, Integer.class);
        Assert.assertEquals("The register value should be the same as the one set", value, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateRegister_IncorrectName() {
        LOG.log(Level.INFO, "################ testCreateRegister_IncorrectName");
        final Automaton<Event, State> automaton = getAutomaton();
        automaton.createRegister(INCORRECT_REGISTER_NAME);
    }

    @Test
    public void testRegisterInitialization_SimpleState() {
        LOG.log(Level.INFO, "################ testRegisterInitialization_SimpleState");
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        automaton.registerInitialization(State.S1);
        automaton.initialize();
        State result = automaton.getCurrentState();
        Assert.assertEquals("The initial state should be the one registered as an initialization: S1", State.S1, result);
    }

    @Test
    public void testRegisterInitialization_SimpleStateWithAction() {
        LOG.log(Level.INFO, "################ testRegisterInitialization_SimpleStateWithAction");
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final ActionImpl actionImpl = new ActionImpl(0);
        automaton.registerInitialization(State.S1, actionImpl);
        Object[] parameters = new Object[]{0.0F, "FOO"};
        automaton.initialize(parameters);
        State result = automaton.getCurrentState();
        Assert.assertEquals("The initial state should be the one registered as an initialization: S1", State.S1, result);
        Object receivedParameter = actionImpl.getExecutionParameters();
        Assert.assertEquals("Initial parameters lost during initialization", parameters[0], receivedParameter);
    }

    @Test
    public void testRegisterInitialization_MultipleStateWithActionAndPreconditionBranch1() {
        LOG.log(Level.INFO, "################ testRegisterInitialization_MultipleStateWithActionAndPreconditionBranch1");
        final Automaton<Event, State> automaton = getFullAutomaton();
        Object[] parametersS1 = new Object[]{true, "FOO1", "FOO2"};
        automaton.initialize(parametersS1);
        State result = automaton.getCurrentState();
        Assert.assertEquals("The initial state should be the one registered as an initialization: S1", State.S1, result);
        Object receivedParameter = a1.getExecutionParameters();
        Assert.assertEquals("Initial parameters lost during initialization", parametersS1[1], receivedParameter);
    }

    @Test
    public void testRegisterInitialization_MultipleStateWithActionAndPreconditionBranch2() {
        LOG.log(Level.INFO, "################ testRegisterInitialization_MultipleStateWithActionAndPreconditionBranch2");
        final Automaton<Event, State> automaton = getFullAutomaton();
        Object[] parametersS2 = new Object[]{false, "FOO1", "FOO2"};
        automaton.initialize(parametersS2);
        State result = automaton.getCurrentState();
        Assert.assertEquals("The initial state should be the one registered as an initialization: S2", State.S2, result);
        Object receivedParameter = a2.getExecutionParameters();
        Assert.assertEquals("Initial parameters lost during initialization", parametersS2[2], receivedParameter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterInitialization_IncorrectState() {
        LOG.log(Level.INFO, "################ testRegisterInitialization_IncorrectState");
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        automaton.registerInitialization(null);
    }

    @Test
    public void testRegisterTransition_3args() {
        LOG.log(Level.INFO, "################ testRegisterTransition_3args");
        final Automaton<Event, State> automaton = getAutomaton();
    }

    @Test
    public void testRegisterTransition_4args() {
        LOG.log(Level.INFO, "################ testRegisterTransition_4args");
        final Automaton<Event, State> automaton = getAutomaton();
    }

    @Test
    public void testRegisterTransition_5args() {
        LOG.log(Level.INFO, "################ testRegisterTransition_5args");
        final Automaton<Event, State> automaton = getAutomaton();
    }

    @Test
    public void testInitialize() {
        LOG.log(Level.INFO, "################ testInitialize");
        final Automaton<Event, State> automaton = getAutomaton();
    }

    @Test
    public void testIsEventEnabled() {
        LOG.log(Level.INFO, "################ testIsEventEnabled");
        final Automaton<Event, State> automaton = getAutomaton();
    }

    @Test
    public void testPropertyChangeListener_InitialState() {
        LOG.log(Level.INFO, "################ testPropertyChangeListener_InitialState");
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final StatePropertyChangeListener statePropertyListener = new StatePropertyChangeListener();
        automaton.addPropertyChangeListener(Automaton.STATE_PROPERTY, statePropertyListener);
        automaton.initialize();
        State expected = State.S1;
        Assert.assertEquals("The initial state should be S1", expected, statePropertyListener.getNewValue());
    }

    @Test
    public void testPropertyChangeListener_InitialEnablingE1() {
        LOG.log(Level.INFO, "################ testPropertyChangeListener_InitialEnablingE1");
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final EventPropertyChangeListener eventPropertyListener = new EventPropertyChangeListener(Event.E1);
        automaton.addPropertyChangeListener(eventPropertyListener.getPropertyName(), eventPropertyListener);
        automaton.initialize();
        Boolean expected = true;
        Assert.assertEquals("E1 should be enabled", expected, eventPropertyListener.getNewValue());
    }

    @Test
    public void testPropertyChangeListener_InitialEnablingE2() {
        LOG.log(Level.INFO, "################ testPropertyChangeListener_InitialEnablingE2");
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final EventPropertyChangeListener eventPropertyListener = new EventPropertyChangeListener(Event.E2);
        automaton.addPropertyChangeListener(eventPropertyListener.getPropertyName(), eventPropertyListener);
        automaton.initialize();
        Boolean expected = false;
        Assert.assertEquals("E2 should not be enabled", expected, eventPropertyListener.getNewValue());
    }

    @Test
    public void testAddPropertyChangeListener_AllowedE1() {
        LOG.log(Level.INFO, "################ testAddPropertyChangeListener_AllowedE1");
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(Event.E1.toString() + Automaton.ENABLED_SUFFIX, listener);
        final PropertyChangeListener[] result = automaton.getPropertyChangeListeners(Event.E1.toString() + Automaton.ENABLED_SUFFIX);
        final int expectedSize = 1;
        Assert.assertEquals("Listener list should contain only one item", expectedSize, result.length);
        Assert.assertEquals("Listener list should contain the added listener", listener, result[0]);
    }

    @Test
    public void testAddPropertyChangeListener_AllowedE2() {
        LOG.log(Level.INFO, "################ testAddPropertyChangeListener_AllowedE2");
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(Event.E2.toString() + Automaton.ENABLED_SUFFIX, listener);
        final PropertyChangeListener[] result = automaton.getPropertyChangeListeners(Event.E2.toString() + Automaton.ENABLED_SUFFIX);
        final int expectedSize = 1;
        Assert.assertEquals("Listener list should contain only one item", expectedSize, result.length);
        Assert.assertEquals("Listener list should contain the added listener", listener, result[0]);
    }

    @Test
    public void testAddPropertyChangeListener_AllowedState() {
        LOG.log(Level.INFO, "################ testAddPropertyChangeListener_AllowedState");
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(Automaton.STATE_PROPERTY, listener);
        final PropertyChangeListener[] result = automaton.getPropertyChangeListeners(Automaton.STATE_PROPERTY);
        final int expectedSize = 1;
        Assert.assertEquals("Listener list should contain only one item", expectedSize, result.length);
        Assert.assertEquals("Listener list should contain the added listener", listener, result[0]);
    }

    @Test
    public void testRemovePropertyChangeListener_PropertyChangeListener() {
        LOG.log(Level.INFO, "################ testRemovePropertyChangeListener_PropertyChangeListener");
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
        LOG.log(Level.INFO, "################ testAddPropertyChangeListener_String_PropertyChangeListener");
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
        LOG.log(Level.INFO, "################ testRemovePropertyChangeListener_String_PropertyChangeListener");
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
        LOG.log(Level.INFO, "################ testAddPropertyChangeListener_StringNotAllowed");
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(INCORRECT_PROPERTY_NAME, listener);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPropertyChangeListener_ListenerNull() {
        LOG.log(Level.INFO, "################ testAddPropertyChangeListener_ListenerNull");
        final Automaton<Event, State> automaton = getAutomaton();
        automaton.addPropertyChangeListener(Automaton.STATE_PROPERTY, null);
    }

    @Test
    public void testToString() {
        LOG.log(Level.INFO, "################ testToString");
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
        final Automaton<Event, State> automaton = getAutomatonWithoutInitialState();
        automaton.registerInitialization(State.S1);

        return automaton;
    }

    private Automaton<Event, State> getAutomatonWithoutInitialState() {
        final Automaton<Event, State> automaton = getAutomaton();
        automaton.registerTransition(State.S1, Event.E1, State.S2);

        return automaton;
    }

    private Automaton<Event, State> getFullAutomaton() {
        final Automaton<Event, State> automaton = getAutomatonWithoutInitialState();
        final List<State> states = new ArrayList<>(Arrays.asList(State.S1, State.S2));
        final List<Action> actions = new ArrayList<>(Arrays.asList(a1, a2));
        final List<Precondition> preconditions = new ArrayList<>(Arrays.asList(p1, p2));
        automaton.registerInitialization(states, actions, preconditions);

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

    private static class FooListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
        }
    }

    private static class ActionImpl implements Action {

        private Object executionParameter;
        private final int index;

        public ActionImpl(int index) {
            this.index = index;
        }

        public Object getExecutionParameters() {
            return executionParameter;
        }

        @Override
        public void execute(Object... parameters) {
            executionParameter = parameters[index];
        }

        public void reinit() {
            executionParameter = null;
        }
    }

    private static class PreconditionImpl implements Precondition {

        private final Boolean condition;

        public PreconditionImpl(Boolean condition) {
            this.condition = condition;
        }

        @Override
        public boolean isVerified(Object... parameters) {
            Boolean value = (Boolean) parameters[0];
            return value == condition;
        }

        @Override
        public String toString() {
            return "PreconditionImpl{" + "condition=" + condition + '}';
        }
    }
}
