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
public class MouseEvent implements INativeEvent {

	private MouseEventType eventType;
	private long x;
	private long y;
	private int flags;
	private long time; 
	
}
