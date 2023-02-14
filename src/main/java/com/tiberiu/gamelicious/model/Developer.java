package com.tiberiu.gamelicious.model;

import jakarta.persistence.*;

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
    private String email;

    @OneToMany(mappedBy = "developer",
            cascade = CascadeType.ALL,
            orphanRemoval = false)
    List<Game> developedGames;

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
}
