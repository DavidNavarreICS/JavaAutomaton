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

import fr.irit.ics.jautomaton.examples.fourbuttonsalternate.TestConfiguration.Event;
import java.util.Arrays;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author David Navarre
 */
@RunWith(Parameterized.class)
public class S1CornerCasesTest extends AbstractCornerCasesTest {

    @Parameterized.Parameters(name = "{index}: trigger event {0} in state S1")
    public static Object[] data() {
        return getData(Arrays.asList(Event.CB1, Event.CB2));
    }

    @Override
    public void putInCorrectState() {
        //DO NOTHING.
    }

}
