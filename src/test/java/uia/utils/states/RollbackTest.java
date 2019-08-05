/*******************************************************************************
 * Copyright 2019 UIA
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

/**
 *
 * @author Kyle K. Lin
 *
 */
public class RollbackTest {

    private static String ST_IDLE = "IDLE";

    private static String ST_PRE_PROCESS = "PRE_PROCESS";

    private static String ST_PROCESSING = "PROCESSING";

    private static String ST_POST_PROCESS = "POST_PROCESS";

    private static String ST_NEXT = "NEXT";

    @Test
    public void testSimple() {
        StateMachine<Object, Object> machine = new StateMachine<Object, Object>("FOUP");
        // IDLE
        machine.registerState(ST_IDLE, 1)
                .addEvent("moveIn", RollbackTest.this::moveIn);

        // PRE_PROCESS
        machine.registerState(ST_PRE_PROCESS, 2)
                .addEvent("trackIn", RollbackTest.this::trackIn);

        // PROCESSING
        machine.registerState(ST_PROCESSING, 3)
                .addEvent("trackIn", RollbackTest.this::trackIn)
                .addEvent("trackOut", RollbackTest.this::trackOut);

        // POST_PROCESS
        machine.registerState(ST_POST_PROCESS, 4)
                .addEvent("moveOut", RollbackTest.this::moveOut);

        // NEXT
        machine.registerState(ST_NEXT, 5)
                .addEvent("ready", RollbackTest.this::ready);

        Assert.assertEquals("FOUP", machine.getName());
        Assert.assertNotNull(machine.getState(ST_IDLE));
        Assert.assertNotNull(machine.getState(ST_PRE_PROCESS));
        Assert.assertNotNull(machine.getState(ST_PROCESSING));
        Assert.assertNotNull(machine.getState(ST_POST_PROCESS));
        Assert.assertNotNull(machine.getState(ST_NEXT));

        Assert.assertFalse(machine.rollback("NOT_EXIST", ST_IDLE));

        machine.changeState(ST_IDLE);
        machine.println();
        Assert.assertEquals("NULL", machine.getPrevState().getName());
        Assert.assertEquals(ST_IDLE, machine.getCurrState().getName());

        machine.run(null, "moveIn", null);
        machine.println();
        Assert.assertEquals(ST_IDLE, machine.getPrevState().getName());
        Assert.assertEquals(ST_PRE_PROCESS, machine.getCurrState().getName());

        machine.run(null, "trackIn", null);
        machine.println();
        Assert.assertEquals(ST_PRE_PROCESS, machine.getPrevState().getName());
        Assert.assertEquals(ST_PROCESSING, machine.getCurrState().getName());
        Assert.assertEquals(3, machine.getCurrState().getSeq());

        machine.rollback(ST_PRE_PROCESS, ST_IDLE);
        machine.println();
        Assert.assertEquals(ST_IDLE, machine.getPrevState().getName());
        Assert.assertEquals(ST_PRE_PROCESS, machine.getCurrState().getName());
        Assert.assertEquals(2, machine.getCurrState().getSeq());

        Assert.assertFalse(machine.rollback(ST_IDLE, "NOT_EXIST"));
        Assert.assertEquals(ST_PRE_PROCESS, machine.getCurrState().getName());
        Assert.assertTrue(machine.rollback(ST_IDLE, null));
        machine.println();
        Assert.assertEquals(ST_IDLE, machine.getCurrState().getName());
        Assert.assertEquals(1, machine.getCurrState().getSeq());
    }

    private String moveIn(Object controller, Object ctx) {
        return ST_PRE_PROCESS;
    }

    private String moveOut(Object controller, Object ctx) {
        return ST_NEXT;
    }

    private String trackIn(Object controller, Object ctx) {
        return ST_PROCESSING;
    }

    private String trackOut(Object controller, Object ctx) {
        return ST_POST_PROCESS;
    }

    private String ready(Object controller, Object ctx) {
        return ST_IDLE;
    }
}