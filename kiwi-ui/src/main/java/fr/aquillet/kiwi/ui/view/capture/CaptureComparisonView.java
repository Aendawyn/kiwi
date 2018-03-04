package fr.aquillet.kiwi.ui.view.capture;

import com.jfoenix.controls.JFXButton;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import fr.aquillet.kiwi.toolkit.ui.fx.ImageComparisonResult;
import fr.aquillet.kiwi.toolkit.ui.fx.ImageUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Optional;

public class CaptureComparisonView implements FxmlView<CaptureComparisonViewModel> {

    private static final int IMAGEVIEW_MAX_WIDTH = 490;
    private static final int IMAGEVIEW_MAX_HEIGHT = 380;

    @FXML
    private StackPane root;
    @FXML
    private ImageView originalImageView;
    @FXML
    private ImageView sourceImageView;
    @FXML
    private Label diffLabel;
    @FXML
    private ImageView comparisonImageView;
    @FXML
    private JFXButton closeButton;

    @InjectViewModel
    private CaptureComparisonViewModel viewModel;

    public void initialize() {
        originalImageView.imageProperty().addListener((obs, oldValue, newValue) -> {
            originalImageView.setFitWidth(Math.min(IMAGEVIEW_MAX_WIDTH, newValue.getWidth()));
            originalImageView.setFitHeight(Math.min(IMAGEVIEW_MAX_HEIGHT, newValue.getHeight()));
        });
        sourceImageView.fitWidthProperty().bind(originalImageView.fitWidthProperty());
        sourceImageView.fitHeightProperty().bind(originalImageView.fitHeightProperty());
        comparisonImageView.fitWidthProperty().bind(originalImageView.fitWidthProperty());
        comparisonImageView.fitHeightProperty().bind(originalImageView.fitHeightProperty());
        viewModel.originalProperty().addListener((obs, olwValue, newValue) -> {
            originalImageView.setImage(newValue);
            computeComparison();
        });
        viewModel.sourceProperty().addListener((obs, olwValue, newValue) -> {
            sourceImageView.setImage(newValue);
            computeComparison();
        });
        Optional.ofNullable(viewModel.originalProperty().get()).ifPresent(originalImageView::setImage);
        Optional.ofNullable(viewModel.sourceProperty().get()).ifPresent(sourceImageView::setImage);
        computeComparison();
    }

    public void closeButtonPressed() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }

    private void computeComparison() {
        comparisonImageView.setImage(null);
        Image original = originalImageView.getImage();
        Image source = sourceImageView.getImage();
        if (original == null || source == null) {
            return;
        }

        ImageComparisonResult imageComparisonResult = ImageUtil.compareImages(original, source);
        comparisonImageView.setImage(imageComparisonResult.getDiffImage());
        NumberFormat instance = DecimalFormat.getInstance();
        instance.setMaximumFractionDigits(2);
        diffLabel.setText("Similitude: " + instance.format(imageComparisonResult.getSimilarity()) + "%");
    }

}
