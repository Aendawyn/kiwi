package fr.aquillet.kiwi.ui.service.notification;

import fr.aquillet.kiwi.model.Campaign;
import fr.aquillet.kiwi.model.CampaignExecutionResult;
import fr.aquillet.kiwi.model.ScenarioExecutionResult;
import io.reactivex.Completable;

public interface IExecutionNotificationService {

    Completable showNotificationFor(ScenarioExecutionResult result);

    Completable showNotificationFor(Campaign campaign, ScenarioExecutionResult result, int scenarioNumber);

    Completable showNotificationFor(Campaign campaign, CampaignExecutionResult result);
}
