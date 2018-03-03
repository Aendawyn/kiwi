package fr.aquillet.kiwi.toolkit.rx;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import org.slf4j.Logger;

public class RxUtils {

    private RxUtils() {
        // utility class
    }

    public static <T> Consumer<T> nothingToDo() {
        return t -> {
            // nothing to do
        };
    }

    public static Action nothingToDoCompletable() {
        return () -> {
            // nothing to do
        };
    }

    public static Consumer<? super Throwable> logError(Logger logger) {
        return logError(logger, "An error occured.");
    }

    public static Consumer<? super Throwable> logError(Logger logger, String message) {
        return t -> logger.error(message, t);
    }
}
