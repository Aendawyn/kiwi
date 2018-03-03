package fr.aquillet.kiwi.ui.view.label;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LabelListView implements FxmlView<LabelListViewModel> {

	@FXML
	private Label titleLabel;

	@InjectViewModel
	private LabelListViewModel viewModel;

	public void initialize() {
		titleLabel.textProperty().bind(viewModel.titleProperty());
		titleLabel.styleProperty().bind(Bindings.createStringBinding(() -> {
			return "-fx-background-color:" + viewModel.colorProperty().get() + ";";
		}, viewModel.colorProperty()));
	}

}
