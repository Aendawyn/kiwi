package fr.aquillet.kiwi.ui.view.dashboard.campaign;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.ui.view.campaign.CampaignViewModel;
import fr.aquillet.kiwi.ui.view.campaign.creation.CreateCampaignView;
import fr.aquillet.kiwi.ui.view.campaign.edition.EditCampaignView;
import fr.aquillet.kiwi.ui.view.label.LabelView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;

import javax.inject.Inject;
import java.util.Optional;

public class DashboardCampaignView implements FxmlView<DashboardCampaignViewModel> {

    @FXML
    private JFXButton addCampaignButton;

    @FXML
    private JFXTreeTableView<CampaignViewModel> campaignsTable;
    @FXML
    private JFXTreeTableColumn<CampaignViewModel, String> campaignNameColumn;
    @FXML
    private JFXTreeTableColumn<CampaignViewModel, LabelView> campaignLabelColumn;
    @FXML
    private JFXTreeTableColumn<CampaignViewModel, Number> campaignScenariosCountColumn;
    @FXML
    private JFXTreeTableColumn<CampaignViewModel, Number> campaignDurationColumn;
    @FXML
    private JFXTreeTableColumn<CampaignViewModel, JFXButton> campaignRunColumn;

    @Inject
    private NotificationCenter notificationCenter;

    @InjectViewModel
    private DashboardCampaignViewModel viewModel;

    public void initialize() {
        TreeItem<CampaignViewModel> root = new RecursiveTreeItem<>(viewModel.campaignsProperty(),
                RecursiveTreeObject::getChildren);
        campaignsTable.setRoot(root);
        campaignsTable.setShowRoot(false);
        campaignsTable.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                Optional.ofNullable(campaignsTable.getSelectionModel().getSelectedItem()) //
                        .ifPresent(selectedItem -> notificationCenter.publish(Commands.OPEN_DIALOG_IN_PARENT, //
                                EditCampaignView.class, selectedItem.getValue().idProperty().get()));
            }
        });

        campaignNameColumn.setCellValueFactory(param -> {
            if (campaignNameColumn.validateValue(param)) {
                return param.getValue().getValue().titleProperty();
            }
            return campaignNameColumn.getComputedValue(param);
        });
        campaignLabelColumn.setCellValueFactory(param -> {
            if (campaignLabelColumn.validateValue(param) && param.getValue().getValue().labelProperty().get() != null
                    && param.getValue().getValue().labelProperty().get().isPresent()) {
                LabelView view = new LabelView();
                view.setModel(param.getValue().getValue().labelProperty().get().get());
                return new SimpleObjectProperty<>(view);
            }
            return campaignLabelColumn.getComputedValue(param);
        });
        campaignScenariosCountColumn.setCellValueFactory(param -> {
            if (campaignScenariosCountColumn.validateValue(param)) {
                return param.getValue().getValue().scenariosCountProperty();
            }
            return campaignScenariosCountColumn.getComputedValue(param);
        });
        campaignDurationColumn.setCellValueFactory(param -> {
            if (campaignDurationColumn.validateValue(param)) {
                return param.getValue().getValue().durationSecondsProperty();
            }
            return campaignDurationColumn.getComputedValue(param);
        });
    }

    public void addCampaignButtonPressed() {
        notificationCenter.publish(Commands.OPEN_DIALOG_IN_PARENT, CreateCampaignView.class);
    }
}
