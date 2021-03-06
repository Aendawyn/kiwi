package fr.aquillet.kiwi.event.launcher;

import lombok.Value;

import java.util.UUID;

@Value
public class LauncherTitleUpdatedEvent {

    private UUID launcherId;
    private String title;

}
