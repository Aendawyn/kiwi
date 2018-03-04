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
import fr.aquillet.kiwi.ui.view.label.LabelListView;
import fr.aquillet.kiwi.ui.view.label.LabelListViewModel;
import fr.aquillet.kiwi.ui.view.label.LabelListViewModelConverter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
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
        Group imageLayer = new Group();
        BorderPane pane = new BorderPane(imageLayer);
        view.setSmooth(true);
        view.fitWidthProperty().bind(pane.widthProperty());
        view.fitHeightProperty().bind(pane.heightProperty());
        // image layer: a group of images
        imageLayer.getChildren().add(view);

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Enregistrement de Scénario: capture");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/kiwi_logo.png")));
            Scene scene = new Scene(pane, image.getWidth(), image.getHeight());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            RubberBandSelection rubberBandSelection = new RubberBandSelection(imageLayer);
            scene.setOnKeyReleased(event -> {
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

    /**
     * Drag rectangle with mouse cursor in order to get selection bounds
     */
    public static class RubberBandSelection {

        final DragContext dragContext = new DragContext();
        Rectangle rect = new Rectangle();
        EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                if (event.isSecondaryButtonDown())
                    return;

                double offsetX = event.getX() - dragContext.mouseAnchorX;
                double offsetY = event.getY() - dragContext.mouseAnchorY;

                if (offsetX > 0)
                    rect.setWidth(offsetX);
                else {
                    rect.setX(event.getX());
                    rect.setWidth(dragContext.mouseAnchorX - rect.getX());
                }

                if (offsetY > 0) {
                    rect.setHeight(offsetY);
                } else {
                    rect.setY(event.getY());
                    rect.setHeight(dragContext.mouseAnchorY - rect.getY());
                }
            }
        };
        private Group group;
        EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                if (event.isSecondaryButtonDown())
                    return;

                // remove old rect
                rect.setX(0);
                rect.setY(0);
                rect.setWidth(0);
                rect.setHeight(0);

                group.getChildren().remove(rect);

                // prepare new drag operation
                dragContext.mouseAnchorX = event.getX();
                dragContext.mouseAnchorY = event.getY();

                rect.setX(dragContext.mouseAnchorX);
                rect.setY(dragContext.mouseAnchorY);
                rect.setWidth(0);
                rect.setHeight(0);

                group.getChildren().add(rect);

            }
        };

        public RubberBandSelection(Group group) {

            this.group = group;

            rect = new Rectangle(0, 0, 0, 0);
            rect.setStroke(Color.BLUE);
            rect.setStrokeWidth(1);
            rect.setStrokeLineCap(StrokeLineCap.ROUND);
            rect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.6));

            group.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
            group.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);

        }

        public Bounds getBounds() {
            return rect.getBoundsInParent();
        }

        private static final class DragContext {

            public double mouseAnchorX;
            public double mouseAnchorY;

        }
    }

}
