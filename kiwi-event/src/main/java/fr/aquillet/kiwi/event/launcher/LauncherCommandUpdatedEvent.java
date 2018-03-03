package fr.aquillet.kiwi.event.launcher;

import lombok.Value;

import java.util.UUID;

@Value
public class LauncherCommandUpdatedEvent {
	
	private UUID id;
	private String command;

}
