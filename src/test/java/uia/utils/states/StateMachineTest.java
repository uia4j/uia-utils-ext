/*******************************************************************************
 * Copyright 2018 UIA
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

public class StateMachineTest {

    private static String IDLE = "IDLE";

    private static String PRE_PROCESS = "PRE_PROCESS";

    private static String PROCESSING = "PROCESSING";

    private static String POST_PROCESS = "POST_PROCESS";

    private static String NEXT = "NEXT";

    private static String RUN_HOLD = "RUN_HOLD";

    private int event;

    @Test
    public void testSimple() {
        StateMachine<Object, Object> machine = new StateMachine<Object, Object>("FOUP");
        // IDLE
        machine.registerState(IDLE)
                .addEvent("validateLot", StateMachineTest.this::validateLot)
                .addEvent("moveIn", StateMachineTest.this::moveIn);

        // PRE_PROCESS
        machine.registerState(PRE_PROCESS)
                .addEvent("trackIn", StateMachineTest.this::trackIn)
                .addEvent("down", StateMachineTest.this::runHold)
                .addEvent("trackOut", StateMachineTest.this::trackIn);

        // PROCESSING
        machine.registerState(PROCESSING)
                .addEvent("trackIn", StateMachineTest.this::trackIn)
                .addEvent("down", StateMachineTest.this::runHold)
                .addEvent("trackOut", StateMachineTest.this::trackOut);

        // POST_PROCESS
        machine.registerState(POST_PROCESS)
                .addEvent("down", StateMachineTest.this::runHold)
                .addEvent("moveOut", StateMachineTest.this::moveOut);

        // NEXT
        machine.registerState(NEXT)
                .addEvent("ready", StateMachineTest.this::ready);

        Assert.assertEquals("FOUP", machine.getName());
        Assert.assertNotNull(machine.getState(IDLE));
        Assert.assertNotNull(machine.getState(PRE_PROCESS));
        Assert.assertNotNull(machine.getState(PROCESSING));
        Assert.assertNotNull(machine.getState(POST_PROCESS));
        Assert.assertNotNull(machine.getState(NEXT));
        Assert.assertNull(machine.getState(RUN_HOLD));

        machine.changeState(IDLE);
        machine.println();
        Assert.assertEquals("NULL", machine.getPrevState().getName());
        Assert.assertEquals(IDLE, machine.getCurrState().getName());

        machine.run(null, "validateLot", null);
        machine.println();
        Assert.assertEquals("NULL", machine.getPrevState().getName());
        Assert.assertEquals(IDLE, machine.getCurrState().getName());

        machine.run(null, "trackIn", null); // NO CHANGED
        machine.println();
        Assert.assertEquals("NULL", machine.getPrevState().getName());
        Assert.assertEquals(IDLE, machine.getCurrState().getName());

        machine.run(null, "moveIn", null);
        machine.println();
        Assert.assertEquals(IDLE, machine.getPrevState().getName());
        Assert.assertEquals(PRE_PROCESS, machine.getCurrState().getName());

        machine.run(null, "trackIn", null);
        machine.println();
        Assert.assertEquals(PRE_PROCESS, machine.getPrevState().getName());
        Assert.assertEquals(PROCESSING, machine.getCurrState().getName());

        machine.run(null, "trackOut", null);
        machine.println();
        Assert.assertEquals(PROCESSING, machine.getPrevState().getName());
        Assert.assertEquals(POST_PROCESS, machine.getCurrState().getName());

        machine.run(null, "moveOut", null);
        machine.println();
        Assert.assertEquals(POST_PROCESS, machine.getPrevState().getName());
        Assert.assertEquals(NEXT, machine.getCurrState().getName());

        machine.run(null, "ready", null);
        machine.println();
        Assert.assertEquals(NEXT, machine.getPrevState().getName());
        Assert.assertEquals(IDLE, machine.getCurrState().getName());
    }

    @Test
    public void testEventListener() {
        StateMachine<Object, Object> machine = new StateMachine<Object, Object>("FOUP");
        // IDLE
        machine.registerState(IDLE)
                .addEvent("validateLot", StateMachineTest.this::validateLot)
                .addEvent("moveIn", StateMachineTest.this::moveIn);

        // PRE_PROCESS
        machine.registerState(PRE_PROCESS)
                .addEvent("trackIn", StateMachineTest.this::trackIn)
                .addEvent("down", StateMachineTest.this::runHold)
                .addEvent("trackOut", StateMachineTest.this::trackIn);

        // PROCESSING
        machine.registerState(PROCESSING)
                .addEvent("trackIn", StateMachineTest.this::trackIn)
                .addEvent("down", StateMachineTest.this::runHold)
                .addEvent("trackOut", StateMachineTest.this::trackOut);

        // POST_PROCESS
        machine.registerState(POST_PROCESS)
                .addEvent("down", StateMachineTest.this::runHold)
                .addEvent("moveOut", StateMachineTest.this::moveOut);

        // NEXT
        machine.registerState(NEXT)
                .addEvent("ready", StateMachineTest.this::ready);

        // RUN_HOLD
        machine.registerState(RUN_HOLD);

        // EVENT LISTENERS
        machine.addChangeListener(IDLE, PRE_PROCESS, x -> Assert.assertEquals(2, this.event));
        machine.addEventListener("trackIn", x -> Assert.assertEquals(3, this.event));
        machine.addEventListener("moveOut", x -> Assert.assertEquals(5, this.event));

        machine.changeState(IDLE);
        Assert.assertEquals(IDLE, machine.getCurrState().getName());

        this.event = 1;
        machine.run(null, "validateLot", null);
        Assert.assertEquals(IDLE, machine.getCurrState().getName());

        this.event = 2;
        machine.run(null, "moveIn", null);
        Assert.assertEquals(PRE_PROCESS, machine.getCurrState().getName());

        this.event = 3;
        machine.run(null, "trackIn", null);
        Assert.assertEquals(PROCESSING, machine.getCurrState().getName());

        this.event = 4;
        machine.run(null, "trackOut", null);
        Assert.assertEquals(POST_PROCESS, machine.getCurrState().getName());

        this.event = 5;
        machine.run(null, "moveOut", null);
        Assert.assertEquals(NEXT, machine.getCurrState().getName());

        this.event = 6;
        machine.run(null, "ready", null);
        Assert.assertEquals(IDLE, machine.getCurrState().getName());
    }

    @Test
    public void testEx() {
        try {
            StateMachine<Object, Object> machine = new StateMachine<Object, Object>("FOUP");
            machine.changeState("unknown");
        }
        catch (StateException ex) {
            Assert.assertEquals("unknown", ex.eventName);
            Assert.assertEquals("State:unknown not found in StateMachine:FOUP", ex.getMessage());
        }
    }

    private String validateLot(Object controller, Object ctx) {
        return null;
    }

    private String moveIn(Object controller, Object ctx) {
        return PRE_PROCESS;
    }

    private String moveOut(Object controller, Object ctx) {
        return NEXT;
    }

    private String trackIn(Object controller, Object ctx) {
        return PROCESSING;
    }

    private String trackOut(Object controller, Object ctx) {
        return POST_PROCESS;
    }

    private String runHold(Object controller, Object ctx) {
        return RUN_HOLD;
    }

    private String ready(Object controller, Object ctx) {
        return IDLE;
    }
}