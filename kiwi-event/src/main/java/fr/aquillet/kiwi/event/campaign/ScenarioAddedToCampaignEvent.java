package fr.aquillet.kiwi.event.campaign;

import lombok.Value;

import java.util.UUID;

@Value
public class ScenarioAddedToCampaignEvent {

    private final UUID campaignId;
    private final UUID scenarioId;

}
