package fr.aquillet.kiwi.ui.service.persistence;

import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.campaign.*;
import fr.aquillet.kiwi.model.Application;
import fr.aquillet.kiwi.model.Campaign;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import fr.aquillet.kiwi.toolkit.jackson.JacksonUtil;
import fr.aquillet.kiwi.ui.service.application.IApplicationService;
import fr.aquillet.kiwi.ui.service.campaign.ICampaignService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class CampaignPersistenceService implements ICampaignPersistenceService {

    private IPersistenceConfiguration configuration;
    private ICampaignService campaignService;
    private IApplicationPersistenceService applicationPersistenceService;
    private IApplicationService applicationService;

    @Inject
    public void setDependencies(final IPersistenceConfiguration configuration, //
                                final ICampaignService campaignService,
                                final IApplicationPersistenceService applicationPersistenceService, //
                                final IApplicationService applicationService, //
                                final NotificationCenter notificationCenter) {
        this.configuration = configuration;
        this.campaignService = campaignService;
        this.applicationPersistenceService = applicationPersistenceService;
        this.applicationService = applicationService;

        notificationCenter.subscribe(Events.CAMPAIGN, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
    }

    @Override
    public List<Campaign> getApplicationCampaigns(Application application) {
        return Optional.ofNullable(application) //
                .map(this::getCampaignsDirectory) //
                .filter(File::exists) //
                .flatMap(folder -> Optional.ofNullable(folder.listFiles(file -> file.getName().endsWith(configuration.getFileExtension()))) //
                        .map(files -> Arrays.stream(files) //
                                .map(file -> JacksonUtil.read(file, Campaign.class)) //
                                .filter(Optional::isPresent) //
                                .map(Optional::get) //
                                .collect(Collectors.toList()))) //
                .orElseGet(Collections::emptyList);
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_IO)
    public void handle(CampaignCreatedEvent event) {
        saveCampaign(event.getCampaign().getId());
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_IO)
    public void handle(CampaignScenariosReorderedEvent event) {
        saveCampaign(event.getCampaignId());
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_IO)
    public void handle(ScenarioAddedToCampaignEvent event) {
        saveCampaign(event.getCampaignId());
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_IO)
    public void handle(ScenarioRemovedFromCampaignEvent event) {
        saveCampaign(event.getCampaignId());
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_IO)
    public void handle(CampaignDeletedEvent event) {
        deleteCampaign(event.getId());
    }

    private void saveCampaign(UUID campaignId) {
        Optional.ofNullable(applicationService.getCurrentApplication()).ifPresent(application -> saveCampaign(application, campaignId));
    }

    private void deleteCampaign(UUID campaignId) {
        Optional.ofNullable(applicationService.getCurrentApplication()).ifPresent(application -> deleteCampaign(application, campaignId));
    }

    private void saveCampaign(Application application, UUID campaignId) {
        campaignService.getCampaignById(campaignId) //
                .ifPresent(campaign -> JacksonUtil.write(campaign, Campaign.class, new File(getCampaignsDirectory(application), getCampaignFileName(campaign))));
    }

    private void deleteCampaign(Application application, UUID campaignId) {
        File file = new File(getCampaignsDirectory(application), getCampaignFileName(campaignId));
        if (!file.delete()) {
            log.error("Unable to delete file {}", file.getAbsolutePath());
        }
    }

    private File getCampaignsDirectory(Application application) {
        return new File(applicationPersistenceService.getApplicationDirectory(application), configuration.getCampaignsDirectoryName());
    }

    private String getCampaignFileName(Campaign campaign) {
        return getCampaignFileName(campaign.getId());
    }

    private String getCampaignFileName(UUID campaignId) {
        return campaignId.toString() + configuration.getFileExtension();
    }


}
