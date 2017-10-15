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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * State machine.
 *
 * @author Kyle K. Lin
 *
 * @param <C> Controller.
 */
public class StateMachine<C, A> {

    public enum RunResultType {

        EVENT_NOT_SUPPORT,

        STATE_KEEP,

        STATE_CHANGED
    }

    private final String name;

    private final TreeMap<String, State<C, A>> states;

    private final TreeMap<String, List<StateListener<A>>> stateChangedListeners;

    private final TreeMap<String, List<StateListener<A>>> eventListeners;

    private State<C, A> prevState;

    private State<C, A> currState;

    /**
     * Constructor.
     * @param name State machine name.
     */
    public StateMachine(String name) {
        this.name = name;
        this.states = new TreeMap<String, State<C, A>>();
        this.stateChangedListeners = new TreeMap<String, List<StateListener<A>>>();
        this.eventListeners = new TreeMap<String, List<StateListener<A>>>();
        this.prevState = new State<C, A>("NULL");
        this.currState = this.prevState;
    }

    /**
     * Get name.
     * @return Name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get state.
     * @param stateName State name.
     * @return State.
     */
    public State<C, A> getState(String stateName) {
        return this.states.get(stateName);
    }

    /**
     *
     * @param stateName
     * @return
     */
    public State<C, A> register(String stateName) {
        State<C, A> state = this.states.get(stateName);
        if (state == null) {
            state = new State<C, A>(stateName);
            this.states.put(stateName, state);
        }
        return state;
    }

    /**
     *
     * @return
     */
    public State<C, A> getPrevState() {
        return this.prevState;
    }

    /**
     *
     * @return
     */
    public State<C, A> getCurrState() {
        return this.currState;
    }

    /**
     * Add event listener.
     * @param eventName Event name.
     * @param listener The listener.
     */
    public void addEventListener(String eventName, StateListener<A> listener) {
        List<StateListener<A>> listeners = this.eventListeners.get(eventName);
        if (listeners == null) {
            listeners = new ArrayList<StateListener<A>>();
            this.eventListeners.put(eventName, listeners);
        }
        listeners.add(listener);
    }

    /**
     * Add state changed listener.
     * @param fromStateName State changed from.
     * @param toStateName State changed to.
     * @param listener The listener.
     */
    public void addChangeListener(String fromStateName, String toStateName, StateListener<A> listener) {
        String key = genKey(fromStateName, toStateName);
        List<StateListener<A>> listeners = this.stateChangedListeners.get(key);
        if (listeners == null) {
            listeners = new ArrayList<StateListener<A>>();
            this.stateChangedListeners.put(key, listeners);
        }
        listeners.add(listener);
    }

    /**
     * Change to a new state. Listeners will not be notified.
     * @param stateName New state name.
     * @return Changed or not.
     * @throws StateException Raise if state control failed.
     */
    public boolean changeState(String stateName) throws StateException {
        if (stateName == null) {
            return false;
        }

        State<C, A> temp = this.states.get(stateName);
        if (temp == null) {
            String message = String.format("Event:%s not found in StateMachine:%s", stateName, this.name);
            throw new StateException(
                    stateName,
                    this.currState.getName(),
                    message,
                    new IllegalArgumentException(message));
        }

        this.prevState = this.currState;
        this.currState = temp;

        return true;
    }

    /**
     * Run an event.
     * @param controller The controller name.
     * @param eventName The event name.
     * @param args Arguments.
     * @throws StateException Raise if state control failed.
     */
    public RunResultType run(C controller, String eventName, A args) throws StateException {
        if (!this.currState.containsEvent(eventName)) {
            raiseEvent(eventName, args);
            return RunResultType.EVENT_NOT_SUPPORT;
        }
        String nextState = this.currState.execute(controller, eventName, args);
        if (changeState(nextState)) {
            this.prevState.raiseOut(eventName, this.prevState.getName(), args);
            this.currState.raiseIn(eventName, this.prevState.getName(), args);
            raiseEvent(eventName, args);
            raiseStateChanged(eventName, this.prevState.getName(), this.currState.getName(), args);
            return RunResultType.STATE_CHANGED;
        }
        else {
            raiseEvent(eventName, args);
            return RunResultType.STATE_KEEP;
        }
    }

    /**
     * Print state information(for debug only.)
     */
    public void println() {
        System.out.println(String.format("CurrState:%-20s, PrevState:%s",
                this.currState,
                this.prevState));
    }

    private void raiseStateChanged(String eventName, String fromStateName, String toStateName, A args) {
        String key = genKey(fromStateName, toStateName);
        List<StateListener<A>> listeners = this.stateChangedListeners.get(key);
        if (listeners == null) {
            return;
        }
        for (StateListener<A> listener : listeners) {
            listener.run(new StateEventArgs<A>(eventName, args));
        }
    }

    private void raiseEvent(String eventName, A args) {
        List<StateListener<A>> listeners = this.eventListeners.get(eventName);
        if (listeners == null) {
            return;
        }
        for (StateListener<A> listener : listeners) {
            listener.run(new StateEventArgs<A>(eventName, args));
        }
    }

    private String genKey(String fromStateName, String toStateName) {
        return fromStateName + "--" + toStateName;
    }
}
