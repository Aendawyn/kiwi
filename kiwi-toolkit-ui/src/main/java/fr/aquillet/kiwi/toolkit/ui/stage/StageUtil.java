package fr.aquillet.kiwi.toolkit.ui.stage;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class StageUtil {

    private StageUtil() {
        // utility class
    }

    public static Stage createStage(String title, String iconPath, String styleSheetPath, Parent root) {
        Stage stage = new Stage();
        stage.setTitle(title);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(styleSheetPath);
        stage.setScene(scene);
        stage.getIcons().add(new Image(StageUtil.class.getResourceAsStream(iconPath)));
        stage.setResizable(false);
        return stage;
    }
}
