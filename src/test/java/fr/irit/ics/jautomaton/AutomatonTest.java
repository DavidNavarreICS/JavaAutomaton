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
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author navarre
 */
public class AutomatonTest {

    private enum State {
        S1, S2, S3
    }

    private enum Event {
        E1, E2
    }
    private static final String INCORRECT_PROPERTY_NAME = "foo";
    private static final String CORRECT_REGISTER_NAME = "a1";
    private static final String INCORRECT_REGISTER_NAME = "!a1";
    private static final Logger LOG = Logger.getLogger(AutomatonTest.class.getName());
    private static final ConditionImpl p1 = new ConditionImpl(Boolean.TRUE);
    private static final ConditionImpl p2 = new ConditionImpl(Boolean.FALSE);
    private static final ConditionNeverVerified p3 = new ConditionNeverVerified();
    private static final ActionImpl a1 = new ActionImpl(1);
    private static final ActionImpl a2 = new ActionImpl(2);
    private static final Object[] parametersS1 = new Object[]{true, "FOO1", "FOO2"};
    private static final Object[] parametersS2 = new Object[]{false, "FOO1", "FOO2"};

    public AutomatonTest() {
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullEventSetShouldFail() {
        final Automaton<Event, State> automaton = new Automaton(null, EnumSet.allOf(State.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorEmptyEventSetShouldFail() {
        final Automaton<Event, State> automaton = new Automaton(new HashSet<Event>(0), EnumSet.allOf(State.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullStateSetShouldFail() {
        final Automaton<Event, State> automaton = new Automaton(EnumSet.allOf(Event.class), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorEmptyStateSetShouldFail() {
        final Automaton<Event, State> automaton = new Automaton(EnumSet.allOf(Event.class), new HashSet<State>(0));
    }

    @Test
    public void testAcceptEventIsCorrect() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        automaton.initialize();
        automaton.acceptEvent(Event.E1);
        State finalState = automaton.getCurrentState();
        State expected = State.S2;
        Assert.assertEquals("The final state should be S2", expected, finalState);
    }

    @Test(expected = IllegalStateException.class)
    public void testAcceptEventE2Incorrect() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        automaton.initialize();
        automaton.acceptEvent(Event.E2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAcceptEventEventNullNotAllowed() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        automaton.initialize();
        automaton.acceptEvent(null);
    }

    @Test
    public void testCreateRegisterCorrectName() {
        final Automaton<Event, State> automaton = getAutomaton();
        automaton.createRegister(CORRECT_REGISTER_NAME);
        final Integer value = 0;
        automaton.setRegisterValue(CORRECT_REGISTER_NAME, value);
        Integer result = automaton.getRegisterValue(CORRECT_REGISTER_NAME, Integer.class);
        Assert.assertEquals("The register value should be the same as the one set", value, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRegisterRegisterDoesNotExists() {
        final Automaton<Event, State> automaton = getAutomaton();
        final Integer value = 0;
        automaton.setRegisterValue(CORRECT_REGISTER_NAME, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRegisterRegisterDoesNotExists() {
        final Automaton<Event, State> automaton = getAutomaton();
        automaton.getRegisterValue(CORRECT_REGISTER_NAME, Integer.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateRegisterIncorrectName() {
        final Automaton<Event, State> automaton = getAutomaton();
        automaton.createRegister(INCORRECT_REGISTER_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateRegisterNullName() {
        final Automaton<Event, State> automaton = getAutomaton();
        automaton.createRegister(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateRegisterNameAlreadyUsed() {
        final Automaton<Event, State> automaton = getAutomaton();
        automaton.createRegister(CORRECT_REGISTER_NAME);
        automaton.createRegister(CORRECT_REGISTER_NAME);
    }

    @Test
    public void testRegisterInitializationSimpleState() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        automaton.registerInitialization(State.S1);
        automaton.initialize();
        State result = automaton.getCurrentState();
        Assert.assertEquals("The initial state should be the one registered as an initialization: S1", State.S1, result);
    }

    @Test
    public void testRegisterInitializationSimpleStateWithAction() {
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
    public void testRegisterInitializationSimpleStateWithNullAction() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final ActionImpl actionImpl = null;
        automaton.registerInitialization(State.S1, actionImpl);
        Object[] parameters = new Object[]{0.0F, "FOO"};
        automaton.initialize(parameters);
        State result = automaton.getCurrentState();
        Assert.assertEquals("The initial state should be the one registered as an initialization: S1", State.S1, result);
    }

    @Test
    public void testRegisterInitializationSimpleStateWithEmptyAction() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final List<State> states = new ArrayList<>(1);
        states.add(State.S1);
        final List<Action> actions = Collections.EMPTY_LIST;
        final List<Condition> conditions = Collections.EMPTY_LIST;
        automaton.registerInitialization(states, actions, conditions);
        Object[] parameters = new Object[]{0.0F, "FOO"};
        automaton.initialize(parameters);
        State result = automaton.getCurrentState();
        Assert.assertEquals("The initial state should be the one registered as an initialization: S1", State.S1, result);
    }

    @Test
    public void testRegisterInitializationSimpleStateWithNullActionAndNullCondition() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final List<State> states = new ArrayList<>(1);
        states.add(State.S1);
        final List<Action> actions = null;
        final List<Condition> conditions = null;
        automaton.registerInitialization(states, actions, conditions);
        Object[] parameters = new Object[]{0.0F, "FOO"};
        automaton.initialize(parameters);
        State result = automaton.getCurrentState();
        Assert.assertEquals("The initial state should be the one registered as an initialization: S1", State.S1, result);
    }

    @Test
    public void testRegisterInitializationMultipleStateWithActionAndConditionBranch1() {
        final Automaton<Event, State> automaton = getFullAutomaton();
        automaton.initialize(parametersS1);
        State result = automaton.getCurrentState();
        Assert.assertEquals("The initial state should be the one registered as an initialization: S1", State.S1, result);
        Object receivedParameter = a1.getExecutionParameters();
        Assert.assertEquals("Initial parameters lost during initialization", parametersS1[1], receivedParameter);
    }

    @Test
    public void testRegisterInitializationMultipleStateWithActionAndConditionBranch2() {
        final Automaton<Event, State> automaton = getFullAutomaton();
        automaton.initialize(parametersS2);
        State result = automaton.getCurrentState();
        Assert.assertEquals("The initial state should be the one registered as an initialization: S2", State.S2, result);
        Object receivedParameter = a2.getExecutionParameters();
        Assert.assertEquals("Initial parameters lost during initialization", parametersS2[2], receivedParameter);
    }

    @Test
    public void testRegisterInitializationMultipleStateWithFewActionsAndFewConditions() {
        a1.reinit();
        a2.reinit();
        final Automaton<Event, State> automaton = getAutomatonWithoutInitialState();
        final List<State> states = new ArrayList<>(Arrays.asList(State.S1, State.S2));
        final List<Action> actions = new ArrayList<>(Arrays.asList(a1));
        final List<Condition> conditions = new ArrayList<>(Arrays.asList(p1));
        automaton.registerInitialization(states, actions, conditions);
        automaton.initialize(parametersS2);
        State result = automaton.getCurrentState();
        Assert.assertEquals("The initial state should be the one registered as an initialization: S2", State.S2, result);
    }

    @Test
    public void testRegisterInitializationMultipleStateWithOneStateNullActionsAndNullConditions() {
        a1.reinit();
        a2.reinit();
        final Automaton<Event, State> automaton = getAutomatonWithoutInitialState();
        final List<State> states = new ArrayList<>(Arrays.asList(State.S1, State.S2));
        final List<Action> actions = null;
        final List<Condition> conditions = null;
        automaton.registerInitialization(states, actions, conditions);
        automaton.initialize(parametersS2);
        State result = automaton.getCurrentState();
        Assert.assertTrue("The initial state should be the one registered as an initialization: S1 or S2", State.S1.equals(result) || State.S2.equals(result));
    }

    @Test
    public void testRegisterInitializationMultipleStateWithOneStateEmptyActionsAndEmptyConditions() {
        a1.reinit();
        a2.reinit();
        final Automaton<Event, State> automaton = getAutomatonWithoutInitialState();
        final List<State> states = new ArrayList<>(Arrays.asList(State.S1, State.S2));
        final List<Action> actions = Collections.emptyList();
        final List<Condition> conditions = Collections.emptyList();
        automaton.registerInitialization(states, actions, conditions);
        automaton.initialize(parametersS2);
        State result = automaton.getCurrentState();
        Assert.assertTrue("The initial state should be the one registered as an initialization: S1 or S2", State.S1.equals(result) || State.S2.equals(result));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterInitializationMultipleStateWithActionAndConditionNeverReachable() {
        final Automaton<Event, State> automaton = getFooAutomaton();
        automaton.initialize(parametersS1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterInitializationMultipleStateWithActionAndConditionNullSetOfState() {
        final Automaton<Event, State> automaton = getFooAutomaton();
        automaton.registerInitialization(null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterInitializationMultipleStateWithActionAndConditionEmptySetOfState() {
        final Automaton<Event, State> automaton = getFooAutomaton();
        automaton.registerInitialization(new ArrayList<State>(0), null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterInitializationIncorrectState() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        automaton.registerInitialization(null);
    }

    @Test
    public void testRegisterTransitionTwoFinalStatesFromSameInitialStateBranch1() {
        final Automaton<Event, State> automaton = getAutomaton();
        a1.reinit();
        a2.reinit();
        automaton.registerInitialization(State.S1);
        automaton.registerTransition(State.S1, Event.E1, State.S2, a1, p1);
        automaton.registerTransition(State.S1, Event.E1, State.S3, a2, p2);
        automaton.initialize();
        Boolean isE1Enabled = automaton.isEventEnabled(Event.E1);
        Assert.assertTrue("E1 should be enabled", isE1Enabled);
        automaton.acceptEvent(Event.E1, parametersS1);
        State result = automaton.getCurrentState();
        Assert.assertEquals("The initial state should be the one registered as an initialization: S2", State.S2, result);
        Object receivedParameter = a1.getExecutionParameters();
        Assert.assertEquals("Initial parameters lost during transition", parametersS1[1], receivedParameter);
    }

    @Test
    public void testRegisterTransitionTwoFinalStatesFromSameInitialStateBranch2() {
        final Automaton<Event, State> automaton = getAutomaton();
        a1.reinit();
        a2.reinit();
        automaton.registerInitialization(State.S1);
        automaton.registerTransition(State.S1, Event.E1, State.S2, a1, p1);
        automaton.registerTransition(State.S1, Event.E1, State.S3, a2, p2);
        automaton.initialize();
        Boolean isE1Enabled = automaton.isEventEnabled(Event.E1);
        Assert.assertTrue("E1 should be enabled", isE1Enabled);
        automaton.acceptEvent(Event.E1, parametersS2);
        State result = automaton.getCurrentState();
        Assert.assertEquals("The initial state should be the one registered as an initialization: S3", State.S3, result);
        Object receivedParameter = a2.getExecutionParameters();
        Assert.assertEquals("Initial parameters lost during transition", parametersS2[2], receivedParameter);
    }

    @Test
    public void testEnabledEventE1IsNotEnabledWhileNotInitialized() {
        final Automaton<Event, State> automaton = getAutomaton();
        a1.reinit();
        a2.reinit();
        automaton.registerInitialization(State.S1);
        automaton.registerTransition(State.S1, Event.E1, State.S2);
        Boolean isE1Enabled = automaton.isEventEnabled(Event.E1);
        Assert.assertFalse("E1 should not be enabled", isE1Enabled);
    }

    @Test
    public void testEnabledEventE1IsEnabled() {
        final Automaton<Event, State> automaton = getAutomaton();
        a1.reinit();
        a2.reinit();
        automaton.registerInitialization(State.S1);
        automaton.registerTransition(State.S1, Event.E1, State.S2);
        automaton.initialize();
        Boolean isE1Enabled = automaton.isEventEnabled(Event.E1);
        Assert.assertTrue("E1 should be enabled", isE1Enabled);
    }

    @Test
    public void testEnabledEventE2IsNotEnabled() {
        final Automaton<Event, State> automaton = getAutomaton();
        a1.reinit();
        a2.reinit();
        automaton.registerInitialization(State.S1);
        automaton.registerTransition(State.S1, Event.E1, State.S2);
        automaton.initialize();
        Boolean isE2Enabled = automaton.isEventEnabled(Event.E2);
        Assert.assertFalse("E2 should not be enabled", isE2Enabled);
    }

    @Test
    public void testEnabledEventE2IsNotEnabledInS1() {
        final Automaton<Event, State> automaton = getAutomaton();
        a1.reinit();
        a2.reinit();
        automaton.registerInitialization(State.S1);
        automaton.registerTransition(State.S1, Event.E1, State.S2);
        automaton.registerTransition(State.S2, Event.E2, State.S1);
        automaton.initialize();
        Boolean isE2Enabled = automaton.isEventEnabled(Event.E2);
        Assert.assertFalse("E2 should not be enabled", isE2Enabled);
    }

    @Test
    public void testEnabledEventE1IsEnabledInS1() {
        final Automaton<Event, State> automaton = getAutomaton();
        automaton.registerInitialization(State.S1);
        automaton.registerTransition(State.S1, Event.E1, State.S2);
        automaton.registerTransition(State.S2, Event.E2, State.S1);
        automaton.initialize();
        Boolean isE1Enabled = automaton.isEventEnabled(Event.E1);
        Assert.assertTrue("E1 should be enabled", isE1Enabled);
    }

    @Test
    public void testPropertyChangeListenerInitialState() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final StatePropertyChangeListener statePropertyListener = new StatePropertyChangeListener();
        automaton.addPropertyChangeListener(Automaton.STATE_PROPERTY, statePropertyListener);
        automaton.initialize();
        State expected = State.S1;
        Assert.assertEquals("The initial state should be S1", expected, statePropertyListener.getNewValue());
    }

    @Test
    public void testPropertyChangeListenerInitialEnablingE1() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final EventPropertyChangeListener eventPropertyListener = new EventPropertyChangeListener(Event.E1);
        automaton.addPropertyChangeListener(eventPropertyListener.getPropertyName(), eventPropertyListener);
        automaton.initialize();
        Boolean expected = true;
        Assert.assertEquals("E1 should be enabled", expected, eventPropertyListener.getNewValue());
    }

    @Test
    public void testPropertyChangeListenerInitialEnablingE2() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final EventPropertyChangeListener eventPropertyListener = new EventPropertyChangeListener(Event.E2);
        automaton.addPropertyChangeListener(eventPropertyListener.getPropertyName(), eventPropertyListener);
        automaton.initialize();
        Boolean expected = false;
        Assert.assertEquals("E2 should not be enabled", expected, eventPropertyListener.getNewValue());
    }

    @Test
    public void testAddPropertyChangeListenerAllowedE1() {
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(Event.E1.toString() + Automaton.ENABLED_SUFFIX, listener);
        final PropertyChangeListener[] result = automaton.getPropertyChangeListeners(Event.E1.toString() + Automaton.ENABLED_SUFFIX);
        final int expectedSize = 1;
        Assert.assertEquals("Listener list should contain only one item", expectedSize, result.length);
        Assert.assertEquals("Listener list should contain the added listener", listener, result[0]);
    }

    @Test
    public void testAddPropertyChangeListenerAllowedE2() {
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(Event.E2.toString() + Automaton.ENABLED_SUFFIX, listener);
        final PropertyChangeListener[] result = automaton.getPropertyChangeListeners(Event.E2.toString() + Automaton.ENABLED_SUFFIX);
        final int expectedSize = 1;
        Assert.assertEquals("Listener list should contain only one item", expectedSize, result.length);
        Assert.assertEquals("Listener list should contain the added listener", listener, result[0]);
    }

    @Test
    public void testAddPropertyChangeListenerAllowedState() {
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(Automaton.STATE_PROPERTY, listener);
        final PropertyChangeListener[] result = automaton.getPropertyChangeListeners(Automaton.STATE_PROPERTY);
        final int expectedSize = 1;
        Assert.assertEquals("Listener list should contain only one item", expectedSize, result.length);
        Assert.assertEquals("Listener list should contain the added listener", listener, result[0]);
    }

    @Test
    public void testRemovePropertyChangeListenerPropertyChangeListener() {
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
    public void testAddPropertyChangeListenerStringPropertyChangeListener() {
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(Automaton.STATE_PROPERTY, listener);
        final PropertyChangeListener[] result = automaton.getPropertyChangeListeners(Automaton.STATE_PROPERTY);
        final int expectedSize = 1;
        Assert.assertEquals("Listener list should contain only one item", expectedSize, result.length);
        Assert.assertEquals("Listener list should contain the added listener", listener, result[0]);
    }

    @Test
    public void testRemovePropertyChangeListenerStringPropertyChangeListener() {
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
    public void testAddPropertyChangeListenerStringNotAllowed() {
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(INCORRECT_PROPERTY_NAME, listener);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPropertyChangeListenerListenerNull() {
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

    @Test
    public void testToStringForComplexAutomaton() {
        final Automaton<Event, State> automaton = getFullAutomaton();
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
        a1.reinit();
        a2.reinit();
        final Automaton<Event, State> automaton = getAutomatonWithoutInitialState();
        final List<State> states = new ArrayList<>(Arrays.asList(State.S1, State.S2));
        final List<Action> actions = new ArrayList<>(Arrays.asList(a1, a2));
        final List<Condition> conditions = new ArrayList<>(Arrays.asList(p1, p2));
        automaton.registerInitialization(states, actions, conditions);

        return automaton;
    }

    private Automaton<Event, State> getFooAutomaton() {
        a1.reinit();
        a2.reinit();
        final Automaton<Event, State> automaton = getAutomatonWithoutInitialState();
        final List<State> states = new ArrayList<>(Arrays.asList(State.S1, State.S2));
        final List<Action> actions = new ArrayList<>(Arrays.asList(a1, a2));
        final List<Condition> conditions = new ArrayList<>(Arrays.asList(p3, p3));
        automaton.registerInitialization(states, actions, conditions);

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

    private static class ConditionImpl implements Condition {

        private final Boolean condition;

        public ConditionImpl(Boolean condition) {
            this.condition = condition;
        }

        @Override
        public boolean isVerified(Object... parameters) {
            Boolean value = (Boolean) parameters[0];
            return value == condition;
        }

        @Override
        public String toString() {
            return "ConditionImpl{" + "condition=" + condition + '}';
        }
    }

    private static class ConditionNeverVerified implements Condition {

        @Override
        public boolean isVerified(Object... parameters) {
            return false;
        }

    }
}
