package fr.aquillet.kiwi.event.application;

import lombok.Value;

import java.util.UUID;

@Value
public class ApplicationTitleUpdatedEvent {
	
	private UUID id;
	private String title;

}
