package fr.aquillet.kiwi.event.campaign;

import lombok.Value;

import java.util.UUID;

@Value
public class CampaignDeletedEvent {

    private final UUID id;
}
