package com.tiberiu.gamelicious.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameDto {

    private Long id;
    private String name;
    private LocalDate releaseDate;
    private Double userReviews;
    private Double criticsReviews;
    private PublisherDto publisherDto;
    private DeveloperDto developerDto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getUserReviews() {
        return userReviews;
    }

    public void setUserReviews(Double userReviews) {
        this.userReviews = userReviews;
    }

    public Double getCriticsReviews() {
        return criticsReviews;
    }

    public void setCriticsReviews(Double criticsReviews) {
        this.criticsReviews = criticsReviews;
    }

    public PublisherDto getPublisherDto() {
        return publisherDto;
    }

    public void setPublisherDto(PublisherDto publisherDto) {
        this.publisherDto = publisherDto;
    }

    public DeveloperDto getDeveloperDto() {
        return developerDto;
    }

    public void setDeveloperDto(DeveloperDto developerDto) {
        this.developerDto = developerDto;
    }
}
