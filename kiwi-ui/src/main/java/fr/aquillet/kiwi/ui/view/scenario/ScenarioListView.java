package fr.aquillet.kiwi.ui.view.scenario;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ScenarioListView implements FxmlView<ScenarioListViewModel> {

    @FXML
    private Label titleLabel;
    @FXML
    private Label labelLabel;

    @InjectViewModel
    private ScenarioListViewModel viewModel;

    public void initialize() {
        titleLabel.textProperty().bind(viewModel.titleProperty());
        System.out.println(viewModel.labelProperty().get().isPresent());
        labelLabel.visibleProperty().bind(Bindings.createBooleanBinding(() -> viewModel.labelProperty().get().isPresent(), viewModel.labelProperty()));
        System.out.println(labelLabel.visibleProperty().get());
        labelLabel.managedProperty().bind(labelLabel.visibleProperty());
        labelLabel.textProperty().bind(Bindings.createStringBinding(() -> viewModel.labelProperty().get() //
                .map(fr.aquillet.kiwi.model.Label::getTitle)  //
                .orElse(""), viewModel.labelProperty()));
        labelLabel.styleProperty().bind(Bindings.createStringBinding(() -> "-fx-background-color:" + viewModel.labelProperty().get() //
                .map(fr.aquillet.kiwi.model.Label::getHexColor) //
                .map(color -> color + ";") //
                .orElse("white;"), viewModel.labelProperty()));
    }
}
