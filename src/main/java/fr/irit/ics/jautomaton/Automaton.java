/*
 * Copyright 2020 David Navarre.
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

import fr.irit.ics.jautomaton.utils.Pair;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * This class allows the creation of simple automata based on the Mealy
 * notation.<br>It embeds means to create and handle:
 * <ul>
 * <li>States</li>
 * <li>Events</li>
 * <li>Event preconditions</li>
 * <li>Actions on transitions</li>
 * </ul>
 *
 * @author navarre
 * @param <E>
 * @param <S>
 */
public final class Automaton<E extends Enum, S extends Enum> {

    /**
     * REGEX that must be followed by regiters names.
     */
    private static final String REGISTER_NAME_PATTERN_STRING = "[a-zA-Z]([a-zA-Z0-9_])*";
    /**
     * REGEX that must be followed by regiters names.
     */
    private static final Pattern REGISTER_NAME_PATTERN = Pattern.compile(REGISTER_NAME_PATTERN_STRING);
    /**
     * Main data structure that contains any items defining the automaton.
     */
    private final Map<Pair<E, S>, Map<Precondition, Pair<S, Action>>> dataStructure;
    /**
     * The state property prefix, used for property change listening.
     */
    public static final String STATE_PROPERTY = "state";
    /**
     * The event enabling property prefix, used for property change listening.
     */
    public static final String ENABLED_SUFFIX = "_enabled";
    /**
     * The class logger.
     */
    private static final Logger LOG = Logger.
            getLogger(Automaton.class.getName());
    /**
     * The set of all allowed events.
     */
    private final Set<E> events;
    /**
     * The set of all allowed states.
     */
    private final Set<S> states;
    /**
     * Stores the current state value.
     */
    private S currentState;
    /**
     * Stores the description on the initial state.
     * <br>Under some conditions, the initial state may vary.
     */
    private final Map<Precondition, Pair<S, Action>> initialStateData;
    /**
     * Delegate that handles listeners for this automaton.
     */
    private final PropertyChangeSupport support;
    /**
     * Stores a set of register that may be used by the automaton (for actions
     * and/or preconditions).
     */
    private final Map<String, Object> registers;
    /**
     * Error message used when the set of states is empty.
     */
    private static final String ERROR_SET_OF_STATES_CANNOT_BE_EMPTY = "The set of States cannot be empty";
    /**
     * Error message used when the set of events is empty.
     */
    private static final String ERROR_SET_OF_EVENTS_CANNOT_BE_EMPTY = "The set of Events cannot be empty";
    /**
     * String used as a prefix for any state in any message.
     */
    private static final String STATE_PREFIX = "State ";
    /**
     * String used as a prefix for any event in any message.
     */
    private static final String EVENT_PREFIX = "Event ";
    /**
     * String used as a prefix for any register in any message.
     */
    private static final String REGISTER_PREFIX = "Register ";
    /**
     * String that provides an error message.
     */
    private static final String ERROR_SET_OF_INITIAL_STATES_CANNOT_BE_EMPTY
            = "The set of initial states cannot be empty";
    /**
     * String that provides an error message.
     */
    private static final String ERROR_INITIAL_STATE_CANNOT_BE_NULL = "The initial state cannot be null";

    /**
     * Build the base structure of an automaton based on a set of events and a
     * set of states.
     *
     * @param theEvents must be set containing at least one event.
     * @param theStates must be set containing at least one state.
     */
    public Automaton(final Set<E> theEvents, final Set<S> theStates) {
        if (Objects.isNull(theEvents) || theEvents.isEmpty()) {
            LOG.log(Level.SEVERE, ERROR_SET_OF_EVENTS_CANNOT_BE_EMPTY);
            throw new IllegalArgumentException(
                    ERROR_SET_OF_EVENTS_CANNOT_BE_EMPTY);
        }
        if (Objects.isNull(theStates) || theStates.isEmpty()) {
            LOG.log(Level.SEVERE, ERROR_SET_OF_STATES_CANNOT_BE_EMPTY);
            throw new IllegalArgumentException(
                    ERROR_SET_OF_STATES_CANNOT_BE_EMPTY);
        }

        this.events = Collections.unmodifiableSet(theEvents);
        this.states = Collections.unmodifiableSet(theStates);
        dataStructure = new HashMap<>();
        initialStateData = new HashMap<>();
        support = new PropertyChangeSupport(this);
        registers = new HashMap<>();
    }

    /**
     * Produces an error message when no state change is possible from the
     * current state.
     *
     * @param event the event that occured
     * @param parameters the parameters used for precondition and/or action
     * computing
     * @return the error message
     */
    private String getErrorMessageNoPossibleStateChange(final E event, final Object... parameters) {
        return "No state change possible with parameters "
                + Arrays.deepToString(parameters)
                + " for " + EVENT_PREFIX + event
                + " from " + STATE_PREFIX + currentState;
    }

    /**
     * Produces an error message used when an event is not allowed when the
     * system is in the current state.
     *
     * @param event the event that occured
     * @return the error message
     */
    private String getErrorMessageEventNotAllowedInContext(final E event) {
        return EVENT_PREFIX + event
                + " is not allowed in " + STATE_PREFIX + currentState;
    }

    /**
     * Provides an error message used when attempting to create an already
     * existing register.
     *
     * @param name the name of the register
     * @return an error message
     */
    private static String getErrorMessageRegisterAlreadyExists(final String name) {
        return REGISTER_PREFIX + name + " already exists.";
    }

    /**
     * Provides an error message used when attempting to use an non-existing
     * register.
     *
     * @param name the name of the register
     * @return an error message
     */
    private static String getErrorMessageRegisterDoesNotExist(final String name) {
        return REGISTER_PREFIX + name + " does not exist.";
    }

    /**
     * Produces an error message used when the refister name is incorrect.
     *
     * @param name the name of the register
     * @return an error message
     */
    private static String getErrorMessageRegisterNameIncorrect(final String name) {
        return "< " + name + " > is not a correct name for a register."
                + "\nName must fit "
                + REGISTER_NAME_PATTERN_STRING;
    }

    /**
     * Try to go from the current state to the future state.
     * <br>The future state is determined amongst a set of possible future
     * states by checking some preconditions (the preconditions may be evaluated
     * using object parameters).
     *
     * @param event the event that occured (only for notification purpose)
     * @param futurState the set of possible future states
     * @param parameters the set of parameters used for evaluating the
     * preconditions and/or the execution of the corresponding action.
     */
    private void tryStateChange(
            final E event,
            final Map<Precondition, Pair<S, Action>> futurState,
            final Object... parameters) {
        boolean foundState = false;
        for (Map.Entry<Precondition, Pair<S, Action>> entry : futurState.
                entrySet()) {
            if (entry.getKey().isVerified(parameters)) {
                goToState(entry.getValue().getFirst());
                entry.getValue().getSecond().execute(parameters);
                foundState = true;
                break;
            }
        }
        if (!foundState) {
            LOG.log(
                    Level.SEVERE, getErrorMessageNoPossibleStateChange(event, parameters));
            throw new IllegalArgumentException(
                    getErrorMessageNoPossibleStateChange(event, parameters));
        }
    }

    /**
     * This methods is used after the corresponding event occured. The object
     * parameters provided may be used for the evaluation of the precondition
     * and/or the execution of the action.
     *
     * @param event the event that has been triggered
     * @param parameters the parameters of both the precondition and the action
     */
    public void acceptEvent(final E event, final Object... parameters) {
        Pair<E, S> key = new Pair<>(event, currentState);
        if (dataStructure.containsKey(key)) {
            tryStateChange(event, dataStructure.get(key), parameters);
        } else {
            LOG.log(Level.SEVERE,
                    getErrorMessageEventNotAllowedInContext(event));
            throw new IllegalStateException(
                    getErrorMessageEventNotAllowedInContext(event));
        }
    }

    /**
     * Adds a register to the automaton.
     *
     * @param name of the register.
     */
    public void createRegister(final String name) {
        if (registers.containsKey(name)) {
            LOG.log(Level.SEVERE, getErrorMessageRegisterAlreadyExists(name));
            throw new IllegalArgumentException(
                    getErrorMessageRegisterAlreadyExists(name));
        } else if (Objects.isNull(name) || !REGISTER_NAME_PATTERN.matcher(name).matches()) {
            LOG.log(Level.SEVERE,
                    getErrorMessageRegisterNameIncorrect(name));
            throw new IllegalArgumentException(
                    getErrorMessageRegisterNameIncorrect(name));
        } else {
            registers.put(name, null);
        }
    }

    /**
     * Returns the register value converted into a given type.
     *
     * @param <T> the expected register value type
     * @param name the name of the register
     * @param type the class of type used for the convertion
     * @return the value of the register in the given type
     */
    public <T> T getRegisterValue(final String name, final Class<T> type) {
        if (!registers.containsKey(name)) {
            LOG.log(Level.SEVERE, getErrorMessageRegisterDoesNotExist(name));
            throw new IllegalArgumentException(
                    getErrorMessageRegisterDoesNotExist(name));
        }
        return type.cast(registers.get(name));
    }

    /**
     * Stes the value of a given register.
     *
     * @param name the name of the register
     * @param value the new value of this register
     */
    public void setRegisterValue(final String name, final Object value) {
        if (!registers.containsKey(name)) {
            LOG.log(Level.SEVERE, getErrorMessageRegisterDoesNotExist(name));
            throw new IllegalArgumentException(
                    getErrorMessageRegisterDoesNotExist(name));
        }
        registers.put(name, value);
    }

    /**
     * Creates asimple initialization for the automaton.
     *
     * @param initialState the initial state
     */
    public void registerInitialization(final S initialState) {
        registerInitialization(initialState, NullAction.getInstance());
    }

    /**
     * Creates an initialization of the automaton by defining the initial state
     * and the action to perform initially.
     *
     * @param initialState the initial not null state
     * @param initialAction the initial action (if null it is set to a void
     * action
     */
    public void registerInitialization(final S initialState,
            final Action initialAction) {
        if (Objects.isNull(initialState)) {
            LOG.log(Level.SEVERE, ERROR_INITIAL_STATE_CANNOT_BE_NULL);
            throw new IllegalArgumentException(
                    ERROR_INITIAL_STATE_CANNOT_BE_NULL);
        }
        final List<S> initialStates = new ArrayList<>(1);
        initialStates.add(initialState);
        final List<Action> initialActions = new ArrayList<>(1);
        if (Objects.isNull(initialAction)) {
            initialActions.add(NullAction.getInstance());
        } else {
            initialActions.add(initialAction);
        }
        registerInitialization(initialStates, initialActions, null);
    }

    /**
     * Registers a complex initialization where several initial states are
     * possible according to some preconditions. The three sets must be ordered.
     *
     * @param initialStates the not empty set of possible initial states
     * @param initialActions the initial set of actions action (if null it is
     * set to a void action
     * @param initialPreconditions the initial set of preconditions (if null it
     * is set to an always precondition
     */
    public void registerInitialization(
            final List<S> initialStates,
            final List<Action> initialActions,
            final List<Precondition> initialPreconditions) {
        if (Objects.isNull(initialStates) || initialStates.isEmpty()) {
            LOG.log(Level.SEVERE, ERROR_SET_OF_INITIAL_STATES_CANNOT_BE_EMPTY);
            throw new IllegalArgumentException(
                    ERROR_SET_OF_INITIAL_STATES_CANNOT_BE_EMPTY);
        }
        if (initialStates.size() == 1) {
            registerSingleStateInitialization(initialActions, initialStates);
        } else {
            registerMultipleStatesInitilization(initialStates, initialActions, initialPreconditions);
        }
    }

    /**
     * Registers the initialization process in the case of a single initial
     * state.
     *
     * @param initialActions the initial action (at most the first item of this
     * list is used)
     * @param initialStates the initial state (at most the first item of this
     * list is used)
     */
    private void registerSingleStateInitialization(final List<Action> initialActions, final List<S> initialStates) {
        final Action action;
        if (Objects.isNull(initialActions) || initialActions.isEmpty()) {
            action = NullAction.getInstance();
        } else {
            action = initialActions.get(0);
        }
        initialStateData.put(TruePrecondition.getInstance(),
                new Pair<>(initialStates.get(0), action));
    }

    /**
     * Registers the initialization process in the case of multiple initial
     * states.
     *
     * @param initialStates the non empty set of initial states
     * @param initialActions a set of actions
     * @param initialPreconditions a set of precondition
     */
    private void registerMultipleStatesInitilization(
            final List<S> initialStates,
            final List<Action> initialActions,
            final List<Precondition> initialPreconditions) {
        for (int i = 0; i < initialStates.size(); i++) {
            final S initialState = initialStates.get(i);
            final Action action;
            final Precondition precondition;
            if (Objects.isNull(initialActions)
                    || initialActions.size() < i + 1) {
                action = NullAction.getInstance();
            } else {
                action = initialActions.get(i);
            }
            if (Objects.isNull(initialPreconditions)
                    || initialPreconditions.size() < i + 1) {
                precondition = TruePrecondition.getInstance();
            } else {
                precondition = initialPreconditions.get(i);
            }
            initialStateData.put(precondition, new Pair<>(initialState,
                    action));
        }
    }

    /**
     * Adds a simple transition between two states that occurs according to a
     * specified event.
     *
     * @param state1 the first state
     * @param event the triggering event
     * @param state2 the future state
     */
    public void registerTransition(final S state1, final E event,
            final S state2) {
        registerTransition(state1, event, state2, NullAction.getInstance());
    }

    /**
     * Adds a transition between two states that occurs according to a specified
     * event. When performing the transition, an action may be executed.
     *
     * @param state1 the first state
     * @param event the triggering event
     * @param state2 the future state
     * @param action the action to performed
     */
    public void registerTransition(final S state1, final E event,
            final S state2, final Action action) {
        registerTransition(state1, event, state2, action, TruePrecondition.
                getInstance());
    }

    /**
     * Adds a transition between two states that occurs according to a specified
     * event under some conditions. When performing the transition, an action
     * may be executed.
     *
     * @param state1 the first state
     * @param event the triggering event
     * @param state2 the future state
     * @param action the action to performed
     * @param precondition the conditions that must be verified to allow the
     * transition
     */
    public void registerTransition(final S state1, final E event,
            final S state2, final Action action,
            final Precondition precondition) {
        final Map<Precondition, Pair<S, Action>> transitions;
        final Pair<E, S> key = new Pair<>(event, state1);
        if (dataStructure.containsKey(key)) {
            transitions = dataStructure.get(key);
        } else {
            transitions = new HashMap<>();
            dataStructure.put(key, transitions);
        }
        transitions.put(precondition, new Pair<>(state2, action));
    }

    /**
     * Forces the initialization of the automaton by providing some parameters
     * that may be used to evaluate preconditions and/or to execute actions.
     *
     * @param parameters the possible object values used for precondition and/or
     * actions.
     */
    public void initialize(final Object... parameters) {
        tryStateChange(null, initialStateData, parameters);
    }

    /**
     * Really performs the state change. When performed, the event enabling is
     * computed.
     *
     * @param state the future state
     */
    private void goToState(final S state) {
        S oldState = currentState;
        currentState = state;
        final Set<E> oldEnableEvents = new HashSet<>(events.size());
        dataStructure.keySet().stream()
                .filter((Pair<E, S> pair) -> (pair.getSecond().equals(oldState)))
                .forEachOrdered((Pair<E, S> pair) -> oldEnableEvents.add(pair.getFirst()));

        final Set<E> enableEvents = new HashSet<>(events.size());
        dataStructure.keySet().stream().
                filter((Pair<E, S> pair) -> (pair.getSecond().equals(currentState))).
                forEachOrdered((Pair<E, S> pair) -> enableEvents.add(pair.getFirst()));
        support.firePropertyChange(STATE_PROPERTY, oldState, currentState);
        events.forEach((E anyEvent)
                -> support.firePropertyChange(anyEvent.toString() + ENABLED_SUFFIX,
                        !enableEvents.contains(anyEvent), enableEvents.contains(
                        anyEvent))
        );
    }

    /**
     * Provides the enabling of an event.
     *
     * @param event the event
     * @return trus if the event should be enabled
     */
    public boolean isEventEnabled(final E event) {
        if (Objects.isNull(currentState)) {
            return false;
        }
        return (dataStructure.keySet().stream()
                .anyMatch((Pair<E, S> pair) -> (currentState.equals(pair.getSecond())
                && pair.getFirst().equals(event))));
    }

    /**
     * Registers a listener of any changes within the automaton.
     *
     * @param listener the new listener to be added
     */
    public void addPropertyChangeListener(
            final PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener the new listener to be removed
     */
    public void removePropertyChangeListener(
            final PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    /**
     * Registers a listener of a particular state change within the automaton.
     *
     * @param propertyName the name of the property to listen to
     * @param listener the new listener to be added
     */
    public void addPropertyChangeListener(
            final String propertyName,
            final PropertyChangeListener listener) {
        support.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Removes a listener.
     *
     * @param propertyName the name of the property that was listened to
     * @param listener the new listener to be removed
     */
    public void removePropertyChangeListener(
            final String propertyName,
            final PropertyChangeListener listener) {
        support.removePropertyChangeListener(propertyName, listener);
    }

    @Override
    public String toString() {
        String toString = "Initial State: " + initialStateData.values() + "\n";
        for (S state : states) {
            toString = dataStructure.
                    entrySet().stream()
                    .filter((Map.Entry<Pair<E, S>, Map<Precondition, Pair<S, Action>>> entry)
                            -> (entry.getKey().getSecond().equals(state)))
                    .map((Map.Entry<Pair<E, S>, Map<Precondition, Pair<S, Action>>> entry)
                            -> state
                    + "=>" + entry.getKey().getFirst()
                    + "=>" + entry.getValue().values()
                    + "\n")
                    .reduce(toString, String::concat);
        }
        return toString;
    }

    /**
     * An action object that does nothing.
     */
    private static class NullAction implements Action {

        /**
         * The null action singleton instance.
         */
        private static final NullAction SINGLETON;

        /**
         * The singleton initialization.
         */
        static {
            SINGLETON = new NullAction();
        }

        /**
         * Provides a unique instance of a null action.
         *
         * @return the null action object
         */
        public static NullAction getInstance() {
            return SINGLETON;
        }

        @Override
        public void execute(final Object... parameters) {
            //Do nothing.
        }
    }

    /**
     * A precondition that is always true.
     */
    private static class TruePrecondition implements Precondition {

        /**
         * The true precondition unique instance.
         */
        private static final TruePrecondition SINGLETON;

        /**
         * The singleton initialization.
         */
        static {
            SINGLETON = new TruePrecondition();
        }

        /**
         * Provides the unique instance of the always true precondition.
         *
         * @return the precondition object
         */
        public static TruePrecondition getInstance() {
            return SINGLETON;
        }

        @Override
        public boolean isVerified(final Object... parameters) {
            return true;
        }
    }
}
