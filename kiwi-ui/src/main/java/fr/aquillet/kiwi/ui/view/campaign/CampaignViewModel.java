package fr.aquillet.kiwi.ui.view.campaign;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.internal.viewloader.DependencyInjector;
import fr.aquillet.kiwi.model.Campaign;
import fr.aquillet.kiwi.ui.service.label.ILabelService;
import fr.aquillet.kiwi.ui.service.scenario.IScenarioService;
import fr.aquillet.kiwi.ui.view.label.LabelListViewModel;
import fr.aquillet.kiwi.ui.view.scenario.ScenarioViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CampaignViewModel extends RecursiveTreeObject<CampaignViewModel> implements ViewModel {

    private Campaign campaign;
    private ObjectProperty<UUID> id = new SimpleObjectProperty<>();
    private StringProperty title = new SimpleStringProperty();
    private ObjectProperty<Optional<LabelListViewModel>> label = new SimpleObjectProperty<>();
    private IntegerProperty scenariosCount = new SimpleIntegerProperty();
    private ObservableList<ScenarioViewModel> scenarios = FXCollections.observableArrayList();
    private LongProperty durationSeconds = new SimpleLongProperty();

    private ILabelService labelService;
    private IScenarioService scenarioService;

    public CampaignViewModel(Campaign campaign) {
        this.campaign = campaign;
        labelService = DependencyInjector.getInstance().getInstanceOf(ILabelService.class);
        scenarioService = DependencyInjector.getInstance().getInstanceOf(IScenarioService.class);
        initialize();
    }

    public void initialize() {
        id.set(campaign.getId());
        title.set(campaign.getTitle());
        label.set(campaign.getLabelId() //
                .flatMap(id -> labelService.getLabelById(id)) //
                .map(LabelListViewModel::new));
        scenarios.addAll(campaign.getScenarioIds().stream()
                .map(scenarioService::getScenarioById) //
                .filter(Optional::isPresent) //
                .map(Optional::get) //
                .map(ScenarioViewModel::new) //
                .collect(Collectors.toList()));
        scenariosCount.set(scenarios.size());
        durationSeconds.set(scenarios.stream() //
                .map(ScenarioViewModel::durationSecondsProperty) //
                .mapToLong(LongProperty::getValue) //
                .sum());
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

    public IntegerProperty scenariosCountProperty() {
        return scenariosCount;
    }

    public LongProperty durationSecondsProperty() {
        return durationSeconds;
    }

    public ObservableList<ScenarioViewModel> scenariosProperty() {
        return scenarios;
    }

    @Override
    public ObservableList<CampaignViewModel> getChildren() {
        return FXCollections.emptyObservableList();
    }

}
