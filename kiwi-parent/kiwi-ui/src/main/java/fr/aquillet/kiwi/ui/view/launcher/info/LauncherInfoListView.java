package fr.aquillet.kiwi.ui.view.launcher.info;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LauncherInfoListView implements FxmlView<LauncherInfoListViewModel> {

	@FXML
	private Label titleLabel;
	@FXML
	private Label classLabel;

	@InjectViewModel
	private LauncherInfoListViewModel viewModel;

	public void initialize() {
		titleLabel.textProperty().bind(viewModel.titleProperty());
		classLabel.textProperty().bind(viewModel.classProperty());
	}
}
