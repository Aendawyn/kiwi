package fr.aquillet.kiwi.ui.service.persistence;

import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.launcher.*;
import fr.aquillet.kiwi.model.Application;
import fr.aquillet.kiwi.model.Launcher;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import fr.aquillet.kiwi.toolkit.jackson.JacksonUtil;
import fr.aquillet.kiwi.ui.service.application.IApplicationService;
import fr.aquillet.kiwi.ui.service.launcher.ILauncherService;

import javax.inject.Inject;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class LauncherPersistenceService implements ILauncherPersistenceService {

    private IPersistenceConfiguration configuration;
    private IApplicationPersistenceService applicationPersistenceService;
    private IApplicationService applicationService;
    private ILauncherService launcherService;

    @Inject
    public void setDependencies(final IPersistenceConfiguration configuration, //
                                final IApplicationPersistenceService applicationPersistenceService, //
                                final IApplicationService applicationService, //
                                final ILauncherService launcherService, //
                                final NotificationCenter notificationCenter) {
        this.configuration = configuration;
        this.applicationPersistenceService = applicationPersistenceService;
        this.applicationService = applicationService;
        this.launcherService = launcherService;

        notificationCenter.subscribe(Events.LAUNCHER, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
    }

    @Override
    public List<Launcher> getApplicationLaunchers(Application application) {
        return Optional.ofNullable(application) //
                .map(this::getLaunchersDirectory) //
                .filter(File::exists) //
                .flatMap(folder -> Optional.ofNullable(folder.listFiles(file -> file.getName().endsWith(configuration.getFileExtension()))) //
                        .map(files -> Arrays.stream(files) //
                                .map(file -> JacksonUtil.read(file, Launcher.class)) //
                                .filter(Optional::isPresent) //
                                .map(Optional::get) //
                                .collect(Collectors.toList()))) //
                .orElseGet(Collections::emptyList);
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_IO)
    public void handle(LauncherCreatedEvent event) {
        saveLauncher(event.getLaucher().getId());
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_IO)
    public void handle(LauncherTitleUpdatedEvent event) {
        saveLauncher(event.getLauncherId());
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_IO)
    public void handle(LauncherCommandUpdatedEvent event) {
        saveLauncher(event.getId());
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_IO)
    public void handle(LauncherWorkingDirectoryUpdatedEvent event) {
        saveLauncher(event.getId());
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_IO)
    public void handle(LauncherStartDelayUpdatedEvent event) {
        saveLauncher(event.getId());
    }

    private void saveLauncher(UUID launcherId) {
        Optional.ofNullable(applicationService.getCurrentApplication()).ifPresent(application -> saveLauncher(application, launcherId));
    }

    private void saveLauncher(Application application, UUID launcherId) {
        launcherService.getLauncherById(launcherId) //
                .ifPresent(launcher -> JacksonUtil.write(launcher, Launcher.class, new File(getLaunchersDirectory(application), getLauncherFileName(launcher))));
    }

    private File getLaunchersDirectory(Application application) {
        return new File(applicationPersistenceService.getApplicationDirectory(application), configuration.getLaunchersDirectoryName());
    }

    private String getLauncherFileName(Launcher launcher) {
        return launcher.getId().toString() + configuration.getFileExtension();
    }

}
