package fr.aquillet.kiwi.ui.service.persistence;

import fr.aquillet.kiwi.model.Application;
import fr.aquillet.kiwi.model.Label;

import java.util.List;

public interface ILabelPersistenceService {

    List<Label> getApplicationLabels(Application application);
}
