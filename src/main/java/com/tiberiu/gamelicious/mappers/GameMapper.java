package com.tiberiu.gamelicious.mappers;

import com.tiberiu.gamelicious.dto.GameDto;
import com.tiberiu.gamelicious.model.Game;

import java.util.ArrayList;
import java.util.List;

public class GameMapper {

    public static GameDto convert(Game game) {

        GameDto gameDto = new GameDto();
        gameDto.setId(game.getId());
        gameDto.setName(game.getName());
        gameDto.setReleaseDate(game.getReleaseDate());
        gameDto.setGenre(game.getGenre());
        gameDto.setUserReviews(game.getUserReviews());
        gameDto.setCriticsReviews(game.getUserReviews());
        gameDto.setBackgroundImageUrl(game.getBackgroundImageUrl());
        gameDto.setShortDescription(game.getShortDescription());

        if (game.getPublisher() != null) {
            gameDto.setPublisherDto(PublisherMapper.convert(game.getPublisher()));
        }

        if (game.getDeveloper() != null) {
            gameDto.setDeveloperDto(DeveloperMapper.convert(game.getDeveloper()));
        }

        return gameDto;
    }

    public static List<GameDto> convert(List<Game> games) {

        List<GameDto> gameDtoList = new ArrayList<>();

        for (Game game : games) {
            gameDtoList.add(convert(game));
        }
        return gameDtoList;
    }
}
