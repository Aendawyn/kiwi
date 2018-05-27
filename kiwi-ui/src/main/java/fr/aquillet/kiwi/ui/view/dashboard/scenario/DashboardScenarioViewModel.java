package fr.aquillet.kiwi.ui.view.dashboard.scenario;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.launcher.ReloadLaunchersCommand;
import fr.aquillet.kiwi.command.scenario.ReloadScenariosCommand;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.label.LabelColorUpdatedEvent;
import fr.aquillet.kiwi.event.label.LabelTitleUpdatedEvent;
import fr.aquillet.kiwi.event.launcher.LauncherCreatedEvent;
import fr.aquillet.kiwi.event.launcher.LaunchersReloadedEvent;
import fr.aquillet.kiwi.event.scenario.ScenarioCreatedEvent;
import fr.aquillet.kiwi.event.scenario.ScenarioDeletedEvent;
import fr.aquillet.kiwi.event.scenario.ScenariosReloadedEvent;
import fr.aquillet.kiwi.model.ScenarioExecutionResult;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import fr.aquillet.kiwi.toolkit.rx.RxUtils;
import fr.aquillet.kiwi.ui.service.configuration.IExecutionConfiguration;
import fr.aquillet.kiwi.ui.service.executor.IScenarioExecutorService;
import fr.aquillet.kiwi.ui.service.launcher.ILauncherService;
import fr.aquillet.kiwi.ui.service.notification.IExecutionNotificationService;
import fr.aquillet.kiwi.ui.service.scenario.IScenarioService;
import fr.aquillet.kiwi.ui.view.label.LabelListViewModel;
import fr.aquillet.kiwi.ui.view.launcher.LauncherListViewModel;
import fr.aquillet.kiwi.ui.view.scenario.ScenarioViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class DashboardScenarioViewModel implements ViewModel {

    private ObservableList<ScenarioViewModel> scenarios = FXCollections.observableArrayList();
    private ObservableList<LauncherListViewModel> launchers = FXCollections.observableArrayList();
    private ObjectProperty<LauncherListViewModel> selectedLauncher = new SimpleObjectProperty<>();
    private DoubleProperty replaySpeed = new SimpleDoubleProperty();
    private BooleanProperty runScenarioPrecondition = new SimpleBooleanProperty();

    @Inject
    private ILauncherService launcherService;
    @Inject
    private IScenarioService scenarioService;
    @Inject
    private IScenarioExecutorService scenarioExecutorService;
    @Inject
    private NotificationCenter notificationCenter;
    @Inject
    private IExecutionNotificationService executionNotificationService;
    @Inject
    private IExecutionConfiguration executionConfiguration;


    public void initialize() {
        runScenarioPrecondition.bind(selectedLauncher.isNotNull());
        notificationCenter.subscribe(Events.LAUNCHER, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
        notificationCenter.publish(Commands.LAUNCHER, new ReloadLaunchersCommand());
        notificationCenter.subscribe(Events.SCENARIO, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
        notificationCenter.publish(Commands.SCENARIO, new ReloadScenariosCommand());
        notificationCenter.subscribe(Events.LABEL, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
    }

    public ObservableList<LauncherListViewModel> launchersProperty() {
        return launchers;
    }

    public ObjectProperty<LauncherListViewModel> selectedLauncherProperty() {
        return selectedLauncher;
    }

    public ObservableList<ScenarioViewModel> scenariosProperty() {
        return scenarios;
    }

    public DoubleProperty replaySpeedProperty() {
        return replaySpeed;
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(ScenarioCreatedEvent event) {
        reloadScenarios();
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(ScenariosReloadedEvent event) {
        reloadScenarios();
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(ScenarioDeletedEvent event) {
        reloadScenarios();
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
    public void handle(LabelTitleUpdatedEvent event) {
        genericLabelUpdate(event.getLabelId(), labelModel -> labelModel.titleProperty().set(event.getTitle()));
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(LabelColorUpdatedEvent event) {
        genericLabelUpdate(event.getLabelId(), labelModel -> labelModel.colorProperty().set(event.getColor()));
    }

    private void genericLabelUpdate(UUID labelId, Consumer<LabelListViewModel> labelModelUpdater) {
        scenarios.stream() //
                .map(scenarioViewModel -> scenarioViewModel.labelProperty().get()) //
                .filter(Optional::isPresent) //
                .map(Optional::get) //
                .filter(labelListViewModel -> labelListViewModel.idProperty().get().equals(labelId)) //
                .forEach(labelModelUpdater);
    }

    public Command runScenarioCommand(ScenarioViewModel scenario) {
        return new DelegateCommand(() -> new Action() {

            @Override
            protected void action() {
                double speed = replaySpeed.get();
                scenarioExecutorService.executeScenario(selectedLauncher.get().idProperty().get(), scenario.idProperty().get(), speed)
                        .concatMap(scenarioExecutionResult -> executionNotificationService.showNotificationFor(scenarioExecutionResult) //
                                .<ScenarioExecutionResult>toObservable() //
                                .delay(executionConfiguration.getScenarioNotificationDurationSeconds(), TimeUnit.SECONDS))
                        .subscribe(RxUtils.nothingToDo(), RxUtils.logError(log));
            }

        }, runScenarioPrecondition, true);
    }

    private void reloadLaunchers() {
        launchers.setAll(launcherService.getLaunchers().stream() //
                .map(LauncherListViewModel::new) //
                .collect(Collectors.toList()));
    }

    private void reloadScenarios() {
        scenarios.setAll(scenarioService.getScenarios().stream() //
                .map(ScenarioViewModel::new) //
                .collect(Collectors.toList()));
    }

}
