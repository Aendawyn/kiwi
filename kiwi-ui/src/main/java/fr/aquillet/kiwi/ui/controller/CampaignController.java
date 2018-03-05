package fr.aquillet.kiwi.ui.controller;

import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.campaign.CreateCampaignCommand;
import fr.aquillet.kiwi.command.campaign.ReloadCampaignsCommand;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.campaign.CampaignCreatedEvent;
import fr.aquillet.kiwi.event.campaign.CampaignsReloadedEvent;
import fr.aquillet.kiwi.model.Campaign;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import fr.aquillet.kiwi.ui.service.application.IApplicationService;
import fr.aquillet.kiwi.ui.service.campaign.ICampaignService;
import fr.aquillet.kiwi.ui.service.persistence.ICampaignPersistenceService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class CampaignController {

    private NotificationCenter notificationCenter;
    private ICampaignService campaignService;
    private IApplicationService applicationService;
    private ICampaignPersistenceService persistenceService;

    @Inject
    private void setDependencies(final NotificationCenter notificationCenter, //
                                 ICampaignService campaignService, //
                                 IApplicationService applicationService, //
                                 ICampaignPersistenceService persistenceService) {
        this.notificationCenter = notificationCenter;
        this.campaignService = campaignService;
        this.applicationService = applicationService;
        this.persistenceService = persistenceService;
        notificationCenter.subscribe(Commands.CAMPAIGN, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
    public void handle(CreateCampaignCommand command) {
        Campaign campaign = campaignService.createCampaign(command.getTitle(), command.getLabelId());
        log.info("Creating a new campaign (id: {}, title: {})", campaign.getId(), campaign.getTitle());
        campaignService.getCampaigns().add(campaign);
        notificationCenter.publish(Events.CAMPAIGN, new CampaignCreatedEvent(campaign));
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
    public void handle(ReloadCampaignsCommand command) {
        log.info("Reloading campaigns");
        campaignService.getCampaigns().clear();
        campaignService.getCampaigns().addAll(persistenceService.getApplicationCampaigns(applicationService.getCurrentApplication()));
        notificationCenter.publish(Events.CAMPAIGN, new CampaignsReloadedEvent());
    }
}
