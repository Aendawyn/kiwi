package fr.aquillet.kiwi.command.launcher;

import lombok.Value;

import java.util.UUID;

@Value
public class UpdateLauncherWorkingDirectoryCommand {

	private UUID id;
	private String workingDirectory;
}
