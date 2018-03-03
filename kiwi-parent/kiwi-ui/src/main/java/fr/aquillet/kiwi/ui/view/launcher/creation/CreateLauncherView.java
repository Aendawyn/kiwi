package fr.aquillet.kiwi.ui.view.launcher.creation;

import javax.inject.Inject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.ui.view.launcher.info.LauncherInfoListView;
import fr.aquillet.kiwi.ui.view.launcher.info.LauncherInfoListViewModel;
import fr.aquillet.kiwi.ui.view.launcher.info.LauncherInfoListViewModelConverter;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CreateLauncherView implements FxmlView<CreateLauncherViewModel> {

	@FXML
	private TextField launcherNameField;
	@FXML
	private TextField launcherCommandField;
	@FXML
	private JFXComboBox<LauncherInfoListViewModel> launcherInfoBox;
	@FXML
	private JFXButton reloadLaunchersInfoButton;
	@FXML
	private TextField launcherWorkingDirectoryField;
	@FXML
	private TextField launcherStartDelayField;
	@FXML
	private JFXButton createLauncherButton;
	@FXML
	private JFXButton cancelButton;

	@InjectViewModel
	private CreateLauncherViewModel viewModel;

	@Inject
	private NotificationCenter notificationCenter;

	public void initialize() {
		launcherNameField.textProperty().bindBidirectional(viewModel.launcherTitleProperty());
		launcherCommandField.textProperty().bindBidirectional(viewModel.launcherCommandProperty());
		launcherInfoBox.setItems(viewModel.launcherInfosProperty());
		launcherInfoBox.setCellFactory(CachedViewModelCellFactory.createForFxmlView(LauncherInfoListView.class));
		launcherInfoBox.setConverter(new LauncherInfoListViewModelConverter());
		viewModel.selectedLauncherInfosProperty().bind(launcherInfoBox.getSelectionModel().selectedItemProperty());
		launcherWorkingDirectoryField.textProperty().bindBidirectional(viewModel.launcherWorkingDirectoryProperty());
		launcherStartDelayField.textProperty().addListener((obs, old, value) -> {
			if (!value.matches("\\d*")) {
				launcherStartDelayField.setText(value.replaceAll("[^\\d]", ""));
			}
		});
		viewModel.launcherStartDelayProperty().bind(Bindings.createIntegerBinding(() -> {
			if (launcherStartDelayField.getText().isEmpty()) {
				return -1;
			}
			return Integer.parseInt(launcherStartDelayField.getText());
		}, launcherStartDelayField.textProperty()));
		
		createLauncherButton.disableProperty().bind(viewModel.getCreateLauncherCommand().notExecutableProperty());
	}

	public void createLauncherButtonPressed() {
		viewModel.getCreateLauncherCommand().execute();
		closeDialog();
	}
	
	public void reloadLaunchersInfoButtonPressed() {
		viewModel.getReloadLaunchersInfosCommand().execute();
	}

	public void cancelButtonPressed() {
		closeDialog();
	}

	private void closeDialog() {
		notificationCenter.publish(Commands.CLOSE_DIALOG, getClass());
	}

}
