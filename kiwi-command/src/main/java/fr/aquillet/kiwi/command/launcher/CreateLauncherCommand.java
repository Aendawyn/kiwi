package fr.aquillet.kiwi.command.launcher;

import lombok.Value;

@Value
public class CreateLauncherCommand {

	private final String title;
	private final String command;
	private final String windowTitle;
	private final String windowClass;
	private final String workingDirectory;
	private final int startDelaySecond;
	
}
