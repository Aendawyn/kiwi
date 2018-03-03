package fr.aquillet.kiwi.ui.view.launcher.edition;

import com.jfoenix.controls.JFXButton;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import javax.inject.Inject;

public class EditLauncherView implements FxmlView<EditLauncherViewModel> {

    @FXML
    private TextField launcherNameField;
    @FXML
    private TextField launcherCommandField;
    @FXML
    private TextField launcherWorkingDirectoryField;
    @FXML
    private TextField launcherStartDelayField;
    @FXML
    private JFXButton testLauncherButton;
    @FXML
    private JFXButton closeButton;

    @InjectViewModel
    private EditLauncherViewModel viewModel;

    @Inject
    private NotificationCenter notificationCenter;

    public void initialize() {
        launcherNameField.textProperty().bindBidirectional(viewModel.launcherTitleProperty());
        viewModel.launcherCommandProperty().bindBidirectional(launcherCommandField.textProperty());
        viewModel.launcherWorkingDirectoryProperty().bindBidirectional(launcherWorkingDirectoryField.textProperty());
        launcherStartDelayField.textProperty().addListener((observable, oldValue, newValue) -> viewModel.launcherStartDelayProperty().setValue(Integer.valueOf(newValue)));
        viewModel.launcherStartDelayProperty().addListener((observable, oldValue, newValue) -> launcherStartDelayField.setText(String.valueOf(newValue)));

        testLauncherButton.disableProperty().bind(viewModel.getTestLauncherCommand().notExecutableProperty());
    }

    public void testLauncherButtonPressed() {
        viewModel.getTestLauncherCommand().execute();
    }

    public void closeButtonPressed() {
        notificationCenter.publish(Commands.CLOSE_DIALOG, getClass());
    }

}
