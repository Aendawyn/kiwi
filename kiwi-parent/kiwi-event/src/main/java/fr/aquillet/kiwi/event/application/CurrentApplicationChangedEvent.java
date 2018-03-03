package fr.aquillet.kiwi.event.application;

import fr.aquillet.kiwi.model.Application;
import lombok.Value;

@Value
public class CurrentApplicationChangedEvent {

    private Application application;

}
