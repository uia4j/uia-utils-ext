/*******************************************************************************
 * Copyright 2017 UIA
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package uia.utils.states;

import org.junit.Assert;
import org.junit.Test;

public class StateTest {

    @Test
    public void testNormal() {
        State<Object, Object> state = new State<Object, Object>("RUN");
        state.addEvent("E1", (c, x) -> {
            return "E2";
        });
        state.addEvent("E2", (c, x) -> {
            return "E3";
        });
        state.addEvent("E3", (c, x) -> {
            return null;
        });

        System.out.println(state.toStringEx());

        Assert.assertEquals("RUN", state.getName());
        Assert.assertTrue(state.containsEvent("E1"));
        Assert.assertTrue(state.containsEvent("E2"));
        Assert.assertTrue(state.containsEvent("E3"));
        Assert.assertFalse(state.containsEvent("E4"));
    }

    @Test
    public void testEx() {
        State<Object, Object> state = new State<Object, Object>("RUN");
        try {
            state.addEvent(null, (c, x) -> {
                return "E2";
            });
        }
        catch (Exception ex) {
            Assert.assertEquals(IllegalArgumentException.class, ex.getClass());
        }
        try {
            state.addEvent("E2", null);
        }
        catch (Exception ex) {
            Assert.assertEquals(IllegalArgumentException.class, ex.getClass());
        }
    }
}
