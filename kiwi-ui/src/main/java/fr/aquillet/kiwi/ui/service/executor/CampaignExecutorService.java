package fr.aquillet.kiwi.ui.service.executor;

import fr.aquillet.kiwi.jna.JnaService;
import fr.aquillet.kiwi.model.Campaign;
import fr.aquillet.kiwi.model.Launcher;
import fr.aquillet.kiwi.ui.service.campaign.ICampaignService;
import fr.aquillet.kiwi.ui.service.launcher.ILauncherService;
import io.reactivex.Completable;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class CampaignExecutorService implements ICampaignExecutorService {

    private ILauncherService launcherService;
    private ICampaignService campaignService;
    private IScenarioExecutorService scenarioExecutorService;
    private JnaService jnaService;

    @Inject
    public void setDependencies(final ILauncherService launcherService, //
                                final ICampaignService campaignService, //
                                final IScenarioExecutorService scenarioExecutorService, //
                                final JnaService jnaService) {
        this.launcherService = launcherService;
        this.campaignService = campaignService;
        this.scenarioExecutorService = scenarioExecutorService;
        this.jnaService = jnaService;
    }

    @Override
    public Completable executeCampaign(UUID launcherId, UUID campaignId, double speedFactor) {
        return Completable.defer(() -> {
            Optional<Launcher> launcherOpt = launcherService.getLauncherById(launcherId);
            Optional<Campaign> campaignOpt = campaignService.getCampaignById(campaignId);
            if (!launcherOpt.isPresent()) {
                log.error("Launcher {} not found", launcherId);
                return Completable.error(new NoSuchElementException("launcher"));
            }
            if (!campaignOpt.isPresent()) {
                log.error("Campaign {} not found", campaignId);
                return Completable.error(new NoSuchElementException("campaign"));
            }
            Launcher launcher = launcherOpt.get();
            Campaign campaign = campaignOpt.get();

            List<Completable> steps = campaign.getScenarioIds().stream()
                    .map(scenarioId -> scenarioExecutorService.executeScenario(launcherId, scenarioId, speedFactor, true))
                    .collect(Collectors.toList());

            return jnaService.runApplication(launcher.getCommand(), launcher.getWorkingDirectory(), launcher.getStartDelaySecond()) //
                    .flatMapCompletable(appProcess -> Completable.concat(steps) //
                            .doOnTerminate(appProcess::destroyForcibly));
        });
    }


}
