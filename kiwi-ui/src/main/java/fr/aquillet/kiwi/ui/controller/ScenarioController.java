package fr.aquillet.kiwi.ui.controller;

import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.scenario.CreateScenarioCommand;
import fr.aquillet.kiwi.command.scenario.ReloadScenariosCommand;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.scenario.ScenarioCreatedEvent;
import fr.aquillet.kiwi.event.scenario.ScenariosReloadedEvent;
import fr.aquillet.kiwi.model.Scenario;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import fr.aquillet.kiwi.ui.service.application.IApplicationService;
import fr.aquillet.kiwi.ui.service.persistence.IScenarioPersistenceService;
import fr.aquillet.kiwi.ui.service.scenario.IScenarioService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class ScenarioController {

    private NotificationCenter notificationCenter;
    private IScenarioService scenarioService;
    private IApplicationService applicationService;
    private IScenarioPersistenceService persistenceService;

    @Inject
    private void setDependencies(final NotificationCenter notificationCenter, //
                                 IScenarioService scenarioService, //
                                 IApplicationService applicationService, //
                                 IScenarioPersistenceService persistenceService) {
        this.notificationCenter = notificationCenter;
        this.scenarioService = scenarioService;
        this.applicationService = applicationService;
        this.persistenceService = persistenceService;
        notificationCenter.subscribe(Commands.SCENARIO, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
    public void handle(ReloadScenariosCommand command) {
        log.info("Reloading scenarios");
        scenarioService.getScenarios().clear();
        scenarioService.getScenarios().addAll(persistenceService.getApplicationScenarios(applicationService.getCurrentApplication()));
        notificationCenter.publish(Events.SCENARIO, new ScenariosReloadedEvent());
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
    public void handle(CreateScenarioCommand command) {
        Scenario scenario = scenarioService.createScenario(command.getTitle(), command.getLabelId(),
                command.getEvents(), command.getEndCapture());
        log.info("Creating new scenario (id: {}, title: {}, label: {}, events count: {})", //
                scenario.getId(), scenario.getTitle(), scenario.getLabelId().orElse("none"),
                scenario.getEvents().size());
        scenarioService.getScenarios().add(scenario);
        notificationCenter.publish(Events.SCENARIO, new ScenarioCreatedEvent(scenario));
    }

}
