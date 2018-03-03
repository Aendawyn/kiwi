package fr.aquillet.kiwi.ui.service.persistence;

import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.application.ApplicationCreatedEvent;
import fr.aquillet.kiwi.event.application.ApplicationTitleUpdatedEvent;
import fr.aquillet.kiwi.model.Application;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import fr.aquillet.kiwi.toolkit.jackson.JacksonUtil;
import fr.aquillet.kiwi.ui.service.application.IApplicationService;

import javax.inject.Inject;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationPersistenceService implements IApplicationPersistenceService {

    private IPersistenceConfiguration configuration;
    private IApplicationService applicationService;

    @Inject
    public void setDependencies(final IPersistenceConfiguration configuration, //
                                final IApplicationService applicationService, //
                                final NotificationCenter notificationCenter) {
        this.configuration = configuration;
        this.applicationService = applicationService;

        notificationCenter.subscribe(Events.APPLICATION, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
    }

    @Override
    public List<Application> getApplications() {
        return Optional.ofNullable(getApplicationsDirectory().listFiles(file -> file.getName().endsWith(configuration.getFileExtension()))) //
                .map(files -> Arrays.stream(files) //
                        .map(file -> JacksonUtil.read(file, Application.class)) //
                        .filter(Optional::isPresent) //
                        .map(Optional::get) //
                        .collect(Collectors.toList())) //
                .orElseGet(Collections::emptyList);
    }

    @Override
    public File getApplicationDirectory(UUID applicationId) {
        return new File(getApplicationsDirectory(), applicationId.toString());
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_IO)
    public void handle(ApplicationCreatedEvent event) {
        saveApplication(event.getApplication().getId());
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_IO)
    public void handle(ApplicationTitleUpdatedEvent event) {
        saveApplication(event.getId());
    }

    private void saveApplication(UUID applicationId) {
        applicationService.getApplicationById(applicationId) //
                .ifPresent(application -> JacksonUtil.write(application, Application.class, new File(getApplicationsDirectory(), getApplicationFileName(application))));
    }

    private File getApplicationsDirectory() {
        return new File(configuration.getRepositoryPath(), configuration.getApplicationsDirectoryName());
    }

    private String getApplicationFileName(Application application) {
        return application.getId().toString() + configuration.getFileExtension();
    }
}
