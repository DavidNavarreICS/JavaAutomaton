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
package fr.irit.ics.jautomaton.examples.fourbuttonsalternate;

import fr.irit.ics.jautomaton.Automaton;
import java.util.EnumSet;

/**
 *
 * @author David Navarre
 */
public class TestConfiguration {

    public enum State {
        S1, S2, S3, S4, S5, S6
    }

    public enum Event {
        CB1, CB2, CB3, CB4
    }

    private static Automaton<Event, State> automaton;

    private static void feedAutomaton() {
        automaton = new Automaton<>(EnumSet.allOf(Event.class), EnumSet.allOf(State.class));
        automaton.registerTransition(State.S1, Event.CB1, State.S2);
        automaton.registerTransition(State.S1, Event.CB2, State.S3);
        automaton.registerTransition(State.S2, Event.CB1, State.S2);
        automaton.registerTransition(State.S2, Event.CB2, State.S4);
        automaton.registerTransition(State.S3, Event.CB1, State.S4);
        automaton.registerTransition(State.S3, Event.CB2, State.S3);
        automaton.registerTransition(State.S4, Event.CB3, State.S5);
        automaton.registerTransition(State.S4, Event.CB4, State.S6);
        automaton.registerTransition(State.S5, Event.CB3, State.S5);
        automaton.registerTransition(State.S5, Event.CB4, State.S1);
        automaton.registerTransition(State.S6, Event.CB3, State.S1);
        automaton.registerTransition(State.S6, Event.CB4, State.S6);
        automaton.registerInitialization(State.S1);
    }

    public static final Automaton<Event, State> getNewAutomaton() {
        feedAutomaton();
        automaton.initialize();
        return automaton;
    }

}
