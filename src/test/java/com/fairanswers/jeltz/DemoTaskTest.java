package com.fairanswers.jeltz;

import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

class DemoTaskTest {
    
    @Test
    void testTaskDiscovery() {
        Reflections reflections = new Reflections("com");
        Set<Class<? extends JeltzTask>> tasks = reflections.getSubTypesOf(JeltzTask.class);
        
        boolean foundDemoTask = tasks.stream()
            .anyMatch(clazz -> clazz.getName().equals("com.fairanswers.jeltz.demo.DemoTask"));
        assertTrue(foundDemoTask, "Should find DemoTask using reflection");
    }
} 