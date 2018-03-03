package fr.aquillet.kiwi.ui.view.label.creation;

import javax.inject.Inject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class CreateLabelView implements FxmlView<CreateLabelViewModel> {

	@FXML
	private TextField labelTitleField;
	@FXML
	private JFXColorPicker labelColorPicker;
	@FXML
	private JFXButton createLabelButton;
	@FXML
	private JFXButton cancelButton;

	@InjectViewModel
	private CreateLabelViewModel viewModel;

	@Inject
	private NotificationCenter notificationCenter;

	public void initialize() {
		labelColorPicker.setValue(Color.WHITE);
		labelTitleField.textProperty().bindBidirectional(viewModel.labelTitleProperty());
		viewModel.labelColorProperty().bind(Bindings.createStringBinding(() -> {
			return "#" + Integer.toHexString(labelColorPicker.getValue().hashCode()).substring(0, 6);
		}, labelColorPicker.valueProperty()));

		createLabelButton.disableProperty().bind(viewModel.getCreateLauncherCommand().notExecutableProperty());
	}

	public void createLabelButtonPressed() {
		viewModel.getCreateLauncherCommand().execute();
		closeDialog();
	}

	public void cancelButtonPressed() {
		closeDialog();
	}

	private void closeDialog() {
		notificationCenter.publish(Commands.CLOSE_DIALOG, getClass());
	}

}
