package fr.aquillet.kiwi.ui.service.persistence;

import fr.aquillet.kiwi.model.CampaignExecutionResult;
import fr.aquillet.kiwi.toolkit.jackson.JacksonUtil;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.File;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
public class CampaignExecutionResultPersistenceService implements ICampaignExecutionResultPersistenceService {

    private static final String RESULT_FILE_NAME = "result";

    private IPersistenceConfiguration configuration;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    @Inject
    public void setDependencies(final IPersistenceConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void save(CampaignExecutionResult campaignExecutionResult) {
        JacksonUtil.write(campaignExecutionResult, CampaignExecutionResult.class, getCampaignExecutionResultFilePath(campaignExecutionResult));
    }

    private File getCampaignExecutionResultFilePath(CampaignExecutionResult executionResult) {
        return new File(getCampaignExecutionResultDirectoryPath(executionResult), getCampaignExecutionResultFileName());
    }

    private File getCampaignExecutionResultDirectoryPath(CampaignExecutionResult executionResult) {
        return new File(configuration.getExecutionResultsPath(), formatter.format(executionResult.getStartDate().atZone(ZoneId.systemDefault())));
    }

    private String getCampaignExecutionResultFileName() {
        return RESULT_FILE_NAME + configuration.getFileExtension();
    }

}
