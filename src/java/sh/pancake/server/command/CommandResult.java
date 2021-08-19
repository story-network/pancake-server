/*
 * Created on Thu Aug 19 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

public class CommandResult {

    private final boolean executed;
    private final int returned;

    public CommandResult(boolean executed, int returned) {
        this.executed = executed;
        this.returned = returned;
    }

    /**
     * true if command executed atleast once.
     * false if command is not found.
     */
    public boolean isExecuted() {
        return executed;
    }

    /**
     * Command returned result.
     * Value is undefined when isExecuted() is false.
     */
    public int getReturned() {
        return returned;
    }
    
}
