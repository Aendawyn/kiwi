package fr.aquillet.kiwi.command.launcher;

import lombok.Value;

import java.util.UUID;

@Value
public class UpdateLauncherCommandCommand {

	private UUID id;
	private String command;
}
