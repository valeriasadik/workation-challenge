package com.workflex.workation.service;

/** A country as exposed by the API: the name to read, the ISO code to render the flag. */
public record CountryDto(String name, String isoCode) {}
