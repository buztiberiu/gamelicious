package com.tiberiu.gamelicious.controller;

import com.tiberiu.gamelicious.dto.PublisherDto;
import com.tiberiu.gamelicious.model.Publisher;
import com.tiberiu.gamelicious.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publishers")
public class PublisherController {

    @Autowired
    private PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping
    public List<PublisherDto> getPublishers() {
        return publisherService.getPublishers();
    }

    @PostMapping
    public void addNewPublisher(@RequestBody Publisher publisher) {
        publisherService.addNewPublisher(publisher);
    }

    @DeleteMapping(path = "{publisherId}")
    public void deletePublisher(@PathVariable("publisherId") Long publisherId) {
        publisherService.deletePublisher(publisherId);
    }

    @PutMapping(path = "{publisherId}")
    public void updatePublisher(@PathVariable("publisherId") Long publisherId,
                                @RequestParam(required = false) String name,
                                @RequestParam(required = false) String email) {
        publisherService.updatePublisher(publisherId, name, email);
    }
}
