package com.example.planner.trip;

import com.example.planner.participants.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired //injeção de dependencia
    private ParticipantService participantService;

    @Autowired //injeção de dependencia
    private TripRepository repository;

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) {

        Trip newTrip = new Trip(payload);

        this.repository.save(newTrip);

        this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip.getId());

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()) );

    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable UUID id){
        Optional<Trip> tripReturn = this.repository.findById(id);  //parametro opcional recebe resultado, essa resposta pode ou nao existir

        return tripReturn.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Trip>> getAllTrips(){
        List<Trip> trips = this.repository.findAll();

        if (trips.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(trips);
        }
    }
}