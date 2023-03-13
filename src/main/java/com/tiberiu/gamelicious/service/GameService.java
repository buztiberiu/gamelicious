package com.tiberiu.gamelicious.service;

import com.tiberiu.gamelicious.dto.FreeToGameDto;
import com.tiberiu.gamelicious.dto.GameDto;
import com.tiberiu.gamelicious.dto.RawgDto;
import com.tiberiu.gamelicious.dto.RawgGameDto;
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
import java.util.*;

@Service
public class GameService {

    @Autowired
    private final GameRepository gameRepository;

    @Autowired
    private final PublisherRepository publisherRepository;

    @Autowired
    private final DeveloperRepository developerRepository;

    @Autowired
    private final DeveloperService developerService;

    public GameService(GameRepository gameRepository,
                       PublisherRepository publisherRepository,
                       DeveloperRepository developerRepository, DeveloperService developerService) {
        this.gameRepository = gameRepository;
        this.developerRepository = developerRepository;
        this.publisherRepository = publisherRepository;
        this.developerService = developerService;
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
        String freeToGameUrl = "https://www.freetogame.com/api/games";
        ResponseEntity<FreeToGameDto[]> responseFreeToGames = restTemplate.getForEntity(freeToGameUrl, FreeToGameDto[].class);

        return responseFreeToGames.getBody();
    }

    public RawgDto fetchRawg(String provider) {

        RestTemplate restTemplate = new RestTemplate();
        String rawgGamesUrl = "https://api.rawg.io/api/games?key=6ba20560f8b54e1095fab5945ca7ca2d";
        ResponseEntity<RawgDto> responseRawgGames = restTemplate.getForEntity(rawgGamesUrl, RawgDto.class);

        return responseRawgGames.getBody();
    }

    public void addRawgDevelopers() {

        RestTemplate restTemplate = new RestTemplate();
        String rawgDevelopersUrl = "https://api.rawg.io/api/developers?key=6ba20560f8b54e1095fab5945ca7ca2d";
        ResponseEntity<RawgDto> responseRawgDevelopers = restTemplate.getForEntity(rawgDevelopersUrl, RawgDto.class);
        RawgDto developersRawg = responseRawgDevelopers.getBody();

        if (developersRawg != null) {
            RawgGameDto[] rawgDevelopers = developersRawg.getResults();
            for (RawgGameDto rawgDev : rawgDevelopers) {
//                List<Game> gameList = new ArrayList<>();
                for (GameDto gameDto : rawgDev.getGames()) {
                    Optional<Game> gameByName = gameRepository.findGameByName(gameDto.getName());
                    if (gameByName.isEmpty()) {
                        Game game = new Game();
                        game.setName(gameDto.getName());
//                        gameList.add(game);
                        Optional<Developer> developerOptional = developerRepository.findDeveloperByName(rawgDev.getName());
                        if (developerOptional.isPresent()) {
                            developerOptional.get().addGameToDevelopedGames(game);
                        } else {
                            Developer developer = new Developer();
                            developer.setName(rawgDev.getName());
                            developer.addGameToDevelopedGames(game);
                            developerRepository.save(developer);
                        }
                    } else {
                        if (gameByName.get().getDeveloper() == null) {
                            Developer developer = new Developer();
                            developer.setName(rawgDev.getName());
                            developer.addGameToDevelopedGames(gameByName.get());
                            developerRepository.save(developer);
                        }
                    }
                }
            }
        }
    }

    public void addRawgPublishers() {

        RestTemplate restTemplate = new RestTemplate();
        String rawgPublishersUrl = "https://api.rawg.io/api/publishers?key=6ba20560f8b54e1095fab5945ca7ca2d";
        ResponseEntity<RawgDto> responseRawgPublishers = restTemplate.getForEntity(rawgPublishersUrl, RawgDto.class);
        RawgDto publishersRawg = responseRawgPublishers.getBody();

        if (publishersRawg != null) {
            RawgGameDto[] rawgPublishers = publishersRawg.getResults();
        }
    }

    public void updateGamesFromRawg() {

        RestTemplate restTemplate = new RestTemplate();
        String rawgGamesUrl = "https://api.rawg.io/api/games?key=6ba20560f8b54e1095fab5945ca7ca2d";
        ResponseEntity<RawgDto> responseRawgGames = restTemplate.getForEntity(rawgGamesUrl, RawgDto.class);
        RawgDto rawgDto = responseRawgGames.getBody();

        if (rawgDto != null) {
            RawgGameDto[] rawgGameDtos = rawgDto.getResults();
            for (RawgGameDto rawgGameDto : rawgGameDtos) {
                Optional<Game> optionalGame = gameRepository.findGameByName(rawgGameDto.getName());
                if (optionalGame.isPresent()) {
                    optionalGame.get().setReleaseDate(LocalDate.parse(rawgGameDto.getReleased()));
                    optionalGame.get().setUserReviews(rawgGameDto.getRating());
                } else {
                    Game createdGame = new Game();
                    createdGame.setReleaseDate(LocalDate.parse(rawgGameDto.getReleased()));
                    createdGame.setUserReviews(rawgGameDto.getRating());
                    addNewGame(createdGame);
                }
            }
        }
    }

    public void addGamesFromFreeToGame() {

        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = "https://www.freetogame.com/api/games";
        ResponseEntity<FreeToGameDto[]> response = restTemplate.getForEntity(fooResourceUrl, FreeToGameDto[].class);
        FreeToGameDto[] freeToGameDto = response.getBody();

        if (freeToGameDto != null) {
            for (FreeToGameDto gameDto : freeToGameDto) {
                Game game = new Game();
                game.setName(gameDto.getTitle());
                try {
                    game.setReleaseDate(LocalDate.parse(gameDto.getRelease_date()));
                } catch (DateTimeException e) {
                    game.setReleaseDate(null);
                }

                Publisher publisher = new Publisher();
                publisher.setName(gameDto.getPublisher());
                game.setPublisher(publisher);

                Developer developer = new Developer();
                developer.setName(gameDto.getDeveloper());
                game.setDeveloper(developer);

                addNewGame(game);
            }
        }
    }

    public List<GameDto> addGamesIfNotInRepository(List<GameDto> games) {
        List<GameDto> gameDtoList = new ArrayList<>();
        for (GameDto gameDto : games) {
            Optional<Game> gameByName = gameRepository.findGameByName(gameDto.getName());
            if (gameByName.isEmpty()) {
                Game game = new Game();
                game.setName(gameDto.getName());
                gameDtoList.add(GameMapper.convert(game));
            }
        }
        return gameDtoList;
    }

    //posturile si puturile trb sa returneze obiecte dto
    //body ul care il primesc la response, sa iau informatiile din body sa le salvez
    //in baza mea de date, nu trb sa salvez intr-un string ci intr-un array de elemente,
    //apoi sa convertesc astea in gamedto si apoi salvate in baza de date.
}

