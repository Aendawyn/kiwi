package fr.aquillet.kiwi.toolkit.dispatch;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DispatchUtils {

    private DispatchUtils() {
        // utility
    }

    public static void dispatch(Object object, Object receiver) {
        Schedulers.computation().createWorker().schedule(() -> {
            List<Method> candidateMethods = Arrays.stream(receiver.getClass().getMethods()) //
                    .filter(method -> Arrays.stream(method.getAnnotations()) //
                            .map(Annotation::annotationType) //
                            .anyMatch(Dispatch.class::equals)) //
                    .filter(method -> method.getParameterCount() == 1) //
                    .filter(method -> method.getParameterTypes()[0].isAssignableFrom(object.getClass())) //
                    .collect(Collectors.toList());

            if (candidateMethods.isEmpty()) {
                return;
            }

            Method matchedMethod = candidateMethods.stream() //
                    .filter(method -> method.getParameterTypes()[0].equals(object.getClass())) //
                    .findFirst() //
                    .orElse(candidateMethods.get(0));

            Scheduler scheduler = Arrays.stream(matchedMethod.getAnnotations()) //
                    .filter(annot -> annot.annotationType().equals(Dispatch.class)) //
                    .findFirst() //
                    .map(annotation -> (Dispatch) annotation) //
                    .map(annotation -> annotation.scheduler().getScheduler()) //
                    .orElse(Schedulers.trampoline());

            scheduler.createWorker().schedule(() -> {
                try {
                    matchedMethod.invoke(receiver, object);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    log.error("Unable to dispatch object", e);
                }
            });
        });

    }

}
