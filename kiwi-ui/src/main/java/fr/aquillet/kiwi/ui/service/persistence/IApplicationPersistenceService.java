package fr.aquillet.kiwi.ui.service.persistence;

import fr.aquillet.kiwi.model.Application;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface IApplicationPersistenceService {

    List<Application> getApplications();

    File getApplicationDirectory(UUID applicationId);

    default File getApplicationDirectory(Application application) {
        return getApplicationDirectory(application.getId());
    }
}
