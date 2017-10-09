package uia.utils.states;

public class StateEventArgs<T> {

    public final String eventName;

    public final T value;

    public StateEventArgs(String eventName, T value) {
        this.eventName = eventName;
        this.value = value;
    }

}
