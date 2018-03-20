package fr.aquillet.kiwi.ui.service.executor;

import io.reactivex.Completable;

import java.util.UUID;

public interface IScenarioExecutorService {

    Completable executeScenario(UUID launcherId, UUID scenarioId, double speedFactor);

    Completable executeScenario(UUID launcherId, UUID scenarioId, double speedFactor, boolean launcherAlreadyActive);
}
