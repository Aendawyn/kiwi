package fr.aquillet.kiwi.ui.view.dashboard.application;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.toolkit.ui.fx.JfxUtil;
import fr.aquillet.kiwi.ui.view.label.LabelListView;
import fr.aquillet.kiwi.ui.view.label.LabelListViewModel;
import fr.aquillet.kiwi.ui.view.label.creation.CreateLabelView;
import fr.aquillet.kiwi.ui.view.label.edition.EditLabelView;
import fr.aquillet.kiwi.ui.view.launcher.LauncherListView;
import fr.aquillet.kiwi.ui.view.launcher.LauncherListViewModel;
import fr.aquillet.kiwi.ui.view.launcher.creation.CreateLauncherView;
import fr.aquillet.kiwi.ui.view.launcher.edition.EditLauncherView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.Optional;

public class DashboardApplicationView implements FxmlView<DashboardApplicationViewModel> {

    @FXML
    private Label applicationIdLabel;
    @FXML
    private JFXTextField applicationTitleField;
    @FXML
    private JFXButton addLauncherButton;
    @FXML
    private JFXListView<LauncherListViewModel> launchersList;

    @FXML
    private JFXButton addLabelButton;
    @FXML
    private JFXListView<LabelListViewModel> labelsList;

    @Inject
    private NotificationCenter notificationCenter;

    @InjectViewModel
    private DashboardApplicationViewModel viewModel;

    public void initialize() {
        applicationIdLabel.textProperty().bind(viewModel.applicationIdProperty());

        applicationTitleField.setText(viewModel.applicationTitleProperty().get());
        JfxUtil.copyValueOnFocusLoss(applicationTitleField, applicationTitleField.textProperty(), viewModel.applicationTitleProperty());

        launchersList.setItems(viewModel.launchersProperty());
        launchersList.setCellFactory(CachedViewModelCellFactory.createForFxmlView(LauncherListView.class));
        launchersList.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                Optional.ofNullable(launchersList.getSelectionModel().getSelectedItem()) //
                        .ifPresent(selectedItem -> notificationCenter.publish(Commands.OPEN_DIALOG_IN_PARENT, EditLauncherView.class, selectedItem.idProperty().get()));
            }
        });

        labelsList.setItems(viewModel.labelsProperty()
                .sorted(Comparator.comparing(l -> l.titleProperty().get())));
        labelsList.setCellFactory(CachedViewModelCellFactory.createForFxmlView(LabelListView.class));
        labelsList.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                Optional.ofNullable(labelsList.getSelectionModel().getSelectedItem()) //
                        .ifPresent(selectedItem -> notificationCenter.publish(Commands.OPEN_DIALOG_IN_PARENT, EditLabelView.class, selectedItem.idProperty().get()));
            }
        });
    }

    public void addLauncherButtonPressed() {
        notificationCenter.publish(Commands.OPEN_DIALOG_IN_PARENT, CreateLauncherView.class);
    }

    public void addLabelButtonPressed() {
        notificationCenter.publish(Commands.OPEN_DIALOG_IN_PARENT, CreateLabelView.class);
    }

}
