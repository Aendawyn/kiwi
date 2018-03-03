package fr.aquillet.kiwi.ui.service.persistence;

import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.scenario.ScenarioCreatedEvent;
import fr.aquillet.kiwi.model.Application;
import fr.aquillet.kiwi.model.Scenario;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import fr.aquillet.kiwi.toolkit.jackson.JacksonUtil;
import fr.aquillet.kiwi.ui.service.application.IApplicationService;
import fr.aquillet.kiwi.ui.service.scenario.IScenarioService;

import javax.inject.Inject;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ScenarioPersistenceService implements IScenarioPersistenceService {

    private IPersistenceConfiguration configuration;
    private IApplicationPersistenceService applicationPersistenceService;
    private IApplicationService applicationService;
    private IScenarioService scenarioService;

    @Inject
    public void setDependencies(final IPersistenceConfiguration configuration, //
                                final IApplicationPersistenceService applicationPersistenceService, //
                                final IApplicationService applicationService, //
                                final IScenarioService scenarioService, //
                                final NotificationCenter notificationCenter) {
        this.configuration = configuration;
        this.applicationPersistenceService = applicationPersistenceService;
        this.applicationService = applicationService;
        this.scenarioService = scenarioService;

        notificationCenter.subscribe(Events.SCENARIO, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
    }

    @Override
    public List<Scenario> getApplicationScenarios(Application application) {
        return Optional.ofNullable(application) //
                .map(this::getScenariosDirectory) //
                .filter(File::exists) //
                .flatMap(folder -> Optional.ofNullable(folder.listFiles(file -> file.getName().endsWith(configuration.getFileExtension()))) //
                        .map(files -> Arrays.stream(files) //
                                .map(file -> JacksonUtil.read(file, Scenario.class)) //
                                .filter(Optional::isPresent) //
                                .map(Optional::get) //
                                .collect(Collectors.toList()))) //
                .orElseGet(Collections::emptyList);
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_IO)
    public void handle(ScenarioCreatedEvent event) {
        saveScenario(event.getScenario().getId());
    }

    private void saveScenario(UUID scenarioId) {
        Optional.ofNullable(applicationService.getCurrentApplication()).ifPresent(application -> saveScenario(application, scenarioId));
    }

    private void saveScenario(Application application, UUID scenarioId) {
        scenarioService.getScenarioById(scenarioId) //
                .ifPresent(scenario -> JacksonUtil.write(scenario, Scenario.class, new File(getScenariosDirectory(application), getScenarioFileName(scenario))));
    }

    private File getScenariosDirectory(Application application) {
        return new File(applicationPersistenceService.getApplicationDirectory(application), configuration.getScenariosDirectoryName());
    }

    private String getScenarioFileName(Scenario scenario) {
        return scenario.getId().toString() + configuration.getFileExtension();
    }


}
