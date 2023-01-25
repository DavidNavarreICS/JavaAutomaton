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
 * <li>Event conditions</li>
 * <li>Actions on transitions</li>
 * </ul>
 * It embeds property change listening means as a notification mechanism too.
 * <h1>Build a simple Automaton</h1>
 * As an example, Fig.1 presents a simple automaton used to illustrate the
 * basics of the building and use of automata.<br>To build an automaton relies
 * on three methods:
 * <ul>
 * <li> The constructor of the class
 * <li> The add transitions methods
 * <br>{@link Automaton#registerTransition(java.lang.Enum, java.lang.Enum, java.lang.Enum)},
 * <br>{@link Automaton#registerTransition(java.lang.Enum, java.lang.Enum, java.lang.Enum,
 * fr.irit.ics.jautomaton.Action)},
 * <br>{@link Automaton#registerTransition(java.lang.Enum, java.lang.Enum, java.lang.Enum,
 * fr.irit.ics.jautomaton.Action, fr.irit.ics.jautomaton.Condition)}
 * <li> The creation of an initial state
 * <br>{@link Automaton#registerInitialization(java.lang.Enum)},
 * <br>{@link Automaton#registerInitialization(java.lang.Enum, fr.irit.ics.jautomaton.Action)},
 * <br>{@link Automaton#registerInitialization(java.util.List, java.util.List, java.util.List)}
 * </ul>
 * <center><img src="./doc-files/Automaton-1.png" alt="Example 1 of Automaton"><br>
 * <em>Fig.1 - Example of a simple automaton.</em><br></center>
 * An automaton may be used as an attribute of any class.<br>
 * <pre>
 * public class MyApp {
 *      private enum State {S1, S2}
 *      private enum Event {Ev1, Ev2}
 *      private Automaton&lt;Event, State&gt; automaton;
 *      ...
 *      public MyApp(...){
 *          //Does something.
 *          configureAutomaton();
 *      }
 *
 *      private void configureAutomaton() {
 *          automaton = new Automaton&lt;&gt;(EnumSet.allOf(Event.class), EnumSet.allOf(State.class));
 *          automaton.registerTransition(State.S1, Event.Ev1, State.S2);
 *          automaton.registerTransition(State.S2, Event.Ev2, State.S1);
 *          automaton.registerInitialization(State.S1);
 *      }
 *
 *      ...
 * }</pre>
 * <h1>Event handling</h1>
 * The automaton is able to handle events using the method null {@link #acceptEvent(java.lang.Enum,
 * java.lang.Object...)}. For instance,
 * <pre>
 *      private void doIt(){
 *          automaton.acceptEvent(Event.Ev1);
 *      }
 * </pre>
 * <h1>Notifications</h1>
 * <br>It is possible to add property change listeners to be notified of state
 * changes and/or event enabling, using {@link Automaton#addPropertyChangeListener(java.lang.String,
 * java.beans.PropertyChangeListener)}. For state changes the property name is
 * {@link Automaton#STATE_PROPERTY} and for event enabling, the property name is
 * the result of the concatenation of the event name and a suffix
 * {@link Automaton#ENABLED_SUFFIX} (for instance: <code>Ev1_enabled</code>).
 * <h1>Actions</h1>
 * It is possible to add actions in two different situations:
 * <ul>
 * <li>For the initialization of the automaton
 * <br>{@link Automaton#registerInitialization(java.lang.Enum, fr.irit.ics.jautomaton.Action)
 * }
 * <li> When creating transitions
 * <br>{@link Automaton#registerTransition(java.lang.Enum,
 * java.lang.Enum, java.lang.Enum, fr.irit.ics.jautomaton.Action)
 * }
 * </ul>
 * Actions must implement the interface {@link fr.irit.ics.jautomaton.Action}
 *
 * <h1>Conditions</h1>
 * It is possible to add conditions in two different situations:
 * <ul>
 * <li>For the initialization of the automaton (case of multiple initial states)
 * {@link Automaton#registerInitialization(java.util.List, java.util.List, java.util.List)}
 * <li> When creating transitions null {@link Automaton#registerTransition(java.lang.Enum, java.lang.Enum,
 * java.lang.Enum, fr.irit.ics.jautomaton.Action, fr.irit.ics.jautomaton.Condition)}
 * </ul>
 * Conditions must implement the interface
 * {@link fr.irit.ics.jautomaton.Condition}
 *
 * @author David Navarre
 *
 * @param <E> the enumeration set of accepted Event values
 * @param <S> the enumeration set of accepted State values
 * @see fr.irit.ics.jautomaton.Action
 * @see fr.irit.ics.jautomaton.Condition
 */
public final class Automaton<E extends Enum, S extends Enum> {

    /**
     * REGEX that must be followed by regiters names.
     */
    private static final String REGISTER_NAME_PATTERN_STRING = "[a-zA-Z]([a-zA-Z0-9_])*";
    /**
     * REGEX that must be followed by regiters names.
     */
    private static final Pattern REGISTER_NAME_PATTERN
            = Pattern.compile(REGISTER_NAME_PATTERN_STRING);
    /**
     * Main data structure that contains any items defining the automaton.
     */
    private final Map<Pair<E, S>, Map<Condition, Pair<S, Action>>> dataStructure;
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
    private final Map<Condition, Pair<S, Action>> initialStateData;
    /**
     * Delegate that handles listeners for this automaton.
     */
    private final PropertyChangeSupport support;
    /**
     * Stores a set of register that may be used by the automaton (for actions
     * and/or conditions).
     */
    private final Map<String, Object> registers;
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
    private static final String ERROR_INITIAL_STATE_CANNOT_BE_NULL
            = "The initial state cannot be null";

    /**
     * Build the base structure of an automaton based on a set of events and a
     * set of states.
     *
     * @param theEvents must be set containing at least one event.
     * @param theStates must be set containing at least one state.
     */
    public Automaton(final Set<E> theEvents, final Set<S> theStates) {
        LOG.log(Level.FINEST,
                "Creating automaton with States={0} and Events={1}",
                new Object[]{theStates, theEvents});
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
     * @param parameters the parameters used for condition and/or action
     * computing
     * @return the error message
     */
    private String getErrorMessageNoPossibleStateChange(
            final E event,
            final Object... parameters) {
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
        return EVENT_PREFIX
                + event
                + " is not allowed in "
                + STATE_PREFIX
                + currentState;
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
     * states by checking some conditions (the conditions may be evaluated using
     * object parameters).
     *
     * @param event the event that occured (only for notification purpose)
     * @param futurState the set of possible future states
     * @param parameters the set of parameters used for evaluating the
     * conditions and/or the execution of the corresponding action.
     */
    private void tryStateChange(
            final E event,
            final Map<Condition, Pair<S, Action>> futurState,
            final Object... parameters) {
        boolean foundState = false;
        for (Map.Entry<Condition, Pair<S, Action>> entry : futurState.
                entrySet()) {
            LOG.log(Level.FINEST, "Trying condition: {0}", entry.getKey());
            if (entry.getKey().isVerified(parameters)) {
                LOG.log(Level.FINEST,
                        "Condition: {0} is verified, going to state {1}",
                        new Object[]{entry.getKey(), entry.getValue().getFirst()});
                goToState(entry.getValue().getFirst());
                entry.getValue().getSecond().execute(parameters);
                foundState = true;
                break;
            }
        }
        if (!foundState) {
            LOG.log(
                    Level.SEVERE,
                    getErrorMessageNoPossibleStateChange(event, parameters));
            throw new IllegalArgumentException(
                    getErrorMessageNoPossibleStateChange(event, parameters));
        }
    }

    /**
     * This methods is used after the corresponding event occured. The object
     * parameters provided may be used for the evaluation of the condition
     * and/or the execution of the action.
     *
     * @param event the event that has been triggered
     * @param parameters the parameters of both the condition and the action
     */
    public void acceptEvent(final E event, final Object... parameters) {
        LOG.log(Level.FINEST,
                "Accepting Event {0} with parameters {1}",
                new Object[]{event, parameters});
        if (Objects.isNull(event)) {
            throw new IllegalArgumentException(
                    "Event used to fire a transition cannot be null");
        }
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
        LOG.log(Level.FINEST,
                "Creating register {0}",
                new Object[]{name});
        if (registers.containsKey(name)) {
            LOG.log(Level.SEVERE, getErrorMessageRegisterAlreadyExists(name));
            throw new IllegalArgumentException(
                    getErrorMessageRegisterAlreadyExists(name));
        } else if (Objects.isNull(name)
                || !REGISTER_NAME_PATTERN.matcher(name).matches()) {
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
        LOG.log(Level.FINEST,
                "Get register value of register {0} of Class {1}",
                new Object[]{name, type});
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
        LOG.log(Level.FINEST,
                "Setting value of the register {0} = {1}",
                new Object[]{name, value});
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
        LOG.log(Level.FINEST,
                "Register Initialization with: {0}",
                new Object[]{initialState});
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
        LOG.log(Level.FINEST,
                "Register Initialization with: {0}, {1}",
                new Object[]{initialState, initialAction});
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
     * possible according to some conditions. The three sets must be ordered.
     *
     * @param initialStates the not empty set of possible initial states
     * @param initialActions the initial set of actions action (if null it is
     * set to a void action
     * @param initialConditions the initial set of conditions (if null it is set
     * to an always condition
     */
    public void registerInitialization(
            final List<S> initialStates,
            final List<Action> initialActions,
            final List<Condition> initialConditions) {
        LOG.log(Level.FINEST,
                "Register Initialization with: {0}, {1}, {2}",
                new Object[]{initialStates, initialConditions, initialActions});
        if (Objects.isNull(initialStates) || initialStates.isEmpty()) {
            LOG.log(Level.SEVERE,
                    ERROR_SET_OF_INITIAL_STATES_CANNOT_BE_EMPTY);
            throw new IllegalArgumentException(
                    ERROR_SET_OF_INITIAL_STATES_CANNOT_BE_EMPTY);
        }
        if (initialStates.size() == 1) {
            LOG.log(Level.FINEST,
                    "Register single state initilization: {0}",
                    initialStates);
            registerSingleStateInitialization(initialActions, initialStates);
        } else {
            LOG.log(Level.FINEST,
                    "Register multiple state initilization: {0}",
                    initialStates);
            registerMultipleStatesInitilization(initialStates, initialActions, initialConditions);
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
    private void registerSingleStateInitialization(
            final List<Action> initialActions,
            final List<S> initialStates) {
        LOG.log(Level.FINEST,
                "Register single state initialisation with: {0}, {1}",
                new Object[]{initialActions, initialStates});
        final Action action;
        if (Objects.isNull(initialActions) || initialActions.isEmpty()) {
            action = NullAction.getInstance();
        } else {
            action = initialActions.get(0);
        }
        initialStateData.put(TrueCondition.getInstance(),
                new Pair<>(initialStates.get(0), action));
    }

    /**
     * Registers the initialization process in the case of multiple initial
     * states.
     *
     * @param initialStates the non empty set of initial states
     * @param initialActions a set of actions
     * @param initialConditions a set of conditions
     */
    private void registerMultipleStatesInitilization(
            final List<S> initialStates,
            final List<Action> initialActions,
            final List<Condition> initialConditions) {
        LOG.log(Level.FINEST,
                "Register multiple state initialisation with: {0}, {1}, {2}",
                new Object[]{initialStates, initialActions, initialStates});
        for (int i = 0; i < initialStates.size(); i++) {
            final S initialState = initialStates.get(i);
            final Action action;
            final Condition condition;
            if (Objects.isNull(initialActions)) {
                action = NullAction.getInstance();
            } else if (initialActions.size() < i + 1) {
                action = NullAction.getInstance();
            } else {
                action = initialActions.get(i);
            }
            if (Objects.isNull(initialConditions)
                    || initialConditions.size() < i + 1) {
                condition = TrueCondition.getInstance();
            } else {
                condition = initialConditions.get(i);
            }
            initialStateData.put(condition, new Pair<>(initialState,
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
        LOG.log(Level.FINEST,
                "Registering new transition from State {0} to State {2} on Event {1}",
                new Object[]{state1, event, state2});
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
        LOG.log(Level.FINEST,
                "Registering new transition from State {0} to State {2} "
                + "on Event {1} with execution of Action {3}",
                new Object[]{state1, event, state2, action});
        registerTransition(state1, event, state2, action, TrueCondition.
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
     * @param condition the conditions that must be verified to allow the
     * transition
     */
    public void registerTransition(final S state1, final E event,
            final S state2, final Action action,
            final Condition condition) {
        LOG.log(Level.FINEST,
                "Registering new transition from State {0} to State {2} "
                + "on Event {1} with execution of Action {3}, "
                + "under condition {4}",
                new Object[]{state1, event, state2, action, condition});
        final Map<Condition, Pair<S, Action>> transitions;
        final Pair<E, S> key = new Pair<>(event, state1);
        if (dataStructure.containsKey(key)) {
            transitions = dataStructure.get(key);
        } else {
            transitions = new HashMap<>();
            dataStructure.put(key, transitions);
        }
        transitions.put(condition, new Pair<>(state2, action));
    }

    /**
     * Forces the initialization of the automaton by providing some parameters
     * that may be used to evaluate conditions and/or to execute actions.
     *
     * @param parameters the possible object values used for condition and/or
     * actions.
     */
    public void initialize(final Object... parameters) {
        LOG.log(Level.FINEST,
                "Initializing with parameters: {0}",
                new Object[]{parameters});
        tryStateChange(null, initialStateData, parameters);
    }

    /**
     * Really performs the state change. When performed, the event enabling is
     * computed.
     *
     * @param state the future state
     */
    private void goToState(final S state) {
        LOG.log(Level.FINEST,
                "Going to State {0}",
                new Object[]{state});
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
        LOG.log(Level.FINEST,
                "Providing enabling of Event {0} in State {1}",
                new Object[]{event, currentState});
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
     * @see
     * java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(
            final PropertyChangeListener listener) {
        LOG.log(Level.FINEST,
                "Registering Listener {0}",
                new Object[]{listener});
        checkListener(listener);
        support.addPropertyChangeListener(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener the new listener to be removed
     * @see
     * java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
     * *
     */
    public void removePropertyChangeListener(
            final PropertyChangeListener listener) {
        LOG.log(Level.FINEST,
                "Removing Listener {0}",
                new Object[]{listener});
        checkListener(listener);
        support.removePropertyChangeListener(listener);
    }

    /**
     * Registers a listener of a particular state change within the automaton.
     *
     * @param propertyName the name of the property to listen to
     * @param listener the new listener to be added
     * @see
     * java.beans.PropertyChangeSupport#addPropertyChangeListener(java.lang.String,
     * java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(
            final String propertyName,
            final PropertyChangeListener listener) {
        LOG.log(Level.FINEST,
                "Registering Listener {0} for Property {1}",
                new Object[]{listener, propertyName});
        checkPropertyName(propertyName);
        checkListener(listener);
        support.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Removes a listener for a particular property.
     *
     * @param propertyName the name of the property that was listened to
     * @param listener the new listener to be removed
     * @see
     * java.beans.PropertyChangeSupport#removePropertyChangeListener(java.lang.String,
     * java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(
            final String propertyName,
            final PropertyChangeListener listener) {
        LOG.log(Level.FINEST,
                "Removing Listener {0} from Property {1}",
                new Object[]{listener, propertyName});
        checkPropertyName(propertyName);
        checkListener(listener);
        support.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Provides the set of listeners of the automaton properties.
     *
     * @return the set of listeners
     * @see java.beans.PropertyChangeSupport#getPropertyChangeListeners()
     */
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return support.getPropertyChangeListeners();
    }

    /**
     * Provides the set of listeners of an automaton particular property.
     *
     * @param propertyName the name of the listened property
     * @return the set of listeners
     * @see
     * java.beans.PropertyChangeSupport#getPropertyChangeListeners(java.lang.String)
     */
    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        checkPropertyName(propertyName);
        return support.getPropertyChangeListeners(propertyName);
    }

    @Override
    public String toString() {
        String toString = "Initial State: " + initialStateData.values() + "\n";
        for (S state : states) {
            toString = dataStructure.
                    entrySet().stream()
                    .filter((Map.Entry<Pair<E, S>, Map<Condition, Pair<S, Action>>> entry)
                            -> (entry.getKey().getSecond().equals(state)))
                    .map((Map.Entry<Pair<E, S>, Map<Condition, Pair<S, Action>>> entry)
                            -> state
                    + "=>" + entry.getKey().getFirst()
                    + "=>" + entry.getValue().values()
                    + "\n")
                    .reduce(toString, String::concat);
        }
        return toString;
    }

    /**
     * Provides the state in which the automaton is.
     *
     * @return the current state
     */
    public S getCurrentState() {
        return currentState;
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
     * A condition that is always true.
     */
    private static class TrueCondition implements Condition {

        /**
         * The true condition unique instance.
         */
        private static final TrueCondition SINGLETON;

        /**
         * The singleton initialization.
         */
        static {
            SINGLETON = new TrueCondition();
        }

        /**
         * Provides the unique instance of the always true condition.
         *
         * @return the condition object
         */
        public static TrueCondition getInstance() {
            return SINGLETON;
        }

        @Override
        public boolean isVerified(final Object... parameters) {
            return true;
        }
    }

    /**
     * Verify if the property listened refers to a state change of to an event
     * enabling change.
     *
     * @param propertyName the property to listen to
     */
    private void checkPropertyName(String propertyName) {
        boolean isCorrect = STATE_PROPERTY.equals(propertyName);
        for (E event : events) {
            final String expected = event.toString() + ENABLED_SUFFIX;
            if (expected.equals(propertyName)) {
                isCorrect = true;
            }
        }
        if (!isCorrect) {
            throw new IllegalArgumentException(
                    "The name of the property to listen to should be 'state' or <anyEvent>_enabled");
        }
    }

    /**
     * Verifies that the provided listener is not null.
     *
     * @param listener the concerned listener
     */
    private static void checkListener(PropertyChangeListener listener) {
        if (Objects.isNull(listener)) {
            throw new IllegalArgumentException("The added listener cannot be null");
        }
    }
}
