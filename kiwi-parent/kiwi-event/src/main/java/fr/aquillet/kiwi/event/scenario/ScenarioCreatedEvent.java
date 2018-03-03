package fr.aquillet.kiwi.event.scenario;

import fr.aquillet.kiwi.model.Scenario;
import lombok.Value;

@Value
public class ScenarioCreatedEvent {

    private final Scenario scenario;
}
