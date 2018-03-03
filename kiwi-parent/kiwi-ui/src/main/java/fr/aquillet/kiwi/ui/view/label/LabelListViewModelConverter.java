package fr.aquillet.kiwi.ui.view.label;

import java.util.Optional;

import javafx.beans.property.StringProperty;
import javafx.util.StringConverter;

public class LabelListViewModelConverter extends StringConverter<LabelListViewModel> {

	@Override
	public String toString(LabelListViewModel label) {
		return Optional.ofNullable(label) //
				.map(LabelListViewModel::titleProperty) //
				.map(StringProperty::getValueSafe) //
				.orElse(null);
	}

	@Override
	public LabelListViewModel fromString(String string) {
		return null;
	}

}
