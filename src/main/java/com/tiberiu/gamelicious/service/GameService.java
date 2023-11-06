package com.tiberiu.gamelicious.service;

import com.tiberiu.gamelicious.dto.*;
import com.tiberiu.gamelicious.exception.GameNotFoundException;
import com.tiberiu.gamelicious.exception.InvalidProvider;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GameService {

    public static final String FREETOGAMES_URL = "https://www.freetogame.com/api/games";
    public static final String RAWGGAMES_URL = "https://api.rawg.io/api/games?key=6ba20560f8b54e1095fab5945ca7ca2d";

    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    private final GameRepository gameRepository;

    @Autowired
    private final PublisherRepository publisherRepository;

    @Autowired
    private final DeveloperRepository developerRepository;

    public GameService(RestTemplate restTemplate, GameRepository gameRepository,
                       PublisherRepository publisherRepository,
                       DeveloperRepository developerRepository) {
        this.restTemplate = restTemplate;
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

    public GameDto addNewGame(Game game) {
        Optional<Game> gameByName = gameRepository.findGameByName(game.getName());
        if (gameByName.isPresent()) {
            throw new IllegalStateException("game name taken");
        }

        if (game.getPublisher() != null) {
            String publisherName = game.getPublisher().getName();
            Optional<Publisher> publisher = publisherRepository.findPublisherByName(publisherName).or(() ->
                    Optional.of(publisherRepository.save(game.getPublisher())));
            publisher.get().addGameToPublishedGames(game);
        }

        if (game.getDeveloper() != null) {
            String developerName = game.getDeveloper().getName();
            Optional<Developer> developer = developerRepository.findDeveloperByName(developerName).or(() ->
                    Optional.of(developerRepository.save(game.getDeveloper())));
            developer.get().addGameToDevelopedGames(game);
        }

        return GameMapper.convert(gameRepository.save(game));
    }

    public void deleteGame(Long gameId) {
        boolean exists = gameRepository.existsById(gameId);
        if (!exists) {
            throw new IllegalStateException("game with id " + gameId + " does not exist");
        }
        gameRepository.deleteById(gameId);
    }

    public GameDto updateGame(Long gameId, GameDto gameDto) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new IllegalStateException(
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

        if (gameDto.getBackgroundImageUrl() != null && !Objects.equals(game.getBackgroundImageUrl(), gameDto.getBackgroundImageUrl())) {
            game.setBackgroundImageUrl(gameDto.getBackgroundImageUrl());
        }
        return GameMapper.convert(gameRepository.save(game));
    }

    private FreeToGameDto[] fetchFreeToGames() {

        ResponseEntity<FreeToGameDto[]> responseFreeToGames = restTemplate.getForEntity(FREETOGAMES_URL, FreeToGameDto[].class);
        return responseFreeToGames.getBody();
    }

    private RawgGameDto[] fetchRawg() {

        ResponseEntity<RawgDto> responseRawgGames = restTemplate.getForEntity(RAWGGAMES_URL, RawgDto.class);
        return Objects.requireNonNull(responseRawgGames.getBody()).getResults();
    }

    public BaseGameDto[] fetchGames(String provider) throws InvalidProvider {

        BaseGameDto[] baseGameDto;

        if (provider.equalsIgnoreCase("freetogame")) {
            baseGameDto = fetchFreeToGames();
        } else if (provider.equalsIgnoreCase("rawg")) {
            baseGameDto = fetchRawg();
        } else {
            throw new InvalidProvider("Invalid Provider");
        }

        return baseGameDto;
    }

    public List<GameDto> updateGamesRawg() {

        ResponseEntity<RawgDto> responseRawgGames = restTemplate.getForEntity(RAWGGAMES_URL, RawgDto.class);
        RawgDto rawgDto = responseRawgGames.getBody();
        List<GameDto> gameDtos = new ArrayList<>();

        if (rawgDto != null) {
            RawgGameDto[] rawgGameDtos = rawgDto.getResults();
            for (RawgGameDto rawgGameDto : rawgGameDtos) {
                Optional<Game> optionalGame = gameRepository.findGameByName(rawgGameDto.getName());
                if (optionalGame.isPresent()) {
                    Game game = optionalGame.get();
                    game.setBackgroundImageUrl(rawgGameDto.getBackground_image());
                    game.setReleaseDate(LocalDate.parse(rawgGameDto.getReleased()));
                    game.setUserReviews(rawgGameDto.getRating());
                    gameRepository.save(game);
                    gameDtos.add(GameMapper.convert(game));
                } else {
                    gameDtos.add(createGame(rawgGameDto.getReleased(),
                            rawgGameDto.getRating(), rawgGameDto.getBackground_image()));
                }
            }
        }
        return gameDtos;
    }

    public List<GameDto> addGamesFromFreeToGame() {

        ResponseEntity<FreeToGameDto[]> response = restTemplate.getForEntity(FREETOGAMES_URL, FreeToGameDto[].class);
        FreeToGameDto[] freeToGameDto = response.getBody();
        List<GameDto> gameDtos = new ArrayList<>();

        if (freeToGameDto != null) {
            for (FreeToGameDto gameDto : freeToGameDto) {

                if (gameRepository.findGameByName(gameDto.getTitle()).isPresent()) {
                    continue;
                }

                Game game = new Game();
                game.setName(gameDto.getTitle());
                try {
                    game.setReleaseDate(LocalDate.parse(gameDto.getRelease_date()));
                } catch (DateTimeException e) {
                    game.setReleaseDate(null);
                }
                game.setBackgroundImageUrl(gameDto.getThumbnail());
                game.setGenre(gameDto.getGenre());

                if (gameDto.getShort_description().length() >= 255) {
                    game.setShortDescription(gameDto.getShort_description().substring(0,255));
                } else {
                    game.setShortDescription(gameDto.getShort_description());
                }

                Optional<Publisher> publisherOptional = publisherRepository.findPublisherByName(gameDto.getPublisher());
                if (publisherOptional.isEmpty()) {
                    Publisher publisher = new Publisher();
                    publisher.setName(gameDto.getPublisher());
                    game.setPublisher(publisher);
                }

                Optional<Developer> developerOptional = developerRepository.findDeveloperByName(gameDto.getDeveloper());
                if (developerOptional.isEmpty()) {
                    Developer developer = new Developer();
                    developer.setName(gameDto.getDeveloper());
                    game.setDeveloper(developer);
                }

                gameDtos.add(GameMapper.convert(game));
                addNewGame(game);
            }
        }
        return gameDtos;
    }

    public List<GameDto> addGamesFromProvider(String provider) throws InvalidProvider {

        List<GameDto> gameDtos;

        if (provider.equalsIgnoreCase("freetogame")) {
            gameDtos = addGamesFromFreeToGame();
        } else if (provider.equalsIgnoreCase("rawg")) {
            gameDtos = updateGamesRawg();
        } else {
            throw new InvalidProvider("Invalid Provider");
        }

        return gameDtos;
    }

    private GameDto createGame(String releaseDate, Double rating, String backgroundImage) {
        Game createdGame = new Game();
        createdGame.setReleaseDate(LocalDate.parse(releaseDate));
        createdGame.setUserReviews(rating);
        createdGame.setBackgroundImageUrl(backgroundImage);
        return addNewGame(createdGame);
    }
}

