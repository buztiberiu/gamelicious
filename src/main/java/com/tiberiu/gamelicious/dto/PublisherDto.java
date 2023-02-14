package com.tiberiu.gamelicious.dto;

import java.util.List;

public class PublisherDto {

    private Long id;
    private String name;
    private String email;
    private List<GameDto> publishedGames;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<GameDto> getPublishedGames() {
        return publishedGames;
    }

    public void setPublishedGames(List<GameDto> publishedGames) {
        this.publishedGames = publishedGames;
    }
}
