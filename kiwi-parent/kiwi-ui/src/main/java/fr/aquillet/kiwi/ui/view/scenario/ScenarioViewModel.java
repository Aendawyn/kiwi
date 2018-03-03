package fr.aquillet.kiwi.ui.view.scenario;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.internal.viewloader.DependencyInjector;
import fr.aquillet.kiwi.jna.event.INativeEvent;
import fr.aquillet.kiwi.model.Scenario;
import fr.aquillet.kiwi.ui.service.label.ILabelService;
import fr.aquillet.kiwi.ui.view.label.LabelListViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Optional;
import java.util.UUID;

public class ScenarioViewModel extends RecursiveTreeObject<ScenarioViewModel> implements ViewModel {

    private Scenario scenario;
    private ObjectProperty<UUID> id = new SimpleObjectProperty<>();
    private StringProperty title = new SimpleStringProperty();
    private ObjectProperty<Optional<LabelListViewModel>> label = new SimpleObjectProperty<>();
    private IntegerProperty stepsCount = new SimpleIntegerProperty();
    private LongProperty durationSeconds = new SimpleLongProperty();

    private ILabelService labelService;

    public ScenarioViewModel(Scenario scenario) {
        this.scenario = scenario;
        labelService = DependencyInjector.getInstance().getInstanceOf(ILabelService.class);
        initialize();
    }

    public void initialize() {
        id.set(scenario.getId());
        title.set(scenario.getTitle());
        label.set(scenario.getLabelId() //
                .flatMap(id -> labelService.getLabelById(UUID.fromString(id))) //
                .map(LabelListViewModel::new));
        stepsCount.set(scenario.getNativeEvents().size());
        durationSeconds.set(scenario.getNativeEvents().stream() //
                .mapToLong(INativeEvent::getTime).sum());
    }

    public ReadOnlyObjectProperty<UUID> idProperty() {
        return id;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public ObjectProperty<Optional<LabelListViewModel>> labelProperty() {
        return label;
    }

    public IntegerProperty stepsCountProperty() {
        return stepsCount;
    }

    public LongProperty durationSecondsProperty() {
        return durationSeconds;
    }

    @Override
    public ObservableList<ScenarioViewModel> getChildren() {
        return FXCollections.emptyObservableList();
    }

}
