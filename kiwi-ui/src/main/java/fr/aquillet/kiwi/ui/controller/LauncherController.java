package fr.aquillet.kiwi.ui.controller;

import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.model.Launcher;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.launcher.CreateLauncherCommand;
import fr.aquillet.kiwi.command.launcher.ReloadLaunchersCommand;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.launcher.LauncherCreatedEvent;
import fr.aquillet.kiwi.event.launcher.LaunchersReloadedEvent;
import fr.aquillet.kiwi.ui.service.launcher.ILauncherService;
import fr.aquillet.kiwi.ui.service.persistence.IPersistenceService;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class LauncherController {

	private NotificationCenter notificationCenter;
	private ILauncherService launcherService;
	private IPersistenceService persistenceService;

	@Inject
	private void setDependencies(final NotificationCenter notificationCenter, //
			ILauncherService launcherService, //
			IPersistenceService persistenceService) {
		this.notificationCenter = notificationCenter;
		this.launcherService = launcherService;
		this.persistenceService = persistenceService;
		notificationCenter.subscribe(Commands.LAUNCHER, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
	}

	@Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
	public void handle(ReloadLaunchersCommand command) {
		log.info("Reloading launchers");
		launcherService.getLaunchers().clear();
		launcherService.getLaunchers().addAll(persistenceService.getLaunchersForCurrentApplication());
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

}
