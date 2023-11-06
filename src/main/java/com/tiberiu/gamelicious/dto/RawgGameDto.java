package com.tiberiu.gamelicious.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RawgGameDto extends BaseGameDto {

    private String name;
    private String released;
    private boolean tba;
    private Double rating;
    private Double rating_top;
    private String image_background;
    private GameDto[] games;
    private Integer games_count;
    private String background_image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public boolean isTba() {
        return tba;
    }

    public void setTba(boolean tba) {
        this.tba = tba;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getRating_top() {
        return rating_top;
    }

    public void setRating_top(Double rating_top) {
        this.rating_top = rating_top;
    }

    //For Publishers & Developers

    public GameDto[] getGames() {
        return games;
    }

    public void setGames(GameDto[] games) {
        this.games = games;
    }

    public Integer getGames_count() {
        return games_count;
    }

    public void setGames_count(Integer games_count) {
        this.games_count = games_count;
    }

    public String getImage_background() {
        return image_background;
    }

    public void setImage_background(String image_background) {
        this.image_background = image_background;
    }

    public String getBackground_image() {
        return background_image;
    }

    public void setBackground_image(String background_image) {
        this.background_image = background_image;
    }
}
