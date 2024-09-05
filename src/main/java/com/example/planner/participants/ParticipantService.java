package com.example.planner.participants;

import com.example.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {

    @Autowired //injeção de dependencia
    private ParticipantRepository participantRepository;

    public void registerParticipantsToEvent(List<String> participantsToInvite, Trip trip){

        List<Participant> participants = participantsToInvite.stream().map(email -> new Participant(email, trip)).toList();

        this.participantRepository.saveAll(participants);

        System.out.println(participants.getFirst().getId());

    }

    public ParticipantCreateResponse registerParticipantToEvent(String email, Trip trip){
        Participant participant = new Participant(email, trip);

        this.participantRepository.save(participant);

        return new ParticipantCreateResponse(participant.getId().toString());
    }

    public void triggerConfirmationEmailToParticipants(UUID tripId){

    }

    public void triggerConfirmationEmailToParticipant(String email){

    }

    public List<ParticipantData> getAllParticipantsFromTrip(UUID tripId){
        return this.participantRepository.findByTripId(tripId).stream().map(participant -> new ParticipantData(participant.getId(), participant.getName(), participant.getEmail(), participant.getIsConfirmed())).toList();
    }
}
