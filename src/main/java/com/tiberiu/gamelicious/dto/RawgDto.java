package com.tiberiu.gamelicious.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RawgDto {

    private RawgGameDto[] results;

    public RawgGameDto[] getResults() {
        return results;
    }

    public void setResults(RawgGameDto[] results) {
        this.results = results;
    }
}
