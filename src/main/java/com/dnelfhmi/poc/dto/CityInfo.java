package com.dnelfhmi.poc.dto;

/**
 * Structured output record: the model must return JSON matching this shape.
 */
public record CityInfo(
        String city,
        String country,
        String currency,
        int population,
        String timezone
) {}
