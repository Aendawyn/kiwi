package fr.aquillet.kiwi.ui.view.label.edition;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import javax.inject.Inject;

public class EditLabelView implements FxmlView<EditLabelViewModel> {

    @FXML
    private TextField labelTitleField;
    @FXML
    private JFXColorPicker labelColorPicker;
    @FXML
    private JFXButton closeButton;

    @InjectViewModel
    private EditLabelViewModel viewModel;

    @Inject
    private NotificationCenter notificationCenter;

    public void initialize() {
        labelColorPicker.setValue(Color.WHITE);
        labelTitleField.textProperty().bindBidirectional(viewModel.labelTitleProperty());
        labelColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> viewModel.labelColorProperty().set("#" + Integer.toHexString(labelColorPicker.getValue().hashCode()).substring(0, 6)));
        viewModel.labelColorProperty().addListener((observable, oldValue, newValue) -> labelColorPicker.valueProperty().set(Color.web(newValue)));
    }

    public void closeButtonPressed() {
        closeDialog();
    }

    private void closeDialog() {
        notificationCenter.publish(Commands.CLOSE_DIALOG, getClass());
    }

}
