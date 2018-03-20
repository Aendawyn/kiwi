package fr.aquillet.kiwi.ui.view.scenario.deletion;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.scenario.DeleteScenarioCommand;
import fr.aquillet.kiwi.ui.service.scenario.IScenarioService;
import fr.aquillet.kiwi.ui.view.scenario.ScenarioViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class DeleteScenarioViewModel implements ViewModel {

    private ObjectProperty<Optional<ScenarioViewModel>> scenario = new SimpleObjectProperty<>(Optional.empty());

    @Inject
    private IScenarioService scenarioService;
    @Inject
    private NotificationCenter notificationCenter;

    public void setArguments(UUID scenarioId) {
        scenarioService.getScenarioById(scenarioId) //
                .map(ScenarioViewModel::new) //
                .ifPresent(model -> scenario.set(Optional.of(model)));
    }

    public ObjectProperty<Optional<ScenarioViewModel>> scenarioProperty() {
        return scenario;
    }

    public Command getDeleteScenarioCommand() {
        return new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                scenario.get().ifPresent(model -> //
                        notificationCenter.publish(Commands.SCENARIO, new DeleteScenarioCommand(model.idProperty().get())));
            }
        }, true);
    }

}
