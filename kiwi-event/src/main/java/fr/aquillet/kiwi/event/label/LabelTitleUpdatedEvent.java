package fr.aquillet.kiwi.event.label;

import lombok.Value;

import java.util.UUID;

@Value
public class LabelTitleUpdatedEvent {

    private final UUID labelId;
    private final String title;
}
