package fr.aquillet.kiwi.ui.view.launcher.creation;

import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.jna.JnaService;
import fr.aquillet.kiwi.model.LauncherInfo;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.launcher.CreateLauncherCommand;
import fr.aquillet.kiwi.ui.view.launcher.info.LauncherInfoListViewModel;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import java.util.Objects;
import java.util.stream.Collectors;

public class CreateLauncherViewModel implements ViewModel, SceneLifecycle {

    private Command createLauncherCommand;
    private Command reloadLaunchersInfoCommand;
    private BooleanProperty createLauncherPrecondition = new SimpleBooleanProperty(false);
    private StringProperty launcherTitle = new SimpleStringProperty();
    private StringProperty launcherCommand = new SimpleStringProperty();
    private ObservableList<LauncherInfoListViewModel> launcherInfos = FXCollections.observableArrayList();
    private ObjectProperty<LauncherInfoListViewModel> selectedLauncherInfos = new SimpleObjectProperty<>();
    private StringProperty launcherWorkingDirectory = new SimpleStringProperty();
    private IntegerProperty launcherStartDelay = new SimpleIntegerProperty();

    @Inject
    private JnaService jnaService;
    @Inject
    private NotificationCenter notificationCenter;

    public void initialize() {
        createLauncherCommand = new DelegateCommand(this::createLauncherAction, //
                createLauncherPrecondition, true);
        reloadLaunchersInfoCommand = new DelegateCommand(this::reloadLaunchersInfosAction, true);

        createLauncherPrecondition.bind(launcherTitle.isNotEmpty() //
                .and(launcherCommand.isNotEmpty()) //
                .and(launcherWorkingDirectory.isNotEmpty()) //
                .and(selectedLauncherInfos.isNotNull()) //
                .and(launcherStartDelay.greaterThanOrEqualTo(0)));

        reloadLaunchersInfo();
    }

    public StringProperty launcherTitleProperty() {
        return launcherTitle;
    }

    public StringProperty launcherCommandProperty() {
        return launcherCommand;
    }

    public ObservableList<LauncherInfoListViewModel> launcherInfosProperty() {
        return launcherInfos;
    }

    public ObjectProperty<LauncherInfoListViewModel> selectedLauncherInfosProperty() {
        return selectedLauncherInfos;
    }

    public StringProperty launcherWorkingDirectoryProperty() {
        return launcherWorkingDirectory;
    }

    public IntegerProperty launcherStartDelayProperty() {
        return launcherStartDelay;
    }

    public Command getCreateLauncherCommand() {
        return createLauncherCommand;
    }

    public Command getReloadLaunchersInfosCommand() {
        return reloadLaunchersInfoCommand;
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

    private Action createLauncherAction() {
        return new Action() {

            @Override
            protected void action() throws Exception {
                notificationCenter.publish(Commands.LAUNCHER,
                        new CreateLauncherCommand(launcherTitle.get(), launcherCommand.get(),
                                selectedLauncherInfos.get().titleProperty().get(),
                                selectedLauncherInfos.get().classProperty().get(), launcherWorkingDirectory.get(),
                                launcherStartDelay.get()));
            }
        };
    }

    private Action reloadLaunchersInfosAction() {
        return new Action() {

            @Override
            protected void action() throws Exception {
                Platform.runLater(() -> reloadLaunchersInfo());
            }
        };
    }

    private void reloadLaunchersInfo() {
        launcherInfos.setAll(jnaService.getActiveWindows().stream() //
                .filter(Objects::nonNull) //
                .filter(window -> !window.getTitle().isEmpty()) //
                .map(window -> {
                    String className = jnaService.getWindowClassName(window.getHWND());
                    return new LauncherInfo(window.getTitle(), className);
                }).map(LauncherInfoListViewModel::new) //
                .collect(Collectors.toList()));
    }

}
