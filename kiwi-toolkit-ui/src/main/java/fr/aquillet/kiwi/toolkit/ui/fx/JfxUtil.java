package fr.aquillet.kiwi.toolkit.ui.fx;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class JfxUtil {

    private JfxUtil() {
        // utility class
    }

    public static <T> void copyValueOnce(ObservableValue<T> from, Property<T> to) {
        copyValueOnce(from, to, value -> value);
    }

    public static <T, R> void copyValueOnce(ObservableValue<T> from, Property<R> to, Function<T, R> converter) {
        AtomicReference<ChangeListener<T>> reference = new AtomicReference<>();
        ChangeListener<T> listener = (observable, oldValue, newValue) -> {
            to.setValue(converter.apply(newValue));
            from.removeListener(reference.get());
        };
        reference.set(listener);
        from.addListener(listener);
    }


    public static <T> void copyValueOnFocusLoss(Node node, ObservableValue<T> from, Property<T> to) {
        copyValueOnFocusLoss(node, from, to, value -> value);
    }

    public static <T, R> void copyValueOnFocusLoss(Node node, ObservableValue<T> from, Property<R> to, Function<T, R> converter) {
        node.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                to.setValue(converter.apply(from.getValue()));
            }
        });
    }

}
