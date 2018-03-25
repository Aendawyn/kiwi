package fr.aquillet.kiwi.ui.view.scenario;

import de.saxsys.mvvmfx.ViewModel;
import fr.aquillet.kiwi.model.Label;
import fr.aquillet.kiwi.model.Scenario;
import javafx.beans.property.*;

import java.util.Optional;
import java.util.UUID;

public class ScenarioListViewModel implements ViewModel {

    private ObjectProperty<UUID> id = new SimpleObjectProperty<>();
    private StringProperty title = new SimpleStringProperty();
    private ObjectProperty<Optional<Label>> label = new SimpleObjectProperty<>(Optional.empty());

    public ScenarioListViewModel(Scenario scenario, Optional<Label> label) {
        id.set(scenario.getId());
        title.set(scenario.getTitle());
        this.label.set(label);
    }

    public ReadOnlyObjectProperty<UUID> idProperty() {
        return id;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public ObjectProperty<Optional<Label>> labelProperty() {
        return label;
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
