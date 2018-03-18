package fr.aquillet.kiwi.command.campaign;

import lombok.Value;

import java.util.UUID;

@Value
public class AddScenarioToCampaignCommand {

    private final UUID campaignId;
    private final UUID scenarioId;

}
