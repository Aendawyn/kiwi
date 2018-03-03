package fr.aquillet.kiwi.ui.view.label;

import de.saxsys.mvvmfx.ViewModel;
import fr.aquillet.kiwi.model.Label;
import javafx.beans.property.*;

import java.util.Objects;
import java.util.UUID;

public class LabelListViewModel implements ViewModel {

    private ObjectProperty<UUID> id = new SimpleObjectProperty<>();
    private StringProperty title = new SimpleStringProperty();
    private StringProperty color = new SimpleStringProperty();

    public LabelListViewModel(Label label) {
        id.set(label.getId());
        title.set(label.getTitle());
        color.set(label.getHexColor());
    }

    public ReadOnlyObjectProperty<UUID> idProperty() {
        return id;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty colorProperty() {
        return color;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof LabelListViewModel)) {
            return false;
        }

        return idProperty().get().equals(((LabelListViewModel) obj).idProperty().get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.get());
    }

}
