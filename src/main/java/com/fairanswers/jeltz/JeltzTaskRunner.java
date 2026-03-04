package com.fairanswers.jeltz;

import org.apache.commons.cli.*;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class JeltzTaskRunner {
    static String DEFAULT_TASK_PACKAGE = "com.fairanswers.jeltz";

    public static Optional<CommandLine> parseCommandLine(String[] args) {
        Options options = new Options();
        Option help = Option.builder("h")
                .longOpt("help")
                .desc("print help")
                .build();
        Option packages = Option.builder("p")
                .longOpt("packages")
                .hasArg()
                .argName("pkg1,pkg2")
                .desc("comma-separated package names")
                .build();
        Option verbose = Option.builder("v")
                .longOpt("verbose")
                .desc("verbose mode")
                .build();
        Option execute = Option.builder("e")
                .longOpt("execute")
                .desc("actually execute discovered tasks; without this flag tasks are only listed")
                .build();

        options.addOption(help);
        options.addOption(packages);
        options.addOption(verbose);
        options.addOption(execute);

        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            if(line.hasOption("h") || line.hasOption("help")){
                printHelp(options);
                return Optional.empty();
            }
            return Optional.of(line);

        }
        catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            printHelp(options);
        }
        return Optional.empty();
    }

    // package-private so tests in the same package can call it
    static void runTasks(CommandLine line) {
        boolean verbose = line.hasOption("v") || line.hasOption("verbose");
        boolean execute = line.hasOption("e") || line.hasOption("execute");

        // Reduce noisy reflections INFO logging in tests/runs
        try {
            Logger.getLogger("org.reflections").setLevel(Level.WARNING);
        } catch (Exception ignored) {}

        // ??? Why are we doing this???
        // Determine packages to scan
        List<String> pkgsToScan;
        if (line.hasOption("p") || line.hasOption("packages")) {
            String raw = line.hasOption("p") ? line.getOptionValue("p") : line.getOptionValue("packages");
            pkgsToScan = Arrays.stream(raw.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        } else {
            // default to the application root packages plus any non-option args
            pkgsToScan = Arrays.asList(DEFAULT_TASK_PACKAGE);
        }

        // Discover tasks in the given packages
        Set<Class<? extends JeltzTask>> found = new java.util.HashSet<>();
        for (String pkg : pkgsToScan) {
            if (verbose) System.out.println("[verbose] Scanning package: " + pkg);
            try {
                Reflections reflections = new Reflections(pkg);
                found.addAll(reflections.getSubTypesOf(JeltzTask.class));
            } catch (Exception ex) {
                System.err.println("Error scanning package '" + pkg + "': " + ex.getMessage());
            }
        }

        if (found.isEmpty()) {
            // If no classes found, maybe user passed task names as non-option args
            String[] remaining = line.getArgs();
            if (remaining != null && remaining.length > 0) {
                // We'll attempt to find tasks by name across the default package
                Reflections reflections = new Reflections(DEFAULT_TASK_PACKAGE);
                found.addAll(reflections.getSubTypesOf(JeltzTask.class));
            }
        }

        if (found.isEmpty()) {
            System.out.println("No tasks found in the specified packages.");
            return;
        }

        // If non-option args are provided, treat them as task names to filter
        String[] remaining = line.getArgs();
        List<String> requestedNames = (remaining != null) ? Arrays.stream(remaining).collect(Collectors.toList()) : java.util.Collections.emptyList();

        // Iterate discovered classes and either list or execute matching ones
        int matched = 0;
        for (Class<? extends JeltzTask> taskClass : found) {
            try {
                JeltzTask task = taskClass.getDeclaredConstructor().newInstance();
                String className = taskClass.getName();
                String desc = task.description();
                Set<String> names = task.names();

                boolean matchesRequested = requestedNames.isEmpty() || requestedNames.stream().anyMatch(names::contains);
                if (!matchesRequested) continue;

                matched++;
                System.out.println("Found task: " + className + " names=" + names + " -- " + desc);

                if (execute) {
                    if (verbose) System.out.println("[verbose] Executing " + className);
                    try {
                        Object res = task.run(remaining);
                        System.out.println("Result for " + className + ": " + res);
                    } catch (Exception ex) {
                        System.err.println("Error running task " + className + ": " + ex.getMessage());
                    }
                    if (verbose) System.out.println("[verbose] Completed " + className);
                } else {
                    System.out.println("(not executed; use -e/--execute to actually run tasks)");
                }

            } catch (NoSuchMethodException nsme) {
                System.err.println("Task class " + taskClass.getName() + " has no accessible default constructor.");
            } catch (Exception ex) {
                System.err.println("Failed to instantiate task " + taskClass.getName() + ": " + ex.getMessage());
            }
        }

        if (matched == 0) {
            System.out.println("No matching tasks found for the requested names: " + requestedNames);
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        String header = "Run Jeltz tasks discovered on the classpath.\n" +
                "If no packages are supplied, the runner scans com.fairanswers.jeltz by default.";
        String footer = "Examples:\n" +
                "  jeltz-runner -p com.fairanswers.jeltz.demo        # list tasks in demo package\n" +
                "  jeltz-runner weather                             # list tasks named 'weather'\n" +
                "  jeltz-runner -p com.fairanswers.jeltz.demo -e    # execute discovered tasks";
        formatter.printHelp("jeltz-runner", header, options, footer, true);
    }

    public static void main(String[] args) {
        Optional<CommandLine> line = parseCommandLine(args);
        if (line.isPresent()) {
            runTasks(line.get());
        }
    }
}
