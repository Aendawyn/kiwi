package fr.aquillet.kiwi.event.campaign;

import lombok.Value;

import java.util.UUID;

@Value
public class ScenarioRemovedFromCampaignEvent {

    private final UUID campaignId;
    private final UUID scenarioId;
    
}
