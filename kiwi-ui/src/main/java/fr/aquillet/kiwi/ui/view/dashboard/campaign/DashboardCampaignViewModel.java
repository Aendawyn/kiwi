package fr.aquillet.kiwi.ui.view.dashboard.campaign;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.campaign.ReloadCampaignsCommand;
import fr.aquillet.kiwi.command.launcher.ReloadLaunchersCommand;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.campaign.*;
import fr.aquillet.kiwi.event.label.LabelColorUpdatedEvent;
import fr.aquillet.kiwi.event.label.LabelTitleUpdatedEvent;
import fr.aquillet.kiwi.event.launcher.LauncherCreatedEvent;
import fr.aquillet.kiwi.event.launcher.LaunchersReloadedEvent;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import fr.aquillet.kiwi.toolkit.rx.RxUtils;
import fr.aquillet.kiwi.ui.service.campaign.ICampaignService;
import fr.aquillet.kiwi.ui.service.executor.ICampaignExecutorService;
import fr.aquillet.kiwi.ui.service.launcher.ILauncherService;
import fr.aquillet.kiwi.ui.view.campaign.CampaignViewModel;
import fr.aquillet.kiwi.ui.view.label.LabelListViewModel;
import fr.aquillet.kiwi.ui.view.launcher.LauncherListViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class DashboardCampaignViewModel implements ViewModel {

    private ObservableList<CampaignViewModel> campaigns = FXCollections.observableArrayList();
    private ObservableList<LauncherListViewModel> launchers = FXCollections.observableArrayList();
    private ObjectProperty<LauncherListViewModel> selectedLauncher = new SimpleObjectProperty<>();
    private BooleanProperty runCampaignPrecondition = new SimpleBooleanProperty();

    @Inject
    private ILauncherService launcherService;
    @Inject
    private ICampaignService campaignService;
    @Inject
    private ICampaignExecutorService campaignExecutorService;
    @Inject
    private NotificationCenter notificationCenter;

    public void initialize() {
        runCampaignPrecondition.bind(selectedLauncher.isNotNull());
        notificationCenter.subscribe(Events.LAUNCHER, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
        notificationCenter.publish(Commands.LAUNCHER, new ReloadLaunchersCommand());
        notificationCenter.subscribe(Events.CAMPAIGN, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
        notificationCenter.publish(Commands.CAMPAIGN, new ReloadCampaignsCommand());
        notificationCenter.subscribe(Events.LABEL, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
    }

    public ObservableList<CampaignViewModel> campaignsProperty() {
        return campaigns;
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(LauncherCreatedEvent event) {
        reloadLaunchers();
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(LaunchersReloadedEvent event) {
        reloadLaunchers();
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

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(CampaignDeletedEvent event) {
        reloadCampaigns();
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(LabelTitleUpdatedEvent event) {
        genericLabelUpdate(event.getLabelId(), labelModel -> labelModel.titleProperty().set(event.getTitle()));
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(LabelColorUpdatedEvent event) {
        genericLabelUpdate(event.getLabelId(), labelModel -> labelModel.colorProperty().set(event.getColor()));
    }

    private void genericLabelUpdate(UUID labelId, Consumer<LabelListViewModel> labelModelUpdater) {
        campaigns.stream() //
                .map(campaignViewModel -> campaignViewModel.labelProperty().get()) //
                .filter(Optional::isPresent) //
                .map(Optional::get) //
                .filter(labelListViewModel -> labelListViewModel.idProperty().get().equals(labelId)) //
                .forEach(labelModelUpdater);
    }

    public Command runCampaignCommand(CampaignViewModel campaign) {
        return new DelegateCommand(() -> new Action() {

            @Override
            protected void action() {
                double speed = 1d;
                campaignExecutorService.executeCampaign(selectedLauncher.get().idProperty().get(), campaign.idProperty().get(), speed)
                        .subscribe(RxUtils.nothingToDo(), RxUtils.logError(log));
            }

        }, runCampaignPrecondition, true);
    }


    public ObjectProperty<LauncherListViewModel> selectedLauncherProperty() {
        return selectedLauncher;
    }

    public ObservableList<LauncherListViewModel> launchersProperty() {
        return launchers;
    }

    private void reloadCampaigns() {
        campaigns.setAll(campaignService.getCampaigns().stream() //
                .map(CampaignViewModel::new) //
                .collect(Collectors.toList()));
    }

    private void reloadLaunchers() {
        launchers.setAll(launcherService.getLaunchers().stream() //
                .map(LauncherListViewModel::new) //
                .collect(Collectors.toList()));
    }

}
