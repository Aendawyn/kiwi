package fr.aquillet.kiwi.ui.service.campaign;

import fr.aquillet.kiwi.model.Campaign;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ICampaignService {

    List<Campaign> getCampaigns();

    Campaign createCampaign(String title, Optional<UUID> labelId);

    Optional<Campaign> getCampaignById(UUID id);

}
