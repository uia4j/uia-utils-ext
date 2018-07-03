package uia.utils.dao;

import java.util.ArrayList;
import java.util.List;

public class CompareResult {

    public final String tableName;

    private boolean passed;

    private boolean missing;

    private final List<String> messages;

    public CompareResult(String tableName) {
        this.tableName = tableName;
        this.missing = false;
        this.passed = true;
        this.messages = new ArrayList<String>();
    }

    public CompareResult(String tableName, boolean passed, String message) {
        this.tableName = tableName;
        this.missing = false;
        this.passed = passed;
        this.messages = new ArrayList<String>();
        this.messages.add(message);
    }

    public boolean isMissing() {
        return this.missing;
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
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

    public void print(boolean printAll) {
        if (printAll || !this.passed) {
            System.out.println(this);
        }
    }

    @Override
    public String toString() {
        String yn = this.missing ? "(?) " : this.passed ? "(v) " : "(x) ";
        return this.messages.size() == 0
                ? yn + this.tableName
                : yn + this.tableName + "\n    " + String.join("\n    ", this.messages);
    }
}
