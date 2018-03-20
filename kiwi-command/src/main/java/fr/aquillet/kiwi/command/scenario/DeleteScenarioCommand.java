package fr.aquillet.kiwi.command.scenario;

import lombok.Value;

import java.util.UUID;

@Value
public class DeleteScenarioCommand {

    private final UUID id;

}
