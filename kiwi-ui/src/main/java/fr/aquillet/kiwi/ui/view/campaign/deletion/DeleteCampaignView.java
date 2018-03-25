package fr.aquillet.kiwi.ui.view.campaign.deletion;

import com.jfoenix.controls.JFXButton;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javax.inject.Inject;

public class DeleteCampaignView implements FxmlView<DeleteCampaignViewModel> {

    @FXML
    private Label deleteCampaignLabel;
    @FXML
    private JFXButton deleteCampaignButton;
    @FXML
    private JFXButton cancelButton;

    @InjectViewModel
    private DeleteCampaignViewModel viewModel;

    @Inject
    private NotificationCenter notificationCenter;

    public void initialize() {
        deleteCampaignLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            return viewModel.campaignProperty().get()
                    .map(campaign -> String.format("La campagne '%s' ayant l'identifiant %s va être supprimé. Voulez-vous continuer ?", //
                            campaign.titleProperty().getValueSafe(), //
                            campaign.idProperty().get()))
                    .orElse("");
        }, viewModel.campaignProperty()));
    }

    @FXML
    public void deleteCampaignButtonPressed() {
        viewModel.getDeleteCampaignCommand().execute();
        closeDialog();
    }

    @FXML
    public void cancelButtonPressed() {
        closeDialog();
    }

    private void closeDialog() {
        notificationCenter.publish(Commands.CLOSE_DIALOG, getClass());
    }


}
