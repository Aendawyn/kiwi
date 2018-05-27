package fr.aquillet.kiwi.ui.view.label.edition;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.label.UpdateLabelColorCommand;
import fr.aquillet.kiwi.command.label.UpdateLabelTitleCommand;
import fr.aquillet.kiwi.ui.service.label.ILabelService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.inject.Inject;
import java.util.UUID;

public class EditLabelViewModel implements ViewModel {

    private static final String COLOR_WHITE_HEX = "#FFFFFF";

    private StringProperty labelTitle = new SimpleStringProperty();
    private StringProperty labelColor = new SimpleStringProperty();

    @Inject
    private ILabelService labelService;
    @Inject
    private NotificationCenter notificationCenter;

    public void setArguments(UUID labelId) {
        labelService.getLabelById(labelId).ifPresent(label -> {
            labelTitle.set(label.getTitle());
            labelColor.set(label.getHexColor());

            labelTitle.addListener((observable, oldValue, newValue) -> notificationCenter.publish(Commands.LABEL, new UpdateLabelTitleCommand(labelId, newValue)));
            labelColor.addListener((observable, oldValue, newValue) -> notificationCenter.publish(Commands.LABEL, new UpdateLabelColorCommand(labelId, newValue)));
        });
    }

    public StringProperty labelTitleProperty() {
        return labelTitle;
    }

    public StringProperty labelColorProperty() {
        return labelColor;
    }

}
