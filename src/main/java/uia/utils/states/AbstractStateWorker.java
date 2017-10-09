package uia.utils.states;

/**
 * Abstract worker using state machine.
 *
 * @author Kyle K. Lin
 *
 * @param <C> Controller.
 */
public abstract class AbstractStateWorker<T, A> {

    /**
     * Allow to print debug information
     */
    public static boolean PRINTABLE = false;

    protected StateMachine<T, A> stateMachine;

    /**
     * Constructor.
     * @param workerName Name.
     */
    protected AbstractStateWorker(String workerName) {
        this.stateMachine = new StateMachine<T, A>(workerName);
        initial();
    }

    /**
     * Constructor.
     * @param workerName Name.
     */
    protected AbstractStateWorker(String workerName, String stateName) {
        this.stateMachine = new StateMachine<T, A>(workerName);
        initial();
        changeState(stateName);
    }

    /**
     * Get name.
     * @return Name.
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
     * @return New state.
     * @throws StateException Raise if state control failed.
    
     */
    public boolean changeState(String stateName) throws StateException {
        return this.stateMachine.changeState(stateName);
    }

    public void addStateInListener(String stateName, StateListener<A> listener) {
        this.stateMachine.getState(stateName).addInListener(listener);
    }

    public void addStateOutListener(String stateName, StateListener<A> listener) {
        this.stateMachine.getState(stateName).addOutListener(listener);
    }

    public void addEventListener(String eventName, StateListener<A> listener) {
        this.stateMachine.addEventListener(eventName, listener);
    }

    public void addStateChangedListener(String fromStateName, String toStateName, StateListener<A> listener) {
        this.stateMachine.addChangeListener(fromStateName, toStateName, listener);
    }

    public static void printlnHeader() {
        System.out.println(String.format("%-25s, %-15s, %-20s, %-20s, %s",
                "Class",
                "Name",
                "Event",
                "State",
                "Others"));
        System.out.println("=================================================================================================");
    }

    public void println(String eventName, String extra) {
        if (PRINTABLE) {
            System.out.println(String.format("%-25s, %-15s, %-20s, %-20s, %s",
                    getClass().getSimpleName(),
                    getName(),
                    eventName,
                    this.stateMachine.getCurrState(),
                    extra));
        }
    }

    /**
     * initial.
     */
    protected abstract void initial();
}
