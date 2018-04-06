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
 * @param <X> Context.
 */
public class StateMachine<C, X> {

    public enum RunResultType {

        EVENT_NOT_SUPPORT,

        STATE_KEEP,

        STATE_CHANGED
    }

    private final String name;

    private final TreeMap<String, State<C, X>> states;

    private final TreeMap<String, List<StateListener<X>>> stateChangedListeners;

    private final TreeMap<String, List<StateListener<X>>> eventListeners;

    private State<C, X> prevState;

    private State<C, X> currState;

    /**
     * Constructor.
     * @param name State machine name.
     */
    public StateMachine(String name) {
        this.name = name;
        this.states = new TreeMap<String, State<C, X>>();
        this.stateChangedListeners = new TreeMap<String, List<StateListener<X>>>();
        this.eventListeners = new TreeMap<String, List<StateListener<X>>>();
        this.prevState = new State<C, X>("NULL");
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
     * Get specific state.
     * @param stateName State name.
     * @return State.
     */
    public State<C, X> getState(String stateName) {
        return this.states.get(stateName);
    }

    /**
     * Register a new state.
     * @param stateName State name.
     * @return Registered state.
     */
    public State<C, X> registerState(String stateName) {
    	return registerState(stateName, 0);
    }


    /**
     * Register a new state.
     * @param stateName State name.
     * @param seq Sequence No.
     * @return Registered state.
     */
    public State<C, X> registerState(String stateName, int seq) {
        State<C, X> state = this.states.get(stateName);
        if (state == null) {
            state = new State<C, X>(stateName, seq);
            this.states.put(stateName, state);
        }
        return state;
    }

    /**
     * Get previous state.
     * @return Previous state.
     */
    public State<C, X> getPrevState() {
        return this.prevState;
    }

    /**
     * Get current state.
     * @return Current state.
     */
    public State<C, X> getCurrState() {
        return this.currState;
    }

    /**
     * Add event listener.
     * @param eventName Event name.
     * @param listener The listener.
     */
    public void addEventListener(String eventName, StateListener<X> listener) {
        List<StateListener<X>> listeners = this.eventListeners.get(eventName);
        if (listeners == null) {
            listeners = new ArrayList<StateListener<X>>();
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
    public void addChangeListener(String fromStateName, String toStateName, StateListener<X> listener) {
        String key = genKey(fromStateName, toStateName);
        List<StateListener<X>> listeners = this.stateChangedListeners.get(key);
        if (listeners == null) {
            listeners = new ArrayList<StateListener<X>>();
            this.stateChangedListeners.put(key, listeners);
        }
        listeners.add(listener);
    }
    
    /**
     * Rollback to specific state.
     * @param stateName State name rollback to.
     * @param prevStateName Previous state name.
     * @return Rollback or not.
     */
    public boolean rollback(String stateName, String prevStateName) {
    	State<C, X> state2 = this.states.get(stateName);
    	if(state2 == null) {
    		return false;
    	}

    	if(prevStateName != null) {
        	State<C, X> state1 = this.states.get(prevStateName);
        	if(state1 == null) {
        		return false;
        	}
        	this.prevState = state1;
    	}
    	else {
    		this.prevState = null;
    	}

    	this.currState = state2;
    	return true;
    }

    /**
     * Change to a new state. Listeners will not be notified.
     * @param stateName New state name.
     * @return Changed or not.
     * @throws StateException Raise if state control failed.
     */
    public boolean changeState(String stateName) throws StateException {
        if (stateName == null || stateName.equals(this.currState.getName())) {
            return false;
        }

        State<C, X> temp = this.states.get(stateName);
        if (temp == null) {
            String message = String.format("State:%s not found in StateMachine:%s", stateName, this.name);
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
     * @param ctx Event context.
     * @return Result.
     * @throws StateException Raise if state control failed.
     */
    public RunResultType run(C controller, String eventName, X ctx) throws StateException {
        if (!this.currState.containsEvent(eventName)) {
            return RunResultType.EVENT_NOT_SUPPORT;
        }

        String nextState = this.currState.execute(controller, eventName, ctx);
        if (changeState(nextState)) {
            this.prevState.raiseOut(eventName, this.prevState.getName(), ctx);
            this.currState.raiseIn(eventName, this.prevState.getName(), ctx);
            raiseEvent(eventName, ctx);
            raiseStateChanged(eventName, this.prevState.getName(), this.currState.getName(), ctx);
            return RunResultType.STATE_CHANGED;
        }
        else {
            raiseEvent(eventName, ctx);
            return RunResultType.STATE_KEEP;
        }
    }

    /**
     * Print internal information. Debug only.
     */
    public void println() {
        System.out.println(String.format("CurrState:%-25s, PrevState:%s",
                this.currState,
                this.prevState));
    }

    private void raiseStateChanged(String eventName, String fromStateName, String toStateName, X ctx) {
        String key = genKey(fromStateName, toStateName);
        List<StateListener<X>> listeners = this.stateChangedListeners.get(key);
        if (listeners == null) {
            return;
        }
        for (StateListener<X> listener : listeners) {
            listener.run(new StateEventContext<X>(eventName, ctx)); 
        } 
    }

    private void raiseEvent(String eventName, X ctx) {
        List<StateListener<X>> listeners = this.eventListeners.get(eventName);
        if (listeners == null) {
            return;
        }
        for (StateListener<X> listener : listeners) {
            listener.run(new StateEventContext<X>(eventName, ctx));
        }
    }

    private String genKey(String fromStateName, String toStateName) {
        return fromStateName + "--" + toStateName;
    }
}
