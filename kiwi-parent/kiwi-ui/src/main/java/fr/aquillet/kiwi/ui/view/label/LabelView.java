package fr.aquillet.kiwi.ui.view.label;

import java.util.Objects;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class LabelView extends HBox {

	private Label titleLabel = new Label();
	private LabelListViewModel viewModel;

	public LabelView() {
		getChildren().add(titleLabel);
		titleLabel.getStyleClass().add("label-node");
		setPadding(new Insets(3d));
	}

	public void setModel(LabelListViewModel viewModel) {
		this.viewModel = viewModel;
		titleLabel.textProperty().bind(viewModel.titleProperty());
		titleLabel.styleProperty().bind(Bindings.createStringBinding(() -> {
			return "-fx-background-color:" + viewModel.colorProperty().get() + ";";
		}, viewModel.colorProperty()));
	}

	public LabelListViewModel getModel() {
		return viewModel;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof LabelView)) {
			return false;
		}

		LabelView other = (LabelView) obj;
		if (viewModel == null) {
			return other.viewModel == null;
		}
		return viewModel.equals(other.viewModel);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(viewModel);
	}

}
