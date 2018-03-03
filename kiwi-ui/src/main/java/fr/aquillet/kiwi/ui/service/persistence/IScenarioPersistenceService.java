package fr.aquillet.kiwi.ui.service.persistence;

import fr.aquillet.kiwi.model.Application;
import fr.aquillet.kiwi.model.Scenario;

import java.util.List;

public interface IScenarioPersistenceService {

    List<Scenario> getApplicationScenarios(Application application);
}
