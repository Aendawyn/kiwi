package fr.aquillet.kiwi.event.campaign;

import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class CampaignScenariosReorderedEvent {

    private final UUID campaignId;
    private final List<UUID> scenarioIds;

}
