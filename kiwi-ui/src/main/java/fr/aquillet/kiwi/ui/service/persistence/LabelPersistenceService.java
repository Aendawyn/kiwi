package fr.aquillet.kiwi.ui.service.persistence;

import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.label.LabelCreatedEvent;
import fr.aquillet.kiwi.model.Application;
import fr.aquillet.kiwi.model.Label;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import fr.aquillet.kiwi.toolkit.jackson.JacksonUtil;
import fr.aquillet.kiwi.ui.service.application.IApplicationService;
import fr.aquillet.kiwi.ui.service.label.ILabelService;

import javax.inject.Inject;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class LabelPersistenceService implements ILabelPersistenceService {

    private IPersistenceConfiguration configuration;
    private IApplicationPersistenceService applicationPersistenceService;
    private IApplicationService applicationService;
    private ILabelService labelService;

    @Inject
    public void setDependencies(final IPersistenceConfiguration configuration, //
                                final IApplicationPersistenceService applicationPersistenceService, //
                                final IApplicationService applicationService, //
                                final ILabelService labelService, //
                                final NotificationCenter notificationCenter) {
        this.configuration = configuration;
        this.applicationPersistenceService = applicationPersistenceService;
        this.applicationService = applicationService;
        this.labelService = labelService;

        notificationCenter.subscribe(Events.LABEL, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
    }

    @Override
    public List<Label> getApplicationLabels(Application application) {
        return Optional.ofNullable(application) //
                .map(this::getLabelsDirectory) //
                .filter(File::exists) //
                .flatMap(folder -> Optional.ofNullable(folder.listFiles(file -> file.getName().endsWith(configuration.getFileExtension()))) //
                        .map(files -> Arrays.stream(files) //
                                .map(file -> JacksonUtil.read(file, Label.class)) //
                                .filter(Optional::isPresent) //
                                .map(Optional::get) //
                                .collect(Collectors.toList()))) //
                .orElseGet(Collections::emptyList);
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_IO)
    public void handle(LabelCreatedEvent event) {
        saveLabel(event.getLabel().getId());
    }

    private void saveLabel(UUID labelId) {
        Optional.ofNullable(applicationService.getCurrentApplication()).ifPresent(application -> saveLabel(application, labelId));
    }

    private void saveLabel(Application application, UUID labelId) {
        labelService.getLabelById(labelId) //
                .ifPresent(label -> JacksonUtil.write(label, Label.class, new File(getLabelsDirectory(application), getLabelFileName(label))));
    }

    private File getLabelsDirectory(Application application) {
        return new File(applicationPersistenceService.getApplicationDirectory(application), configuration.getLabelsDirectoryName());
    }

    private String getLabelFileName(Label label) {
        return label.getId().toString() + configuration.getFileExtension();
    }
}
