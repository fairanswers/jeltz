package com.fairanswers.jeltz;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.reflections.Reflections;
import java.util.Set;

class JeltzTaskRunnerTest {
    
    @Test
    void testParseCommandLineWithHelp() {
        String[] args = {"-h"};
        assertFalse(JeltzTaskRunner.parseCommandLine(args).isPresent(), "Should return false when help is requested");
    }
    
    @Test
    void testParseCommandLineWithPackageNames() {
        String[] args = {"-p", "asdf"};
        assertTrue(JeltzTaskRunner.parseCommandLine(args).isPresent(), "Should parse successfully with packageName");
    }
    
    @Test
    void testParseCommandLineWithAllOptions() {
        String[] args = {"-p", "dummyPkg", "-v"};
        assertTrue(JeltzTaskRunner.parseCommandLine(args).isPresent(), "Should parse successfully with all options");
    }

    @Test
    void testDemonstrateReflections() {
        // Enable verbose mode to see detailed output
        String[] args = {"-v"};
        JeltzTaskRunner.parseCommandLine(args);
        
        // Verify that at least one class was found
        Reflections reflections = new Reflections("com");
        Set<Class<? extends JeltzTask>> allClasses = reflections.getSubTypesOf(JeltzTask.class);
        assertTrue(allClasses.size() > 0, "Should find at least one class with @JeltzTask annotation");
        
        // Verify that DemoTask is among the found classes
        boolean foundJeltzDemo = allClasses.stream()
            .anyMatch(clazz -> clazz.getName().equals("com.fairanswers.jeltz.demo.DemoTask"));
        assertTrue(foundJeltzDemo, "Should find the JeltzTaskRunner class with @JeltzTask annotation");
    }

    @Test
    void testDemonstrateWeather() {
        // Enable verbose mode to see detailed output
        String[] args = {"weather"};
        JeltzTaskRunner.main(args);
    }
} 