package fr.aquillet.kiwi.ui;

import com.google.inject.Module;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.guice.MvvmfxGuiceApplication;
import de.saxsys.mvvmfx.internal.viewloader.DependencyInjector;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.jna.JnaService;
import fr.aquillet.kiwi.toolkit.rx.RxUtils;
import fr.aquillet.kiwi.ui.configuration.GlobalConfiguration;
import fr.aquillet.kiwi.ui.module.ControllerModule;
import fr.aquillet.kiwi.ui.module.ServiceModule;
import fr.aquillet.kiwi.ui.view.MainView;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class KiwiApplication extends MvvmfxGuiceApplication {

    public static void main(String[] args) {
        GlobalConfiguration.init() //
                .subscribe(() -> launch(args), RxUtils.logError(log));
    }

    @Override
    public void startMvvmfx(Stage stage) throws Exception {
        try {
            Scene scene = new Scene(FluentViewLoader.fxmlView(MainView.class).load().getView());
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add("style/default-style.css");
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Kiwi");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/kiwi_logo.png")));
            stage.setScene(scene);
            stage.show();

            NotificationCenter notificationCenter = DependencyInjector.getInstance()
                    .getInstanceOf(NotificationCenter.class);
            notificationCenter.subscribe(Commands.HIDE_APPLICATION, (key, payload) -> {
                Platform.setImplicitExit(false);
                Platform.runLater(stage::hide);
            });
            notificationCenter.subscribe(Commands.SHOW_APPLICATION, (key, payload) -> {
                Platform.runLater(stage::show);
                Platform.setImplicitExit(true);
            });
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void stopMvvmfx() throws Exception {
        super.stopMvvmfx();
        DependencyInjector.getInstance().getInstanceOf(JnaService.class).shutDown();
    }

    @Override
    public void initGuiceModules(List<Module> modules) throws Exception {
        modules.add(new ControllerModule());
        modules.add(new ServiceModule());
    }

}
