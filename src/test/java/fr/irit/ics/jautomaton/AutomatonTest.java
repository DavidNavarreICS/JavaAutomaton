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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author David Navarre &lt;David.Navarre@irit.fr&gt;
 */
class AutomatonTest {

    /**
     * Error message used when the set of states is empty.
     */
    private static final String ERROR_SET_OF_STATES_CANNOT_BE_EMPTY
            = "The set of States cannot be empty";
    /**
     * Error message used when the set of events is empty.
     */
    private static final String ERROR_SET_OF_EVENTS_CANNOT_BE_EMPTY
            = "The set of Events cannot be empty";
    /**
     * String that provides an error message.
     */
    private static final String ERROR_SET_OF_INITIAL_STATES_CANNOT_BE_EMPTY
            = "The set of initial states cannot be empty";
    /**
     * String that provides an error message.
     */
    private static final String ERROR_INITIAL_STATE_CANNOT_BE_NULL
            = "The initial state cannot be null";
    /**
     * String that provides an error message.
     */
    private static final String NULL_SET_OF_EVENTS_SHOULD_TRIGGER_AN_EXCEPTION
            = "A null set of events should trigger an IllegalArgumentException";
    /**
     * String that provides an error message.
     */
    private static final String EMPTY_SET_OF_EVENTS_SHOULD_TRIGGER_AN_EXCEPTION
            = "An empty set of events should trigger an IllegalArgumentException";
    /**
     * String that provides an error message.
     */
    private static final String NULL_SET_OF_STATES_SHOULD_TRIGGER_AN_EXCEPTION
            = "A null set of states should trigger an IllegalArgumentException";
    /**
     * String that provides an error message.
     */
    private static final String EMPTY_SET_OF_STATES_SHOULD_TRIGGER_AN_EXCEPTION
            = "An empty set of states should trigger an IllegalArgumentException";

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

    AutomatonTest() {
    }

    @Test
    void testConstructorNullEventSetShouldFail() {
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Automaton<>(null, EnumSet.allOf(State.class)), "Null event not allowed.");
        final String exceptionMessage = exception.getMessage();
        Assertions.assertEquals(ERROR_SET_OF_EVENTS_CANNOT_BE_EMPTY, exceptionMessage,
                NULL_SET_OF_EVENTS_SHOULD_TRIGGER_AN_EXCEPTION);
    }

    @Test
    void testConstructorEmptyEventSetShouldFail() {
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Automaton<>(new HashSet<Event>(0), EnumSet.allOf(State.class)), "Empty event set not allowed.");
        final String exceptionMessage = exception.getMessage();
        Assertions.assertEquals(ERROR_SET_OF_EVENTS_CANNOT_BE_EMPTY, exceptionMessage,
                EMPTY_SET_OF_EVENTS_SHOULD_TRIGGER_AN_EXCEPTION);
    }

    @Test
    void testConstructorNullStateSetShouldFail() {
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Automaton<>(EnumSet.allOf(Event.class), null), "Null state not allowed.");
        final String exceptionMessage = exception.getMessage();
        Assertions.assertEquals(ERROR_SET_OF_STATES_CANNOT_BE_EMPTY, exceptionMessage,
                NULL_SET_OF_STATES_SHOULD_TRIGGER_AN_EXCEPTION);
    }

    @Test
    void testConstructorEmptyStateSetShouldFail() {
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Automaton<>(EnumSet.allOf(Event.class), new HashSet<State>(0)), "Empty state set not allowed.");
        final String exceptionMessage = exception.getMessage();
        Assertions.assertEquals(ERROR_SET_OF_STATES_CANNOT_BE_EMPTY, exceptionMessage,
                EMPTY_SET_OF_STATES_SHOULD_TRIGGER_AN_EXCEPTION);
    }

    @Test
    void testAcceptEventIsCorrect() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        automaton.initialize();
        automaton.acceptEvent(Event.E1);
        State finalState = automaton.getCurrentState();
        State expected = State.S2;
        Assertions.assertEquals(expected, finalState, "The final state should be S2");
    }

    @Test
    void testAcceptEventE2StateIncorrect() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        automaton.initialize();
        final IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> automaton.acceptEvent(Event.E2), "Cannot accept E2 in this state.");
        Assertions.assertNotNull(exception, String.format("{0}", exception.getMessage()));
    }

    @Test
    void testAcceptEventEventNullNotAllowed() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        automaton.initialize();
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> automaton.acceptEvent(null), "Null event not allowed.");
        Assertions.assertNotNull(exception, String.format("{0}", exception.getMessage()));
    }

    @Test
    void testCreateRegisterCorrectName() {
        final Automaton<Event, State> automaton = getAutomaton();
        automaton.createRegister(CORRECT_REGISTER_NAME);
        final Integer value = 0;
        automaton.setRegisterValue(CORRECT_REGISTER_NAME, value);
        Integer result = automaton.getRegisterValue(CORRECT_REGISTER_NAME, Integer.class);
        Assertions.assertEquals(value, result, "The register value should be the same as the one set");
    }

    @Test
    void testSetRegisterRegisterDoesNotExists() {
        final Automaton<Event, State> automaton = getAutomaton();
        final Integer value = 0;
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> automaton.setRegisterValue(CORRECT_REGISTER_NAME, value),
                "Non declared register cannot be update.");
        Assertions.assertNotNull(exception, String.format("{0}", exception.getMessage()));

    }

    @Test
    void testGetRegisterRegisterDoesNotExists() {
        final Automaton<Event, State> automaton = getAutomaton();
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> automaton.getRegisterValue(CORRECT_REGISTER_NAME, Integer.class),
                "Non declared register cannot be retrieved.");
        Assertions.assertNotNull(exception, String.format("{0}", exception.getMessage()));

    }

    @Test
    void testCreateRegisterIncorrectName() {
        final Automaton<Event, State> automaton = getAutomaton();
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> automaton.createRegister(INCORRECT_REGISTER_NAME),
                "Name must fit the correct pattern for registers.");
        Assertions.assertNotNull(exception, String.format("{0}", exception.getMessage()));

    }

    @Test
    void testCreateRegisterNullName() {
        final Automaton<Event, State> automaton = getAutomaton();
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> automaton.createRegister(null), "Null name not allowed.");
        Assertions.assertNotNull(exception, String.format("{0}", exception.getMessage()));
    }

    @Test
    void testCreateRegisterNameAlreadyUsed() {
        final Automaton<Event, State> automaton = getAutomaton();
        automaton.createRegister(CORRECT_REGISTER_NAME);
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> automaton.createRegister(CORRECT_REGISTER_NAME), "Nom cannot be used twice.");
        Assertions.assertNotNull(exception, String.format("{0}", exception.getMessage()));
    }

    @Test
    void testRegisterInitializationSimpleState() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        automaton.registerInitialization(State.S1);
        automaton.initialize();
        State result = automaton.getCurrentState();
        Assertions.assertEquals(State.S1, result,
                "The initial state should be the one registered as an initialization: S1");
    }

    @Test
    void testRegisterInitializationSimpleStateWithAction() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final ActionImpl actionImpl = new ActionImpl(0);
        automaton.registerInitialization(State.S1, actionImpl);
        Object[] parameters = new Object[]{0.0F, "FOO"};
        automaton.initialize(Arrays.asList(parameters));
        State result = automaton.getCurrentState();
        Assertions.assertEquals(State.S1, result,
                "The initial state should be the one registered as an initialization: S1");
        Object receivedParameter = actionImpl.getExecutionParameters();
        Assertions.assertEquals(parameters[0], receivedParameter, "Initial parameters lost during initialization");
    }

    @Test
    void testRegisterInitializationSimpleStateWithNullAction() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final ActionImpl actionImpl = null;
        automaton.registerInitialization(State.S1, actionImpl);
        Object[] parameters = new Object[]{0.0F, "FOO"};
        automaton.initialize(Arrays.asList(parameters));
        State result = automaton.getCurrentState();
        Assertions.assertEquals(State.S1, result,
                "The initial state should be the one registered as an initialization: S1");
    }

    @Test
    void testRegisterInitializationSimpleStateWithEmptyAction() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final List<State> states = new ArrayList<>(1);
        states.add(State.S1);
        final List<Action> actions = Collections.EMPTY_LIST;
        final List<Condition> conditions = Collections.EMPTY_LIST;
        automaton.registerInitialization(states, actions, conditions);
        Object[] parameters = new Object[]{0.0F, "FOO"};
        automaton.initialize(Arrays.asList(parameters));
        State result = automaton.getCurrentState();
        Assertions.assertEquals(State.S1,
                result, "The initial state should be the one registered as an initialization: S1");
    }

    @Test
    void testRegisterInitializationSimpleStateWithNullActionAndNullCondition() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final List<State> states = new ArrayList<>(1);
        states.add(State.S1);
        final List<Action> actions = null;
        final List<Condition> conditions = null;
        automaton.registerInitialization(states, actions, conditions);
        Object[] parameters = new Object[]{0.0F, "FOO"};
        automaton.initialize(Arrays.asList(parameters));
        State result = automaton.getCurrentState();
        Assertions.assertEquals(State.S1, result,
                "The initial state should be the one registered as an initialization: S1");
    }

    @Test
    void testRegisterInitializationMultipleStateWithActionAndConditionBranch1() {
        final Automaton<Event, State> automaton = getFullAutomaton();
        automaton.initialize(Arrays.asList(parametersS1));
        State result = automaton.getCurrentState();
        Assertions.assertEquals(State.S1, result,
                "The initial state should be the one registered as an initialization: S1");
        Object receivedParameter = a1.getExecutionParameters();
        Assertions.assertEquals(parametersS1[1], receivedParameter, "Initial parameters lost during initialization");
    }

    @Test
    void testRegisterInitializationMultipleStateWithActionAndConditionBranch2() {
        final Automaton<Event, State> automaton = getFullAutomaton();
        automaton.initialize(Arrays.asList(parametersS2));
        State result = automaton.getCurrentState();
        Assertions.assertEquals(State.S2, result,
                "The initial state should be the one registered as an initialization: S2");
        Object receivedParameter = a2.getExecutionParameters();
        Assertions.assertEquals(parametersS2[2], receivedParameter, "Initial parameters lost during initialization");
    }

    @Test
    void testRegisterInitializationMultipleStateWithFewActionsAndFewConditions() {
        a1.reinit();
        a2.reinit();
        final Automaton<Event, State> automaton = getAutomatonWithoutInitialState();
        final List<State> states = new ArrayList<>(Arrays.asList(State.S1, State.S2));
        final List<Action> actions = new ArrayList<>(Arrays.asList(a1));
        final List<Condition> conditions = new ArrayList<>(Arrays.asList(p1));
        automaton.registerInitialization(states, actions, conditions);
        automaton.initialize(Arrays.asList(parametersS2));
        State result = automaton.getCurrentState();
        Assertions.assertEquals(State.S2, result,
                "The initial state should be the one registered as an initialization: S2");
    }

    @Test
    void testRegisterInitializationMultipleStateWithOneStateNullActionsAndNullConditions() {
        a1.reinit();
        a2.reinit();
        final Automaton<Event, State> automaton = getAutomatonWithoutInitialState();
        final List<State> states = new ArrayList<>(Arrays.asList(State.S1, State.S2));
        final List<Action> actions = null;
        final List<Condition> conditions = null;
        automaton.registerInitialization(states, actions, conditions);
        automaton.initialize(Arrays.asList(parametersS2));
        State result = automaton.getCurrentState();
        Assertions.assertTrue(State.S1.equals(result) || State.S2.equals(result),
                "The initial state should be the one registered as an initialization: S1 or S2");
    }

    @Test
    void testRegisterInitializationMultipleStateWithOneStateEmptyActionsAndEmptyConditions() {
        a1.reinit();
        a2.reinit();
        final Automaton<Event, State> automaton = getAutomatonWithoutInitialState();
        final List<State> states = new ArrayList<>(Arrays.asList(State.S1, State.S2));
        final List<Action> actions = Collections.emptyList();
        final List<Condition> conditions = Collections.emptyList();
        automaton.registerInitialization(states, actions, conditions);
        automaton.initialize(Arrays.asList(parametersS2));
        State result = automaton.getCurrentState();
        Assertions.assertTrue(State.S1.equals(result) || State.S2.equals(result),
                "The initial state should be the one registered as an initialization: S1 or S2");
    }

    @Test
    void testRegisterInitializationMultipleStateWithActionAndConditionNeverReachable() {
        final Automaton<Event, State> automaton = getFooAutomaton();
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> automaton.initialize(Arrays.asList(parametersS1)), "Conditions must be reachable.");
        Assertions.assertNotNull(exception, String.format("{0}", exception.getMessage()));
    }

    @Test
    void testRegisterInitializationMultipleStateWithActionAndConditionNullSetOfState() {
        final Automaton<Event, State> automaton = getFooAutomaton();
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> automaton.registerInitialization(null, null, null), "Multiple initial state requiers conditions.");
        Assertions.assertNotNull(exception, String.format("{0}", exception.getMessage()));
    }

    @Test
    void testRegisterInitializationMultipleStateWithActionAndConditionEmptySetOfState() {
        final Automaton<Event, State> automaton = getFooAutomaton();
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> automaton.registerInitialization(new ArrayList<State>(0), null, null),
                "At least one initial state.");
        Assertions.assertNotNull(exception, String.format("{0}", exception.getMessage()));
    }

    @Test
    void testRegisterInitializationIncorrectState() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> automaton.registerInitialization(null), "Initialization must be correct.");
        Assertions.assertNotNull(exception, String.format("{0}", exception.getMessage()));
    }

    @Test
    void testRegisterTransitionTwoFinalStatesFromSameInitialStateBranch1() {
        final Automaton<Event, State> automaton = getAutomaton();
        a1.reinit();
        a2.reinit();
        automaton.registerInitialization(State.S1);
        automaton.registerTransition(State.S1, Event.E1, State.S2, a1, p1);
        automaton.registerTransition(State.S1, Event.E1, State.S3, a2, p2);
        automaton.initialize();
        Boolean isE1Enabled = automaton.isEventEnabled(Event.E1);
        Assertions.assertTrue(isE1Enabled, "E1 should be enabled");
        automaton.acceptEvent(Event.E1, Arrays.asList(parametersS1));
        State result = automaton.getCurrentState();
        Assertions.assertEquals(State.S2, result,
                "The initial state should be the one registered as an initialization: S2");
        Object receivedParameter = a1.getExecutionParameters();
        Assertions.assertEquals(parametersS1[1], receivedParameter, "Initial parameters lost during transition");
    }

    @Test
    void testRegisterTransitionTwoFinalStatesFromSameInitialStateBranch2() {
        final Automaton<Event, State> automaton = getAutomaton();
        a1.reinit();
        a2.reinit();
        automaton.registerInitialization(State.S1);
        automaton.registerTransition(State.S1, Event.E1, State.S2, a1, p1);
        automaton.registerTransition(State.S1, Event.E1, State.S3, a2, p2);
        automaton.initialize();
        Boolean isE1Enabled = automaton.isEventEnabled(Event.E1);
        Assertions.assertTrue(isE1Enabled, "E1 should be enabled");
        automaton.acceptEvent(Event.E1, Arrays.asList(parametersS2));
        State result = automaton.getCurrentState();
        Assertions.assertEquals(State.S3, result,
                "The initial state should be the one registered as an initialization: S3");
        Object receivedParameter = a2.getExecutionParameters();
        Assertions.assertEquals(parametersS2[2], receivedParameter, "Initial parameters lost during transition");
    }

    @Test
    void testEnabledEventE1IsNotEnabledWhileNotInitialized() {
        final Automaton<Event, State> automaton = getAutomaton();
        a1.reinit();
        a2.reinit();
        automaton.registerInitialization(State.S1);
        automaton.registerTransition(State.S1, Event.E1, State.S2);
        Boolean isE1Enabled = automaton.isEventEnabled(Event.E1);
        Assertions.assertFalse(isE1Enabled, "E1 should not be enabled");
    }

    @Test
    void testEnabledEventE1IsEnabled() {
        final Automaton<Event, State> automaton = getAutomaton();
        a1.reinit();
        a2.reinit();
        automaton.registerInitialization(State.S1);
        automaton.registerTransition(State.S1, Event.E1, State.S2);
        automaton.initialize();
        Boolean isE1Enabled = automaton.isEventEnabled(Event.E1);
        Assertions.assertTrue(isE1Enabled, "E1 should be enabled");
    }

    @Test
    void testEnabledEventE2IsNotEnabled() {
        final Automaton<Event, State> automaton = getAutomaton();
        a1.reinit();
        a2.reinit();
        automaton.registerInitialization(State.S1);
        automaton.registerTransition(State.S1, Event.E1, State.S2);
        automaton.initialize();
        Boolean isE2Enabled = automaton.isEventEnabled(Event.E2);
        Assertions.assertFalse(isE2Enabled, "E2 should not be enabled");
    }

    @Test
    void testEnabledEventE2IsNotEnabledInS1() {
        final Automaton<Event, State> automaton = getAutomaton();
        a1.reinit();
        a2.reinit();
        automaton.registerInitialization(State.S1);
        automaton.registerTransition(State.S1, Event.E1, State.S2);
        automaton.registerTransition(State.S2, Event.E2, State.S1);
        automaton.initialize();
        Boolean isE2Enabled = automaton.isEventEnabled(Event.E2);
        Assertions.assertFalse(isE2Enabled, "E2 should not be enabled");
    }

    @Test
    void testEnabledEventE1IsEnabledInS1() {
        final Automaton<Event, State> automaton = getAutomaton();
        automaton.registerInitialization(State.S1);
        automaton.registerTransition(State.S1, Event.E1, State.S2);
        automaton.registerTransition(State.S2, Event.E2, State.S1);
        automaton.initialize();
        Boolean isE1Enabled = automaton.isEventEnabled(Event.E1);
        Assertions.assertTrue(isE1Enabled, "E1 should be enabled");
    }

    @Test
    void testPropertyChangeListenerInitialState() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final StatePropertyChangeListener statePropertyListener = new StatePropertyChangeListener();
        automaton.addPropertyChangeListener(Automaton.STATE_PROPERTY, statePropertyListener);
        automaton.initialize();
        State expected = State.S1;
        Assertions.assertEquals(expected, statePropertyListener.getNewValue(), "The initial state should be S1");
    }

    @Test
    void testPropertyChangeListenerInitialEnablingE1() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final EventPropertyChangeListener eventPropertyListener = new EventPropertyChangeListener(Event.E1);
        automaton.addPropertyChangeListener(eventPropertyListener.getPropertyName(), eventPropertyListener);
        automaton.initialize();
        Boolean expected = true;
        Assertions.assertEquals(expected, eventPropertyListener.getNewValue(), "E1 should be enabled");
    }

    @Test
    void testPropertyChangeListenerInitialEnablingE2() {
        final Automaton<Event, State> automaton = getAutomatonWithInitialState();
        final EventPropertyChangeListener eventPropertyListener = new EventPropertyChangeListener(Event.E2);
        automaton.addPropertyChangeListener(eventPropertyListener.getPropertyName(), eventPropertyListener);
        automaton.initialize();
        Boolean expected = false;
        Assertions.assertEquals(expected, eventPropertyListener.getNewValue(), "E2 should not be enabled");
    }

    @Test
    void testAddPropertyChangeListenerAllowedE1() {
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(Event.E1.toString() + Automaton.ENABLED_SUFFIX, listener);
        final PropertyChangeListener[] result = automaton.getPropertyChangeListeners(
                Event.E1.toString() + Automaton.ENABLED_SUFFIX);
        final int expectedSize = 1;
        Assertions.assertEquals(expectedSize, result.length, "Listener list should contain only one item");
        Assertions.assertEquals(listener, result[0], "Listener list should contain the added listener");
    }

    @Test
    void testAddPropertyChangeListenerAllowedE2() {
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(Event.E2.toString() + Automaton.ENABLED_SUFFIX, listener);
        final PropertyChangeListener[] result = automaton.getPropertyChangeListeners(
                Event.E2.toString() + Automaton.ENABLED_SUFFIX);
        final int expectedSize = 1;
        Assertions.assertEquals(expectedSize, result.length, "Listener list should contain only one item");
        Assertions.assertEquals(listener, result[0], "Listener list should contain the added listener");
    }

    @Test
    void testAddPropertyChangeListenerAllowedState() {
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(Automaton.STATE_PROPERTY, listener);
        final PropertyChangeListener[] result = automaton.getPropertyChangeListeners(Automaton.STATE_PROPERTY);
        final int expectedSize = 1;
        Assertions.assertEquals(expectedSize, result.length, "Listener list should contain only one item");
        Assertions.assertEquals(listener, result[0], "Listener list should contain the added listener");
    }

    @Test
    void testRemovePropertyChangeListenerPropertyChangeListener() {
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(listener);
        PropertyChangeListener[] result = automaton.getPropertyChangeListeners();
        int expectedSize = 1;
        Assertions.assertEquals(expectedSize, result.length, "Listener list should contain only one item");
        Assertions.assertEquals(listener, result[0], "Listener list should contain the added listener");
        automaton.removePropertyChangeListener(listener);
        result = automaton.getPropertyChangeListeners();
        expectedSize = 0;
        Assertions.assertEquals(expectedSize, result.length, "Listener list should be empty");
    }

    @Test
    void testAddPropertyChangeListenerStringPropertyChangeListener() {
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(Automaton.STATE_PROPERTY, listener);
        final PropertyChangeListener[] result = automaton.getPropertyChangeListeners(Automaton.STATE_PROPERTY);
        final int expectedSize = 1;
        Assertions.assertEquals(expectedSize, result.length, "Listener list should contain only one item");
        Assertions.assertEquals(listener, result[0], "Listener list should contain the added listener");
    }

    @Test
    void testRemovePropertyChangeListenerStringPropertyChangeListener() {
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        automaton.addPropertyChangeListener(Automaton.STATE_PROPERTY, listener);
        PropertyChangeListener[] result = automaton.getPropertyChangeListeners(Automaton.STATE_PROPERTY);
        int expectedSize = 1;
        Assertions.assertEquals(expectedSize, result.length, "Listener list should contain only one item");
        Assertions.assertEquals(listener, result[0], "Listener list should contain the added listener");
        automaton.removePropertyChangeListener(Automaton.STATE_PROPERTY, listener);
        result = automaton.getPropertyChangeListeners(Automaton.STATE_PROPERTY);
        expectedSize = 0;
        Assertions.assertEquals(expectedSize, result.length, "Listener list should be empty");
    }

    @Test
    void testAddPropertyChangeListenerStringNotAllowed() {
        final Automaton<Event, State> automaton = getAutomaton();
        final PropertyChangeListener listener = new FooListener();
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> automaton.addPropertyChangeListener(INCORRECT_PROPERTY_NAME, listener),
                "Cannot register for unknown property.");
        Assertions.assertNotNull(exception, String.format("{0}", exception.getMessage()));
    }

    @Test
    void testAddPropertyChangeListenerListenerNull() {
        final Automaton<Event, State> automaton = getAutomaton();
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> automaton.addPropertyChangeListener(Automaton.STATE_PROPERTY, null), "Null listener not allowed.");
        Assertions.assertNotNull(exception, String.format("{0}", exception.getMessage()));
    }

    @Test
    void testToString() {
        final Automaton<Event, State> automaton = getAutomaton();
        String result = automaton.toString();
        Boolean isNull = Objects.isNull(result);
        Boolean isEmpty = result.isEmpty();
        Assertions.assertFalse(isNull, "toString cannot be null");
        Assertions.assertFalse(isEmpty, "toString cannot be empty");
    }

    @Test
    void testToStringForComplexAutomaton() {
        final Automaton<Event, State> automaton = getFullAutomaton();
        String result = automaton.toString();
        Boolean isNull = Objects.isNull(result);
        Boolean isEmpty = result.isEmpty();
        Assertions.assertFalse(isNull, "toString cannot be null");
        Assertions.assertFalse(isEmpty, "toString cannot be empty");
    }

    private static Automaton<Event, State> getAutomaton() {
        final Automaton<Event, State> automaton = new Automaton<>(
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

        EventPropertyChangeListener(Event event) {
            propertyName = event.toString() + Automaton.ENABLED_SUFFIX;
        }

        public Object getNewValue() {
            return newValue;
        }

        public String getPropertyName() {
            return propertyName;
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

        ActionImpl(int index) {
            this.index = index;
        }

        @Override
        public void execute(List<Object> parameters) {
            executionParameter = parameters.get(index);
        }

        public Object getExecutionParameters() {
            return executionParameter;
        }

        public void reinit() {
            executionParameter = null;
        }
    }

    private static class ConditionImpl implements Condition {

        private final Boolean condition;

        ConditionImpl(Boolean condition) {
            this.condition = condition;
        }

        @Override
        public boolean isVerified(List<Object> parameters) {
            Boolean value = (Boolean) parameters.get(0);
            return Objects.equals(value, condition);
        }

        @Override
        public String toString() {
            return "ConditionImpl{" + "condition=" + condition + '}';
        }
    }

    private static class ConditionNeverVerified implements Condition {

        @Override
        public boolean isVerified(List<Object> parameters) {
            return false;
        }

    }
}
