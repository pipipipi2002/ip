package Duke.Tasks;

import java.util.ArrayList;

public class Task {
    private String item;
    private boolean isCompleted;

    protected Task(String item, boolean isCompleted) {
        this.item = item;
        this.isCompleted = isCompleted;
    }

    protected void setCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    protected boolean hasCompleted() {
        return isCompleted;
    }

    protected String getTaskName() {
        return item;
    }

    protected String getTime() {
        return "";
    }

    protected String getCompleteDescription() {
        String output = "[ ][";
        output += (hasCompleted() ? "X] " : " ] ");
        output += getTaskName() + "\n";
        return output;
    }
}