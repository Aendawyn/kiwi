package fr.aquillet.kiwi.ui.controller;

import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.model.Application;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.application.ChangeCurrentApplicationCommand;
import fr.aquillet.kiwi.command.application.CreateApplicationCommand;
import fr.aquillet.kiwi.command.application.ReloadApplicationsCommand;
import fr.aquillet.kiwi.command.application.UpdateApplicationTitleCommand;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.application.ApplicationCreatedEvent;
import fr.aquillet.kiwi.event.application.ApplicationTitleUpdatedEvent;
import fr.aquillet.kiwi.event.application.ApplicationsReloadedEvent;
import fr.aquillet.kiwi.event.application.CurrentApplicationChangedEvent;
import fr.aquillet.kiwi.ui.service.application.IApplicationService;
import fr.aquillet.kiwi.ui.service.persistence.IPersistenceService;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class ApplicationController {

    private NotificationCenter notificationCenter;
    private IApplicationService applicationService;
    private IPersistenceService persistenceService;

    @Inject
    private void setDependencies(final NotificationCenter notificationCenter, //
                                 IApplicationService applicationService, //
                                 IPersistenceService persistenceService) {
        this.notificationCenter = notificationCenter;
        this.applicationService = applicationService;
        this.persistenceService = persistenceService;
        notificationCenter.subscribe(Commands.APPLICATION, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
    public void handle(ReloadApplicationsCommand command) {
        log.info("Reloading applications");
        applicationService.getApplications().clear();
        applicationService.getApplications().addAll(persistenceService.getApplications());
        notificationCenter.publish(Events.APPLICATION, new ApplicationsReloadedEvent());
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
    public void handle(ChangeCurrentApplicationCommand command) {
        log.info("Changing current application (id: {})", command.getId());
        applicationService.setCurrentApplication(command.getId());
        notificationCenter.publish(Events.APPLICATION,
                new CurrentApplicationChangedEvent(applicationService.getCurrentApplication()));
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
    public void handle(CreateApplicationCommand command) {
        Application application = applicationService.createApplication(command.getTitle());
        log.info("Creating a new application (id: {}, title: {})", application.getId(), application.getTitle());
        applicationService.getApplications().add(application);
        notificationCenter.publish(Events.APPLICATION, new ApplicationCreatedEvent(application));
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
    public void handle(UpdateApplicationTitleCommand command) {
        applicationService.getApplicationById(command.getId()) //
                .ifPresent(application -> {
                    log.info("Updating application title (id: {}, new title: {})", command.getId(), command.getTitle());
                    application.setTitle(command.getTitle());
                    notificationCenter.publish(Events.APPLICATION,
                            new ApplicationTitleUpdatedEvent(command.getId(), command.getTitle()));
                });
    }

}
