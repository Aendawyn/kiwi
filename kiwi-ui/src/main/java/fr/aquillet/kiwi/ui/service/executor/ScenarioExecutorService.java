package fr.aquillet.kiwi.ui.service.executor;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import fr.aquillet.kiwi.jna.JnaService;
import fr.aquillet.kiwi.jna.event.KeyboardEvent;
import fr.aquillet.kiwi.model.*;
import fr.aquillet.kiwi.toolkit.ui.fx.ImageUtil;
import fr.aquillet.kiwi.ui.service.launcher.ILauncherService;
import fr.aquillet.kiwi.ui.service.scenario.IScenarioService;
import fr.aquillet.kiwi.ui.view.capture.CaptureComparisonView;
import fr.aquillet.kiwi.ui.view.capture.CaptureComparisonViewModel;
import io.reactivex.Completable;
import io.reactivex.Observable;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class ScenarioExecutorService implements IScenarioExecutorService {

    private static final int KEY_CODE_ESCAPE = 27;
    private static final int ESCAPE_SEQUENCE_COUNT = 3;
    private static final int ESCAPE_SEQUENCE_DELAY_MS = 800;

    private ILauncherService launcherService;
    private IScenarioService scenarioService;
    private JnaService jnaService;

    @Inject
    public void setDependencies(final ILauncherService launcherService, //
                                final IScenarioService scenarioService, //
                                final JnaService jnaService) {
        this.launcherService = launcherService;
        this.scenarioService = scenarioService;
        this.jnaService = jnaService;
    }

    @Override
    public Completable executeScenario(UUID launcherId, UUID scenarioId, double speedFactor) {
        return Completable.defer(() -> {
            Optional<Launcher> launcherOpt = launcherService.getLauncherById(launcherId);
            Optional<Scenario> scenarioOpt = scenarioService.getScenarioById(scenarioId);
            if (!launcherOpt.isPresent()) {
                log.error("Launcher {} not found", launcherId);
                return Completable.error(new NoSuchElementException("launcher"));
            }
            if (!scenarioOpt.isPresent()) {
                log.error("Scenario {} not found", scenarioId);
                return Completable.error(new NoSuchElementException("scenario"));
            }
            Launcher launcher = launcherOpt.get();
            Scenario scenario = scenarioOpt.get();

            List<Observable<?>> steps = scenario.getEvents().stream() //
                    .map(event -> scenarioEventToCommand(event) //
                            .delay((int) (event.getTime() / speedFactor), TimeUnit.MILLISECONDS))
                    .map(Completable::toObservable) //
                    .collect(Collectors.toList());

            Observable<Boolean> escapeSequenceObs = jnaService
                    .getNativeEventsStream() //
                    .ofType(KeyboardEvent.class) //
                    .filter(event -> event.getVirtualKeyCode() == KEY_CODE_ESCAPE) //
                    .buffer(ESCAPE_SEQUENCE_DELAY_MS, TimeUnit.MILLISECONDS) //
                    .switchMap(buffer -> { //
                        if (buffer.size() >= ESCAPE_SEQUENCE_COUNT) {
                            log.warn("Stopped by user");
                            return Observable.just(true);
                        }
                        return Observable.empty();
                    });

            return jnaService.runApplication(launcher.getCommand(), launcher.getWorkingDirectory(), launcher.getStartDelaySecond()) //
                    .flatMap(appProcess -> Observable.concat(steps) //
                            .doOnComplete(() -> compareWithCapture(scenario.getEndCapture())) //
                            .doOnTerminate(appProcess::destroy)) //
                    .takeUntil(escapeSequenceObs)
                    .ignoreElements();
        });
    }

    private Completable scenarioEventToCommand(IScenarioEvent event) {
        if (event instanceof ScenarioNativeEvent) {
            return jnaService.sendNativeEventInput(((ScenarioNativeEvent) event).getNativeEvent());
        }
        log.warn("Unknown scenario event type {}", event.getClass().getName());
        return Completable.complete();
    }

    private void compareWithCapture(Capture capture) {
        ImageUtil.takeForegroundApplicationScreenShot(jnaService.getForegroundWindowBounds()) //
                .ifPresent(screenShot -> {
                    Image croppedImage = ImageUtil.cropImage(screenShot, capture.getX(), capture.getY(), capture.getWidth(), capture.getHeight());
                    Image reference = ImageUtil.createImageFrom(capture.getContent());

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
                });
    }

}
