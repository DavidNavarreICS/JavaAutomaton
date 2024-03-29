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

import java.util.List;

/**
 * This interface must be implemented by any action used within an automaton. It allows any number of action parameters.
 *
 * @author David Navarre &lt;David.Navarre@irit.fr&gt;
 */
public interface Action {

    /**
     * Execute the action using the given parameters.
     *
     * @param parameters the parameters.
     */
    void execute(List<Object> parameters);
}
