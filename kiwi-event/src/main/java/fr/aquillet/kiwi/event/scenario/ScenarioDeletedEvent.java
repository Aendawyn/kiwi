package fr.aquillet.kiwi.event.scenario;

import lombok.Value;

import java.util.UUID;

@Value
public class ScenarioDeletedEvent {

    private final UUID id;
}
