package fr.aquillet.kiwi.ui.view.scenario.deletion;

import com.jfoenix.controls.JFXButton;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javax.inject.Inject;

public class DeleteScenarioView implements FxmlView<DeleteScenarioViewModel> {

    @FXML
    private Label deleteScenarioLabel;
    @FXML
    private JFXButton deleteScenarioButton;
    @FXML
    private JFXButton cancelButton;

    @InjectViewModel
    private DeleteScenarioViewModel viewModel;

    @Inject
    private NotificationCenter notificationCenter;

    public void initialize() {
        deleteScenarioLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            return viewModel.scenarioProperty().get()
                    .map(scenario -> String.format("Le scénario '%s' ayant l'identifiant %s va être supprimé. Voulez-vous continuer ?", //
                            scenario.titleProperty().getValueSafe(), //
                            scenario.idProperty().get()))
                    .orElse("");
        }, viewModel.scenarioProperty()));
    }

    @FXML
    public void deleteScenarioButtonPressed() {
        viewModel.getDeleteScenarioCommand().execute();
        closeDialog();
    }

    @FXML
    public void cancelButtonPressed() {
        closeDialog();
    }

    private void closeDialog() {
        notificationCenter.publish(Commands.CLOSE_DIALOG, getClass());
    }


}
