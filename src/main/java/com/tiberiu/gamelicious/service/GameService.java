package com.tiberiu.gamelicious.service;

import com.tiberiu.gamelicious.dto.GameDto;
import com.tiberiu.gamelicious.exception.GameNotFoundException;
import com.tiberiu.gamelicious.mappers.GameMapper;
import com.tiberiu.gamelicious.model.Developer;
import com.tiberiu.gamelicious.model.Game;
import com.tiberiu.gamelicious.model.Publisher;
import com.tiberiu.gamelicious.repository.DeveloperRepository;
import com.tiberiu.gamelicious.repository.GameRepository;
import com.tiberiu.gamelicious.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GameService {

    @Autowired
    private final GameRepository gameRepository;

    @Autowired
    private final PublisherRepository publisherRepository;

    @Autowired
    private final DeveloperRepository developerRepository;

    public GameService(GameRepository gameRepository,
                       PublisherRepository publisherRepository,
                       DeveloperRepository developerRepository) {
        this.gameRepository = gameRepository;
        this.developerRepository = developerRepository;
        this.publisherRepository = publisherRepository;
    }

    public GameDto getOneGame(String name) throws GameNotFoundException {
        Optional<Game> optionalGame = gameRepository.findGameByName(name);

        if (optionalGame.isPresent()) {
            return GameMapper.convert(optionalGame.get());
        }
        throw new GameNotFoundException("Game not found");
    }

   public List<GameDto> getGames() {
        return GameMapper.convert(gameRepository.findAll());
   }

   public void addNewGame(Game game) {
       Optional<Game> gameByName = gameRepository.findGameByName(game.getName());
       if (gameByName.isPresent()) {
           throw new IllegalStateException("game name taken");
       }

       String publisherName = game.getPublisher().getName();
       Optional<Publisher> optionalPublisher = publisherRepository.findPublisherByName(publisherName).or(() ->
            Optional.of(publisherRepository.save(game.getPublisher())));

       String developerName = game.getDeveloper().getName();
       Optional<Developer> developerOptional = developerRepository.findDeveloperByName(developerName).or(() ->
               Optional.of(developerRepository.save(game.getDeveloper())));

       gameRepository.save(game);
   }

   public void deleteGame(Long gameId) {
        boolean exists = gameRepository.existsById(gameId);
        if (!exists) {
            throw new IllegalStateException("game with id " + gameId + " does not exist");
        }
        gameRepository.deleteById(gameId);
   }

   public void updateGame(Long gameId, String name, Integer userReviews, Integer criticsReviews, LocalDate releaseDate) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new IllegalStateException(
                "game with id " + gameId + " does not exist"
        ));

        if (name != null && name.length() > 0 && !Objects.equals(game.getName(), name)) {
            Optional<Game> optionalGame = gameRepository.findGameByName(name);
            if (optionalGame.isPresent()) {
                throw new IllegalStateException("game name taken");
            }
            game.setName(name);
        }

        if (userReviews != null && userReviews >= 0 && !Objects.equals(game.getUserReviews(), userReviews)) {
            game.setUserReviews(userReviews);
        }

       if (criticsReviews != null && criticsReviews >= 0 && !Objects.equals(game.getCriticsReviews(), criticsReviews)) {
           game.setCriticsReviews(criticsReviews);
       }

       if (releaseDate != null && !Objects.equals(game.getReleaseDate(), releaseDate)) {
           game.setReleaseDate(releaseDate);
       }
       gameRepository.save(game);
   }
}

