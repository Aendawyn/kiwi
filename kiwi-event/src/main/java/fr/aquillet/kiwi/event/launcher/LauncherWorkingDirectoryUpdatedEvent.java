package fr.aquillet.kiwi.event.launcher;

import lombok.Value;

import java.util.UUID;

@Value
public class LauncherWorkingDirectoryUpdatedEvent {
	
	private UUID id;
	private String workingDirectory;

}
