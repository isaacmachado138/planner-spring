package com.example.planner.participants;

import com.example.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    @Autowired //injeção de dependencia
    private ParticipantRepository repository;
    /****** POST Routes ******/

    /*Confirm Trip: call when participant confirm your participation in a trip. Insert data of her*/
    @PostMapping("/{id}/confirm")
    public ResponseEntity<Participant> confirmTrip(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload){
        Optional<Participant> participantReturn = this.repository.findById(id);

        if(participantReturn.isPresent()){
            Participant participantUpd = participantReturn.get();
            participantUpd.setIsConfirmed(true);
            participantUpd.setName(payload.name());

            this.repository.save(participantUpd);

            return ResponseEntity.ok(participantUpd);
        }
        return ResponseEntity.notFound().build();
    }
}
