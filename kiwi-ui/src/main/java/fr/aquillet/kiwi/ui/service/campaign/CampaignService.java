package fr.aquillet.kiwi.ui.service.campaign;

import fr.aquillet.kiwi.model.Campaign;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CampaignService implements ICampaignService {

    private List<Campaign> campaigns = new ArrayList<>();

    @Override
    public List<Campaign> getCampaigns() {
        return campaigns;
    }

    @Override
    public Campaign createCampaign(String title, Optional<UUID> labelId) {
        return new Campaign(UUID.randomUUID(), title, labelId, new ArrayList<>());
    }

    @Override
    public Optional<Campaign> getCampaignById(UUID id) {
        return campaigns.stream() //
                .filter(campaign -> campaign.getId().equals(id)) //
                .findFirst();
    }
}
