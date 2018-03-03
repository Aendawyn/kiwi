package fr.aquillet.kiwi.ui.view.dashboard.application;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.event.launcher.LauncherTitleUpdatedEvent;
import fr.aquillet.kiwi.model.Application;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.application.UpdateApplicationTitleCommand;
import fr.aquillet.kiwi.command.label.ReloadLabelsCommand;
import fr.aquillet.kiwi.command.launcher.ReloadLaunchersCommand;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.label.LabelCreatedEvent;
import fr.aquillet.kiwi.event.label.LabelsReloadedEvent;
import fr.aquillet.kiwi.event.launcher.LauncherCreatedEvent;
import fr.aquillet.kiwi.event.launcher.LaunchersReloadedEvent;
import fr.aquillet.kiwi.ui.service.application.IApplicationService;
import fr.aquillet.kiwi.ui.service.label.ILabelService;
import fr.aquillet.kiwi.ui.service.launcher.ILauncherService;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import fr.aquillet.kiwi.ui.view.label.LabelListViewModel;
import fr.aquillet.kiwi.ui.view.launcher.LauncherListViewModel;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import java.util.stream.Collectors;

public class DashboardApplicationViewModel implements ViewModel {

    private StringProperty applicationId = new SimpleStringProperty();
    private StringProperty applicationTitle = new SimpleStringProperty();
    private ObservableList<LauncherListViewModel> launchers = FXCollections.observableArrayList();
    private ObservableList<LabelListViewModel> labels = FXCollections.observableArrayList();

    @Inject
    private IApplicationService applicationService;
    @Inject
    private ILauncherService launcherService;
    @Inject
    private ILabelService labelService;
    @Inject
    private NotificationCenter notificationCenter;

    public void initialize() {
        Application currentApplication = applicationService.getCurrentApplication();
        applicationId.set(currentApplication.getId().toString());
        applicationTitle.set(currentApplication.getTitle());

        applicationTitle.addListener((obs, oldValue, newValue) -> {
            notificationCenter.publish(Commands.APPLICATION, //
                    new UpdateApplicationTitleCommand(currentApplication.getId(), newValue));
        });

        notificationCenter.subscribe(Events.LAUNCHER, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
        notificationCenter.publish(Commands.LAUNCHER, new ReloadLaunchersCommand());

        notificationCenter.subscribe(Events.LABEL, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
        notificationCenter.publish(Commands.LABEL, new ReloadLabelsCommand());
    }

    public ReadOnlyStringProperty applicationIdProperty() {
        return applicationId;
    }

    public StringProperty applicationTitleProperty() {
        return applicationTitle;
    }

    public ObservableList<LauncherListViewModel> launchersProperty() {
        return launchers;
    }

    public ObservableList<LabelListViewModel> labelsProperty() {
        return labels;
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
    public void handle(LauncherTitleUpdatedEvent event) {
        reloadLaunchers();
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(LabelCreatedEvent event) {
        reloadLabels();
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(LabelsReloadedEvent event) {
        reloadLabels();
    }

    private void reloadLaunchers() {
        launchers.setAll(launcherService.getLaunchers().stream() //
                .map(LauncherListViewModel::new) //
                .collect(Collectors.toList()));
    }

    private void reloadLabels() {
        labels.setAll(labelService.getLabels().stream() //
                .map(LabelListViewModel::new) //
                .collect(Collectors.toList()));
    }

}
