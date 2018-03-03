package fr.aquillet.kiwi.ui.view.application;

import de.saxsys.mvvmfx.ViewModel;
import fr.aquillet.kiwi.model.Application;
import javafx.beans.property.*;

import java.util.UUID;

public class ApplicationListViewModel implements ViewModel {

    private ObjectProperty<UUID> id = new SimpleObjectProperty<>();
    private StringProperty title = new SimpleStringProperty();

    public ApplicationListViewModel(Application application) {
        title.set(application.getTitle());
        id.set(application.getId());
    }

    public ReadOnlyObjectProperty<UUID> idProperty() {
        return id;
    }

    public StringProperty titleProperty() {
        return title;
    }

}
