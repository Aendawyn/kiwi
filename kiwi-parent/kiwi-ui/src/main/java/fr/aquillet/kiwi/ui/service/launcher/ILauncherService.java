package fr.aquillet.kiwi.ui.service.launcher;

import fr.aquillet.kiwi.model.Launcher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ILauncherService {

    List<Launcher> getLaunchers();

    Launcher createLauncher(String title, String command, String windowTitle, String windowClass, String workingDirectory, int startDelaySec);

    Optional<Launcher> getLauncherById(UUID id);

}
