package fr.aquillet.kiwi.ui.service.persistence;

import java.io.File;

public interface IPersistenceConfiguration {

    File getRepositoryPath();

    File getExecutionResultsPath();

    String getApplicationsDirectoryName();

    String getLaunchersDirectoryName();

    String getLabelsDirectoryName();

    String getScenariosDirectoryName();

    String getCampaignsDirectoryName();

    String getFileExtension();
}
