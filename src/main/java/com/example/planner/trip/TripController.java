package com.example.planner.trip;

import com.example.planner.activities.*;
import com.example.planner.link.LinkData;
import com.example.planner.link.LinkRequestPayload;
import com.example.planner.link.LinkResponse;
import com.example.planner.link.LinkService;
import com.example.planner.participants.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private ActivityService activityService;

    @Autowired
    private LinkService linkService;

    /************ Endpoints of TRIP ************/

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




    /************ Endpoints of PARTICIPANTS ************/


    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantData>> getParticipantsByTripId(@PathVariable UUID id){
        Optional<Trip> tripReturn = this.repository.findById(id);

        if(tripReturn.isPresent()){

            return ResponseEntity.ok(this.participantService.getAllParticipantsFromTrip(id));

        }
        return ResponseEntity.notFound().build();
    }

    /************ Endpoints of ACTIVITIES ************/

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID id){
        List<ActivityData> activityDataList = this.activityService.getAllActivitiesFromId(id);

        if (activityDataList.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(activityDataList);
        }
    }

    //Create activity on trip
    @PostMapping("{id}/activities")
    public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id, @RequestBody ActivityRequestPayload payload) {

        Optional<Trip> tripReturn = this.repository.findById(id);

        if(tripReturn.isPresent()){
            Trip rawTrip = tripReturn.get();

            ActivityResponse activityResponse = this.activityService.saveActivity(payload, rawTrip);

            return ResponseEntity.ok(activityResponse);
        }
        return ResponseEntity.notFound().build();
    }

    /************ Endpoints of LINKS ************/

    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayload payload){
        Optional<Trip> trip = repository.findById(id);

        if (trip.isPresent()){
            Trip newTrip = trip.get();

            LinkResponse response = linkService.registerLink(payload, newTrip);


            return ResponseEntity.ok(response);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinkData>> getAllinks(@PathVariable UUID id){
        List<LinkData> linkData = linkService.getAllLinksFromTrip(id);

        return ResponseEntity.ok(linkData);
    }


}