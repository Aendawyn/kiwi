package fr.aquillet.kiwi.event.application;

import fr.aquillet.kiwi.model.Application;
import lombok.Value;

@Value
public class ApplicationCreatedEvent {

    private final Application application;
}
