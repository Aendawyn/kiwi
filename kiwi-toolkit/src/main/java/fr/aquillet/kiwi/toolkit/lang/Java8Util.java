package fr.aquillet.kiwi.toolkit.lang;

import java.util.Optional;
import java.util.function.Consumer;

public class Java8Util {

    private Java8Util() {
        // utility class
    }

    public static <T> void ifPresentOrElse(Optional<T> opt, Consumer<T> ifPresent, Runnable orElse) {
        if (opt.isPresent()) {
            ifPresent.accept(opt.get());
        } else {
            orElse.run();
        }
    }
}
