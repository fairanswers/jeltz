package com.fairanswers.jeltz.demo;

import com.fairanswers.jeltz.JeltzTask;

import java.util.Set;

/**
 * A demonstration task that shows how to use the JeltzTask annotation.
 * This task simulates a data processing operation with progress reporting.
 */
public class DemoTask implements JeltzTask {
    
    @Override
    public Set<String> names() {
        // The names of the task, used to identify it in the command line
        // This task can be invoked with "demo" as the command line argument
        // For java version 17 and above, use Set.of("demo");
        Set<String> strings = new java.util.HashSet<>();
        strings.add("demo");
        return strings;
    }

    @Override
    public String description() {
        return "A demonstration task that shows how to use the JeltzTask implementation.";
    }

    @Override
    public Integer run(String [] args) {
        print("This is the demo task.");
        print("Args inside the task are: " + String.join(", ", args));
        return 0;
    }

}