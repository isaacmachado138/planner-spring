package com.example.planner.trip;

import com.example.planner.participants.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    @Autowired //injeção de dependencia
    private ParticipantRepository participantRepository;

    /****** POST Routes ******/

    //Inserting trip
    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) {

        Trip newTrip = new Trip(payload);

        this.repository.save(newTrip);

        this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()) );

    }

    //Invite participant on trip
    @PostMapping("{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload) {

        Optional<Trip> tripReturn = this.repository.findById(id);

        if(tripReturn.isPresent()){
            Trip rawTrip = tripReturn.get();

            ParticipantCreateResponse participantResponse = participantService.registerParticipantToEvent(payload.email(), rawTrip);

            if(rawTrip.getIsConfirmed()){
                participantService.triggerConfirmationEmailToParticipant(payload.email());
            }
            return ResponseEntity.ok(participantResponse);
        }
        return ResponseEntity.notFound().build();

    }

    /****** GET Routes ******/

    // Getting trip by ID
    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable UUID id){
        Optional<Trip> tripReturn = this.repository.findById(id);  //parametro opcional recebe resultado, essa resposta pode ou nao existir

        return tripReturn.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Getting all trips
    @GetMapping
    public ResponseEntity<List<Trip>> getAllTrips(){
        List<Trip> trips = this.repository.findAll();

        if (trips.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(trips);
        }
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantData>> getParticipantsByTripId(@PathVariable UUID id){
        Optional<Trip> tripReturn = this.repository.findById(id);

        if(tripReturn.isPresent()){

            return ResponseEntity.ok(this.participantService.getAllParticipantsFromTrip(id));

        }
        return ResponseEntity.notFound().build();
    }

    // Confirm trip by ID
    @GetMapping("/{id}/confirm")
    ResponseEntity<Trip> confirmTrip(@PathVariable UUID id){
        Optional<Trip> tripReturn = this.repository.findById(id);

        if(tripReturn.isPresent()){
            Trip tripUpd = tripReturn.get();
            tripUpd.setIsConfirmed(true);
            this.repository.save(tripUpd);

            this.participantService.triggerConfirmationEmailToParticipants(id);

            return ResponseEntity.ok(tripUpd);
        }
        return ResponseEntity.notFound().build();
    }

    /****** PUT Routes ******/

    // Putting trip by ID
    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTripById(@PathVariable UUID id, @RequestBody TripRequestPayload payload){
        Optional<Trip> tripReturn = this.repository.findById(id);  //parametro opcional recebe resultado, essa resposta pode ou nao existir

        if(tripReturn.isPresent()){
            Trip tripUpd = tripReturn.get();//pega o que foi retornado pelo metodo
            tripUpd.setDestination(payload.destination());
            tripUpd.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            tripUpd.setEndsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            this.repository.save(tripUpd);

            return ResponseEntity.ok(tripUpd);

        }
        return ResponseEntity.notFound().build();
    }
}