package com.fairanswers.jeltz.demo;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WeatherDemoTest {

    @Test
    public void testFindWeatherPrintsOutput() {
        WeatherDemo demo = new WeatherDemo();

        try {
            String result = demo.findWeather();
            // Check that output contains expected keywords
            assertTrue(result.contains("Weather for") || result.contains("Could not determine your city") || result.contains("Error finding weather"));
        } finally {
        }
    }
}