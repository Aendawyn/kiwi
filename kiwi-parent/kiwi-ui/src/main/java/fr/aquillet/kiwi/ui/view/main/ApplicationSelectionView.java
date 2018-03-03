package fr.aquillet.kiwi.ui.view.main;

import java.util.Optional;

import javax.inject.Inject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import fr.aquillet.kiwi.toolkit.ui.dialog.IDialogService;
import fr.aquillet.kiwi.ui.view.application.ApplicationListView;
import fr.aquillet.kiwi.ui.view.application.ApplicationListViewModel;
import fr.aquillet.kiwi.ui.view.application.ApplicationListViewModelConverter;
import fr.aquillet.kiwi.ui.view.application.creation.CreateApplicationView;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class ApplicationSelectionView implements FxmlView<ApplicationSelectionViewModel> {

	@FXML
	private StackPane root;
	@FXML
	private JFXButton addApplicationButton;
	@FXML
	private JFXButton loadApplicationButton;
	@FXML
	private JFXComboBox<ApplicationListViewModel> applicationChoiceBox;

	@InjectViewModel
	private ApplicationSelectionViewModel viewModel;

	@Inject
	private IDialogService dialogService;

	public void initialize() {
		applicationChoiceBox.setItems(viewModel.applicationsProperty());
		applicationChoiceBox.setConverter(new ApplicationListViewModelConverter());
		applicationChoiceBox.setCellFactory(CachedViewModelCellFactory.createForFxmlView(ApplicationListView.class));

		viewModel.selectedApplicationIdProperty()
				.bind(Bindings.createObjectBinding(
						() -> Optional.ofNullable(applicationChoiceBox.getSelectionModel().getSelectedItem()) //
								.map(ApplicationListViewModel::idProperty).map(ReadOnlyObjectProperty::get) //
								.orElse(null), //
						applicationChoiceBox.getSelectionModel().selectedItemProperty()));

		loadApplicationButton.disableProperty().bind(viewModel.getLoadApplicationCommand().notExecutableProperty());
	}

	public void loadApplicationButtonPressed() {
		viewModel.getLoadApplicationCommand().execute();
	}

	public void createApplicationButtonPressed() {
		dialogService.openDialog(CreateApplicationView.class, root);
	}

}
