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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public class Automaton<Event extends Enum, State extends Enum> {

    private final Map<Pair<Event, State>, Map<Precondition, Pair<State, Action>>> automaton;
    private final Event[] events;
    private final State[] states;
    private State currentState;
    private final Map<Precondition, Pair<State, Action>> initialStateData;
    private final PropertyChangeSupport support;
    private final Map<String, Object> registers;

    public void tryStateChange(
            final Event event,
            final Map<Precondition, Pair<State, Action>> futurState,
            final Object... parameters) {
        boolean foundState = false;
        for (Map.Entry<Precondition, Pair<State, Action>> entry : futurState.
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
                    "No state change possible with parameters " + Arrays.
                            deepToString(parameters) + " for event " + event + " from state " + currentState);
        }
    }

    public void acceptEvent(Event event, Object... parameters) {
        Pair<Event, State> key = new Pair<>(event, currentState);
        if (automaton.containsKey(key)) {
            tryStateChange(event, automaton.get(key), parameters);
        } else {
            throw new IllegalStateException(
                    "Event " + event + " is not allowed in state " + currentState);
        }
    }

    public Automaton(final Event[] theEvents, final State[] theStates) {
        if (Objects.isNull(theEvents) || theEvents.length == 0) {
            throw new IllegalArgumentException(
                    "The set of Events cannot be empty");
        }
        if (Objects.isNull(theStates) || theStates.length == 0) {
            throw new IllegalArgumentException(
                    "The set of States cannot be empty");
        }

        this.events = theEvents;
        this.states = theStates;
        automaton = new HashMap<>();
        initialStateData = new HashMap<>();
        support = new PropertyChangeSupport(this);
        registers = new HashMap<>();
    }

    public void createRegister(final String name) {
        if (registers.containsKey(name)) {
            throw new IllegalArgumentException(
                    "Register " + name + " already exists.");
        } else if (Objects.isNull(name) || !name.matches(
                "[a-zA-Z]([a-zA-Z0-9_])*")) {
            throw new IllegalArgumentException(
                    "< " + name + " > is not a correct name for a register. Name must fit [a-zA-Z]([a-zA-Z0-9_])*");
        }
        registers.put(name, null);
    }

    public <T> T getRegisterValue(final String name, final Class<T> type) {
        if (!registers.containsKey(name)) {
            throw new IllegalArgumentException(
                    "Register " + name + " does not exist.");
        }
        return type.cast(registers.get(name));
    }

    public void setRegisterValue(final String name, final Object value) {
        if (!registers.containsKey(name)) {
            throw new IllegalArgumentException(
                    "Register " + name + " does not exist.");
        }
        registers.put(name, value);
    }

    public void registerInitialization(final State initialState) {
        registerInitialization(initialState, NullAction.getInstance());
    }

    public void registerInitialization(final State initialState,
            final Action initialAction) {
        final List<State> initialStates = new ArrayList<>(1);
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

    public void registerInitialization(
            final List<State> initialStates,
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
                final State initialState = initialStates.get(i);
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

    public void registerTransition(final State state1, final Event event,
            final State state2) {
        registerTransition(state1, event, state2, NullAction.getInstance());
    }

    public void registerTransition(final State state1, final Event event,
            final State state2, final Action action) {
        registerTransition(state1, event, state2, action, TruePrecondition.
                getInstance());
    }

    public void registerTransition(final State state1, final Event event,
            final State state2, final Action action,
            final Precondition precondition) {
        final Map<Precondition, Pair<State, Action>> transitions;
        final Pair<Event, State> key = new Pair<>(event, state1);
        if (automaton.containsKey(key)) {
            transitions = automaton.get(key);
        } else {
            transitions = new HashMap<>();
            automaton.put(key, transitions);
        }
        transitions.put(precondition, new Pair<>(state2, action));
    }

    public void initialize(Object... parameters) {
        tryStateChange(null, initialStateData, parameters);
    }

    private void goToState(final State state) {
        State oldState = currentState;
        currentState = state;
        final Set<Event> oldEnableEvents = new HashSet<>(events.length);
        for (Pair<Event, State> pair : automaton.keySet()) {
            if (pair.getSecond().equals(oldState)) {
                oldEnableEvents.add(pair.getFirst());
            }
        }

        final Set<Event> enableEvents = new HashSet<>(events.length);
        automaton.keySet().stream().
                filter((pair) -> (pair.getSecond().equals(currentState))).
                forEachOrdered((pair) -> {
                    enableEvents.add(pair.getFirst());
                });
        support.firePropertyChange(STATE_PROPERTY, oldState, currentState);
        for (Event anyEvent : events) {
            support.firePropertyChange(anyEvent.toString() + ENABLED_SUFFIX,
                    !enableEvents.contains(anyEvent), enableEvents.contains(
                    anyEvent));
        }
    }
    private static final Logger LOG = Logger.
            getLogger(Automaton.class.getName());

    public boolean isEventEnabled(Event event) {
        if (Objects.isNull(currentState)) {
            return false;
        }
        for (Pair<Event, State> pair : automaton.keySet()) {
            if (currentState.equals(pair.getSecond()) && pair.getFirst().equals(
                    event)) {
                return true;
            }
        }
        return false;
    }
    public static final String STATE_PROPERTY = "state";
    public static final String ENABLED_SUFFIX = "_enabled";

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        support.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        support.removePropertyChangeListener(propertyName, listener);
    }

    private static class NullAction implements Action {

        private static final NullAction singleton;

        static {
            singleton = new NullAction();
        }

        public static NullAction getInstance() {
            return singleton;
        }

        @Override
        public void execute(Object... parameters) {
            //Do nothing.
        }
    }

    private static class TruePrecondition implements Precondition {

        private static final TruePrecondition singleton;

        static {
            singleton = new TruePrecondition();
        }

        public static TruePrecondition getInstance() {
            return singleton;
        }

        @Override
        public boolean isVerified(Object... parameters) {
            return true;
        }
    }

    private class Pair<First, Second> {

        private final First first;
        private final Second second;

        public Pair(First first, Second second) {
            this.first = first;
            this.second = second;
        }

        public First getFirst() {
            return first;
        }

        public Second getSecond() {
            return second;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 53 * hash + Objects.hashCode(this.first);
            hash = 53 * hash + Objects.hashCode(this.second);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
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
        for (State state : states) {
            for (Map.Entry<Pair<Event, State>, Map<Precondition, Pair<State, Action>>> entry : automaton.
                    entrySet()) {
                if (entry.getKey().second.equals(state)) {
                    toString += state + "=>" + entry.getKey().first + "=>" + entry.
                            getValue().values() + "\n";
                }
            }
        }
        return toString;
    }

}
