package com.tiberiu.gamelicious.controller;

import com.tiberiu.gamelicious.dto.FreeToGameDto;
import com.tiberiu.gamelicious.dto.GameDto;
import com.tiberiu.gamelicious.dto.RawgDto;
import com.tiberiu.gamelicious.model.Game;
import com.tiberiu.gamelicious.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public List<GameDto> getGames() {
        return gameService.getGames();
    }

    @PostMapping
    public void addNewGame(@RequestBody Game game) {
        gameService.addNewGame(game);
    }

    @DeleteMapping(path = "{gameId}")
    public void deleteGames(@PathVariable("gameId") Long gameId) {
        gameService.deleteGame(gameId);
    }

    @PutMapping(path = "{gameId}")
    public void updateGame(@PathVariable("gameId") Long gameId,
                           @RequestBody GameDto gameDto) {
        gameService.updateGame(gameDto);
    }

    @GetMapping(path = "/fetch/{provider}")
    public FreeToGameDto[] getFreeToGames(@PathVariable("provider") String provider) {
        return gameService.fetchFreeToGames(provider);
    }

    @GetMapping(path = "/fetch2/{provider}")
    public RawgDto getRawg(@PathVariable("provider") String provider) {
        return gameService.fetchRawg(provider);
    }

    @PostMapping(path = "/fetch/{provider}")
    public void addFreeToGames() {
        gameService.addGamesFromFreeToGame();
    }

    @PostMapping(path = "/fetch2/{provider}")
    public void addRawgDevelopers() {
        gameService.addRawgDevelopers();
    }

}
