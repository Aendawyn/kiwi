package fr.aquillet.kiwi.ui.service.scenario;

import fr.aquillet.kiwi.jna.event.INativeEvent;
import fr.aquillet.kiwi.model.Capture;
import fr.aquillet.kiwi.model.Scenario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IScenarioService {

    List<Scenario> getScenarios();

    Scenario createScenario(String title, Optional<String> labelId, List<INativeEvent> events, Capture endCapture);

    Optional<Scenario> getScenarioById(UUID id);

}
