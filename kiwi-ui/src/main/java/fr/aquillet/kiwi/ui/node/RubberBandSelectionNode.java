package fr.aquillet.kiwi.ui.node;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

public class RubberBandSelectionNode extends Group {

    private static final Color SELECTION_STROKE_COLOR = Color.BLUE;
    private static final Color SELECTION_FILL_COLOR = Color.LIGHTBLUE.deriveColor(0, 1.3, 1, 0.5);
    private static final double SELECTION_STROKE_WIDTH = 1d;

    private final DragContext dragContext = new DragContext();
    private final Rectangle rect = new Rectangle(0, 0, 0, 0);

    public RubberBandSelectionNode(Node... children) {
        super(children);
        rect.setStroke(SELECTION_STROKE_COLOR);
        rect.setStrokeWidth(SELECTION_STROKE_WIDTH);
        rect.setStrokeLineCap(StrokeLineCap.ROUND);
        rect.setFill(SELECTION_FILL_COLOR);

        addEventHandler(MouseEvent.MOUSE_PRESSED, getOnMousePressedEventHandler());
        addEventHandler(MouseEvent.MOUSE_DRAGGED, getOnMouseDraggedEventHandler());
    }

    public Bounds getBounds() {
        return rect.getBoundsInParent();
    }

    private EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return event -> {
            if (event.isSecondaryButtonDown()) {
                return;
            }

            double offsetX = event.getX() - dragContext.mouseAnchorX;
            double offsetY = event.getY() - dragContext.mouseAnchorY;

            if (offsetX > 0) {
                rect.setWidth(offsetX);
            } else {
                rect.setX(event.getX());
                rect.setWidth(dragContext.mouseAnchorX - rect.getX());
            }

            if (offsetY > 0) {
                rect.setHeight(offsetY);
            } else {
                rect.setY(event.getY());
                rect.setHeight(dragContext.mouseAnchorY - rect.getY());
            }
        };
    }

    private EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return event -> {
            if (event.isSecondaryButtonDown()) {
                return;
            }
            rect.setX(0);
            rect.setY(0);
            rect.setWidth(0);
            rect.setHeight(0);
            getChildren().remove(rect);

            dragContext.mouseAnchorX = event.getX();
            dragContext.mouseAnchorY = event.getY();

            rect.setX(dragContext.mouseAnchorX);
            rect.setY(dragContext.mouseAnchorY);
            rect.setWidth(0);
            rect.setHeight(0);
            getChildren().add(rect);
        };
    }

    private static final class DragContext {
        private double mouseAnchorX;
        private double mouseAnchorY;

    }
}
