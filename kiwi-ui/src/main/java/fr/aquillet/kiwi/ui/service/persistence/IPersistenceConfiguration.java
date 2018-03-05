package fr.aquillet.kiwi.ui.service.persistence;

import java.io.File;

public interface IPersistenceConfiguration {

    File getRepositoryPath();

    String getApplicationsDirectoryName();

    String getLaunchersDirectoryName();

    String getLabelsDirectoryName();

    String getScenariosDirectoryName();

    String getCampaignsDirectoryName();

    String getFileExtension();
}
