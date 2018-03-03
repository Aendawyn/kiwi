package fr.aquillet.kiwi.toolkit.ui.dialog;

import com.jfoenix.controls.JFXDialog;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import de.saxsys.mvvmfx.utils.notifications.NotificationObserver;
import fr.aquillet.kiwi.command.Commands;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class DialogService implements IDialogService {

    private NotificationCenter notificationCenter;

    @Inject
    public void setDependencies(final NotificationCenter notificationCenter) {
        this.notificationCenter = notificationCenter;
    }

    @Override
    public <ViewType extends FxmlView<? extends ViewModelType>, ViewModelType extends ViewModel> //
    void openDialog(Class<? extends ViewType> contentClass, StackPane parent) {
        openDialog(contentClass, parent, Collections.emptyList());
    }

    @Override
    public <ViewType extends FxmlView<? extends ViewModelType>, ViewModelType extends ViewModel> //
    void openDialog(Class<? extends ViewType> contentClass, StackPane parent, List<Object> arguments) {
        JFXDialog dialog = new JFXDialog();
        ViewTuple<? extends ViewType, ViewModelType> viewTuple = FluentViewLoader.fxmlView(contentClass).load();
        Region content = (Region) viewTuple.getView();
        ViewModelType model = viewTuple.getViewModel();
        dialog.setContent(content);
        dialog.setOverlayClose(false);
        dialog.show(parent);

        if (!arguments.isEmpty()) {
            try {
                Method argMethod = model.getClass().getMethod("setArguments", arguments.stream().map(Object::getClass).toArray(Class<?>[]::new));
                try {
                    argMethod.invoke(model, arguments.toArray());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error("Unable to set arguments to view model " + model.getClass().getName() + ": error during method invocation", e);
                }
            } catch (NoSuchMethodException e) {
                log.warn("Unable to set arguments to view model {}: no matching argument method", model.getClass().getName());
            }
        }

        AtomicReference<NotificationObserver> obs = new AtomicReference<>();
        obs.set((key, payload) -> {
            if (payload.length == 1 && contentClass.equals(payload[0])) {
                dialog.close();
                notificationCenter.unsubscribe(obs.get());
            }
        });
        notificationCenter.subscribe(Commands.CLOSE_DIALOG, obs.get());
    }

}
