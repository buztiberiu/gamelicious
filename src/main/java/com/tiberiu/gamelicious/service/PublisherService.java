package com.tiberiu.gamelicious.service;

import com.tiberiu.gamelicious.dto.PublisherDto;
import com.tiberiu.gamelicious.exception.PublisherNotFoundException;
import com.tiberiu.gamelicious.mappers.PublisherMapper;
import com.tiberiu.gamelicious.model.Game;
import com.tiberiu.gamelicious.model.Publisher;
import com.tiberiu.gamelicious.repository.GameRepository;
import com.tiberiu.gamelicious.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PublisherService {

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

        publisherRepository.save(publisher);
    }

    public void deletePublisher(Long publisherId) {
        boolean exists = publisherRepository.existsById(publisherId);
        if (!exists) {
            throw new IllegalStateException("publisher with id " + publisherId + " does not exist");
        }
        publisherRepository.deleteById(publisherId);
    }

    public void updatePublisher(Long publisherId, String name, String email) {
        Publisher publisher = publisherRepository.findById(publisherId).orElseThrow(() -> new IllegalStateException(
                        "publisher with id " + publisherId + " does not exist"));

        if (name != null && name.length() > 0 && !Objects.equals(publisher.getName(), name)) {
            Optional<Publisher> publisherOptional = publisherRepository.findPublisherByName(name);
            if (publisherOptional.isPresent()) {
                throw new IllegalStateException("name taken");
            }
            publisher.setName(name);
        }

        if (email != null && email.length() > 0 && !Objects.equals(publisher.getEmail(), email)) {
            Optional<Publisher> publisherOptional = publisherRepository.findPublisherByEmail(email);
            if (publisherOptional.isPresent()) {
                throw new IllegalStateException("email taken");
            }
            publisher.setEmail(email);
        }
        publisherRepository.save(publisher);
    }
}
