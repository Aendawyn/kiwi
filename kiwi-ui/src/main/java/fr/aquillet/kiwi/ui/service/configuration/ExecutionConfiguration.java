package fr.aquillet.kiwi.ui.service.configuration;

import fr.aquillet.kiwi.ui.configuration.GlobalConfiguration;

public class ExecutionConfiguration implements IExecutionConfiguration {

    private static final String KEY_CAMPAIGN_NOTIF_DURATION_S = "execution.result.campaign.notification.duration.s";
    private static final String KEY_SCENARIO_NOTIF_DURATION_S = "execution.result.scenario.notification.duration.s";

    @Override
    public int getCampaignNotificationDurationSeconds() {
        return GlobalConfiguration.getIntValue(KEY_CAMPAIGN_NOTIF_DURATION_S);
    }

    @Override
    public int getScenarioNotificationDurationSeconds() {
        return GlobalConfiguration.getIntValue(KEY_SCENARIO_NOTIF_DURATION_S);
    }
}
