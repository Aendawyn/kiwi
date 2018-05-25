package fr.aquillet.kiwi.ui.service.notification;

import com.google.common.collect.ImmutableList;
import fr.aquillet.kiwi.model.Campaign;
import fr.aquillet.kiwi.model.CampaignExecutionResult;
import fr.aquillet.kiwi.model.ExecutionStatus;
import fr.aquillet.kiwi.model.ScenarioExecutionResult;
import fr.aquillet.kiwi.toolkit.number.DoubleFormatter;
import fr.aquillet.kiwi.toolkit.ui.notification.NotificationUtil;
import fr.aquillet.kiwi.ui.service.configuration.IExecutionConfiguration;
import io.reactivex.Completable;

import javax.inject.Inject;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExecutionNotificationService implements IExecutionNotificationService {

    private IExecutionConfiguration executionConfiguration;

    @Inject
    public void setDependencies(final IExecutionConfiguration executionConfiguration) {
        this.executionConfiguration = executionConfiguration;
    }

    @Override
    public Completable showNotificationFor(ScenarioExecutionResult result) {
        return Completable.defer(() -> {
            String title = result.getScenarioLabel();
            String status = "Status: " + executionStatusToLabel(result.getStatus());
            String score = "Similitude: " + DoubleFormatter.format2Digits(result.getScore()) + "% (seuil: " + result.getToleranceThreshold() + "%)";
            List<String> content = Arrays.asList(status, score);

            showNotification(title, content, executionConfiguration.getScenarioNotificationDurationSeconds(), result.getStatus());
            return Completable.complete();
        });
    }

    @Override
    public Completable showNotificationFor(Campaign campaign, ScenarioExecutionResult result, int scenarioNumber) {
        return Completable.defer(() -> {
            String title = campaign.getTitle();
            String header = result.getScenarioLabel() + " (" + scenarioNumber + "/" + campaign.getScenarioIds().size() + ")";
            String status = "Status: " + executionStatusToLabel(result.getStatus());
            String score = "Similitude: " + DoubleFormatter.format2Digits(result.getScore()) + "% (seuil: " + result.getToleranceThreshold() + "%)";
            List<String> content = Arrays.asList(header, score, status);

            showNotification(title, content, executionConfiguration.getScenarioNotificationDurationSeconds(), result.getStatus());
            return Completable.complete();
        });
    }

    @Override
    public Completable showNotificationFor(Campaign campaign, CampaignExecutionResult result) {
        return Completable.defer(() -> {
            Duration campaignDuration = Duration.between(result.getStartDate(), result.getEndDate());
            String title = campaign.getTitle();
            String duration = "Durée: " + campaignDuration.toMinutes() + "m" + (campaignDuration.getSeconds() - (campaignDuration.toMinutes() * 60)) + "s";
            String scenarios = "Scenarios: " + result.getScenarioResults().size() + "/" + result.getScenariosCount()  //
                    + "  (" + result.getScenarioResults().stream().filter(r -> r.getStatus().equals(ExecutionStatus.FAILURE)).count() + " échec(s), " //
                    + result.getScenarioResults().stream().filter(r -> r.getStatus().equals(ExecutionStatus.UNKNOWN)).count() + " inconnus)";
            String status = "Status: " + executionStatusToLabel(result.getStatus());
            List<String> content = Arrays.asList(duration, scenarios, status);

            showNotification(title, content, executionConfiguration.getCampaignNotificationDurationSeconds(), result.getStatus());
            return Completable.complete();
        });
    }

    private void showNotification(String title, List<String> content, int durationS, ExecutionStatus status) {
        if (isExecutionInSuccess(status)) {
            NotificationUtil.showInfoNotification(title, content, durationS);
        }
        if (isExecutionInFailure(status)) {
            NotificationUtil.showErrorNotification(title, content, durationS);
        }
        if (isExecutionInWarning(status)) {
            NotificationUtil.showWarningNotification(title, content, durationS);
        }
    }

    private boolean isExecutionInSuccess(ExecutionStatus status) {
        return status.equals(ExecutionStatus.SUCCESS) || status.equals(ExecutionStatus.SUCCESS_PENDING);
    }

    private boolean isExecutionInFailure(ExecutionStatus status) {
        return status.equals(ExecutionStatus.FAILURE);
    }

    private boolean isExecutionInWarning(ExecutionStatus status) {
        return status.equals(ExecutionStatus.UNKNOWN) || status.equals(ExecutionStatus.ABORTED);
    }

    private String executionStatusToLabel(ExecutionStatus status) {
        switch (status) {
            case SUCCESS:
            case SUCCESS_PENDING:
                return "succès";
            case FAILURE:
                return "échec";
            case ABORTED:
                return "abandonné";
            case UNKNOWN:
                return "inconnu";
            default:
                return "NON DEFINI";
        }
    }
}
