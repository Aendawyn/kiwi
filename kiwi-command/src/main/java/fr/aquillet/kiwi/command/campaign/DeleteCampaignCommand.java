package fr.aquillet.kiwi.command.campaign;

import lombok.Value;

import java.util.UUID;

@Value
public class DeleteCampaignCommand {

    private final UUID campaignId;

}
