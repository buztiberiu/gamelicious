package com.tiberiu.gamelicious.controller;

import com.tiberiu.gamelicious.dto.DeveloperDto;
import com.tiberiu.gamelicious.model.Developer;
import com.tiberiu.gamelicious.service.DeveloperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    @Autowired
    private DeveloperService developerService;

    public DeveloperController(DeveloperService developerService) {
        this.developerService = developerService;
    }

    @GetMapping
    public List<DeveloperDto> getDevelopers() {
        return developerService.getDevelopers();
    }

    @PostMapping
    public void addNewDeveloper(@RequestBody Developer developer) {
        developerService.addNewDeveloper(developer);
    }

    @DeleteMapping(path = "{developerId}")
    public void deleteDeveloper(@PathVariable("developerId") Long developerId) {
        developerService.deleteDeveloper(developerId);
    }

    @PutMapping(path = "{developerId}")
    public void updateDeveloper(@PathVariable("developerId") Long developerId,
                                @RequestParam(required = false) String name,
                                @RequestParam(required = false) String email) {
        developerService.updateDeveloper(developerId, name, email);
    }
}
