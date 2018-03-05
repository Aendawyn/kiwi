package fr.aquillet.kiwi.event.campaign;

import fr.aquillet.kiwi.model.Campaign;
import lombok.Value;

@Value
public class CampaignCreatedEvent {

    private final Campaign campaign;
}
