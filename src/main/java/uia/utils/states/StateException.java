package uia.utils.states;

public class StateException extends RuntimeException {

    private static final long serialVersionUID = 8187383561451669713L;

    public final String eventName;

    public final String stateName;

    public StateException(String eventName, String stateName, String message, Throwable th) {
        super(message, th);
        this.eventName = eventName;
        this.stateName = stateName;
    }
}
