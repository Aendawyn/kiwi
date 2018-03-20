package fr.aquillet.kiwi.ui.view.dashboard.scenario;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.ui.view.label.LabelView;
import fr.aquillet.kiwi.ui.view.launcher.LauncherListView;
import fr.aquillet.kiwi.ui.view.launcher.LauncherListViewModel;
import fr.aquillet.kiwi.ui.view.launcher.LauncherListViewModelConverter;
import fr.aquillet.kiwi.ui.view.scenario.ScenarioViewModel;
import fr.aquillet.kiwi.ui.view.scenario.creation.CreateScenarioView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import javax.inject.Inject;

public class DashboardScenarioView implements FxmlView<DashboardScenarioViewModel> {

	@FXML
	private JFXComboBox<LauncherListViewModel> activeLauncherBox;
	@FXML
	private JFXSlider replaySpeedSlider;
	@FXML
	private JFXButton addScenarioButton;

	@FXML
	private JFXTreeTableView<ScenarioViewModel> scenariosTable;
	@FXML
	private JFXTreeTableColumn<ScenarioViewModel, String> scenarioNameColumn;
	@FXML
	private JFXTreeTableColumn<ScenarioViewModel, LabelView> scenarioLabelColumn;
	@FXML
	private JFXTreeTableColumn<ScenarioViewModel, Number> scenarioStepsCountColumn;
	@FXML
	private JFXTreeTableColumn<ScenarioViewModel, Number> scenarioDurationColumn;
	@FXML
	private JFXTreeTableColumn<ScenarioViewModel, JFXButton> scenarioRunColumn;

	@Inject
	private NotificationCenter notificationCenter;

	@InjectViewModel
	private DashboardScenarioViewModel viewModel;

	public void initialize() {
		activeLauncherBox.setItems(viewModel.launchersProperty());
		activeLauncherBox.setConverter(new LauncherListViewModelConverter());
		activeLauncherBox.setCellFactory(CachedViewModelCellFactory.createForFxmlView(LauncherListView.class));
		viewModel.selectedLauncherProperty().bind(activeLauncherBox.getSelectionModel().selectedItemProperty());
		
		replaySpeedSlider.setMax(3);
		replaySpeedSlider.setMin(0.5d);
		replaySpeedSlider.setShowTickMarks(true);
		replaySpeedSlider.setMajorTickUnit(0.5f);
		replaySpeedSlider.setBlockIncrement(0.5f);
		viewModel.replaySpeedProperty().bind(replaySpeedSlider.valueProperty());
		replaySpeedSlider.setValue(1);

		TreeItem<ScenarioViewModel> root = new RecursiveTreeItem<>(viewModel.scenariosProperty(),
				RecursiveTreeObject::getChildren);
		scenariosTable.setRoot(root);
		scenariosTable.setShowRoot(false);

		scenarioNameColumn.setCellValueFactory(param -> {
			if (scenarioNameColumn.validateValue(param)) {
				return param.getValue().getValue().titleProperty();
			}
			return scenarioNameColumn.getComputedValue(param);
		});
		scenarioLabelColumn.setCellValueFactory(param -> {
			if (scenarioLabelColumn.validateValue(param) && param.getValue().getValue().labelProperty().get() != null
					&& param.getValue().getValue().labelProperty().get().isPresent()) {
				LabelView view = new LabelView();
				view.setModel(param.getValue().getValue().labelProperty().get().get());
				return new SimpleObjectProperty<>(view);
			}
			return scenarioLabelColumn.getComputedValue(param);
		});
		scenarioStepsCountColumn.setCellValueFactory(param -> {
			if (scenarioStepsCountColumn.validateValue(param)) {
				return param.getValue().getValue().stepsCountProperty();
			}
			return scenarioStepsCountColumn.getComputedValue(param);
		});
		scenarioDurationColumn.setCellValueFactory(param -> {
			if (scenarioDurationColumn.validateValue(param)) {
				return param.getValue().getValue().durationMsProperty().divide(1000d).divide(replaySpeedSlider.valueProperty());
			}
			return scenarioDurationColumn.getComputedValue(param);
		});
		scenarioRunColumn.setCellValueFactory(param -> {
			if (scenarioRunColumn.validateValue(param)) {
				JFXButton runButton = new JFXButton();
				runButton.prefWidth(40D);
				runButton.getStyleClass().add("button-raised");
				Glyph glyph = new Glyph("FontAwesome", FontAwesome.Glyph.PLAY);
				glyph.getStyleClass().add("light-glyph");
				runButton.setGraphic(glyph);
				Command scenrarioCommand = viewModel.runScenarioCommand(param.getValue().getValue());
				runButton.setOnAction(e -> scenrarioCommand.execute());
				runButton.disableProperty().bind(scenrarioCommand.notExecutableProperty());
				return new SimpleObjectProperty<>(runButton);
			}
			return scenarioRunColumn.getComputedValue(param);
		});
	}

	public void addScenarioButtonPressed() {
		notificationCenter.publish(Commands.OPEN_DIALOG_IN_PARENT, CreateScenarioView.class);
	}
}
