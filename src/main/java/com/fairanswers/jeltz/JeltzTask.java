package com.fairanswers.jeltz;

import java.util.Set;

public interface JeltzTask {

    /**
     * Returns the set of names associated with this task.
     * These names are used to identify the task in the command line.
     *
     * @return a set of strings representing the task names
     */
    public Set<String> names();

    /**
     * Provides a description of the task.
     * This description can be used for documentation or help messages.
     *
     * @return a string describing the task
     */
    public String description();

    /**
     * Executes the task with the given arguments.
     *
     * @param args an array of strings representing the arguments for the task
     * @return an object representing the result of the task execution
     */
    public Object run(String[] args);

    /**
     * Prints a message to the standard output.
     * This is a default method that can be used by implementing classes.
     * Can be overridden if needed for custom behavior.
     *
     * @param message the message to be printed
     */
    public default void print(String message) {
        System.out.println(message);
    }
}