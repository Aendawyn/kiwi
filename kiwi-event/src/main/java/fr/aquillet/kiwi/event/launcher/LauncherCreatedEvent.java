package fr.aquillet.kiwi.event.launcher;

import fr.aquillet.kiwi.model.Launcher;
import lombok.Value;

@Value
public class LauncherCreatedEvent {

    private final Launcher laucher;
}
