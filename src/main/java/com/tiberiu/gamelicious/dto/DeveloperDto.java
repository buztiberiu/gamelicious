package com.tiberiu.gamelicious.dto;

import java.util.List;

public class DeveloperDto {

    private Long id;
    private String name;
    private String email;
    private List<GameDto> developedGames;

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

    public List<GameDto> getDevelopedGames() {
        return developedGames;
    }

    public void setDevelopedGames(List<GameDto> developedGames) {
        this.developedGames = developedGames;
    }
}
