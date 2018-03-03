package fr.aquillet.kiwi.command.label;

import lombok.Value;

@Value
public class CreateLabelCommand {

	private final String title;
	private final String hexColor;
	
}
