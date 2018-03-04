package fr.aquillet.kiwi.ui.module;

import com.google.inject.AbstractModule;
import fr.aquillet.kiwi.jna.JnaService;
import fr.aquillet.kiwi.toolkit.ui.dialog.DialogService;
import fr.aquillet.kiwi.toolkit.ui.dialog.IDialogService;
import fr.aquillet.kiwi.ui.service.application.ApplicationService;
import fr.aquillet.kiwi.ui.service.application.IApplicationService;
import fr.aquillet.kiwi.ui.service.executor.IScenarioExecutorService;
import fr.aquillet.kiwi.ui.service.executor.ScenarioExecutorService;
import fr.aquillet.kiwi.ui.service.label.ILabelService;
import fr.aquillet.kiwi.ui.service.label.LabelService;
import fr.aquillet.kiwi.ui.service.launcher.ILauncherService;
import fr.aquillet.kiwi.ui.service.launcher.LauncherService;
import fr.aquillet.kiwi.ui.service.scenario.IScenarioService;
import fr.aquillet.kiwi.ui.service.scenario.ScenarioService;

public class ServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IDialogService.class).to(DialogService.class).asEagerSingleton();
		bind(IApplicationService.class).to(ApplicationService.class).asEagerSingleton();
		bind(ILauncherService.class).to(LauncherService.class).asEagerSingleton();
		bind(ILabelService.class).to(LabelService.class).asEagerSingleton();
		bind(IScenarioService.class).to(ScenarioService.class).asEagerSingleton();
		bind(DialogService.class).toInstance(new DialogService());
		bind(JnaService.class).toInstance(new JnaService());
		bind(IScenarioExecutorService.class).toInstance(new ScenarioExecutorService());
	}

}
