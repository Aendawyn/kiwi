package fr.aquillet.kiwi.ui.view.launcher.info;

import de.saxsys.mvvmfx.ViewModel;
import fr.aquillet.kiwi.model.LauncherInfo;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LauncherInfoListViewModel implements ViewModel {

    private StringProperty title = new SimpleStringProperty();
    private StringProperty className = new SimpleStringProperty();

    public LauncherInfoListViewModel(LauncherInfo labelInfo) {
        title.set(labelInfo.getTitle());
        className.set(labelInfo.getClassName());
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty classProperty() {
        return className;
    }

}
