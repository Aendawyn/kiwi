package fr.aquillet.kiwi.ui.view;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import fr.aquillet.kiwi.event.Events;
import fr.aquillet.kiwi.event.application.CurrentApplicationChangedEvent;
import fr.aquillet.kiwi.toolkit.dispatch.Dispatch;
import fr.aquillet.kiwi.toolkit.dispatch.DispatchUtils;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import javax.inject.Inject;

public class MainViewModel implements ViewModel {

    private IntegerProperty currentPageIndex = new SimpleIntegerProperty(0);

    @Inject
    private NotificationCenter notificationCenter;

    public void initialize() {
        notificationCenter.subscribe(Events.APPLICATION, (key, payload) -> DispatchUtils.dispatch(payload[0], this));
    }

    public IntegerProperty currentPageIndexProperty() {
        return currentPageIndex;
    }

    @Dispatch(scheduler = Dispatch.DispatchScheduler.SCHEDULER_JAVAFX)
    public void handle(CurrentApplicationChangedEvent event) {
        currentPageIndex.set(1);
    }

}
