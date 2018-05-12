package fr.aquillet.kiwi.ui.service.executor;

import fr.aquillet.kiwi.jna.JnaService;
import fr.aquillet.kiwi.jna.event.KeyboardEvent;
import fr.aquillet.kiwi.model.*;
import fr.aquillet.kiwi.toolkit.lang.Java8Util;
import fr.aquillet.kiwi.toolkit.ui.fx.ImageComparisonResult;
import fr.aquillet.kiwi.toolkit.ui.fx.ImageUtil;
import fr.aquillet.kiwi.ui.service.launcher.ILauncherService;
import fr.aquillet.kiwi.ui.service.scenario.IScenarioService;
import io.reactivex.Completable;
import io.reactivex.Observable;
import javafx.scene.image.Image;
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
    public Observable<ScenarioExecutionResult> executeScenario(UUID launcherId, UUID scenarioId, double speedFactor, boolean launcherAlreadyActive) {
        return Observable.defer(() -> {
            Optional<Launcher> launcherOpt = launcherService.getLauncherById(launcherId);
            Optional<Scenario> scenarioOpt = scenarioService.getScenarioById(scenarioId);
            if (!launcherOpt.isPresent()) {
                log.error("Launcher {} not found", launcherId);
                return Observable.error(new NoSuchElementException("launcher"));
            }
            if (!scenarioOpt.isPresent()) {
                log.error("Scenario {} not found", scenarioId);
                return Observable.error(new NoSuchElementException("scenario"));
            }
            Launcher launcher = launcherOpt.get();
            Scenario scenario = scenarioOpt.get();

            List<Observable<ScenarioExecutionResult>> steps = scenario.getEvents().stream() //
                    .map(event -> scenarioEventToCommand(event) //
                            .delay((int) (event.getTime() / speedFactor), TimeUnit.MILLISECONDS))
                    .map(Completable::<ScenarioExecutionResult>toObservable) //
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

            if (launcherAlreadyActive) {
                return jnaService.bringApplicationForeground(launcher.getWindowTitle())
                        .andThen(Observable.concat(steps) //
                                .concatWith(createExecutionResult(scenario)) //
                                .takeUntil(escapeSequenceObs) //
                                .switchIfEmpty(Observable.just(createAbortedExecutionResult(scenario))));
            } else {
                return jnaService.runApplication(launcher.getCommand(), launcher.getWorkingDirectory(), launcher.getStartDelaySecond()) //
                        .flatMap(appProcess -> Observable.concat(steps) //
                                .concatWith(createExecutionResult(scenario)) //
                                .takeUntil(escapeSequenceObs) //
                                .switchIfEmpty(Observable.just(createAbortedExecutionResult(scenario))));
            }

        });
    }

    private Completable scenarioEventToCommand(IScenarioEvent event) {
        if (event instanceof ScenarioNativeEvent) {
            return jnaService.sendNativeEventInput(((ScenarioNativeEvent) event).getNativeEvent());
        }
        log.warn("Unknown scenario event type {}", event.getClass().getName());
        return Completable.complete();
    }

    private Observable<ScenarioExecutionResult> createExecutionResult(Scenario scenario) {
        return Observable.defer(() -> {
            ScenarioExecutionResult.ScenarioExecutionResultBuilder builder = ScenarioExecutionResult.builder()
                    .scenarioId(scenario.getId()) //
                    .scenarioLabel(scenario.getTitle()) //
                    .toleranceThresold(0);
            Java8Util.ifPresentOrElse( //
                    ImageUtil.takeForegroundApplicationScreenShot(jnaService.getForegroundWindowBounds()), //
                    screenShot -> { //
                        Capture capture = scenario.getEndCapture();
                        Image croppedImage = ImageUtil.cropImage(screenShot, capture.getX(), capture.getY(), capture.getWidth(), capture.getHeight());
                        Image reference = ImageUtil.createImageFrom(capture.getContent());
                        ImageComparisonResult imageComparisonResult = ImageUtil.compareImages(reference, croppedImage);
                        builder.score(imageComparisonResult.getSimilarity());
                        builder.status(imageComparisonResult.getSimilarity() == 100 ? ExecutionStatus.SUCCESS : ExecutionStatus.FAILURE);
                    }, () -> {
                        builder.score(0);
                        builder.status(ExecutionStatus.UNKNOWN);
                    });
            return Observable.just(builder.build());
        });
    }

    private ScenarioExecutionResult createAbortedExecutionResult(Scenario scenario) {
        return ScenarioExecutionResult.builder() //
                .scenarioId(scenario.getId()) //
                .scenarioLabel(scenario.getTitle()) //
                .score(0) //
                .toleranceThresold(0) //
                .status(ExecutionStatus.ABORTED) //
                .build();
    }

}
