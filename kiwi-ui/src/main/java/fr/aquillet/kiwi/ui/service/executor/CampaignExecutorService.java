package fr.aquillet.kiwi.ui.service.executor;

import com.google.common.collect.ImmutableList;
import fr.aquillet.kiwi.jna.JnaService;
import fr.aquillet.kiwi.model.*;
import fr.aquillet.kiwi.ui.service.campaign.ICampaignService;
import fr.aquillet.kiwi.ui.service.configuration.IExecutionConfiguration;
import fr.aquillet.kiwi.ui.service.launcher.ILauncherService;
import fr.aquillet.kiwi.ui.service.notification.IExecutionNotificationService;
import fr.aquillet.kiwi.ui.service.persistence.ICampaignExecutionResultPersistenceService;
import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class CampaignExecutorService implements ICampaignExecutorService {

    private static final double SUCCESS_SCORE = 100D;

    private ILauncherService launcherService;
    private ICampaignService campaignService;
    private IScenarioExecutorService scenarioExecutorService;
    private JnaService jnaService;
    private ICampaignExecutionResultPersistenceService resultPersistenceService;
    private IExecutionConfiguration executionConfiguration;
    private IExecutionNotificationService executionNotificationService;

    @Inject
    public void setDependencies(final ILauncherService launcherService, //
                                final ICampaignService campaignService, //
                                final IScenarioExecutorService scenarioExecutorService, //
                                final JnaService jnaService,
                                final ICampaignExecutionResultPersistenceService resultPersistenceService, //
                                final IExecutionConfiguration executionConfiguration, //
                                final IExecutionNotificationService executionNotificationService) {
        this.launcherService = launcherService;
        this.campaignService = campaignService;
        this.scenarioExecutorService = scenarioExecutorService;
        this.jnaService = jnaService;
        this.resultPersistenceService = resultPersistenceService;
        this.executionConfiguration = executionConfiguration;
        this.executionNotificationService = executionNotificationService;
    }

    @Override
    public Observable<CampaignExecutionResult> executeCampaign(UUID launcherId, UUID campaignId, double speedFactor) {
        return Observable.defer(() -> {
            Optional<Launcher> launcherOpt = launcherService.getLauncherById(launcherId);
            Optional<Campaign> campaignOpt = campaignService.getCampaignById(campaignId);
            if (!launcherOpt.isPresent()) {
                log.error("Launcher {} not found", launcherId);
                return Observable.error(new NoSuchElementException("launcher"));
            }
            if (!campaignOpt.isPresent()) {
                log.error("Campaign {} not found", campaignId);
                return Observable.error(new NoSuchElementException("campaign"));
            }
            Launcher launcher = launcherOpt.get();
            Campaign campaign = campaignOpt.get();

            List<Observable<ScenarioExecutionResult>> steps = new ArrayList<>();
            for (int i = 0; i < campaign.getScenarioIds().size(); i++) {
                UUID scenarioId = campaign.getScenarioIds().get(i);
                int scenarioNumber = i + 1;
                steps.add(scenarioExecutorService.executeScenario(launcherId, scenarioId, speedFactor, true) //
                        .concatMap(result ->
                                executionNotificationService.showNotificationFor(campaign, result, scenarioNumber) //
                                        .<ScenarioExecutionResult>toObservable() //
                                        .concatWith(Observable.just(result) //
                                                .delay(executionConfiguration.getScenarioNotificationDurationSeconds(), TimeUnit.SECONDS)) //
                        ));
            }

            return jnaService.runApplication(launcher.getCommand(), launcher.getWorkingDirectory(), launcher.getStartDelaySecond()) //
                    .flatMap(appProcess -> Observable.concat(steps) //
                            .scan(CampaignExecutionResult.builder() //
                                            .campaignId(campaignId) //
                                            .campaignLabel(campaign.getTitle()) //
                                            .status(ExecutionStatus.SUCCESS_PENDING) //
                                            .scenariosCount(campaign.getScenarioIds().size()) //
                                            .successRate(SUCCESS_SCORE) //
                                            .scenarioResults(Collections.emptyList()) //
                                            .startDate(Instant.now()) //
                                            .endDate(Instant.now()) //
                                            .build(), //
                                    (acc, result) -> {
                                        List<ScenarioExecutionResult> newResultsList = ImmutableList.<ScenarioExecutionResult>builder() //
                                                .addAll(acc.getScenarioResults()) //
                                                .add(result) //
                                                .build();
                                        double newSuccessRate = ((double) (newResultsList.stream()
                                                .collect(Collectors.partitioningBy(r -> r.getStatus().equals(ExecutionStatus.SUCCESS)))
                                                .get(Boolean.TRUE).size()) / (double) (newResultsList.size())) * SUCCESS_SCORE;
                                        ExecutionStatus newStatus = ExecutionStatus.SUCCESS;
                                        if (result.getStatus().equals(ExecutionStatus.ABORTED)) {
                                            newStatus = ExecutionStatus.ABORTED;
                                        } else {
                                            if (newSuccessRate == SUCCESS_SCORE) {
                                                if (newResultsList.size() == campaign.getScenarioIds().size()) {
                                                    newStatus = ExecutionStatus.SUCCESS;
                                                } else {
                                                    newStatus = ExecutionStatus.SUCCESS_PENDING;
                                                }
                                            } else {
                                                if (newResultsList.stream().allMatch(r -> r.getStatus().equals(ExecutionStatus.SUCCESS) || r.getStatus().equals(ExecutionStatus.UNKNOWN))) {
                                                    newStatus = ExecutionStatus.UNKNOWN;
                                                } else {
                                                    newStatus = ExecutionStatus.FAILURE;
                                                }
                                            }
                                        }

                                        return acc.toBuilder() //
                                                .scenarioResults(newResultsList) //
                                                .status(newStatus) //
                                                .successRate(newSuccessRate) //
                                                .endDate(Instant.now()) //
                                                .build();
                                    }) //
                            .takeUntil((Predicate<? super CampaignExecutionResult>) campaignExecutionResult -> campaignExecutionResult.getStatus().equals(ExecutionStatus.ABORTED)) //
                            .takeLast(1) //
                            .doOnNext(resultPersistenceService::save) //
                            .concatMap(campaignExecutionResult -> executionNotificationService.showNotificationFor(campaign, campaignExecutionResult) //
                                    .<CampaignExecutionResult>toObservable() //
                                    .concatWith(Observable.just(campaignExecutionResult))) //
                            .doOnTerminate(appProcess::destroyForcibly));
        });
    }


}
