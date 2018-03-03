package fr.aquillet.kiwi.ui.service.persistence;

import fr.aquillet.kiwi.model.Application;
import fr.aquillet.kiwi.model.Launcher;

import java.util.List;

public interface ILauncherPersistenceService {

    List<Launcher> getApplicationLaunchers(Application application);
}
