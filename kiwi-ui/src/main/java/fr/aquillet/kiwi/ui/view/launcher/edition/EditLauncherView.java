package fr.aquillet.kiwi.ui.view.launcher.edition;

import com.jfoenix.controls.JFXButton;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.toolkit.ui.fx.JfxUtil;
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
        JfxUtil.copyValueOnce(viewModel.launcherTitleProperty(), launcherNameField.textProperty());
        JfxUtil.copyValueOnce(viewModel.launcherCommandProperty(), launcherCommandField.textProperty());
        JfxUtil.copyValueOnce(viewModel.launcherWorkingDirectoryProperty(), launcherWorkingDirectoryField.textProperty());
        JfxUtil.copyValueOnce(viewModel.launcherStartDelayProperty(), launcherStartDelayField.textProperty(), String::valueOf);

        JfxUtil.copyValueOnFocusLoss(launcherNameField, launcherNameField.textProperty(), viewModel.launcherTitleProperty());
        JfxUtil.copyValueOnFocusLoss(launcherCommandField, launcherCommandField.textProperty(), viewModel.launcherCommandProperty());
        JfxUtil.copyValueOnFocusLoss(launcherWorkingDirectoryField, launcherWorkingDirectoryField.textProperty(), viewModel.launcherWorkingDirectoryProperty());
        JfxUtil.copyValueOnFocusLoss(launcherStartDelayField, launcherStartDelayField.textProperty(), viewModel.launcherStartDelayProperty(), Integer::valueOf);

        launcherStartDelayField.textProperty().addListener((obs, old, value) -> {
            if (!value.matches("\\d*")) {
                launcherStartDelayField.setText(value.replaceAll("[^\\d]", ""));
            }
        });

        testLauncherButton.disableProperty().bind(viewModel.getTestLauncherCommand().notExecutableProperty());
    }

    public void testLauncherButtonPressed() {
        viewModel.getTestLauncherCommand().execute();
    }

    public void closeButtonPressed() {
        notificationCenter.publish(Commands.CLOSE_DIALOG, getClass());
    }

}
