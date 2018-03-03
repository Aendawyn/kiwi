package fr.aquillet.kiwi.ui.view.launcher.edition;

import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.launcher.UpdateLauncherCommandCommand;
import fr.aquillet.kiwi.command.launcher.UpdateLauncherStartDelayCommand;
import fr.aquillet.kiwi.command.launcher.UpdateLauncherTitleCommand;
import fr.aquillet.kiwi.command.launcher.UpdateLauncherWorkingDirectoryCommand;
import fr.aquillet.kiwi.jna.JnaService;
import fr.aquillet.kiwi.toolkit.rx.RxUtils;
import fr.aquillet.kiwi.ui.service.launcher.ILauncherService;
import javafx.beans.property.*;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.UUID;

@Slf4j
public class EditLauncherViewModel implements ViewModel, SceneLifecycle {

    private Command testLauncherCommand;
    private BooleanProperty testLauncherPrecondition = new SimpleBooleanProperty(false);
    private StringProperty launcherTitle = new SimpleStringProperty("");
    private StringProperty launcherCommand = new SimpleStringProperty("");
    private StringProperty launcherWorkingDirectory = new SimpleStringProperty("");
    private IntegerProperty launcherStartDelay = new SimpleIntegerProperty(0);

    @Inject
    private JnaService jnaService;
    @Inject
    private ILauncherService launcherService;
    @Inject
    private NotificationCenter notificationCenter;

    public void initialize() {
        testLauncherCommand = new DelegateCommand(this::testLauncherAction, //
                testLauncherPrecondition, true);

        testLauncherPrecondition.bind(launcherTitle.isNotEmpty() //
                .and(launcherCommand.isNotEmpty()) //
                .and(launcherWorkingDirectory.isNotEmpty()) //
                .and(launcherStartDelay.greaterThanOrEqualTo(0)));
    }

    public void setArguments(UUID launcherId) {
        launcherService.getLauncherById(launcherId).ifPresent(launcher -> {
            launcherTitle.set(launcher.getTitle());
            launcherCommand.set(launcher.getCommand());
            launcherWorkingDirectory.set(launcher.getWorkingDirectory());
            launcherStartDelay.set(launcher.getStartDelaySecond());

            launcherTitle.addListener((obs, oldValue, newValue) -> notificationCenter.publish(Commands.LAUNCHER, new UpdateLauncherTitleCommand(launcher.getId(), newValue)));
            launcherCommand.addListener((obs, oldValue, newValue) -> notificationCenter.publish(Commands.LAUNCHER, new UpdateLauncherCommandCommand(launcher.getId(), newValue)));
            launcherWorkingDirectory.addListener((obs, oldValue, newValue) -> notificationCenter.publish(Commands.LAUNCHER, new UpdateLauncherWorkingDirectoryCommand(launcher.getId(), newValue)));
            launcherStartDelay.addListener((obs, oldValue, newValue) -> notificationCenter.publish(Commands.LAUNCHER, new UpdateLauncherStartDelayCommand(launcher.getId(), newValue.intValue())));
        });
    }

    public StringProperty launcherTitleProperty() {
        return launcherTitle;
    }

    public StringProperty launcherCommandProperty() {
        return launcherCommand;
    }

    public StringProperty launcherWorkingDirectoryProperty() {
        return launcherWorkingDirectory;
    }

    public IntegerProperty launcherStartDelayProperty() {
        return launcherStartDelay;
    }

    public Command getTestLauncherCommand() {
        return testLauncherCommand;
    }

    @Override
    public void onViewAdded() {
        launcherTitle.set("");
        launcherCommand.set("");
        launcherWorkingDirectory.set("");
    }

    @Override
    public void onViewRemoved() {
        // nothing
    }

    private Action testLauncherAction() {
        return new Action() {

            @Override
            protected void action() {
                jnaService.runApplication(launcherCommand.getValueSafe(), launcherWorkingDirectory.getValueSafe(), launcherStartDelay.get())
                        .subscribe(RxUtils.nothingToDo(), RxUtils.logError(log));
            }
        };
    }

}
