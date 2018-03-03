package fr.aquillet.kiwi.ui.view.application;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ApplicationListView implements FxmlView<ApplicationListViewModel> {

	@FXML
	private Label titleLabel;

	@InjectViewModel
	private ApplicationListViewModel viewModel;

	public void initialize() {
		titleLabel.textProperty().bind(viewModel.titleProperty());
	}
}
