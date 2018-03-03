package fr.aquillet.kiwi.toolkit.ui.dialog;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.ViewModel;
import javafx.scene.layout.StackPane;

import java.util.List;

public interface IDialogService {

    <ViewType extends FxmlView<? extends ViewModelType>, ViewModelType extends ViewModel> //
    void openDialog(Class<? extends ViewType> contentClass, StackPane parent);

    <ViewType extends FxmlView<? extends ViewModelType>, ViewModelType extends ViewModel> //
    void openDialog(Class<? extends ViewType> contentClass, StackPane parent, List<Object> arguments);
}
