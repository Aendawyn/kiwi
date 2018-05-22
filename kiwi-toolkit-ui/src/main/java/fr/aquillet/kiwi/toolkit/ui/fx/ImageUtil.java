package fr.aquillet.kiwi.toolkit.ui.fx;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j
public class ImageUtil {

    private static final String PNG_FORMAT = "png";
    private static final Color IMAGE_COMPARISON_DELTA_COLOR = Color.RED;
    private static final double PERCENT_100 = 100d;

    private ImageUtil() {
        // utility class
    }

    public static Optional<WritableImage> takeForegroundApplicationScreenShot(Bounds bounds) {
        try {
            Rectangle rect = new Rectangle((int) bounds.getMinX(), (int) bounds.getMinY(), (int) bounds.getWidth(), (int) bounds.getHeight());
            BufferedImage screenCapture = new Robot().createScreenCapture(rect);
            return Optional.of(SwingFXUtils.toFXImage(screenCapture, null));
        } catch (HeadlessException | AWTException e) {
            log.error("Unable to capture screenshot of foreground application", e);
            return Optional.empty();
        }
    }

    public static WritableImage cropImage(Image source, int x, int y, int width, int height) {
        return new WritableImage(source.getPixelReader(), x, y, width, height);
    }

    public static WritableImage copyImage(Image source) {
        return cropImage(source, 0, 0, (int) source.getWidth(), (int) source.getHeight());
    }

    public static Image createImageFrom(byte[] bytes) {
        return new Image(new ByteArrayInputStream(bytes));
    }

    public static byte[] convertImageToByteArray(Image source) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(source, null), PNG_FORMAT, byteArrayOutputStream);
        } catch (IOException e) {
            log.error("Unable to convert image to byte array", e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static void saveToFile(Image image, File dest) {
        if (!dest.getParentFile().exists() && !dest.getParentFile().mkdirs()) {
            log.error("Unable to create directory " + dest.getParentFile().getAbsolutePath());
            return;
        }
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", dest);
        } catch (IOException e) {
            log.error("Unable to save image to file " + dest.getAbsolutePath(), e);
        }
    }

    public static ImageComparisonResult compareImages(final Image source, final Image image) {
        final int sourceWidth = (int) source.getWidth();
        final int sourceHeight = (int) source.getHeight();
        final PixelReader sourceReader = source.getPixelReader();
        final PixelReader imageReader = image.getPixelReader();
        WritableImage delta = copyImage(source);

        final double notEqualPixelsCount = IntStream.range(0, sourceWidth) //
                .parallel() //
                .mapToLong(i -> IntStream.range(0, sourceHeight) //
                        .parallel() //
                        .filter(j -> sourceReader.getArgb(i, j) != imageReader.getArgb(i, j)) //
                        .peek(j -> delta.getPixelWriter().setColor(i, j, IMAGE_COMPARISON_DELTA_COLOR)) //
                        .count()) //
                .sum();

        return ImageComparisonResult.builder() //
                .diffImage(delta) //
                .similarity(PERCENT_100 - notEqualPixelsCount / (sourceWidth * sourceHeight) * PERCENT_100) //
                .build();
    }


}
