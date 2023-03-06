package com.tiberiu.gamelicious.service;

import com.tiberiu.gamelicious.dto.DeveloperDto;
import com.tiberiu.gamelicious.exception.DeveloperNotFoundException;
import com.tiberiu.gamelicious.mappers.DeveloperMapper;
import com.tiberiu.gamelicious.model.Developer;
import com.tiberiu.gamelicious.model.Publisher;
import com.tiberiu.gamelicious.repository.DeveloperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DeveloperService {

    @Autowired
    private final DeveloperRepository developerRepository;

    public DeveloperService(DeveloperRepository developerRepository) {
        this.developerRepository = developerRepository;
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

}


