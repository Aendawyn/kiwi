package fr.aquillet.kiwi.ui.view.application;

import java.util.Optional;

import javafx.beans.property.StringProperty;
import javafx.util.StringConverter;

public class ApplicationListViewModelConverter extends StringConverter<ApplicationListViewModel> {

	@Override
	public String toString(ApplicationListViewModel application) {
		return Optional.ofNullable(application) //
				.map(ApplicationListViewModel::titleProperty) //
				.map(StringProperty::getValueSafe) //
				.orElse(null);
	}

	@Override
	public ApplicationListViewModel fromString(String string) {
		return null;
	}

}
