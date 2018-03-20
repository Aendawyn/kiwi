package fr.aquillet.kiwi.ui.view.scenario;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ScenarioListView implements FxmlView<ScenarioListViewModel> {

	@FXML
	private Label titleLabel;

	@InjectViewModel
	private ScenarioListViewModel viewModel;

	public void initialize() {
		titleLabel.textProperty().bind(viewModel.titleProperty());
	}
}
