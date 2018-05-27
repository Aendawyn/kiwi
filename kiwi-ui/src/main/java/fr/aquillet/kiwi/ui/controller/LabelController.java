package fr.aquillet.kiwi.ui.controller;

import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.label.CreateLabelCommand;
import fr.aquillet.kiwi.command.label.ReloadLabelsCommand;
import fr.aquillet.kiwi.command.label.UpdateLabelColorCommand;
import fr.aquillet.kiwi.command.label.UpdateLabelTitleCommand;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.label.LabelColorUpdatedEvent;
import fr.aquillet.kiwi.event.label.LabelCreatedEvent;
import fr.aquillet.kiwi.event.label.LabelTitleUpdatedEvent;
import fr.aquillet.kiwi.event.label.LabelsReloadedEvent;
import fr.aquillet.kiwi.model.Label;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import fr.aquillet.kiwi.ui.service.application.IApplicationService;
import fr.aquillet.kiwi.ui.service.label.ILabelService;
import fr.aquillet.kiwi.ui.service.persistence.ILabelPersistenceService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class LabelController {

    private NotificationCenter notificationCenter;
    private ILabelService labelService;
    private IApplicationService applicationService;
    private ILabelPersistenceService persistenceService;

    @Inject
    private void setDependencies(final NotificationCenter notificationCenter, //
                                 ILabelService labelService, //
                                 IApplicationService applicationService, //
                                 ILabelPersistenceService persistenceService) {
        this.notificationCenter = notificationCenter;
        this.labelService = labelService;
        this.applicationService = applicationService;
        this.persistenceService = persistenceService;
        notificationCenter.subscribe(Commands.LABEL, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
    public void handle(ReloadLabelsCommand command) {
        log.info("Reloading labels");
        labelService.getLabels().clear();
        labelService.getLabels().addAll(persistenceService.getApplicationLabels(applicationService.getCurrentApplication()));
        notificationCenter.publish(Events.LABEL, new LabelsReloadedEvent());
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
    public void handle(CreateLabelCommand command) {
        Label label = labelService.createLabel(command.getTitle(), command.getHexColor());
        log.info("Creating new label (id: {}, title: {}, color: {})", label.getId(), label.getTitle(), label.getHexColor());
        labelService.getLabels().add(label);
        notificationCenter.publish(Events.LABEL, new LabelCreatedEvent(label));
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
    public void handle(UpdateLabelTitleCommand command) {
        labelService.getLabelById(command.getLabelId())//
                .ifPresent(label -> {
                    log.info("Updating label title (id: {}, new title: {})", label.getId(), command.getTitle());
                    label.setTitle(command.getTitle());
                    notificationCenter.publish(Events.LABEL, new LabelTitleUpdatedEvent(label.getId(), label.getTitle()));
                });
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_CONTROLLER)
    public void handle(UpdateLabelColorCommand command) {
        labelService.getLabelById(command.getLabelId())//
                .ifPresent(label -> {
                    log.info("Updating label color (id: {}, new color: {})", label.getId(), command.getColor());
                    label.setHexColor(command.getColor());
                    notificationCenter.publish(Events.LABEL, new LabelColorUpdatedEvent(label.getId(), label.getHexColor()));
                });
    }

}
