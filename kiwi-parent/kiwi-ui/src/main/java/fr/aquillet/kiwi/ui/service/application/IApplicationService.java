package fr.aquillet.kiwi.ui.service.application;

import fr.aquillet.kiwi.model.Application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IApplicationService {

    List<Application> getApplications();

    Application createApplication(String name);

    Application getCurrentApplication();

    void setCurrentApplication(UUID id);

    Optional<Application> getApplicationById(UUID id);

}
