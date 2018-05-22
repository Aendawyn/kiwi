package fr.aquillet.kiwi.ui.module;

import com.google.inject.AbstractModule;
import fr.aquillet.kiwi.ui.service.persistence.*;

public class PersistenceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IPersistenceConfiguration.class).toInstance(new PersistenceConfiguration());
        bind(IApplicationPersistenceService.class).toInstance(new ApplicationPersistenceService());
        bind(ILauncherPersistenceService.class).toInstance(new LauncherPersistenceService());
        bind(ILabelPersistenceService.class).toInstance(new LabelPersistenceService());
        bind(IScenarioPersistenceService.class).toInstance(new ScenarioPersistenceService());
        bind(ICampaignPersistenceService.class).toInstance(new CampaignPersistenceService());

        bind(ICampaignExecutionResultPersistenceService.class).toInstance(new CampaignExecutionResultPersistenceService());
    }
}
