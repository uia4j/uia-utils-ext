package uia.utils.states;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * State.
 *
 * @author Kyle K. Lin
 *
 * @param <C>
 * @param <A>
 */
public class State<C, A> {

    private final String name;

    private final TreeMap<String, EventExecutor<C, A>> executors;

    private final ArrayList<StateListener<A>> inListeners;

    private final ArrayList<StateListener<A>> outListeners;

    /**
     * Constructor.
     *
     * @param name State name.
     */
    public State(String name) {
        this.name = name;
        this.executors = new TreeMap<String, EventExecutor<C, A>>();
        this.inListeners = new ArrayList<StateListener<A>>();
        this.outListeners = new ArrayList<StateListener<A>>();
    }

    public boolean containsEvent(String eventName) {
        return this.executors.containsKey(eventName);
    }

    public void addInListener(StateListener<A> listener) {
        this.inListeners.add(listener);
    }

    public void addOutListener(StateListener<A> listener) {
        this.outListeners.add(listener);
    }

    public String getName() {
        return this.name;
    }

    public State<C, A> addEvent(String eventName, EventExecutor<C, A> executor) {
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

    public String toStringEx() {
        return this.name + ", events:{" + String.join(",", this.executors.keySet().toArray(new String[0])) + "}";
    }

    String execute(C controller, String eventName, A args) {
        EventExecutor<C, A> executor = this.executors.get(eventName);
        return executor.run(controller, args);
    }

    void raiseIn(String eventName, String prevState, A value) {
        for (StateListener<A> l : this.inListeners) {
            try {
                l.run(new StateEventArgs<A>(eventName, value));
            }
            catch (Exception ex) {

            }
        }
    }

    void raiseOut(String eventName, String prevState, A value) {
        for (StateListener<A> l : this.outListeners) {
            try {
                l.run(new StateEventArgs<A>(eventName, value));
            }
            catch (Exception ex) {

            }
        }
    }
}
