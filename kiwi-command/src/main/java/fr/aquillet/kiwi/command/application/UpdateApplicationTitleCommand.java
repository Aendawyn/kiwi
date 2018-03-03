package fr.aquillet.kiwi.command.application;

import lombok.Value;

import java.util.UUID;

@Value
public class UpdateApplicationTitleCommand {

	private UUID id;
	private String title;
}
