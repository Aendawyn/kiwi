package fr.aquillet.kiwi.ui.view.campaign.creation;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.ui.view.label.LabelListView;
import fr.aquillet.kiwi.ui.view.label.LabelListViewModel;
import fr.aquillet.kiwi.ui.view.label.LabelListViewModelConverter;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import java.util.Optional;

public class CreateCampaignView implements FxmlView<CreateCampaignViewModel> {

    @FXML
    private TextField campaignTitleField;
    @FXML
    private JFXComboBox<LabelListViewModel> campaignLabelBox;

    @FXML
    private JFXButton createCampaignButton;
    @FXML
    private JFXButton cancelButton;

    @InjectViewModel
    private CreateCampaignViewModel viewModel;

    @Inject
    private NotificationCenter notificationCenter;

    public void initialize() {
        campaignTitleField.textProperty().bindBidirectional(viewModel.titleProperty());

        campaignLabelBox.setItems(viewModel.labelsProperty());
        campaignLabelBox.setConverter(new LabelListViewModelConverter());
        campaignLabelBox.setCellFactory(CachedViewModelCellFactory.createForFxmlView(LabelListView.class));
        viewModel.labelProperty().bind(Bindings.createObjectBinding(() -> {
            return Optional.ofNullable(campaignLabelBox.getSelectionModel().getSelectedItem());
        }, campaignLabelBox.getSelectionModel().selectedItemProperty()));

        createCampaignButton.disableProperty().bind(viewModel.getCreateCampaignCommand().notExecutableProperty());
    }

    public void createCampaignButtonPressed() {
        viewModel.getCreateCampaignCommand().execute();
        closeDialog();
    }

    public void cancelButtonPressed() {
        closeDialog();
    }

    private void closeDialog() {
        notificationCenter.publish(Commands.CLOSE_DIALOG, getClass());
    }

}
