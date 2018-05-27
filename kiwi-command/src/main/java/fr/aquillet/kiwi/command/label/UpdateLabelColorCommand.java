package fr.aquillet.kiwi.command.label;

import lombok.Value;

import java.util.UUID;

@Value
public class UpdateLabelColorCommand {

	private final UUID labelId;
	private final String color;
	
}
