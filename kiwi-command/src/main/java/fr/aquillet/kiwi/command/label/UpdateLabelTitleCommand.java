package fr.aquillet.kiwi.command.label;

import lombok.Value;

import java.util.UUID;

@Value
public class UpdateLabelTitleCommand {

	private final UUID labelId;
	private final String title;
	
}
