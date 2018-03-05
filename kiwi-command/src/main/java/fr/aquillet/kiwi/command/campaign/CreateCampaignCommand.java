package fr.aquillet.kiwi.command.campaign;

import lombok.Value;

import java.util.Optional;
import java.util.UUID;

@Value
public class CreateCampaignCommand {

    private final String title;
    private final Optional<UUID> labelId;

}
