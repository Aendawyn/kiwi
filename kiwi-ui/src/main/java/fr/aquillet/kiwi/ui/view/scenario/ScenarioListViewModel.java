package fr.aquillet.kiwi.ui.view.scenario;

import de.saxsys.mvvmfx.ViewModel;
import fr.aquillet.kiwi.model.Scenario;
import javafx.beans.property.*;

import java.util.UUID;

public class ScenarioListViewModel implements ViewModel {

    private ObjectProperty<UUID> id = new SimpleObjectProperty<>();
    private StringProperty title = new SimpleStringProperty();

    public ScenarioListViewModel(Scenario scenario) {
        id.set(scenario.getId());
        title.set(scenario.getTitle());
    }

    public ReadOnlyObjectProperty<UUID> idProperty() {
        return id;
    }

    public StringProperty titleProperty() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ScenarioListViewModel)) {
            return false;
        }

        return id.get().equals(((ScenarioListViewModel) obj).id.get());
    }

    @Override
    public int hashCode() {
        return id.get().hashCode();
    }
}
