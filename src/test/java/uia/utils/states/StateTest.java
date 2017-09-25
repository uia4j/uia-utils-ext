package uia.utils.states;

import org.junit.Assert;
import org.junit.Test;

public class StateTest {

    @Test
    public void testNormal() {
        State<Object> state = new State<Object>("RUN");
        state.addEvent("E1", (c, a) -> {
            return "E2";
        });
        state.addEvent("E2", (c, a) -> {
            return "E3";
        });
        state.addEvent("E3", (c, a) -> {
            return null;
        });

        System.out.println(state.toStringEx());

        Assert.assertEquals("RUN", state.getName());
        Assert.assertTrue(state.containsEvent("E1"));
        Assert.assertTrue(state.containsEvent("E2"));
        Assert.assertTrue(state.containsEvent("E3"));
        Assert.assertFalse(state.containsEvent("E4"));
    }

    @Test
    public void testEx() {
        State<Object> state = new State<Object>("RUN");
        try {
            state.addEvent(null, (c, a) -> {
                return "E2";
            });
        }
        catch (NullPointerException ex) {
            Assert.assertEquals(NullPointerException.class, ex.getClass());
        }
        try {
            state.addEvent("E2", null);
        }
        catch (NullPointerException ex) {
            Assert.assertEquals(NullPointerException.class, ex.getClass());
        }
    }
}
