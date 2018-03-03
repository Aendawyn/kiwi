package fr.aquillet.kiwi.ui.view;

import com.jfoenix.controls.JFXButton;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import fr.aquillet.kiwi.ui.view.dashboard.DashboardView;
import fr.aquillet.kiwi.ui.view.main.ApplicationSelectionView;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainView implements FxmlView<MainViewModel> {

	private double viewInitialX;
	private double viewInitialY;

	@FXML
	private HBox headerBox;
	@FXML
	private JFXButton homeButton;
	@FXML
	private JFXButton closeButton;
	@FXML
	private Pagination pagination;

	@InjectViewModel
	private MainViewModel viewModel;

	public void initialize() {
		pagination.currentPageIndexProperty().bindBidirectional(viewModel.currentPageIndexProperty());
		pagination.setPageFactory(this::paginationPageFactory);
	}

	private Node paginationPageFactory(int index) {
		switch (index) {
		case 0:
			return FluentViewLoader.fxmlView(ApplicationSelectionView.class).load().getView();
		case 1:
			return FluentViewLoader.fxmlView(DashboardView.class).load().getView();
		}
		return null;
	}

	@FXML
	public void onHomeButtonPressed() {
		viewModel.currentPageIndexProperty().set(0);
	}

	@FXML
	public void onCloseButtonPressed() {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}

	@FXML
	public void onMousePressed(MouseEvent event) {
		this.viewInitialX = event.getSceneX();
		this.viewInitialY = event.getSceneY();
	}

	@FXML
	public void onMouseDragged(MouseEvent event) {
		headerBox.getScene().getWindow().setX(event.getScreenX() - viewInitialX);
		headerBox.getScene().getWindow().setY(event.getScreenY() - viewInitialY);
	}

}
