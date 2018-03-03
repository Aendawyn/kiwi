package fr.aquillet.kiwi.ui.view.launcher;

import java.util.Optional;

import javafx.beans.property.StringProperty;
import javafx.util.StringConverter;

public class LauncherListViewModelConverter extends StringConverter<LauncherListViewModel> {

	@Override
	public String toString(LauncherListViewModel launcher) {
		return Optional.ofNullable(launcher) //
				.map(LauncherListViewModel::titleProperty) //
				.map(StringProperty::getValueSafe) //
				.orElse(null);
	}

	@Override
	public LauncherListViewModel fromString(String string) {
		return null;
	}

}
