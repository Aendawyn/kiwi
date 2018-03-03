package fr.aquillet.kiwi.ui.view.capture;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

public class CaptureComparisonViewModel implements ViewModel {

	private ObjectProperty<Image> original = new SimpleObjectProperty<>();
	private ObjectProperty<Image> source = new SimpleObjectProperty<>();

	public ObjectProperty<Image> originalProperty() {
		return original;
	}

	public ObjectProperty<Image> sourceProperty() {
		return source;
	}

}
