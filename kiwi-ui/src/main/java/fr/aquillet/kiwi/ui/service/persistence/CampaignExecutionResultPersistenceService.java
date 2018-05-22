package fr.aquillet.kiwi.ui.service.persistence;

import fr.aquillet.kiwi.model.CampaignExecutionResult;
import fr.aquillet.kiwi.model.ExecutionStatus;
import fr.aquillet.kiwi.model.ScenarioExecutionResult;
import fr.aquillet.kiwi.toolkit.jackson.JacksonUtil;
import fr.aquillet.kiwi.toolkit.ui.fx.ImageUtil;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.File;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
public class CampaignExecutionResultPersistenceService implements ICampaignExecutionResultPersistenceService {

    private static final String RESULT_FILE_NAME = "result";
    private static final String ORIGINAL_CAPTURE_NAME = "original_capture.png";
    private static final String EXECUTION_CAPTURE_NAME = "execution_capture.png";
    private static final String DIFF_CAPTURE_NAME = "diff_capture.png";

    private IPersistenceConfiguration configuration;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    @Inject
    public void setDependencies(final IPersistenceConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void save(CampaignExecutionResult campaignExecutionResult) {
        JacksonUtil.write(campaignExecutionResult, CampaignExecutionResult.class, getCampaignExecutionResultFilePath(campaignExecutionResult));
        campaignExecutionResult.getScenarioResults().stream() //
                .filter(scenarioExecutionResult -> scenarioExecutionResult.getStatus().equals(ExecutionStatus.FAILURE)) //
                .forEach(scenarioExecutionResult -> {
                    File outputDir = getScenarioExecutionResultDirectoryPath(getCampaignExecutionResultDirectoryPath(campaignExecutionResult), scenarioExecutionResult);
                    ImageUtil.saveToFile(scenarioExecutionResult.getOriginalCapture(), new File(outputDir, ORIGINAL_CAPTURE_NAME));
                    ImageUtil.saveToFile(scenarioExecutionResult.getExecutionCapture(), new File(outputDir, EXECUTION_CAPTURE_NAME));
                    ImageUtil.saveToFile(scenarioExecutionResult.getDiffCapture(), new File(outputDir, DIFF_CAPTURE_NAME));
                });
    }

    private File getCampaignExecutionResultFilePath(CampaignExecutionResult executionResult) {
        return new File(getCampaignExecutionResultDirectoryPath(executionResult), getCampaignExecutionResultFileName());
    }

    private File getCampaignExecutionResultDirectoryPath(CampaignExecutionResult executionResult) {
        return new File(configuration.getExecutionResultsPath(), formatter.format(executionResult.getStartDate().atZone(ZoneId.systemDefault())));
    }

    private File getScenarioExecutionResultDirectoryPath(File campaignExecutionResultDir, ScenarioExecutionResult executionResult) {
        return new File(campaignExecutionResultDir, executionResult.getScenarioId().toString());
    }

    private String getCampaignExecutionResultFileName() {
        return RESULT_FILE_NAME + configuration.getFileExtension();
    }

}
