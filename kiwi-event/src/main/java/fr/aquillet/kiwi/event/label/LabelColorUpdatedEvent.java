package fr.aquillet.kiwi.event.label;

import lombok.Value;

import java.util.UUID;

@Value
public class LabelColorUpdatedEvent {

    private final UUID labelId;
    private final String color;
}
