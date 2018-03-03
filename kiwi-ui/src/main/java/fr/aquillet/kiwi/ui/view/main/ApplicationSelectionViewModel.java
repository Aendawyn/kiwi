package fr.aquillet.kiwi.ui.view.main;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.application.ChangeCurrentApplicationCommand;
import fr.aquillet.kiwi.command.application.ReloadApplicationsCommand;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.application.ApplicationCreatedEvent;
import fr.aquillet.kiwi.event.application.ApplicationsReloadedEvent;
import fr.aquillet.kiwi.ui.service.application.IApplicationService;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import fr.aquillet.kiwi.ui.view.application.ApplicationListViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import java.util.UUID;
import java.util.stream.Collectors;

public class ApplicationSelectionViewModel implements ViewModel {

    private Command loadApplicationCommand;
    private BooleanProperty loadApplicationPrecondition = new SimpleBooleanProperty(false);
    private ObservableList<ApplicationListViewModel> applications = FXCollections.observableArrayList();
    private ObjectProperty<UUID> selectedApplicationId = new SimpleObjectProperty<>();

    @Inject
    private IApplicationService applicationService;

    @Inject
    private NotificationCenter notificationCenter;

    public void initialize() {
        loadApplicationCommand = new DelegateCommand(() -> this.loadApplicationAction(), //
                loadApplicationPrecondition, true);

        loadApplicationPrecondition.bind(selectedApplicationId.isNotNull() //
                .and(loadApplicationCommand.notRunningProperty()));

        notificationCenter.subscribe(Events.APPLICATION, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
        notificationCenter.publish(Commands.APPLICATION, new ReloadApplicationsCommand());
    }

    public Command getLoadApplicationCommand() {
        return loadApplicationCommand;
    }

    public ObjectProperty<UUID> selectedApplicationIdProperty() {
        return selectedApplicationId;
    }

    public ObservableList<ApplicationListViewModel> applicationsProperty() {
        return applications;
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(ApplicationCreatedEvent event) {
        reloadApplications();
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(ApplicationsReloadedEvent event) {
        reloadApplications();
    }

    private void reloadApplications() {
        applications.setAll(applicationService.getApplications().stream() //
                .map(ApplicationListViewModel::new) //
                .collect(Collectors.toList()));
    }

    private Action loadApplicationAction() {
        return new Action() {

            @Override
            protected void action() throws Exception {
                notificationCenter.publish(Commands.APPLICATION, //
                        new ChangeCurrentApplicationCommand(selectedApplicationId.get()));
            }
        };
    }

}
