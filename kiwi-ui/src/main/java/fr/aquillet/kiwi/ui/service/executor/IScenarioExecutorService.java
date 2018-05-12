package fr.aquillet.kiwi.ui.service.executor;

import fr.aquillet.kiwi.model.ScenarioExecutionResult;
import io.reactivex.Observable;

import java.util.UUID;

public interface IScenarioExecutorService {

    default Observable<ScenarioExecutionResult> executeScenario(UUID launcherId, UUID scenarioId, double speedFactor) {
        return executeScenario(launcherId, scenarioId, speedFactor, false);
    }

    Observable<ScenarioExecutionResult> executeScenario(UUID launcherId, UUID scenarioId, double speedFactor, boolean launcherAlreadyActive);
}
