package fr.aquillet.kiwi.jna.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ //
		@Type(value = KeyboardEvent.class), //
		@Type(value = MouseEvent.class), //
		@Type(value = MouseScrollEvent.class), //
		@Type(value = PauseEvent.class) //
})
public interface INativeEvent {

	long getTime();

	void setTime(long time);

}
