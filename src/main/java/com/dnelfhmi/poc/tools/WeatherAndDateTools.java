package com.dnelfhmi.poc.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Tools the LLM can call autonomously. Each @Tool method becomes a function
 * the model can invoke with JSON arguments — the heart of agentic behaviour.
 */
@Service
public class WeatherAndDateTools {

    /**
     * Minimal deterministic "weather" lookup so the POC works with no external API.
     * Swap the body for a real weather API (e.g. Open-Meteo) in production.
     */
    @Tool(description = "Get the current weather condition for a given city")
    public String getWeather(
            @ToolParam(description = "City name, e.g. 'Kuala Lumpur'") String city) {
        // Deterministic pseudo-weather keyed by city hash for a stable POC demo.
        int seed = Math.abs(city.hashCode());
        String[] conditions = {"sunny, 31C", "partly cloudy, 29C", "light rain, 27C", "thunderstorms, 26C"};
        String condition = conditions[seed % conditions.length];
        return "Weather in " + city + ": " + condition + " (POC data)";
    }

    @Tool(description = "Get today's date in the given IANA timezone, e.g. 'Asia/Kuala_Lumpur'")
    public String getCurrentDate(
            @ToolParam(description = "IANA timezone id, e.g. 'Asia/Kuala_Lumpur'") String timezone) {
        ZoneId zone = ZoneId.of(timezone);
        String date = LocalDate.now(zone).format(DateTimeFormatter.ISO_LOCAL_DATE);
        return "Today in " + timezone + " is " + date;
    }
}
