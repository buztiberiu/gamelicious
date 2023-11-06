package com.tiberiu.gamelicious.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gamelicious_publisher")
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private String backgroundImageUrl;

    @Column
    private String email;

    @OneToMany(mappedBy = "publisher",
            cascade = CascadeType.ALL,
            orphanRemoval = true) //was false
    private List<Game> publishedGames = new ArrayList<>();

    public Publisher() {
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

    public List<Game> getPublishedGames() {
        return publishedGames;
    }

    public void setPublishedGames(List<Game> publishedGames) {
        this.publishedGames = publishedGames;
    }

    public void addGameToPublishedGames(Game game) {
        this.publishedGames.add(game);
        game.setPublisher(this);
    }

    public void removeGameFromPublishedGames(Game game) {
        this.publishedGames.remove(game);
        game.setPublisher(null);
    }
}
