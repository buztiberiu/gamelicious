package com.tiberiu.gamelicious.controller;

import com.tiberiu.gamelicious.dto.GameDto;
import com.tiberiu.gamelicious.dto.BaseGameDto;
import com.tiberiu.gamelicious.exception.InvalidProvider;
import com.tiberiu.gamelicious.model.Game;
import com.tiberiu.gamelicious.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired //manages the lifecycle of a class/object -> create, destroy etc.
    private GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public List<GameDto> getGames() {
        return gameService.getGames();
    }

    @PostMapping
    public GameDto addNewGame(@RequestBody Game game) {
        return gameService.addNewGame(game);
    }

    @DeleteMapping(path = "{gameId}")
    public void deleteGames(@PathVariable("gameId") Long gameId) {
        gameService.deleteGame(gameId);
    }

    @PutMapping(path = "{gameId}")
    public GameDto updateGame(@PathVariable("gameId") Long gameId,
                           @RequestBody GameDto gameDto) {
        return gameService.updateGame(gameId, gameDto);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(path = "/fetch/{provider}")
    public BaseGameDto[] getFreeToGames(@PathVariable("provider") String provider) throws InvalidProvider {

       return gameService.fetchGames(provider);
    }

    @GetMapping(path = "/update/{provider}")
    public List<GameDto> addGamesFromProvider(@PathVariable("provider") String provider) throws InvalidProvider {

        return gameService.addGamesFromProvider(provider);
    }


}
