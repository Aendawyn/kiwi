package fr.aquillet.kiwi.ui.view.capture;

import com.jfoenix.controls.JFXButton;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.stream.IntStream;

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
        if (originalImageView.getImage() == null || sourceImageView.getImage() == null) {
            return;
        }
        Image original = originalImageView.getImage();
        Image source = sourceImageView.getImage();
        WritableImage comparison = new WritableImage(original.getPixelReader(), 0, 0, //
                (int) original.getWidth(), (int) original.getHeight());
        comparisonImageView.setImage(comparison);
        double percent = computeSnapshotSimilarity(original, source, comparison);
        NumberFormat instance = DecimalFormat.getInstance();
        instance.setMaximumFractionDigits(2);
        diffLabel.setText("Similitude: " + instance.format(percent) + "%");
    }

    private double computeSnapshotSimilarity(final Image image1, final Image image2, final WritableImage diff) {
        final int width = (int) image1.getWidth();
        final int height = (int) image1.getHeight();
        final PixelReader reader1 = image1.getPixelReader();
        final PixelReader reader2 = image2.getPixelReader();

        final double nbNonSimilarPixels = IntStream.range(0, width) //
                .parallel() //
                .mapToLong(i -> IntStream.range(0, height) //
                        .parallel() //
                        .filter(j -> reader1.getArgb(i, j) != reader2.getArgb(i, j)) //
                        .peek(j -> {
                            diff.getPixelWriter().setColor(i, j, Color.RED);
                        }).count()) //
                .sum();

        return 100d - nbNonSimilarPixels / (width * height) * 100d;
    }

}
