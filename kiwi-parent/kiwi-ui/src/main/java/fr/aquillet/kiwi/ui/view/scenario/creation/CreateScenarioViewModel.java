package fr.aquillet.kiwi.ui.view.scenario.creation;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.jna.JnaService;
import fr.aquillet.kiwi.jna.event.INativeEvent;
import fr.aquillet.kiwi.jna.event.KeyboardEvent;
import fr.aquillet.kiwi.jna.event.KeyboardEventType;
import fr.aquillet.kiwi.jna.event.PauseEvent;
import fr.aquillet.kiwi.model.Capture;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.scenario.CreateScenarioCommand;
import fr.aquillet.kiwi.toolkit.rx.RxUtils;
import fr.aquillet.kiwi.ui.service.label.ILabelService;
import fr.aquillet.kiwi.ui.view.label.LabelListViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class CreateScenarioViewModel implements ViewModel {

    private static final int KEYCODE_PRINT_SCREEN = 44;

    private Command createScenarioCommand;
    private Command captureCommand;
    private BooleanProperty createScenarioPrecondition = new SimpleBooleanProperty(false);
    private StringProperty scenarioTitle = new SimpleStringProperty();
    private ObjectProperty<Optional<LabelListViewModel>> scenarioLabel = new SimpleObjectProperty<>();
    private ObjectProperty<Image> screenshot = new SimpleObjectProperty<>();
    private ObjectProperty<Capture> capture = new SimpleObjectProperty<>();
    private ObservableList<INativeEvent> events = FXCollections.observableArrayList();
    private ObservableList<LabelListViewModel> labels = FXCollections.observableArrayList();
    private IntegerBinding eventsCount;

    @Inject
    private ILabelService labelService;
    @Inject
    private JnaService jnaService;
    @Inject
    private NotificationCenter notificationCenter;

    public void initialize() {
        createScenarioCommand = new DelegateCommand(() -> this.createScenarioAction(), //
                createScenarioPrecondition, true);
        captureCommand = new DelegateCommand(() -> this.captureAction(), true);
        eventsCount = Bindings.createIntegerBinding(() -> events.size(), events);

        createScenarioPrecondition.bind(scenarioTitle.isNotEmpty() //
                .and(eventsCount.greaterThan(0)) //
                .and(capture.isNotNull()));

        capture.addListener((obs, oldValue, newValue) -> {
            notificationCenter.publish(Commands.SHOW_APPLICATION);
        });

        reloadLabels();
    }

    public StringProperty titleProperty() {
        return scenarioTitle;
    }

    public ObjectProperty<Optional<LabelListViewModel>> labelProperty() {
        return scenarioLabel;
    }

    public ObjectProperty<Image> screenshotProperty() {
        return screenshot;
    }

    public ObjectProperty<Capture> captureProperty() {
        return capture;
    }

    public ObservableList<INativeEvent> eventsProperty() {
        return events;
    }

    public IntegerBinding eventsCountProperty() {
        return eventsCount;
    }

    public Command getCreateScenarioCommand() {
        return createScenarioCommand;
    }

    public ObservableList<LabelListViewModel> labelsProperty() {
        return labels;
    }

    public Command getCaptureCommand() {
        return captureCommand;
    }

    private Action captureAction() {
        return new Action() {

            @Override
            protected void action() throws Exception {
                notificationCenter.publish(Commands.HIDE_APPLICATION);
                jnaService.getNativeEventsStream() //
                        .skipWhile(event -> { //
                            if (!(event instanceof KeyboardEvent)) {
                                return true;
                            }
                            if (((KeyboardEvent) event).getVirtualKeyCode() != KEYCODE_PRINT_SCREEN) {
                                return true;
                            }

                            if (((KeyboardEvent) event).getEventType() != KeyboardEventType.KEY_UP) {
                                return true;
                            }

                            return false;
                        }).skip(1) //
                        .takeUntil(event -> { //
                            return (event instanceof KeyboardEvent)
                                    && ((KeyboardEvent) event).getVirtualKeyCode() == KEYCODE_PRINT_SCREEN;
                        }) //
                        .doOnTerminate(() -> takeScreenShot()) //
                        .toList() //
                        .map(list -> {
                            INativeEvent nativeEvent = list.remove(list.size() - 1);
                            list.add(PauseEvent.builder().time(nativeEvent.getTime()).build());
                            return list;
                        })
                        .subscribe(list -> events.setAll(list), RxUtils.logError(log));
            }
        };
    }

    private void takeScreenShot() {
        try {
            BufferedImage screenCapture = new Robot().createScreenCapture(jnaService.getForegroundWindowBounds());
            Image image = SwingFXUtils.toFXImage(screenCapture, null);
            screenshot.set(image);
        } catch (HeadlessException | AWTException e) {
            e.printStackTrace();
        }
    }

    private Action createScenarioAction() {
        return new Action() {
            @Override
            protected void action() throws Exception {
                Optional<String> labelId = Optional.ofNullable(scenarioLabel) //
                        .map(ObjectProperty::get)//
                        .filter(Objects::nonNull) //
                        .flatMap(o -> o) //
                        .map(LabelListViewModel::idProperty) //
                        .map(ReadOnlyObjectProperty::get) //
                        .map(UUID::toString);

                notificationCenter.publish(Commands.SCENARIO,
                        new CreateScenarioCommand(scenarioTitle.get(), labelId, events, capture.get()));
            }
        };
    }

    private void reloadLabels() {
        labels.setAll(labelService.getLabels().stream() //
                .map(LabelListViewModel::new) //
                .collect(Collectors.toList()));
    }

}
