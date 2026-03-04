package com.fairanswers.jeltz;

import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class JeltzTaskRunnerRunTasksTest {

    @Test
    void testListDemoTasks() {
        String[] args = {"-p", "com.fairanswers.jeltz.demo"};
        CommandLine line = JeltzTaskRunner.parseCommandLine(args).orElseThrow(() -> new AssertionError("Failed to parse CLI"));

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintStream origOut = System.out;
        System.setOut(new PrintStream(bout));
        try {
            JeltzTaskRunner.runTasks(line);
        } finally {
            System.setOut(origOut);
        }

        String out = bout.toString();
        assertTrue(out.contains("Found task"), "Should list found tasks");
        assertTrue(out.contains("(not executed; use -e/--execute to actually run tasks)"), "Should not execute tasks by default");
    }

    @Test
    void testExecuteDemoTasks() {
        String[] args = {"-p", "com.fairanswers.jeltz.demo", "-e"};
        CommandLine line = JeltzTaskRunner.parseCommandLine(args).orElseThrow(() -> new AssertionError("Failed to parse CLI"));

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintStream origOut = System.out;
        PrintStream origErr = System.err;
        System.setOut(new PrintStream(bout));
        System.setErr(new PrintStream(bout));
        try {
            JeltzTaskRunner.runTasks(line);
        } finally {
            System.setOut(origOut);
            System.setErr(origErr);
        }

        String out = bout.toString();
        assertTrue(out.contains("Found task"), "Should list found tasks");
        assertTrue(out.contains("Result for"), "Should print result when executed");
        assertTrue(out.contains("0"), "DemoTask returns 0");
    }
}

