package com.fairanswers.jeltz;

import org.reflections.Reflections;
import org.apache.commons.cli.*;

import java.util.*;
import java.util.stream.Collectors;

public class JeltzTaskRunner {
    private static boolean verbose = false;

    public static void main(String[] args) {
        Optional<CommandLine> commandLineOptional = parseCommandLine(args);
        if (args.length == 0 || !commandLineOptional.isPresent()) {
            printUsage();
            System.exit(1);
        }

        CommandLine commandLine = commandLineOptional.get();
        if (commandLine.hasOption("-v")) {
            print("Verbose mode enabled");
        }
        String packageName = getPackageName(commandLine);

        Optional<Set<? extends JeltzTask>> candidateClassesOptional = getCandidateClasses(packageName);
        if (!candidateClassesOptional.isPresent() || candidateClassesOptional.get().isEmpty()) {
            print("No candidate classes found");
            System.exit(1);
        }
        Set<? extends JeltzTask> candidateClasses = candidateClassesOptional.get();
        if (candidateClasses.size() == 0) {
            print("No tasks found on the classpath.");
            System.exit(1);
        }

        Set<JeltzTask> tasks = getMatchingTasks(candidateClasses, commandLine);
        if(tasks.isEmpty() ){
            print("No tasks found matching: " + commandLine.getArgs()[0]);
            System.exit(1);
        }

        runTasks(tasks, commandLine);

        print("JeltzTaskRunner completed");
    }

    private static Set<JeltzTask> getMatchingTasks(Set<? extends JeltzTask> candidateClasses, CommandLine commandLine) {
        return candidateClasses.stream()
                .filter(task -> task.names().contains(commandLine.getArgs()[0]))
                .collect(Collectors.toSet());
    }

    private static void runTasks(Set<? extends JeltzTask> candidateClasses, CommandLine commandLine) {
        candidateClasses.forEach(task -> {
            print("Running task: " + task.getClass().getSimpleName());
            Optional<?> instanceOptional = getInstance(task.getClass());
            if (instanceOptional.isPresent()) {
                JeltzTask taskInstance = (JeltzTask) instanceOptional.get();
                taskInstance.run(commandLine.getArgs());
            } else {
                print("Failed to create instance of task: " + task.getClass().getSimpleName());
            }
        });
    }

    private static String getPackageName(CommandLine commandLine) {
        String packageName = "com";
        if (commandLine.hasOption("packageName")) {
            packageName = commandLine.getOptionValue("packageName");
            print("Package name: " + packageName);
        }
        if (verbose) print("packageName = " + packageName);
        return packageName;
    }

    private static Optional<Set<? extends JeltzTask>> getCandidateClasses(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends JeltzTask>> subtypes = new HashSet<>();
        try {
            subtypes = reflections.getSubTypesOf(JeltzTask.class);
            return Optional.of(subtypes.stream()
                    .map((task) -> {
                        try {
                            return task.getDeclaredConstructor().newInstance();
                        } catch (Exception ex) {
                            print("Error creating instance of class: " + ex.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
        } catch (Exception ex) {
            print("Error getting candidate classes: " + ex.getMessage());
        }
        return Optional.empty();
    }

    public static Optional<CommandLine> parseCommandLine(String[] args) {
        Options options = new Options();

        Option verboseOption = Option.builder("v")
                .longOpt("verbose")
                .desc("Enable verbose output")
                .build();
        Option packageNameOption = Option.builder("p")
                .longOpt("packageName")
                .desc("Change package name to scan for JeltzTask classes")
                .hasArg(true)
                .build();
        Option help = Option.builder("h")
                .longOpt("help")
                .desc("Show this help message")
                .build();
        options.addOption(packageNameOption);
        options.addOption(verboseOption);
        options.addOption(help);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("help")) {
                formatter.printHelp("JeltzTaskRunner", options);
                return Optional.empty();
            }
            return Optional.of(cmd);

        } catch (ParseException e) {
            print("Error parsing command line arguments: " + e.getMessage());
            formatter.printHelp("JeltzTaskRunner", options);
            return Optional.empty();
        }
    }

    private static <T> Optional<T> getInstance(Class<T> clazz) {
        try {
            return Optional.of(clazz.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static void printUsage() {
        print("Usage: java -jar jeltz.jar [options] <taskName> <taskArgs>");
        print("Options:");
        print("  -v, --verbose         Enable verbose output");
        print("  -h, --help            Show this help message");
        print("  -p, --packageName     Package name to scan for JeltzTask classes.  Default is 'com'");
    }


    public static void print(String message) {
        System.out.println(message);
    }
}