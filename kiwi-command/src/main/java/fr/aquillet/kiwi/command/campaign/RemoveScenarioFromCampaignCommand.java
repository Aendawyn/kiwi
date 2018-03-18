package fr.aquillet.kiwi.command.campaign;

import lombok.Value;

import java.util.UUID;

@Value
public class RemoveScenarioFromCampaignCommand {

    private final UUID campaignId;
    private final UUID scenarioId;

}
