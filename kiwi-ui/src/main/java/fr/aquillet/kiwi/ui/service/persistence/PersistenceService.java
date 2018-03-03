package fr.aquillet.kiwi.ui.service.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.model.Application;
import fr.aquillet.kiwi.model.Label;
import fr.aquillet.kiwi.model.Launcher;
import fr.aquillet.kiwi.model.Scenario;
import fr.aquillet.kiwi.ui.configuration.GlobalConfiguration;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.application.ApplicationCreatedEvent;
import fr.aquillet.kiwi.event.application.ApplicationTitleUpdatedEvent;
import fr.aquillet.kiwi.event.label.LabelCreatedEvent;
import fr.aquillet.kiwi.event.launcher.LauncherCreatedEvent;
import fr.aquillet.kiwi.event.scenario.ScenarioCreatedEvent;
import fr.aquillet.kiwi.ui.service.application.IApplicationService;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class PersistenceService implements IPersistenceService {

    private static final String CONFIG_REPOSITORY_PATH_KEY = "service.persistence.repository.path";
    private static final String JSON_EXT = ".json";
    private final ObjectMapper mapper = new ObjectMapper();
    private IApplicationService applicationService;
    private File applicationsDir;

    @Inject
    private void setDependencies(final NotificationCenter notificationCenter, //
                                 final IApplicationService applicationService) {
        this.applicationService = applicationService;
        initFileSystemPersistence();
        notificationCenter.subscribe(Events.APPLICATION, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
        notificationCenter.subscribe(Events.LAUNCHER, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
        notificationCenter.subscribe(Events.LABEL, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
        notificationCenter.subscribe(Events.SCENARIO, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
    }

    @Override
    public List<Application> getApplications() {
        return Arrays.stream(applicationsDir.listFiles(file -> file.getName().endsWith(JSON_EXT))) //
                .map(file -> read(file, Application.class)) //
                .filter(Optional::isPresent) //
                .map(Optional::get) //
                .collect(Collectors.toList());
    }

    @Override
    public List<Launcher> getLaunchersForCurrentApplication() {
        return Optional.ofNullable(applicationService.getCurrentApplication()) //
                .map(Stream::of) //
                .orElseGet(Stream::empty) //
                .map(this::getLauncherFolder) //
                .filter(File::exists) //
                .flatMap(folder -> Arrays.stream(folder.listFiles(file -> file.getName().endsWith(JSON_EXT)))) //
                .map(file -> read(file, Launcher.class)) //
                .filter(Optional::isPresent) //
                .map(Optional::get) //
                .collect(Collectors.toList());
    }

    @Override
    public List<Label> getLabelsForCurrentApplication() {
        return Optional.ofNullable(applicationService.getCurrentApplication()) //
                .map(Stream::of) //
                .orElseGet(Stream::empty) //
                .map(this::getLabelFolder) //
                .filter(File::exists) //
                .flatMap(folder -> Arrays.stream(folder.listFiles(file -> file.getName().endsWith(JSON_EXT)))) //
                .map(file -> read(file, Label.class)) //
                .filter(Optional::isPresent) //
                .map(Optional::get) //
                .collect(Collectors.toList());
    }

    @Override
    public List<Scenario> getScenariosForCurrentApplication() {
        return Optional.ofNullable(applicationService.getCurrentApplication()) //
                .map(Stream::of) //
                .orElseGet(Stream::empty) //
                .map(this::getScenarioFolder) //
                .filter(File::exists) //
                .flatMap(folder -> Arrays.stream(folder.listFiles(file -> file.getName().endsWith(JSON_EXT)))) //
                .map(file -> read(file, Scenario.class)) //
                .filter(Optional::isPresent) //
                .map(Optional::get) //
                .collect(Collectors.toList());
    }

    /* ******** Applications ******** */

    @Dispatch
    public void handle(ApplicationCreatedEvent event) {
        write(event.getApplication(), Application.class,
                new File(applicationsDir, getApplicationFileName(event.getApplication())));
    }

    @Dispatch
    public void handle(ApplicationTitleUpdatedEvent event) {
        applicationService.getApplicationById(event.getId()) //
                .ifPresent(application -> {
                    write(application, Application.class,
                            new File(applicationsDir, getApplicationFileName(application)));
                });
    }

    private String getApplicationFileName(Application application) {
        return application.getId().toString() + JSON_EXT;
    }

    /* ******** Launchers ******** */

    @Dispatch
    public void handle(LauncherCreatedEvent event) {
        Application currentApplication = applicationService.getCurrentApplication();
        write(event.getLaucher(), Launcher.class,
                new File(getLauncherFolder(currentApplication), getLauncherFileName(event.getLaucher())));
    }

    private File getLauncherFolder(Application application) {
        return new File(getApplicationFolder(application), "launchers");
    }

    private File getApplicationFolder(Application application) {
        return new File(applicationsDir, application.getId().toString());
    }

    private String getLauncherFileName(Launcher launcher) {
        return launcher.getId().toString() + JSON_EXT;
    }

    /* ******** Labels ******** */

    @Dispatch
    public void handle(LabelCreatedEvent event) {
        Application currentApplication = applicationService.getCurrentApplication();
        write(event.getLabel(), Label.class,
                new File(getLabelFolder(currentApplication), getLabelFileName(event.getLabel())));
    }

    private File getLabelFolder(Application application) {
        return new File(getApplicationFolder(application), "labels");
    }

    private String getLabelFileName(Label label) {
        return label.getId().toString() + JSON_EXT;
    }

    /* ******** Scenarios ******** */

    @Dispatch
    public void handle(ScenarioCreatedEvent event) {
        Application currentApplication = applicationService.getCurrentApplication();
        write(event.getScenario(), Scenario.class,
                new File(getScenarioFolder(currentApplication), getScenarioFileName(event.getScenario())));
    }

    private File getScenarioFolder(Application application) {
        return new File(getApplicationFolder(application), "scenarios");
    }

    private String getScenarioFileName(Scenario scenario) {
        return scenario.getId().toString() + JSON_EXT;
    }

    private void initFileSystemPersistence() {
        File repositoryDir = GlobalConfiguration.getFileValue(CONFIG_REPOSITORY_PATH_KEY);
        applicationsDir = new File(repositoryDir, "applications");
        applicationsDir.mkdirs();
        mapper //
                .registerModule(new Jdk8Module()) //
                .registerModule(new JavaTimeModule());
    }

    private void write(Object object, Class<?> type, File dest) {
        try {
            dest.getParentFile().mkdirs();
            mapper.writerWithDefaultPrettyPrinter().forType(type).writeValue(dest, object);
        } catch (IOException e) {
            log.error("Unable to persist object {}", object);
            log.error("Details", e);
        }
    }

    private <T> Optional<T> read(File source, Class<T> type) {
        try {
            return Optional.ofNullable(mapper.readerFor(type).readValue(source));
        } catch (IOException e) {
            log.error("Unable to read object of type {} in file {}", type, source);
            log.error("Details", e);
            return Optional.empty();
        }
    }

}
