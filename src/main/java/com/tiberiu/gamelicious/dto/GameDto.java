package com.tiberiu.gamelicious.dto;

import java.time.LocalDate;

public class GameDto {

    private Long id;
    private String name;
    private LocalDate releaseDate;
    private Integer userReviews;
    private Integer criticsReviews;
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

    public Integer getUserReviews() {
        return userReviews;
    }

    public void setUserReviews(Integer userReviews) {
        this.userReviews = userReviews;
    }

    public Integer getCriticsReviews() {
        return criticsReviews;
    }

    public void setCriticsReviews(Integer criticsReviews) {
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
