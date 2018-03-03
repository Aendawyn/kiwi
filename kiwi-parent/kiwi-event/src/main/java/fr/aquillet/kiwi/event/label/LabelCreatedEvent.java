package fr.aquillet.kiwi.event.label;

import fr.aquillet.kiwi.model.Label;
import lombok.Value;

@Value
public class LabelCreatedEvent {

    private final Label label;
}
