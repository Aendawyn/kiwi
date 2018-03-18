package fr.aquillet.kiwi.ui.view.campaign.edition;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.campaign.AddScenarioToCampaignCommand;
import fr.aquillet.kiwi.command.campaign.RemoveScenarioFromCampaignCommand;
import fr.aquillet.kiwi.command.campaign.ReorderCampaignScenariosCommand;
import fr.aquillet.kiwi.model.Campaign;
import fr.aquillet.kiwi.ui.service.campaign.ICampaignService;
import fr.aquillet.kiwi.ui.service.scenario.IScenarioService;
import fr.aquillet.kiwi.ui.view.scenario.ScenarioListViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class EditCampaignViewModel implements ViewModel {

    private ObservableList<ScenarioListViewModel> campaignScenarios = FXCollections.observableArrayList();
    private ObservableList<ScenarioListViewModel> availableScenarios = FXCollections.observableArrayList();
    private BooleanProperty swaping = new SimpleBooleanProperty(false);

    @Inject
    private IScenarioService scenarioService;
    @Inject
    private ICampaignService campaignService;
    @Inject
    private NotificationCenter notificationCenter;

    public void setArguments(UUID campaignId) {
        campaignScenarios.setAll(campaignService.getCampaignById(campaignId) //
                .map(Campaign::getScenarioIds) //
                .map(list -> list.stream() //
                        .map(scenarioService::getScenarioById) //
                        .filter(Optional::isPresent) //
                        .map(Optional::get) //
                        .map(ScenarioListViewModel::new) //
                        .collect(Collectors.toList())) //
                .orElse(Collections.emptyList()));

        availableScenarios.setAll(scenarioService.getScenarios().stream() //
                .filter(scenario -> campaignScenarios.stream().noneMatch(s -> s.idProperty().get().equals(scenario.getId()))) //
                .map(ScenarioListViewModel::new) //
                .collect(Collectors.toList()));

        campaignScenarios.addListener((ListChangeListener<? super ScenarioListViewModel>) c -> {
            while (c.next()) {
                c.getAddedSubList().stream() //
                        .filter(e -> !c.getRemoved().contains(e)) //
                        .forEach(scenario -> { //
                            availableScenarios.remove(scenario);
                            if (!swaping.get()) {
                                notificationCenter.publish(Commands.CAMPAIGN, new AddScenarioToCampaignCommand(campaignId, scenario.idProperty().get()));
                            }
                        });
                c.getRemoved().stream() //
                        .filter(e -> !c.getAddedSubList().contains(e)) //
                        .filter(e -> !campaignScenarios.contains(e)) // gestion du swap (swap = added + remove)
                        .forEach(scenario -> { //
                            availableScenarios.add(scenario);
                            if (!swaping.get()) {
                                notificationCenter.publish(Commands.CAMPAIGN, new RemoveScenarioFromCampaignCommand(campaignId, scenario.idProperty().get()));
                            }
                        });
            }
        });

        swapingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // Les scénarios ont été réordonnés
                notificationCenter.publish(Commands.CAMPAIGN, new ReorderCampaignScenariosCommand(campaignId, campaignScenarios.stream() //
                        .map(model -> model.idProperty().get()) //
                        .collect(Collectors.toList())));
            }
        });

    }

    public ObservableList<ScenarioListViewModel> availableScenariosProperty() {
        return availableScenarios;
    }

    public ObservableList<ScenarioListViewModel> campaignScenariosProperty() {
        return campaignScenarios;
    }

    public BooleanProperty swapingProperty() {
        return swaping;
    }

}
