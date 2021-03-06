package fr.aquillet.kiwi.model;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class CampaignExecutionResult {

    @NonNull
    private UUID campaignId;
    @NonNull
    private String campaignLabel;
    @NonNull
    private Instant startDate;
    @NonNull
    private Instant endDate;
    @NonNull
    private ExecutionStatus status;
    private double successRate;
    private int scenariosCount;
    @NonNull
    private List<ScenarioExecutionResult> scenarioResults;

}
