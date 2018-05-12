package fr.aquillet.kiwi.ui.service.executor;

import fr.aquillet.kiwi.model.CampaignExecutionResult;
import io.reactivex.Observable;

import java.util.UUID;

public interface ICampaignExecutorService {

    Observable<CampaignExecutionResult> executeCampaign(UUID launcherId, UUID campaignId, double speedFactor);
}
