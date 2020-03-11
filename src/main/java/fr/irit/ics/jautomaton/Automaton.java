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
import java.util.logging.Logger;

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
     * Main data structure that contains any items defining the automaton.
     */
    private final Map<Pair<E, S>, Map<Precondition, Pair<S, Action>>> automaton;
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
            throw new IllegalArgumentException(
                    "No state change possible with parameters "
                    + Arrays.deepToString(parameters)
                    + " for event " + event
                    + " from state " + currentState);
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
        if (automaton.containsKey(key)) {
            tryStateChange(event, automaton.get(key), parameters);
        } else {
            throw new IllegalStateException(
                    "Event " + event
                    + " is not allowed in state " + currentState);
        }
    }

    /**
     * Build the base structure of an automaton based on a set of events and a
     * set of states.
     *
     * @param theEvents must be set containing at least one event.
     * @param theStates must be set containing at least one state.
     */
    public Automaton(final Set<E> theEvents, final Set<S> theStates) {
        if (Objects.isNull(theEvents) || theEvents.isEmpty()) {
            throw new IllegalArgumentException(
                    "The set of Events cannot be empty");
        }
        if (Objects.isNull(theStates) || theStates.isEmpty()) {
            throw new IllegalArgumentException(
                    "The set of States cannot be empty");
        }

        this.events = Collections.unmodifiableSet(theEvents);
        this.states = Collections.unmodifiableSet(theStates);
        automaton = new HashMap<>();
        initialStateData = new HashMap<>();
        support = new PropertyChangeSupport(this);
        registers = new HashMap<>();
    }

    /**
     * Adds a register to the automaton.
     *
     * @param name of the register.
     */
    public void createRegister(final String name) {
        if (registers.containsKey(name)) {
            throw new IllegalArgumentException(
                    "Register " + name + " already exists.");
        } else if (Objects.isNull(name) || !name.matches(
                "[a-zA-Z]([a-zA-Z0-9_])*")) {
            throw new IllegalArgumentException(
                    "< " + name + " > is not a correct name for a register."
                    + "\nName must fit [a-zA-Z]([a-zA-Z0-9_])*");
        }
        registers.put(name, null);
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
            throw new IllegalArgumentException(
                    "Register " + name + " does not exist.");
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
            throw new IllegalArgumentException(
                    "Register " + name + " does not exist.");
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
        final List<S> initialStates = new ArrayList<>(1);
        final List<Action> initialActions = new ArrayList<>(1);
        if (Objects.isNull(initialState)) {
            throw new IllegalArgumentException(
                    "The initial state cannot be null");
        }
        initialStates.add(initialState);
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
            throw new IllegalArgumentException(
                    "The set of initial states cannot be empty");
        }
        if (initialStates.size() == 1) {
            final Action action;
            if (initialActions.isEmpty()) {
                action = NullAction.getInstance();
            } else {
                action = initialActions.get(0);
            }
            initialStateData.put(TruePrecondition.getInstance(),
                    new Pair<>(initialStates.get(0), action));
        } else {
            for (int i = 0; i < initialStates.size(); i++) {
                final S initialState = initialStates.get(i);
                final Action action;
                final Precondition precondition;
                if (initialActions.size() < i + 1) {
                    action = NullAction.getInstance();
                } else {
                    action = initialActions.get(i);
                }
                if (initialPreconditions.size() < i + 1) {
                    precondition = TruePrecondition.getInstance();
                } else {
                    precondition = initialPreconditions.get(i);
                }
                initialStateData.put(precondition, new Pair<>(initialState,
                        action));
            }
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
        if (automaton.containsKey(key)) {
            transitions = automaton.get(key);
        } else {
            transitions = new HashMap<>();
            automaton.put(key, transitions);
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
        automaton.keySet().stream()
                .filter((pair) -> (pair.getSecond().equals(oldState)))
                .forEachOrdered((pair) -> {
                    oldEnableEvents.add(pair.getFirst());
                });

        final Set<E> enableEvents = new HashSet<>(events.size());
        automaton.keySet().stream().
                filter((pair) -> (pair.getSecond().equals(currentState))).
                forEachOrdered((pair) -> {
                    enableEvents.add(pair.getFirst());
                });
        support.firePropertyChange(STATE_PROPERTY, oldState, currentState);
        events.forEach((anyEvent) -> {
            support.firePropertyChange(anyEvent.toString() + ENABLED_SUFFIX,
                    !enableEvents.contains(anyEvent), enableEvents.contains(
                    anyEvent));
        });
    }
    /**
     * The class logger.
     */
    private static final Logger LOG = Logger.
            getLogger(Automaton.class.getName());

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
        return (automaton.keySet().stream()
                .anyMatch((pair) -> (currentState.equals(pair.getSecond())
                && pair.getFirst().equals(event))));
    }
    /**
     * The state property prefix, used for property change listening.
     */
    public static final String STATE_PROPERTY = "state";
    /**
     * The event enabling property prefix, used for property change listening.
     */
    public static final String ENABLED_SUFFIX = "_enabled";

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

    /**
     * Supports the creation of typed pair of objects.
     *
     * @param <E1> the type of the first object of the pair
     * @param <E2> the type of the second object of the pair
     */
    private class Pair<E1, E2> {

        /**
         * Used to compute the hashcode.
         */
        private static final int HASHCODE_MODIFIER = 53;
        /**
         * Used to compute the hashcode.
         */
        private static final int HASHCODE_BASE = 5;
        /**
         * The first object.
         */
        private final E1 first;
        /**
         * The second object.
         */
        private final E2 second;

        /**
         * Build a typed pair of two objects.
         *
         * @param aFirstObject the first object
         * @param aSecondObject the second object
         */
        Pair(final E1 aFirstObject, final E2 aSecondObject) {
            this.first = aFirstObject;
            this.second = aSecondObject;
        }

        /**
         * Provides the first object of the pair.
         *
         * @return the first object
         */
        public E1 getFirst() {
            return first;
        }

        /**
         * Provides the second object of the pair.
         *
         * @return the second object
         */
        public E2 getSecond() {
            return second;
        }

        @Override
        public int hashCode() {
            int hash = HASHCODE_BASE;
            hash = HASHCODE_MODIFIER * hash + Objects.hashCode(this.first);
            hash = HASHCODE_MODIFIER * hash + Objects.hashCode(this.second);
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Pair<?, ?> other = (Pair<?, ?>) obj;
            if (!Objects.equals(this.first, other.first)) {
                return false;
            }
            return (Objects.equals(this.second, other.second));
        }

        @Override
        public String toString() {
            return "Pair{" + "<" + first + ", " + second + ">}";
        }

    }

    @Override
    public String toString() {
        String toString = "Initial State: " + initialStateData.values() + "\n";
        for (S state : states) {
            toString = automaton.
                    entrySet().stream()
                    .filter((entry) -> (entry.getKey().second.equals(state)))
                    .map((entry)
                            -> state
                    + "=>" + entry.getKey().first
                    + "=>" + entry.getValue().values()
                    + "\n")
                    .reduce(toString, String::concat);
        }
        return toString;
    }

}
