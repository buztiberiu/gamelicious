package com.tiberiu.gamelicious.controller;

import com.tiberiu.gamelicious.dto.GameDto;
import com.tiberiu.gamelicious.model.Game;
import com.tiberiu.gamelicious.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
                           @RequestParam(required = false) String name,
                           @RequestParam(required = false) Integer userReviews,
                           @RequestParam(required = false) Integer criticsReviews,
                           @RequestParam(required = false)LocalDate releaseDate) {
        gameService.updateGame(gameId, name, userReviews, criticsReviews, releaseDate);
    }
}
