package fr.aquillet.kiwi.ui.view.campaign.deletion;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.campaign.DeleteCampaignCommand;
import fr.aquillet.kiwi.ui.service.campaign.ICampaignService;
import fr.aquillet.kiwi.ui.view.campaign.CampaignViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class DeleteCampaignViewModel implements ViewModel {

    private ObjectProperty<Optional<CampaignViewModel>> campaign = new SimpleObjectProperty<>(Optional.empty());

    @Inject
    private ICampaignService campaignService;
    @Inject
    private NotificationCenter notificationCenter;

    public void setArguments(UUID campaignId) {
        campaignService.getCampaignById(campaignId) //
                .map(CampaignViewModel::new) //
                .ifPresent(model -> campaign.set(Optional.of(model)));
    }

    public ObjectProperty<Optional<CampaignViewModel>> campaignProperty() {
        return campaign;
    }

    public Command getDeleteCampaignCommand() {
        return new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                campaign.get().ifPresent(model -> //
                        notificationCenter.publish(Commands.CAMPAIGN, new DeleteCampaignCommand(model.idProperty().get())));
            }
        }, true);
    }

}
