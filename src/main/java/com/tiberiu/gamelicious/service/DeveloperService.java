package com.tiberiu.gamelicious.service;

import com.tiberiu.gamelicious.dto.DeveloperDto;
import com.tiberiu.gamelicious.dto.GameDto;
import com.tiberiu.gamelicious.dto.RawgDto;
import com.tiberiu.gamelicious.dto.RawgGameDto;
import com.tiberiu.gamelicious.exception.DeveloperNotFoundException;
import com.tiberiu.gamelicious.mappers.DeveloperMapper;
import com.tiberiu.gamelicious.model.Developer;
import com.tiberiu.gamelicious.model.Game;
import com.tiberiu.gamelicious.model.Publisher;
import com.tiberiu.gamelicious.repository.DeveloperRepository;
import com.tiberiu.gamelicious.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DeveloperService {

    public static final String RAWGDEVELOPERS_URL = "https://api.rawg.io/api/developers?key=6ba20560f8b54e1095fab5945ca7ca2d";

    @Autowired
    private final DeveloperRepository developerRepository;

    @Autowired
    private final GameRepository gameRepository;

    public DeveloperService(DeveloperRepository developerRepository, GameRepository gameRepository) {
        this.developerRepository = developerRepository;
        this.gameRepository = gameRepository;
    }

    public DeveloperDto getOneDeveloper(String name) throws DeveloperNotFoundException {
        Optional<Developer> optionalDeveloper = developerRepository.findDeveloperByName(name);

        if (optionalDeveloper.isPresent()) {
            return DeveloperMapper.convert(optionalDeveloper.get());
        }
        throw new DeveloperNotFoundException("Developer not found");
    }

    public List<DeveloperDto> getDevelopers() {
        return DeveloperMapper.convert(developerRepository.findAll());
    }

    public void addNewDeveloper(Developer developer) {
        Optional<Developer> developerByName = developerRepository.findDeveloperByName(developer.getName());
        if (developerByName.isPresent()) {
            throw new IllegalStateException("developer name taken");
        }
        developerRepository.save(developer);
    }

    public void deleteDeveloper(Long developerId) {
        boolean exists = developerRepository.existsById(developerId);
        if (!exists) {
            throw new IllegalStateException("developer with id " + developerId + " does not exist");
        }
        developerRepository.deleteById(developerId);
    }

    public void updateDeveloper(DeveloperDto developerDto) {
        Developer developer = developerRepository.findById(developerDto.getId()).orElseThrow(() -> new IllegalStateException(
                "developer with id " + developerDto.getId() + " does not exist"));

        if (developerDto.getName() != null && developerDto.getName().length() > 0 && !Objects.equals(developer.getName(), developerDto.getName())) {
            Optional<Developer> developerOptional = developerRepository.findDeveloperByName(developerDto.getName());
            if (developerOptional.isPresent()) {
                throw new IllegalStateException("name taken");
            }
            developer.setName(developerDto.getName());
        }

        if (developerDto.getEmail() != null && developerDto.getEmail().length() > 0 && !Objects.equals(developer.getEmail(), developerDto.getEmail())) {
            Optional<Publisher> developerOptional = developerRepository.findPublisherByEmail(developerDto.getEmail());
            if (developerOptional.isPresent()) {
                throw new IllegalStateException("email taken");
            }
            developer.setEmail(developerDto.getEmail());
        }
        developerRepository.save(developer);
    }

    public void addRawgDevelopers() {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<RawgDto> responseRawgDevelopers = restTemplate.getForEntity(RAWGDEVELOPERS_URL, RawgDto.class);
        RawgDto developersRawg = responseRawgDevelopers.getBody();

        if (developersRawg != null) {
            RawgGameDto[] rawgDevelopers = developersRawg.getResults();
            for (RawgGameDto rawgDev : rawgDevelopers) {

                for (GameDto gameDto : rawgDev.getGames()) {
                    Optional<Game> gameByName = gameRepository.findGameByName(gameDto.getName());
                    if (gameByName.isEmpty()) {
                        Game game = new Game();
                        game.setName(gameDto.getName());
                        Optional<Developer> developerOptional = developerRepository.findDeveloperByName(rawgDev.getName());
                        if (developerOptional.isPresent()) {
                            if (developerOptional.get().getBackgroundImageUrl() == null) {
                                developerOptional.get().setBackgroundImageUrl(rawgDev.getImage_background());
                            }
                            developerOptional.get().addGameToDevelopedGames(game);
                        } else {
                            createDeveloper(rawgDev.getName(), rawgDev.getImage_background(), game);
                        }
                    } else {
                        if (gameByName.get().getDeveloper() == null) {
                            createDeveloper(rawgDev.getName(), rawgDev.getImage_background(), gameByName.get());
                        }
                    }
                }
            }
        }
    }

    private void createDeveloper(String name, String backgroundImage, Game game) {
        Developer developer = new Developer();
        developer.setName(name);
        developer.setBackgroundImageUrl(backgroundImage);
        developer.addGameToDevelopedGames(game);
        developerRepository.save(developer);
    }

}


