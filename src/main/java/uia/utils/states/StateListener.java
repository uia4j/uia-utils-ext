package uia.utils.states;

public interface StateListener<T> {

    public void run(StateEventArgs<T> args);
}
