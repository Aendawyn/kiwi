package fr.aquillet.kiwi.model;

import lombok.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Scenario {

    @NonNull
    UUID id;
    @NonNull
    private String title;
    private Optional<String> labelId;
    @NonNull
    private List<IScenarioEvent> events;
    @NonNull
    private Capture endCapture;

}
