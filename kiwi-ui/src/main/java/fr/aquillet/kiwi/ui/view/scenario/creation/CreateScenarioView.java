package fr.aquillet.kiwi.ui.view.scenario.creation;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.model.Capture;
import fr.aquillet.kiwi.toolkit.ui.fx.ImageUtil;
import fr.aquillet.kiwi.ui.node.RubberBandSelectionNode;
import fr.aquillet.kiwi.ui.util.KiwiStageUtil;
import fr.aquillet.kiwi.ui.view.label.LabelListView;
import fr.aquillet.kiwi.ui.view.label.LabelListViewModel;
import fr.aquillet.kiwi.ui.view.label.LabelListViewModelConverter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.inject.Inject;
import java.util.Optional;

public class CreateScenarioView implements FxmlView<CreateScenarioViewModel> {

    @FXML
    private TextField scenarioTitleField;
    @FXML
    private JFXComboBox<LabelListViewModel> scenarioLabelBox;
    @FXML
    private Label scenarioStepsCountLabel;
    @FXML
    private JFXButton captureButton;
    @FXML
    private ImageView captureView;
    @FXML
    private JFXButton createScenarioButton;
    @FXML
    private JFXButton cancelButton;

    @InjectViewModel
    private CreateScenarioViewModel viewModel;

    @Inject
    private NotificationCenter notificationCenter;

    public void initialize() {
        scenarioTitleField.textProperty().bindBidirectional(viewModel.titleProperty());
        scenarioStepsCountLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            return viewModel.eventsCountProperty().get() + " évènements";
        }, viewModel.eventsCountProperty()));

        scenarioLabelBox.setItems(viewModel.labelsProperty());
        scenarioLabelBox.setConverter(new LabelListViewModelConverter());
        scenarioLabelBox.setCellFactory(CachedViewModelCellFactory.createForFxmlView(LabelListView.class));
        viewModel.labelProperty().bind(Bindings.createObjectBinding(() -> {
            return Optional.ofNullable(scenarioLabelBox.getSelectionModel().getSelectedItem());
        }, scenarioLabelBox.getSelectionModel().selectedItemProperty()));

        createScenarioButton.disableProperty().bind(viewModel.getCreateScenarioCommand().notExecutableProperty());
        captureButton.disableProperty().bind(viewModel.getCaptureCommand().runningProperty());

        viewModel.screenshotProperty().addListener((obs, oldValue, newValue) -> {
            showScreenShot(newValue);
        });
    }

    public void createScenarioButtonPressed() {
        viewModel.getCreateScenarioCommand().execute();
        closeDialog();
    }

    public void captureButtonPressed() {
        viewModel.getCaptureCommand().execute();
    }

    public void cancelButtonPressed() {
        closeDialog();
    }

    private void closeDialog() {
        notificationCenter.publish(Commands.CLOSE_DIALOG, getClass());
    }

    private void showScreenShot(Image image) {
        ImageView view = new ImageView(image);
        view.setPreserveRatio(true);
        RubberBandSelectionNode rubberBandSelection = new RubberBandSelectionNode(view);
        BorderPane pane = new BorderPane(rubberBandSelection);
        view.fitWidthProperty().bind(pane.widthProperty());
        view.fitHeightProperty().bind(pane.heightProperty());

        Platform.runLater(() -> {
            Stage stage = KiwiStageUtil.createStage("Enregistrement de Scénario: capture", pane);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setWidth(image.getWidth());
            stage.setHeight(image.getHeight());
            stage.setResizable(false);
            stage.show();
            stage.centerOnScreen();
            stage.getScene().setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    Bounds bounds = rubberBandSelection.getBounds();
                    Image croppedImage = ImageUtil.cropImage(image, (int) bounds.getMinX(), (int) bounds.getMinY(), (int) bounds.getWidth(), (int) bounds.getHeight());
                    Capture capture = new Capture((int) bounds.getWidth(), (int) bounds.getHeight(), (int) bounds.getMinX(), (int) bounds.getMinY(), ImageUtil.convertImageToByteArray(croppedImage));
                    viewModel.captureProperty().set(capture);
                    captureView.setImage(croppedImage);
                    stage.close();
                }
            });
        });
    }

}
