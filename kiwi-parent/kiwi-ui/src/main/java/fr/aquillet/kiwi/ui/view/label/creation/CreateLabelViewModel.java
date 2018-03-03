package fr.aquillet.kiwi.ui.view.label.creation;

import javax.inject.Inject;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.label.CreateLabelCommand;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CreateLabelViewModel implements ViewModel {

	private static final String COLOR_WHITE_HEX = "#FFFFFF";

	private Command createLabelCommand;
	private BooleanProperty createLabelPrecondition = new SimpleBooleanProperty(false);
	private StringProperty labelTitle = new SimpleStringProperty();
	private StringProperty labelColor = new SimpleStringProperty();

	@Inject
	private NotificationCenter notificationCenter;

	public void initialize() {
		createLabelCommand = new DelegateCommand(() -> this.createLabelAction(), //
				createLabelPrecondition, true);

		createLabelPrecondition.bind(labelTitle.isNotEmpty() //
				.and(labelColor.isNotEqualToIgnoreCase(COLOR_WHITE_HEX)));
	}

	public StringProperty labelTitleProperty() {
		return labelTitle;
	}

	public StringProperty labelColorProperty() {
		return labelColor;
	}

	public Command getCreateLauncherCommand() {
		return createLabelCommand;
	}

	private Action createLabelAction() {
		return new Action() {

			@Override
			protected void action() throws Exception {
				notificationCenter.publish(Commands.LABEL, new CreateLabelCommand(labelTitle.get(), labelColor.get()));
			}
		};
	}

}
