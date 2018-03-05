package fr.aquillet.kiwi.ui.service.persistence;

import fr.aquillet.kiwi.model.Application;
import fr.aquillet.kiwi.model.Campaign;

import java.util.List;

public interface ICampaignPersistenceService {

    List<Campaign> getApplicationCampaigns(Application application);
}
