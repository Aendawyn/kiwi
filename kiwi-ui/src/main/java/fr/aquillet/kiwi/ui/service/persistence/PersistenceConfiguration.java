package fr.aquillet.kiwi.ui.service.persistence;

import fr.aquillet.kiwi.ui.configuration.GlobalConfiguration;

import java.io.File;

public class PersistenceConfiguration implements IPersistenceConfiguration {

    private static final String CONFIG_REPOSITORY_PATH_KEY = "service.persistence.repository.path";

    @Override
    public File getRepositoryPath() {
        return GlobalConfiguration.getFileValue(CONFIG_REPOSITORY_PATH_KEY);
    }

    @Override
    public String getApplicationsDirectoryName() {
        return "applications";
    }

    @Override
    public String getLaunchersDirectoryName() {
        return "launchers";
    }

    @Override
    public String getLabelsDirectoryName() {
        return "labels";
    }

    @Override
    public String getScenariosDirectoryName() {
        return "scenarios";
    }

    @Override
    public String getCampaignsDirectoryName() {
        return "campaigns";
    }

    @Override
    public String getFileExtension() {
        return ".json";
    }
}
