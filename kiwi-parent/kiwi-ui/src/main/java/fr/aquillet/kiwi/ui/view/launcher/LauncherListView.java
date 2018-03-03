package fr.aquillet.kiwi.ui.view.launcher;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LauncherListView implements FxmlView<LauncherListViewModel> {

	@FXML
	private Label titleLabel;

	@InjectViewModel
	private LauncherListViewModel viewModel;

	public void initialize() {
		titleLabel.textProperty().bind(viewModel.titleProperty());
	}
}
