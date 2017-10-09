package uia.utils.states;

/**
 * Event executor.
 *
 * @author Kyle K. Lin
 *
 * @param <C> Controller.
 * @param <A>
 *
 */
public interface EventExecutor<C, A> {

    /**
     * Run this event.
     * @param controller The controller.
     * @param args Arguments.
     * @return State name changed to.
     */
    public String run(C controller, A args);
}
