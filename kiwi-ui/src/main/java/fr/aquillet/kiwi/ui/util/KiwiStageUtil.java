package fr.aquillet.kiwi.ui.util;

import fr.aquillet.kiwi.toolkit.ui.stage.StageUtil;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class KiwiStageUtil {

    public static final String KIWI_STYLE_SHEET_PATH = "style/default-style.css";
    public static final String KIWI_ICON_PATH = "/images/kiwi_logo.png";

    private KiwiStageUtil() {
        // utility class
    }

    public static Stage createStage(String title, Parent root) {
        return StageUtil.createStage(title, KIWI_ICON_PATH, KIWI_STYLE_SHEET_PATH, root);
    }
}
