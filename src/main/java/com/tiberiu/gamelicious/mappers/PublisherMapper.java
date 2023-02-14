package com.tiberiu.gamelicious.mappers;

import com.tiberiu.gamelicious.dto.PublisherDto;
import com.tiberiu.gamelicious.model.Publisher;

import java.util.ArrayList;
import java.util.List;

public class PublisherMapper {

    public static PublisherDto convert(Publisher publisher) {

        PublisherDto publisherDto = new PublisherDto();
        publisherDto.setId(publisher.getId());
        publisherDto.setName(publisher.getName());
        publisherDto.setEmail(publisher.getEmail());

        return publisherDto;
    }

    public static List<PublisherDto> convert(List<Publisher> publishers) {

        List<PublisherDto> publisherDtoList = new ArrayList<>();

        for (Publisher publisher : publishers) {
            publisherDtoList.add(convert(publisher));
        }
        return publisherDtoList;
    }
}
