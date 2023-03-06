package com.tiberiu.gamelicious.service;

import com.tiberiu.gamelicious.dto.FreeToGameDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DateTimeException;
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
       Optional<Publisher> publisher = publisherRepository.findPublisherByName(publisherName).or(() ->
               Optional.of(publisherRepository.save(game.getPublisher())));
//       game.setPublisher(publisher.get());
       publisher.get().addGameToPublishedGames(game);

       String developerName = game.getDeveloper().getName();
       Optional<Developer> developer = developerRepository.findDeveloperByName(developerName).or(() ->
               Optional.of(developerRepository.save(game.getDeveloper())));
//       game.setDeveloper(developer.get());
       developer.get().addGameToDevelopedGames(game);

       gameRepository.save(game);
   }

   public void deleteGame(Long gameId) {
        boolean exists = gameRepository.existsById(gameId);
        if (!exists) {
            throw new IllegalStateException("game with id " + gameId + " does not exist");
        }
        gameRepository.deleteById(gameId);
   }

   public GameDto updateGame(GameDto gameDto) {
        Game game = gameRepository.findById(gameDto.getId()).orElseThrow(() -> new IllegalStateException(
                "game with id " + gameDto.getId() + " does not exist"
        ));

        if (gameDto.getName() != null && gameDto.getName().length() > 0 && !Objects.equals(game.getName(), gameDto.getName())) {
            Optional<Game> optionalGame = gameRepository.findGameByName(gameDto.getName());
            if (optionalGame.isPresent()) {
                throw new IllegalStateException("game name taken");
            }
            game.setName(gameDto.getName());
        }

        if (gameDto.getUserReviews() != null && gameDto.getUserReviews() >= 0 && !Objects.equals(game.getUserReviews(), gameDto.getUserReviews())) {
            game.setUserReviews(gameDto.getUserReviews());
        }

       if (gameDto.getCriticsReviews() != null && gameDto.getCriticsReviews() >= 0 && !Objects.equals(game.getCriticsReviews(), gameDto.getCriticsReviews())) {
           game.setCriticsReviews(gameDto.getCriticsReviews());
       }

       if (gameDto.getReleaseDate() != null && !Objects.equals(game.getReleaseDate(), gameDto.getReleaseDate())) {
           game.setReleaseDate(gameDto.getReleaseDate());
       }
       return GameMapper.convert(gameRepository.save(game));
   }

   public FreeToGameDto[] fetchFreeToGames(String provider) {

       RestTemplate restTemplate = new RestTemplate();
       String fooResourceUrl = "https://www.freetogame.com/api/games";
       ResponseEntity<FreeToGameDto[]> response = restTemplate.getForEntity(fooResourceUrl, FreeToGameDto[].class);

        if (provider.equals("rawg")) {
            return null;
        }

        if (provider.equals("freetogame")) {
            return response.getBody();
        }
        return null;
   }

   public void addGamesFromFreeToGame() {

       RestTemplate restTemplate = new RestTemplate();
       String fooResourceUrl = "https://www.freetogame.com/api/games";
       ResponseEntity<FreeToGameDto[]> response = restTemplate.getForEntity(fooResourceUrl, FreeToGameDto[].class);
       FreeToGameDto[] freeToGameDtos = response.getBody();

       if (freeToGameDtos != null) {
           for (FreeToGameDto freeToGameDto : freeToGameDtos) {
                Game game = new Game();
                game.setName(freeToGameDto.getTitle());
                try {
                    game.setReleaseDate(LocalDate.parse(freeToGameDto.getRelease_date()));
                } catch (DateTimeException e) {
                    game.setReleaseDate(null);
                }

                Publisher publisher = new Publisher();
                publisher.setName(freeToGameDto.getPublisher());
                game.setPublisher(publisher);

                Developer developer = new Developer();
                developer.setName(freeToGameDto.getDeveloper());
                game.setDeveloper(developer);

                addNewGame(game);
           }
       }
   }

   //posturile si puturile trb sa returneze obiecte dto
    //body ul care il primesc la response, sa iau informatiile din body sa le salvez
    //in baza mea de date, nu trb sa salvez intr-un string ci intr-un array de elemente,
    //apoi sa convertesc astea in gamedto si apoi salvate in baza de date.
}

