package fr.aquillet.kiwi.ui.service.launcher;

import fr.aquillet.kiwi.model.Launcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LauncherService implements ILauncherService {

    private List<Launcher> launchers = new ArrayList<>();

    @Override
    public List<Launcher> getLaunchers() {
        return launchers;
    }

    @Override
    public Launcher createLauncher(String title, String command, String windowTitle, String windowClass,
                                   String workingDirectory, int startDelaySec) {
        return new Launcher(UUID.randomUUID(), title, command, windowTitle, windowClass, workingDirectory,
                startDelaySec);
    }

    @Override
    public Optional<Launcher> getLauncherById(UUID id) {
        return launchers.stream() //
                .filter(launcher -> launcher.getId().equals(id)) //
                .findFirst();
    }

}
