package fr.aquillet.kiwi.ui.controller;

import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.launcher.*;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.launcher.*;
import fr.aquillet.kiwi.model.Launcher;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import fr.aquillet.kiwi.ui.service.application.IApplicationService;
import fr.aquillet.kiwi.ui.service.launcher.ILauncherService;
import fr.aquillet.kiwi.ui.service.persistence.ILauncherPersistenceService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class LauncherController {

    private NotificationCenter notificationCenter;
    private ILauncherService launcherService;
    private IApplicationService applicationService;
    private ILauncherPersistenceService persistenceService;

    @Inject
    private void setDependencies(final NotificationCenter notificationCenter, //
                                 ILauncherService launcherService, //
                                 IApplicationService applicationService, //
                                 ILauncherPersistenceService persistenceService) {
        this.notificationCenter = notificationCenter;
        this.launcherService = launcherService;
        this.applicationService = applicationService;
        this.persistenceService = persistenceService;
        notificationCenter.subscribe(Commands.LAUNCHER, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
    public void handle(ReloadLaunchersCommand command) {
        log.info("Reloading launchers");
        launcherService.getLaunchers().clear();
        launcherService.getLaunchers().addAll(persistenceService.getApplicationLaunchers(applicationService.getCurrentApplication()));
        notificationCenter.publish(Events.LAUNCHER, new LaunchersReloadedEvent());
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
    public void handle(CreateLauncherCommand command) {
        Launcher launcher = launcherService.createLauncher(command.getTitle(), command.getCommand(),
                command.getWindowTitle(), command.getWindowClass(), command.getWorkingDirectory(),
                command.getStartDelaySecond());
        log.info("Creating new launcher (id: {}, title: {}, window title: {}, window class: {})", //
                launcher.getId(), launcher.getTitle(), launcher.getWindowTitle(), launcher.getWindowClass());
        launcherService.getLaunchers().add(launcher);
        notificationCenter.publish(Events.LAUNCHER, new LauncherCreatedEvent(launcher));
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
    public void handle(UpdateLauncherTitleCommand command) {
        launcherService.getLauncherById(command.getId()) //
                .ifPresent(launcher -> {
                    log.info("Updating launcher title (id: {}, new title: {})", command.getId(), command.getTitle());
                    launcher.setTitle(command.getTitle());
                    notificationCenter.publish(Events.LAUNCHER, new LauncherTitleUpdatedEvent(command.getId(), command.getTitle()));
                });
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
    public void handle(UpdateLauncherCommandCommand command) {
        launcherService.getLauncherById(command.getId()) //
                .ifPresent(launcher -> {
                    log.info("Updating launcher command (id: {}, new command: {})", command.getId(), command.getCommand());
                    launcher.setCommand(command.getCommand());
                    notificationCenter.publish(Events.LAUNCHER, new LauncherCommandUpdatedEvent(command.getId(), command.getCommand()));
                });
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
    public void handle(UpdateLauncherWorkingDirectoryCommand command) {
        launcherService.getLauncherById(command.getId()) //
                .ifPresent(launcher -> {
                    log.info("Updating launcher working directory (id: {}, new working directory: {})", command.getId(), command.getWorkingDirectory());
                    launcher.setWorkingDirectory(command.getWorkingDirectory());
                    notificationCenter.publish(Events.LAUNCHER, new LauncherWorkingDirectoryUpdatedEvent(command.getId(), command.getWorkingDirectory()));
                });
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
    public void handle(UpdateLauncherStartDelayCommand command) {
        launcherService.getLauncherById(command.getId()) //
                .ifPresent(launcher -> {
                    log.info("Updating launcher start delay (id: {}, new start delay: {})", command.getId(), command.getStartDelay());
                    launcher.setStartDelaySecond(command.getStartDelay());
                    notificationCenter.publish(Events.LAUNCHER, new LauncherStartDelayUpdatedEvent(command.getId(), command.getStartDelay()));
                });
    }

}
