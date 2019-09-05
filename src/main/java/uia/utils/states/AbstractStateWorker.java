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

/**
 * Abstract worker using state machine.
 *
 * @author Kyle K. Lin
 *
 * @param <C> Controller.
 * @param <X> Event context.
 */
public abstract class AbstractStateWorker<C, X> {

    protected StateMachine<C, X> stateMachine;

    /**
     * Constructor.
     * @param workerName Worker name.
     */
    protected AbstractStateWorker(String workerName) {
        this(workerName, null);
    }

    /**
     * Constructor.
     * @param workerName Worker name.
     * @param stateName State name.
     */
    protected AbstractStateWorker(String workerName, String stateName) {
        this.stateMachine = new StateMachine<>(workerName);
        initial();
        changeState(stateName);
    }

    /**
     * Get worker name.
     * @return Worker name.
     */
    public String getName() {
        return this.stateMachine.getName();
    }

    /**
     * Get current state.
     * @return Current state.
     */
    public String getCurrState() {
        return this.stateMachine.getCurrState().getName();
    }

    /**
     * change to new state.
     * @param stateName New state name.
     * @return New state.
     * @throws StateException Raise if failed.
     */
    public boolean changeState(String stateName) throws StateException {
        return this.stateMachine.changeState(stateName);
    }

    /**
     * Add state-in listener.
     * @param stateName State name.
     * @param listener The listener.
     */
    public void addStateInListener(String stateName, StateListener<X> listener) {
        this.stateMachine.getState(stateName).addInListener(listener);
    }

    /**
     * Add state-out listener.
     * @param stateName State name.
     * @param listener The listener.
     */
    public void addStateOutListener(String stateName, StateListener<X> listener) {
        this.stateMachine.getState(stateName).addOutListener(listener);
    }

    /**
     * Add event listener.
     * @param eventName Event name.
     * @param listener The listener.
     */
    public void addEventListener(String eventName, StateListener<X> listener) {
        this.stateMachine.addEventListener(eventName, listener);
    }

    /**
     * Add state changed listener.
     * @param fromStateName State from.
     * @param toStateName State to.
     * @param listener The listener.
     */
    public void addStateChangedListener(String fromStateName, String toStateName, StateListener<X> listener) {
        this.stateMachine.addChangeListener(fromStateName, toStateName, listener);
    }

    /**
     * Print internal information. Debug only.
     */
    public static void printlnHeader() {
        System.out.println(String.format("%-25s, %-15s, %-20s, %-20s, %s",
                "Class",
                "Name",
                "Event",
                "State",
                "Others"));
        System.out.println("=================================================================================================");
    }

    /**
     * Print internal information. Debug only.
     * @param eventName Event name.
     * @param extra Extra information.
     */
    public void println(String eventName, String extra) {
        System.out.println(String.format("%-25s, %-15s, %-20s, %-20s, %s",
                getClass().getSimpleName(),
                getName(),
                eventName,
                this.stateMachine.getCurrState(),
                extra));
    }

    /**
     * initial.
     */
    protected abstract void initial();
}
