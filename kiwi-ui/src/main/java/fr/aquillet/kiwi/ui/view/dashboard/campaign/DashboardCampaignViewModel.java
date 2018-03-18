package fr.aquillet.kiwi.ui.view.dashboard.campaign;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.campaign.ReloadCampaignsCommand;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.campaign.CampaignCreatedEvent;
import fr.aquillet.kiwi.event.campaign.CampaignsReloadedEvent;
import fr.aquillet.kiwi.event.campaign.ScenarioAddedToCampaignEvent;
import fr.aquillet.kiwi.event.campaign.ScenarioRemovedFromCampaignEvent;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import fr.aquillet.kiwi.ui.service.campaign.ICampaignService;
import fr.aquillet.kiwi.ui.view.campaign.CampaignViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.stream.Collectors;

@Slf4j
public class DashboardCampaignViewModel implements ViewModel {

    private ObservableList<CampaignViewModel> campaigns = FXCollections.observableArrayList();

    @Inject
    private ICampaignService campaignService;
    @Inject
    private NotificationCenter notificationCenter;

    public void initialize() {
        notificationCenter.subscribe(Events.CAMPAIGN, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
        notificationCenter.publish(Commands.CAMPAIGN, new ReloadCampaignsCommand());
    }

    public ObservableList<CampaignViewModel> campaignsProperty() {
        return campaigns;
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(CampaignCreatedEvent event) {
        reloadCampaigns();
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(CampaignsReloadedEvent event) {
        reloadCampaigns();
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(ScenarioAddedToCampaignEvent event) {
        reloadCampaigns();
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(ScenarioRemovedFromCampaignEvent event) {
        reloadCampaigns();
    }

    private void reloadCampaigns() {
        campaigns.setAll(campaignService.getCampaigns().stream() //
                .map(CampaignViewModel::new) //
                .collect(Collectors.toList()));
    }

}
