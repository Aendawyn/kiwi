package fr.aquillet.kiwi.ui.view.application.creation;

import javax.inject.Inject;

import com.jfoenix.controls.JFXButton;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CreateApplicationView implements FxmlView<CreateApplicationViewModel> {

	@FXML
	private TextField applicationTitleField;
	@FXML
	private JFXButton createApplicationButton;
	@FXML
	private JFXButton cancelButton;

	@InjectViewModel
	private CreateApplicationViewModel viewModel;

	@Inject
	private NotificationCenter notificationCenter;

	public void initialize() {
		applicationTitleField.textProperty().bindBidirectional(viewModel.applicationTitleProperty());
		createApplicationButton.disableProperty().bind(viewModel.getCreateApplicationCommand().notExecutableProperty());
	}

	public void createApplicationButtonPressed() {
		viewModel.getCreateApplicationCommand().execute();
		closeDialog();
	}

	public void cancelButtonPressed() {
		closeDialog();
	}
	
	private void closeDialog() {
		notificationCenter.publish(Commands.CLOSE_DIALOG, getClass());
	}

}
