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
public class MouseScrollEvent implements INativeEvent {

	private final MouseEventType eventType = MouseEventType.MOUSE_SCROLL;
	private int scroll;
	private long time;
	
	public boolean isDown() {
		return scroll > 0;
	}
	
}
