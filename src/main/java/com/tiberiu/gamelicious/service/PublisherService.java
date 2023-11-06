package com.tiberiu.gamelicious.service;

import com.tiberiu.gamelicious.dto.GameDto;
import com.tiberiu.gamelicious.dto.PublisherDto;
import com.tiberiu.gamelicious.dto.RawgDto;
import com.tiberiu.gamelicious.dto.RawgGameDto;
import com.tiberiu.gamelicious.exception.PublisherNotFoundException;
import com.tiberiu.gamelicious.mappers.PublisherMapper;
import com.tiberiu.gamelicious.model.Game;
import com.tiberiu.gamelicious.model.Publisher;
import com.tiberiu.gamelicious.repository.GameRepository;
import com.tiberiu.gamelicious.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PublisherService {

    public static final String RAWGPUBLISHERS_URL = "https://api.rawg.io/api/publishers?key=6ba20560f8b54e1095fab5945ca7ca2d";

    @Autowired
    private final PublisherRepository publisherRepository;

    @Autowired
    private final GameRepository gameRepository;

    public PublisherService(PublisherRepository publisherRepository,
                            GameRepository gameRepository) {
        this.publisherRepository = publisherRepository;
        this.gameRepository = gameRepository;
    }

    public PublisherDto getOnePublisher(String name) throws PublisherNotFoundException {
        Optional<Publisher> publisherOptional = publisherRepository.findPublisherByName(name);

        if (publisherOptional.isPresent()) {
            return PublisherMapper.convert(publisherOptional.get());
        }

        throw new PublisherNotFoundException("Publisher not found");
    }

    public List<PublisherDto> getPublishers() {
        return PublisherMapper.convert(publisherRepository.findAll());
    }

    public void addNewPublisher(Publisher publisher) {
        Optional<Publisher> publisherByName = publisherRepository.findPublisherByName(publisher.getName());
        if (publisherByName.isPresent()) {
            throw new IllegalStateException("publisher name taken");
        }
        List<Game> games = publisher.getPublishedGames();
        //need to also do with list

        publisherRepository.save(publisher);
    }

    public void deletePublisher(Long publisherId) {
        boolean exists = publisherRepository.existsById(publisherId);
        if (!exists) {
            throw new IllegalStateException("publisher with id " + publisherId + " does not exist");
        }
        publisherRepository.deleteById(publisherId);
    }

    public void updatePublisher(PublisherDto publisherDto) {
        Publisher publisher = publisherRepository.findById(publisherDto.getId()).orElseThrow(() -> new IllegalStateException(
                        "publisher with id " + publisherDto.getId() + " does not exist"));

        if (publisherDto.getName() != null && publisherDto.getName().length() > 0 && !Objects.equals(publisher.getName(), publisherDto.getName())) {
            Optional<Publisher> publisherOptional = publisherRepository.findPublisherByName(publisherDto.getName());
            if (publisherOptional.isPresent()) {
                throw new IllegalStateException("name taken");
            }
            publisher.setName(publisherDto.getName());
        }

        if (publisherDto.getEmail() != null && publisherDto.getEmail().length() > 0 && !Objects.equals(publisher.getEmail(), publisherDto.getEmail())) {
            Optional<Publisher> publisherOptional = publisherRepository.findPublisherByEmail(publisherDto.getEmail());
            if (publisherOptional.isPresent()) {
                throw new IllegalStateException("email taken");
            }
            publisher.setEmail(publisherDto.getEmail());
        }
        publisherRepository.save(publisher);
    }

    public void addRawgPublishers() {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<RawgDto> responseRawgPublishers = restTemplate.getForEntity(RAWGPUBLISHERS_URL, RawgDto.class);
        RawgDto publishersRawg = responseRawgPublishers.getBody();

        if (publishersRawg != null) {
            RawgGameDto[] rawgPublishers = publishersRawg.getResults();

            for (RawgGameDto rawgPub : rawgPublishers) {

                for (GameDto gameDto : rawgPub.getGames()) {
                    Optional<Game> gameByName = gameRepository.findGameByName(gameDto.getName());
                    if (gameByName.isEmpty()) {
                        Game game = new Game();
                        game.setName(gameDto.getName());
                        Optional<Publisher> publisherOptional = publisherRepository.findPublisherByName(rawgPub.getName());
                        if (publisherOptional.isPresent()) {
                            if (publisherOptional.get().getBackgroundImageUrl() == null) {
                                publisherOptional.get().setBackgroundImageUrl(rawgPub.getImage_background());
                            }
                            publisherOptional.get().addGameToPublishedGames(game);
                        } else {
                            createPublisher(rawgPub.getName(), rawgPub.getImage_background(), game);
                        }
                    } else {
                        if (gameByName.get().getPublisher() == null) {
                            createPublisher(rawgPub.getName(), rawgPub.getBackground_image(), gameByName.get());
                        }
                    }
                }
            }
        }
    }

    private void createPublisher(String name, String backgroundImage, Game game) {
        Publisher publisher = new Publisher();
        publisher.setName(name);
        publisher.setBackgroundImageUrl(backgroundImage);
        publisher.addGameToPublishedGames(game);
        publisherRepository.save(publisher);
    }

}
