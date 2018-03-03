package fr.aquillet.kiwi.ui.service.label;

import fr.aquillet.kiwi.model.Label;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ILabelService {

    List<Label> getLabels();

    Label createLabel(String title, String hexColor);

    Optional<Label> getLabelById(UUID id);

}
