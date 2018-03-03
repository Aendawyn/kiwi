package fr.aquillet.kiwi.ui.service.persistence;

import fr.aquillet.kiwi.model.Application;
import fr.aquillet.kiwi.model.Label;
import fr.aquillet.kiwi.model.Launcher;
import fr.aquillet.kiwi.model.Scenario;

import java.util.List;

public interface IPersistenceService {

    List<Application> getApplications();

    List<Launcher> getLaunchersForCurrentApplication();

    List<Label> getLabelsForCurrentApplication();

    List<Scenario> getScenariosForCurrentApplication();

}
