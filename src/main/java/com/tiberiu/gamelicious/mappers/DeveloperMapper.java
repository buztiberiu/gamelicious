package com.tiberiu.gamelicious.mappers;

import com.tiberiu.gamelicious.dto.DeveloperDto;
import com.tiberiu.gamelicious.model.Developer;

import java.util.ArrayList;
import java.util.List;

public class DeveloperMapper {

    public static DeveloperDto convert(Developer developer) {

        DeveloperDto developerDto = new DeveloperDto();
        developerDto.setId(developer.getId());
        developerDto.setName(developer.getName());
        developerDto.setEmail(developer.getEmail());

        return developerDto;
    }

    public static List<DeveloperDto> convert(List<Developer> developers) {

        List<DeveloperDto> developerDtoList = new ArrayList<>();

        for (Developer developer : developers) {
            developerDtoList.add(convert(developer));
        }
        return developerDtoList;
    }
}
