package fr.aquillet.kiwi.ui.view.campaign.edition;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.ui.view.scenario.ScenarioListView;
import fr.aquillet.kiwi.ui.view.scenario.ScenarioListViewModel;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.*;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class EditCampaignView implements FxmlView<EditCampaignViewModel> {

    private static final DataFormat SCENARIO_FORMAT = new DataFormat("scenario");

    @FXML
    private JFXListView<ScenarioListViewModel> availableScenariosListView;
    @FXML
    private JFXListView<ScenarioListViewModel> campaignScenariosListView;
    @FXML
    private JFXButton moveScenarioUp;
    @FXML
    private JFXButton moveScenarioDown;

    @InjectViewModel
    private EditCampaignViewModel viewModel;

    @Inject
    private NotificationCenter notificationCenter;

    public void initialize() {
        availableScenariosListView.setItems(viewModel.availableScenariosProperty());
        availableScenariosListView.setCellFactory(CachedViewModelCellFactory.createForFxmlView(ScenarioListView.class));
        availableScenariosListView.setOnDragOver(onDragOver());
        availableScenariosListView.setOnDragDetected(onDragDetected(availableScenariosListView));
        availableScenariosListView.setOnDragDropped(availableScenariosOnDragDropped());

        campaignScenariosListView.setItems(viewModel.campaignScenariosProperty());
        campaignScenariosListView.setCellFactory(CachedViewModelCellFactory.createForFxmlView(ScenarioListView.class));
        campaignScenariosListView.setOnDragDetected(onDragDetected(campaignScenariosListView));
        campaignScenariosListView.setOnDragOver(onDragOver());
        campaignScenariosListView.setOnDragDropped(campaignScenariosOnDragDropped());

        moveScenarioUp.setOnAction(e -> Optional.ofNullable(campaignScenariosListView.getSelectionModel().getSelectedItem()) //
                .ifPresent(item -> { //
                    int index = campaignScenariosListView.getItems().indexOf(item);
                    viewModel.swapingProperty().setValue(true);
                    Collections.swap(campaignScenariosListView.getItems(), index, max(0, index - 1));
                    viewModel.swapingProperty().setValue(false);
                    campaignScenariosListView.getSelectionModel().select(item);
                }));
        moveScenarioDown.setOnAction(e -> Optional.ofNullable(campaignScenariosListView.getSelectionModel().getSelectedItem()) //
                .ifPresent(item -> { //
                    int index = campaignScenariosListView.getItems().indexOf(item);
                    viewModel.swapingProperty().setValue(true);
                    Collections.swap(campaignScenariosListView.getItems(), index, min(campaignScenariosListView.getItems().size() - 1, index + 1));
                    viewModel.swapingProperty().setValue(false);
                    campaignScenariosListView.getSelectionModel().select(item);
                }));

        moveScenarioUp.disableProperty().bind(campaignScenariosListView.getSelectionModel().selectedIndexProperty().greaterThan(0).not());
        moveScenarioDown.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            int selectedIndex = campaignScenariosListView.getSelectionModel().getSelectedIndex();
            return selectedIndex == -1 || selectedIndex >= campaignScenariosListView.getItems().size() - 1;
        }, campaignScenariosListView.getSelectionModel().selectedIndexProperty(), campaignScenariosListView.getItems()));
    }

    public void closeButtonPressed() {
        closeDialog();
    }

    private void closeDialog() {
        notificationCenter.publish(Commands.CLOSE_DIALOG, getClass());
    }

    private EventHandler<DragEvent> onDragOver() {
        return event -> event.acceptTransferModes(TransferMode.MOVE);
    }

    private EventHandler<MouseEvent> onDragDetected(ListView<ScenarioListViewModel> source) {
        return event -> {
            ScenarioListViewModel selectedItem = source.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                return;
            }

            Dragboard dragboard = source.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.put(SCENARIO_FORMAT, selectedItem.idProperty().get());
            dragboard.setContent(content);
        };
    }

    private EventHandler<DragEvent> availableScenariosOnDragDropped() {
        return event -> {
            Dragboard db = event.getDragboard();

            if (db.hasContent(SCENARIO_FORMAT)) {
                UUID id = (UUID) db.getContent(SCENARIO_FORMAT);
                viewModel.campaignScenariosProperty().removeIf(model -> model.idProperty().get().equals(id));
            }
            event.setDropCompleted(true);
        };
    }

    private EventHandler<DragEvent> campaignScenariosOnDragDropped() {
        return event -> {
            Dragboard db = event.getDragboard();

            if (db.hasContent(SCENARIO_FORMAT)) {
                UUID id = (UUID) db.getContent(SCENARIO_FORMAT);
                viewModel.availableScenariosProperty().stream() //
                        .filter(model -> model.idProperty().get().equals(id)) //
                        .findFirst()
                        .ifPresent(availableScenario -> campaignScenariosListView.getItems().add(availableScenario));
            }

            event.setDropCompleted(true);
        };
    }

}
