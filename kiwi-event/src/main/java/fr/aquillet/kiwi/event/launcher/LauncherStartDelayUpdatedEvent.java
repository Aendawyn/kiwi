package fr.aquillet.kiwi.event.launcher;

import lombok.Value;

import java.util.UUID;

@Value
public class LauncherStartDelayUpdatedEvent {
	
	private UUID id;
	private int startDelay;

}
