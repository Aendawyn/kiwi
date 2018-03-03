package fr.aquillet.kiwi.ui.service.application;

import fr.aquillet.kiwi.model.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ApplicationService implements IApplicationService {

    private List<Application> applications = new ArrayList<>();
    private UUID currentApplicationId;

    @Override
    public Application createApplication(String title) {
        return new Application(UUID.randomUUID(), title);
    }

    @Override
    public List<Application> getApplications() {
        return applications;
    }

    @Override
    public Application getCurrentApplication() {
        return applications.stream() //
                .filter(app -> app.getId().equals(currentApplicationId)) //
                .findFirst() //
                .orElse(null);
    }

    @Override
    public void setCurrentApplication(UUID id) {
        this.currentApplicationId = id;
    }

    @Override
    public Optional<Application> getApplicationById(UUID id) {
        return applications.stream() //
                .filter(app -> app.getId().equals(id)) //
                .findFirst();
    }

}
