package com.tiberiu.gamelicious.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "gamelicious_game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private LocalDate releaseDate;

    @Column
    private Double userReviews;

    @Column
    private Double criticsReviews;

    @ManyToOne(fetch = FetchType.LAZY)
    private Publisher publisher;

    @ManyToOne(fetch = FetchType.LAZY)
    private Developer developer;

    public Game() {

    }

    public Game(String name, LocalDate releaseDate) {
        this.name = name;
        this.releaseDate = releaseDate;
    }

    public Game(String name, LocalDate releaseDate, Double userReviews, Double criticsReviews, Publisher publisher, Developer developer) {
        this.name = name;
        this.releaseDate = releaseDate;
        this.userReviews = userReviews;
        this.criticsReviews = criticsReviews;
        this.publisher = publisher;
        this.developer = developer;
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

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", releaseDate=" + releaseDate +
                ", userReviews=" + userReviews +
                ", criticsReviews=" + criticsReviews +
                ", publisher=" + publisher +
                ", developer=" + developer +
                '}';
    }
}
