package fr.aquillet.kiwi.command.campaign;

import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class ReorderCampaignScenariosCommand {

    private final UUID campaignId;
    private final List<UUID> scenarioIds;

}
