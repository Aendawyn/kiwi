package fr.aquillet.kiwi.ui.module;

import com.google.inject.AbstractModule;

import fr.aquillet.kiwi.ui.controller.*;

public class ControllerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ApplicationController.class).toInstance(new ApplicationController());
		bind(LauncherController.class).toInstance(new LauncherController());
		bind(LabelController.class).toInstance(new LabelController());
		bind(ScenarioController.class).toInstance(new ScenarioController());
		bind(CampaignController.class).toInstance(new CampaignController());
	}

}
