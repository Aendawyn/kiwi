package fr.aquillet.kiwi.toolkit.dispatch;

import io.reactivex.Scheduler;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Dispatch {

    DispatchScheduler scheduler() default DispatchScheduler.SCHEDULER_COMPUTATION;

    enum DispatchScheduler {
        SCHEDULER_COMPUTATION(Schedulers.computation()),
        SCHEDULER_IO(Schedulers.io()),
        SCHEDULER_CONTROLLER(Schedulers.single()),
        SCHEDULER_JAVAFX(JavaFxScheduler.platform());

        private final Scheduler scheduler;

        DispatchScheduler(Scheduler scheduler) {
            this.scheduler = scheduler;
        }

        public Scheduler getScheduler() {
            return scheduler;
        }
    }

}
