package fr.aquillet.kiwi.ui.view.dashboard;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.toolkit.ui.dialog.IDialogService;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DashboardView implements FxmlView<DashboardViewModel> {

    @FXML
    private StackPane root;

    @Inject
    private NotificationCenter notificationCenter;
    @Inject
    private IDialogService dialogService;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void initialize() {
        notificationCenter.subscribe(Commands.OPEN_DIALOG_IN_PARENT, (key, payload) -> //
                dialogService.openDialog(((Class<? extends FxmlView>) payload[0]), root, Arrays.stream(payload).skip(1).collect(Collectors.toList())));
    }

}
