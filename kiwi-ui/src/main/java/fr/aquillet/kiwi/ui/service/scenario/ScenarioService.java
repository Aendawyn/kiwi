package fr.aquillet.kiwi.ui.service.scenario;

import fr.aquillet.kiwi.jna.event.INativeEvent;
import fr.aquillet.kiwi.model.Capture;
import fr.aquillet.kiwi.model.Scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ScenarioService implements IScenarioService {

    private List<Scenario> scenarios = new ArrayList<>();

    @Override
    public List<Scenario> getScenarios() {
        return scenarios;
    }

    @Override
    public Scenario createScenario(String title, Optional<String> labelId, List<INativeEvent> events, Capture endCapture) {
        return new Scenario(UUID.randomUUID(), title, labelId, events, endCapture);
    }

    @Override
    public Optional<Scenario> getScenarioById(UUID id) {
        return scenarios.stream() //
                .filter(scenario -> scenario.getId().equals(id)) //
                .findFirst();
    }

}
