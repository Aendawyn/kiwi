package fr.aquillet.kiwi.toolkit.ui.notification;

import javafx.application.Platform;
import javafx.geometry.Pos;
import org.controlsfx.control.Notifications;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationUtil {

    private NotificationUtil() {
        // utility class
    }

    public static void showInfoNotification(String title, List<String> content, int hideAfterInSeconds) {
        Platform.runLater(() -> createNotification(title, content, hideAfterInSeconds).showInformation());
    }

    public static void showWarningNotification(String title, List<String> content, int hideAfterInSeconds) {
        Platform.runLater(() -> createNotification(title, content, hideAfterInSeconds).showWarning());
    }

    public static void showErrorNotification(String title, List<String> content, int hideAfterInSeconds) {
        Platform.runLater(() -> createNotification(title, content, hideAfterInSeconds).showError());
    }

    private static Notifications createNotification(String title, //
                                                    List<String> content, //
                                                    int hideAfterInSeconds) {
        return Notifications.create() //
                .hideAfter(javafx.util.Duration.seconds(hideAfterInSeconds)) //
                .position(Pos.BOTTOM_RIGHT) //
                .hideCloseButton() //
                .text(content.stream().collect(Collectors.joining(System.lineSeparator()))) //
                .title(title);
    }

}
