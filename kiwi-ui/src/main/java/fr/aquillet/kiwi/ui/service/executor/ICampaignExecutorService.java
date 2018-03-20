package fr.aquillet.kiwi.ui.service.executor;

import io.reactivex.Completable;

import java.util.UUID;

public interface ICampaignExecutorService {

    Completable executeCampaign(UUID launcherId, UUID campaignId, double speedFactor);
}
