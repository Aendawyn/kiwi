package fr.aquillet.kiwi.ui.view.dashboard.scenario;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.launcher.ReloadLaunchersCommand;
import fr.aquillet.kiwi.command.scenario.ReloadScenariosCommand;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.launcher.LauncherCreatedEvent;
import fr.aquillet.kiwi.event.launcher.LaunchersReloadedEvent;
import fr.aquillet.kiwi.event.scenario.ScenarioCreatedEvent;
import fr.aquillet.kiwi.event.scenario.ScenariosReloadedEvent;
import fr.aquillet.kiwi.jna.JnaService;
import fr.aquillet.kiwi.jna.event.KeyboardEvent;
import fr.aquillet.kiwi.model.Capture;
import fr.aquillet.kiwi.model.Scenario;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import fr.aquillet.kiwi.toolkit.rx.RxUtils;
import fr.aquillet.kiwi.ui.service.launcher.ILauncherService;
import fr.aquillet.kiwi.ui.service.scenario.IScenarioService;
import fr.aquillet.kiwi.ui.view.capture.CaptureComparisonView;
import fr.aquillet.kiwi.ui.view.capture.CaptureComparisonViewModel;
import fr.aquillet.kiwi.ui.view.launcher.LauncherListViewModel;
import fr.aquillet.kiwi.ui.view.scenario.ScenarioViewModel;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class DashboardScenarioViewModel implements ViewModel {

    private ObservableList<ScenarioViewModel> scenarios = FXCollections.observableArrayList();
    private ObservableList<LauncherListViewModel> launchers = FXCollections.observableArrayList();
    private ObjectProperty<LauncherListViewModel> selectedLauncher = new SimpleObjectProperty<>();
    private DoubleProperty replaySpeed = new SimpleDoubleProperty();
    private BooleanProperty runScenarioPrecondition = new SimpleBooleanProperty();

    @Inject
    private JnaService jnaService;
    @Inject
    private ILauncherService launcherService;
    @Inject
    private IScenarioService scenarioService;
    @Inject
    private NotificationCenter notificationCenter;

    public void initialize() {
        runScenarioPrecondition.bind(selectedLauncher.isNotNull());
        notificationCenter.subscribe(Events.LAUNCHER, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
        notificationCenter.publish(Commands.LAUNCHER, new ReloadLaunchersCommand());
        notificationCenter.subscribe(Events.SCENARIO, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
        notificationCenter.publish(Commands.SCENARIO, new ReloadScenariosCommand());
    }

    public ObservableList<LauncherListViewModel> launchersProperty() {
        return launchers;
    }

    public ObjectProperty<LauncherListViewModel> selectedLauncherProperty() {
        return selectedLauncher;
    }

    public ObservableList<ScenarioViewModel> scenariosProperty() {
        return scenarios;
    }

    public DoubleProperty replaySpeedProperty() {
        return replaySpeed;
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(ScenarioCreatedEvent event) {
        reloadScenarios();
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(ScenariosReloadedEvent event) {
        reloadScenarios();
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(LauncherCreatedEvent event) {
        reloadLaunchers();
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(LaunchersReloadedEvent event) {
        reloadLaunchers();
    }

    public Command runScenrarioCommand(ScenarioViewModel scenario) {
        return new DelegateCommand(() -> new Action() {

            @Override
            protected void action() {
                double speed = replaySpeed.get();
                scenarioService.getScenarioById(scenario.idProperty().get()) //
                        .ifPresent(scenario -> {
                            List<Observable<?>> steps = scenario.getNativeEvents().stream() //
                                    .map(event -> jnaService.sendNativeEventinput(event)
                                            .delay((int) (event.getTime() / speed), TimeUnit.MILLISECONDS))
                                    .map(Completable::toObservable) //
                                    .collect(Collectors.toList());

                            launcherService.getLauncherById(selectedLauncher.get().idProperty().get()) //
                                    .ifPresent(launcher -> {
                                        CompositeDisposable disposables = new CompositeDisposable();
                                        disposables.add(jnaService //
                                                .runApplication(launcher.getCommand(), launcher.getWorkingDirectory(),
                                                        launcher.getStartDelaySecond())
                                                .flatMap(process -> Observable.concat(steps)
                                                        .doOnComplete(() -> { //
                                                            disposables.clear();
                                                            compareWithCapture(scenario);
                                                        }) //
                                                        .doOnTerminate(process::destroy)) //
                                                .mergeWith(jnaService
                                                        .getNativeEventsStream() //
                                                        .ofType(KeyboardEvent.class) //
                                                        .filter(event -> event.getVirtualKeyCode() == 27) //
                                                        .buffer(800, TimeUnit.MILLISECONDS) //
                                                        .switchMap(buffer -> { //
                                                            if (buffer.size() >= 3) {
                                                                return Observable
                                                                        .error(new RuntimeException("Stopped by user"));
                                                            }
                                                            return Observable.empty();
                                                        })) //
                                                .subscribe(RxUtils.nothingToDo(), RxUtils.logError(log)));
                                    });
                        });
            }

        }, runScenarioPrecondition, true);
    }

    private void reloadLaunchers() {
        launchers.setAll(launcherService.getLaunchers().stream() //
                .map(LauncherListViewModel::new) //
                .collect(Collectors.toList()));
    }

    private void reloadScenarios() {
        scenarios.setAll(scenarioService.getScenarios().stream() //
                .map(ScenarioViewModel::new) //
                .collect(Collectors.toList()));
    }

    private void compareWithCapture(Scenario scenario) {
        try {
            Capture endCapture = scenario.getEndCapture();
            WritableImage screenShot = takeScreenShot();
            PixelReader reader = screenShot.getPixelReader();
            WritableImage croppedImage = new WritableImage(reader, endCapture.getX(), endCapture.getY(),
                    endCapture.getWidth(), endCapture.getHeight());
            Image reference = new Image(new ByteArrayInputStream(endCapture.getContent()));

            Platform.runLater(() -> {
                Stage stage = new Stage();
                stage.setTitle("Résultat du scénario");
                ViewTuple<CaptureComparisonView, CaptureComparisonViewModel> viewTuple = FluentViewLoader
                        .fxmlView(CaptureComparisonView.class).load();
                viewTuple.getViewModel().originalProperty().set(reference);
                viewTuple.getViewModel().sourceProperty().set(croppedImage);
                Scene scene = new Scene(viewTuple.getView());
                scene.getStylesheets().add("style/default-style.css");
                stage.setScene(scene);
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/kiwi_logo.png")));
                stage.setResizable(false);
                stage.show();

            });

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private WritableImage takeScreenShot() {
        try {
            BufferedImage screenCapture = new Robot().createScreenCapture(jnaService.getForegroundWindowBounds());
            return SwingFXUtils.toFXImage(screenCapture, null);
        } catch (HeadlessException | AWTException e) {
            e.printStackTrace();
        }
        return null;
    }

}
