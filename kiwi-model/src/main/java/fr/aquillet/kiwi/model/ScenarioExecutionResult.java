package fr.aquillet.kiwi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.image.Image;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ScenarioExecutionResult {

    @NonNull
    private UUID scenarioId;
    @NonNull
    private String scenarioLabel;
    @NonNull
    private ExecutionStatus status;
    private double score;
    private double toleranceThreshold;

    @JsonIgnore
    private Image originalCapture;
    @JsonIgnore
    private Image executionCapture;
    @JsonIgnore
    private Image diffCapture;

}
