package uia.utils.dao;

import java.util.ArrayList;
import java.util.List;

public class CompareResult {

    public final String tableName;

    private boolean passed;

    private final List<String> messages;

    public CompareResult(String tableName) {
        this.tableName = tableName;
        this.passed = true;
        this.messages = new ArrayList<String>();
    }

    public boolean isPassed() {
        return this.passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }

    public List<String> getMessages() {
        return this.messages;
    }

    public void print() {
        System.out.println(this);
    }

    public void printFailed() {
        if (!this.passed) {
            System.out.println(this);
        }
    }

    @Override
    public String toString() {
        return this.tableName + " compare passed:" + this.passed + "\n  " + String.join("\n  ", this.messages);
    }
}
