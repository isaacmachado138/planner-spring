package com.example.planner.link;

import com.example.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LinkService {

    @Autowired
    private LinkRepository repository;

    public LinkService(LinkRepository repository) {
        this.repository = repository;
    }


    public LinkResponse registerLink(LinkRequestPayload payload, Trip trips){
        Link link = new Link(payload.title(), payload.url(), trips);

        repository.save(link);

        return new LinkResponse(link.getId());
    }

    public List<LinkData> getAllLinksFromTrip(UUID id) {
        return repository.findByTripId(id).stream().map(links -> new LinkData(
                links.getId(),
                links.getTitle(),
                links.getUrl()
        )).toList();
    }
}
