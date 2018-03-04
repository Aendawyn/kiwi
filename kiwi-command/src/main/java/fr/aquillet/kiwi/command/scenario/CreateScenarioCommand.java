package fr.aquillet.kiwi.command.scenario;

import fr.aquillet.kiwi.model.Capture;
import fr.aquillet.kiwi.model.IScenarioEvent;
import lombok.Value;

import java.util.List;
import java.util.Optional;

@Value
public class CreateScenarioCommand {

    private final String title;
    private final Optional<String> labelId;
    private final List<IScenarioEvent> events;
    private final Capture endCapture;

}
