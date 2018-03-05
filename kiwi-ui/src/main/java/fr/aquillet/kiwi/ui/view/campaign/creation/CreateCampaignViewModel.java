package fr.aquillet.kiwi.ui.view.campaign.creation;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.campaign.CreateCampaignCommand;
import fr.aquillet.kiwi.ui.service.label.ILabelService;
import fr.aquillet.kiwi.ui.view.label.LabelListViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class CreateCampaignViewModel implements ViewModel {

    private Command createCampaignCommand;
    private BooleanProperty createCampaignPrecondition = new SimpleBooleanProperty(false);
    private StringProperty campaignTitle = new SimpleStringProperty();
    private ObjectProperty<Optional<LabelListViewModel>> campaignLabel = new SimpleObjectProperty<>();
    private ObservableList<LabelListViewModel> labels = FXCollections.observableArrayList();

    @Inject
    private ILabelService labelService;
    @Inject
    private NotificationCenter notificationCenter;

    public void initialize() {
        createCampaignCommand = new DelegateCommand(this::createCampaignAction, createCampaignPrecondition, true);
        createCampaignPrecondition.bind(campaignTitle.isNotEmpty());

        reloadLabels();
    }

    public StringProperty titleProperty() {
        return campaignTitle;
    }

    public ObjectProperty<Optional<LabelListViewModel>> labelProperty() {
        return campaignLabel;
    }

    public Command getCreateCampaignCommand() {
        return createCampaignCommand;
    }

    public ObservableList<LabelListViewModel> labelsProperty() {
        return labels;
    }

    private Action createCampaignAction() {
        return new Action() {
            @Override
            protected void action() throws Exception {
                Optional<String> labelId = Optional.ofNullable(campaignLabel) //
                        .map(ObjectProperty::get) //
                        .flatMap(o -> o) //
                        .map(LabelListViewModel::idProperty) //
                        .map(ReadOnlyObjectProperty::get) //
                        .map(UUID::toString);

                notificationCenter.publish(Commands.CAMPAIGN, new CreateCampaignCommand(campaignTitle.get(), labelId.map(UUID::fromString)));
            }
        };
    }

    private void reloadLabels() {
        labels.setAll(labelService.getLabels().stream() //
                .map(LabelListViewModel::new) //
                .collect(Collectors.toList()));
    }

}
