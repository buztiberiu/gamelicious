package com.tiberiu.gamelicious.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gamelicious_developer")
public class Developer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private String backgroundImageUrl;

    @Column
    private String email;

    @OneToMany(mappedBy = "developer",
            cascade = CascadeType.ALL,
            orphanRemoval = true) //was false
    private List<Game> developedGames = new ArrayList<>();

    public Developer() {
    }

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

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Game> getDevelopedGames() {
        return developedGames;
    }

    public void setDevelopedGames(List<Game> developedGames) {
        this.developedGames = developedGames;
    }

    public void addGameToDevelopedGames(Game game) {
        this.developedGames.add(game);
        game.setDeveloper(this);
    }

    public void removeGameFromDevelopedGames(Game game) {
        this.developedGames.remove(game);
        game.setDeveloper(null);
    }
}
