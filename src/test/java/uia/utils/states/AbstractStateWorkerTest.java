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

import uia.utils.states.StateMachine.RunResultType;

public class AbstractStateWorkerTest extends AbstractStateWorker<AbstractStateWorkerTest, Object> {

    private static String IDLE = "IDLE";

    private static String PRE_PROCESS = "PRE_PROCESS";

    private static String PROCESSING = "PROCESSING";

    private static String POST_PROCESS = "POST_PROCESS";

    private static String NEXT = "NEXT";

    private static String RUN_HOLD = "RUN_HOLD";

    private int seqNo;

    public AbstractStateWorkerTest() {
        super("TEST");
    }

    @Test
    public void testNormal() {
        printlnHeader();

        addEventListener("moveIn", x -> {
            Assert.assertEquals(4, this.seqNo);
        });
        addStateInListener(PRE_PROCESS, x -> {
            Assert.assertEquals(4, this.seqNo);
        });
        addStateOutListener(PRE_PROCESS, x -> {
            Assert.assertEquals(5, this.seqNo);
        });
        addStateChangedListener(PROCESSING, POST_PROCESS, x -> {
            Assert.assertEquals(6, this.seqNo);
        });

        RunResultType rt;

        changeState(IDLE);
        Assert.assertEquals(IDLE, getCurrState());

        this.seqNo++;
        rt = run("???", null);
        Assert.assertEquals(RunResultType.EVENT_NOT_SUPPORT, rt);
        println("???", null);

        this.seqNo++;
        rt = run("validateLot", null);
        Assert.assertEquals(RunResultType.STATE_KEEP, rt);
        Assert.assertEquals(IDLE, getCurrState());
        println("validateLot", null);

        this.seqNo++;
        rt = run("trackIn", null);
        Assert.assertEquals(RunResultType.EVENT_NOT_SUPPORT, rt);
        Assert.assertEquals(IDLE, getCurrState());
        println("trackIn", null);

        this.seqNo++;
        rt = run("moveIn", null);
        Assert.assertEquals(RunResultType.STATE_CHANGED, rt);
        Assert.assertEquals(PRE_PROCESS, getCurrState());
        println("moveIn", null);

        this.seqNo++;
        rt = run("trackIn", null);
        Assert.assertEquals(RunResultType.STATE_CHANGED, rt);
        Assert.assertEquals(PROCESSING, getCurrState());
        println("trackIn", null);

        this.seqNo++;
        rt = run("trackOut", null);
        Assert.assertEquals(RunResultType.STATE_CHANGED, rt);
        Assert.assertEquals(POST_PROCESS, getCurrState());
        println("trackOut", null);

        this.seqNo++;
        rt = run("moveOut", null);
        Assert.assertEquals(RunResultType.STATE_CHANGED, rt);
        Assert.assertEquals(NEXT, getCurrState());
        println("moveOut", null);

        this.seqNo++;
        rt = run("ready", null);
        Assert.assertEquals(RunResultType.STATE_CHANGED, rt);
        Assert.assertEquals(IDLE, getCurrState());
        println("ready", null);
    }

    @Override
    protected void initial() {
        // IDLE
        this.stateMachine.registerState(IDLE)
                .addEvent("validateLot", AbstractStateWorkerTest.this::validateLot)
                .addEvent("moveIn", AbstractStateWorkerTest.this::moveIn);

        // PRE_PROCESS
        this.stateMachine.registerState(PRE_PROCESS)
                .addEvent("trackIn", AbstractStateWorkerTest.this::trackIn)
                .addEvent("down", AbstractStateWorkerTest.this::runHold)
                .addEvent("trackOut", AbstractStateWorkerTest.this::trackIn);

        // PROCESSING
        this.stateMachine.registerState(PROCESSING)
                .addEvent("trackIn", AbstractStateWorkerTest.this::trackIn)
                .addEvent("down", AbstractStateWorkerTest.this::runHold)
                .addEvent("trackOut", AbstractStateWorkerTest.this::trackOut);

        // POST_PROCESS
        this.stateMachine.registerState(POST_PROCESS)
                .addEvent("down", AbstractStateWorkerTest.this::runHold)
                .addEvent("moveOut", AbstractStateWorkerTest.this::moveOut);

        // NEXT
        this.stateMachine.registerState(NEXT)
                .addEvent("ready", AbstractStateWorkerTest.this::ready);

        // RUN_HOLD
        this.stateMachine.registerState(RUN_HOLD);
    }

    private RunResultType run(String eventName, Object ctx) {
        return this.stateMachine.run(this, eventName, ctx);
    }

    private String validateLot(AbstractStateWorker<AbstractStateWorkerTest, Object> controller, Object ctx) {
        return null;
    }

    private String moveIn(AbstractStateWorker<AbstractStateWorkerTest, Object> controller, Object ctx) {
        return PRE_PROCESS;
    }

    private String moveOut(AbstractStateWorker<AbstractStateWorkerTest, Object> controller, Object ctx) {
        return NEXT;
    }

    private String trackIn(AbstractStateWorker<AbstractStateWorkerTest, Object> controller, Object ctx) {
        return PROCESSING;
    }

    private String trackOut(AbstractStateWorker<AbstractStateWorkerTest, Object> controller, Object ctx) {
        return POST_PROCESS;
    }

    private String runHold(AbstractStateWorker<AbstractStateWorkerTest, Object> controller, Object ctx) {
        return RUN_HOLD;
    }

    private String ready(AbstractStateWorker<AbstractStateWorkerTest, Object> controller, Object ctx) {
        return IDLE;
    }

}
