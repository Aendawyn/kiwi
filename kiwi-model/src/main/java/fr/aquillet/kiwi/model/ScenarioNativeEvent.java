package fr.aquillet.kiwi.model;

import fr.aquillet.kiwi.jna.event.INativeEvent;
import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ScenarioNativeEvent implements IScenarioEvent {

    @NonNull
    private INativeEvent nativeEvent;

    @Override
    public long getTime() {
        return nativeEvent.getTime();
    }

    @Override
    public void setTime(long time) {
        nativeEvent.setTime(time);
    }
}
