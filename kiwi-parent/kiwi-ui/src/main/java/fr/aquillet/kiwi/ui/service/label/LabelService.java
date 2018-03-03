package fr.aquillet.kiwi.ui.service.label;

import fr.aquillet.kiwi.model.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LabelService implements ILabelService {

    private List<Label> labels = new ArrayList<>();

    @Override
    public List<Label> getLabels() {
        return labels;
    }

    @Override
    public Label createLabel(String title, String hexColor) {
        return new Label(UUID.randomUUID(), title, hexColor);
    }

    @Override
    public Optional<Label> getLabelById(UUID id) {
        return labels.stream() //
                .filter(label -> label.getId().equals(id)) //
                .findFirst();
    }

}
