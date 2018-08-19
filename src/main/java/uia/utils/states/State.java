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

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * State.
 *
 * @author Kyle K. Lin
 *
 * @param <C> Controller.
 * @param <X> Event context.
 */
public class State<C, X> {
	
	private final int seq;

    private final String name;

    private final TreeMap<String, EventExecutor<C, X>> executors;

    private final ArrayList<StateListener<X>> inListeners;

    private final ArrayList<StateListener<X>> outListeners;

    /**
     * Constructor.
     *
     * @param name State name.
     */
    public State(String name) {
    	this(name, 0);
    }

    /**
     * Constructor.
     *
     * @param name State name.
     * @param seq Sequence No.
     */
    public State(String name, int seq) {
        this.name = name;
        this.seq = seq;
        this.executors = new TreeMap<String, EventExecutor<C, X>>();
        this.inListeners = new ArrayList<StateListener<X>>();
        this.outListeners = new ArrayList<StateListener<X>>();
    }

    /**
     * Check if the event will be handled or not.
     * @param eventName Event name.
     * @return Handled or not.
     */
    public boolean containsEvent(String eventName) {
        return this.executors.containsKey(eventName);
    }

    /**
     * Add state-in listener.
     * @param listener State listener.
     */
    public void addInListener(StateListener<X> listener) {
        this.inListeners.add(listener);
    }

    /**
     * Add state-out listener.
     * @param listener State listener.
     */
    public void addOutListener(StateListener<X> listener) {
        this.outListeners.add(listener);
    }

    /**
     * Get state name.
     * @return State name.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Get sequence No.
     * @return Sequence No,
     */
    public int getSeq() {
    	return this.seq;
    }

    /**
     * Add an event executor.
     * @param eventName Event name.
     * @param executor Event executor.
     * @return State.
     */
    public State<C, X> addEvent(String eventName, EventExecutor<C, X> executor) {
        if (eventName == null || executor == null) {
            throw new IllegalArgumentException("EventName or EventExecutor is null");
        }
        this.executors.put(eventName, executor);
        return this;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Another toString. Debug only.
     * @return Message.
     */
    public String toStringEx() {
        return this.name + ", events:{" + String.join(",", this.executors.keySet().toArray(new String[0])) + "}";
    }

    String execute(C controller, String eventName, X ctx) {
        EventExecutor<C, X> executor = this.executors.get(eventName);
        return executor.run(controller, ctx);
    }

    void raiseIn(String eventName, String prevState, X ctx) {
        for (StateListener<X> l : this.inListeners) {
            try {
                l.run(new StateEventContext<X>(eventName, ctx));
            }
            catch (Exception ex) {

            }
        }
    }

    void raiseOut(String eventName, String prevState, X ctx) {
        for (StateListener<X> l : this.outListeners) {
            try {
                l.run(new StateEventContext<X>(eventName, ctx));
            }
            catch (Exception ex) {

            }
        }
    }
}
