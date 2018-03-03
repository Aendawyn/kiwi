package fr.aquillet.kiwi.ui.view.application.creation;

import javax.inject.Inject;

import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.command.Commands;
import fr.aquillet.kiwi.command.application.CreateApplicationCommand;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CreateApplicationViewModel implements ViewModel, SceneLifecycle {

	private Command createApplicationCommand;
	private BooleanProperty createApplicationPrecondition = new SimpleBooleanProperty(false);
	private StringProperty applicationTitle = new SimpleStringProperty();

	@Inject
	private NotificationCenter notificationCenter;

	public void initialize() {
		createApplicationCommand = new DelegateCommand(() -> this.createApplicationAction(), //
				createApplicationPrecondition, true);

		createApplicationPrecondition.bind(applicationTitle.isNotEmpty());
	}

	public StringProperty applicationTitleProperty() {
		return applicationTitle;
	}

	public Command getCreateApplicationCommand() {
		return createApplicationCommand;
	}

	@Override
	public void onViewAdded() {
		applicationTitle.set("");
	}

	@Override
	public void onViewRemoved() {
		// nothing
	}

	private Action createApplicationAction() {
		return new Action() {

			@Override
			protected void action() throws Exception {
				notificationCenter.publish(Commands.APPLICATION,
						new CreateApplicationCommand(applicationTitle.getValueSafe()));
			}
		};
	}

}
