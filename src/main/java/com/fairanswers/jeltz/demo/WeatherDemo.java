package com.fairanswers.jeltz.demo;

import com.fairanswers.jeltz.JeltzTask;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

public class WeatherDemo implements JeltzTask {

    @Override
    public Set<String> names() {
        Set<String> strings = new java.util.HashSet<>();
        strings.add("weather");
        return strings;
    }

    @Override
    public String description() {
        return "A demonstration task that shows how to use the JeltzTask implementation to get local weather.";
    }

    @Override
    public Integer run(String[] args) {
        print("This is the weather demo task.");
        print("Args inside the task are: " + String.join(", ", args));
        print(findWeather());
        return 0;
    }

    public String findWeather() {
        try {
            // Step 1: Get public IP geolocation (lat/lon)
            URL ipUrl = new URL("https://ipinfo.io/json");
            HttpURLConnection ipConn = (HttpURLConnection) ipUrl.openConnection();
            ipConn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(ipConn.getInputStream()));
            StringBuilder ipResponse = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                ipResponse.append(inputLine);
            }
            in.close();
            JSONObject ipInfo = new JSONObject(ipResponse.toString());
            String loc = ipInfo.optString("loc", ""); // format: "lat,lon"
            String city = ipInfo.optString("city", "");
            String country = ipInfo.optString("country", "");

            if (loc.isEmpty()) {
                throw new RuntimeException("Could not determine your location from IP.");

            }

            String[] latlon = loc.split(",");
            if (latlon.length != 2) {
                throw new RuntimeException("Invalid location format from IP info.");

            }
            String lat = latlon[0];
            String lon = latlon[1];

            // Step 2: Get weather.gov forecast URL for this point
            String pointsUrl = "https://api.weather.gov/points/" + lat + "," + lon;
            HttpURLConnection pointsConn = (HttpURLConnection) new URL(pointsUrl).openConnection();
            pointsConn.setRequestProperty("User-Agent", "JeltzWeatherDemo/1.0 (fairanswers@example.com)");
            pointsConn.setRequestMethod("GET");
            BufferedReader pointsIn = new BufferedReader(new InputStreamReader(pointsConn.getInputStream()));
            StringBuilder pointsResponse = new StringBuilder();
            while ((inputLine = pointsIn.readLine()) != null) {
                pointsResponse.append(inputLine);
            }
            pointsIn.close();
            JSONObject pointsJson = new JSONObject(pointsResponse.toString());
            String forecastUrl = pointsJson.getJSONObject("properties").optString("forecast", "");

            if (forecastUrl.isEmpty()) {
                throw new RuntimeException("Could not get forecast URL from weather.gov.");
            }

            // Step 3: Get the forecast
            HttpURLConnection forecastConn = (HttpURLConnection) new URL(forecastUrl).openConnection();
            forecastConn.setRequestProperty("User-Agent", "JeltzWeatherDemo/1.0 (fairanswers@example.com)");
            forecastConn.setRequestMethod("GET");
            BufferedReader forecastIn = new BufferedReader(new InputStreamReader(forecastConn.getInputStream()));
            StringBuilder forecastResponse = new StringBuilder();
            while ((inputLine = forecastIn.readLine()) != null) {
                forecastResponse.append(inputLine);
            }
            forecastIn.close();
            JSONObject forecastJson = new JSONObject(forecastResponse.toString());
            // Get the first period (usually "Today" or "Tonight")
            JSONObject firstPeriod = forecastJson.getJSONObject("properties")
                    .getJSONArray("periods").getJSONObject(0);
            String name = firstPeriod.optString("name", "Today");
            String detailedForecast = firstPeriod.optString("detailedForecast", "");

            return "Weather for " + city + ", " + country + " (" + lat + "," + lon + "):" +
            name + ": " + detailedForecast;

        } catch (Exception e) {
            throw new RuntimeException("Error finding weather: " + e.getMessage());
        }
    }
}
