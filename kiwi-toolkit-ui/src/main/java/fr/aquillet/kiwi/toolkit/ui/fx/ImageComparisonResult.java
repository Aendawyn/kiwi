package fr.aquillet.kiwi.toolkit.ui.fx;

import javafx.scene.image.Image;
import lombok.Builder;
import lombok.Value;


@Value
@Builder
public class ImageComparisonResult {

    private double similarity;
    private Image diffImage;
}
