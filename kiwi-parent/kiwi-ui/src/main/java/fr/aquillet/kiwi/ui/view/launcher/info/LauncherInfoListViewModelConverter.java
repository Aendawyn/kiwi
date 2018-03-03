package fr.aquillet.kiwi.ui.view.launcher.info;

import java.util.Optional;

import javafx.beans.property.StringProperty;
import javafx.util.StringConverter;

public class LauncherInfoListViewModelConverter extends StringConverter<LauncherInfoListViewModel> {

	@Override
	public String toString(LauncherInfoListViewModel launcherInfo) {
		return Optional.ofNullable(launcherInfo) //
				.map(LauncherInfoListViewModel::titleProperty) //
				.map(StringProperty::getValueSafe) //
				.orElse(null);
	}

	@Override
	public LauncherInfoListViewModel fromString(String string) {
		return null;
	}

}
