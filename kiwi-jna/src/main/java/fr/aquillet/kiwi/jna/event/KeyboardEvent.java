package fr.aquillet.kiwi.jna.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class KeyboardEvent implements INativeEvent {

	private KeyboardEventType eventType;
	private int virtualKeyCode;
	private int scanCode;
	private int flags;
	private long time; 
	
}
