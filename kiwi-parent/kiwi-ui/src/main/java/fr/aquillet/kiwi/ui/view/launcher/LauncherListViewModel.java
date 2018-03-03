package fr.aquillet.kiwi.ui.view.launcher;

import de.saxsys.mvvmfx.ViewModel;
import fr.aquillet.kiwi.model.Launcher;
import javafx.beans.property.*;

import java.util.UUID;

public class LauncherListViewModel implements ViewModel {

    private ObjectProperty<UUID> id = new SimpleObjectProperty<>();
    private StringProperty title = new SimpleStringProperty();

    public LauncherListViewModel(Launcher launcher) {
        id.set(launcher.getId());
        title.set(launcher.getTitle());
    }

    public ReadOnlyObjectProperty<UUID> idProperty() {
        return id;
    }

    public StringProperty titleProperty() {
        return title;
    }

}
