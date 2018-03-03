package fr.aquillet.kiwi.toolkit.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class JacksonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());
    }

    private JacksonUtil() {
        // utility class
    }

    public static void write(Object object, Class<?> type, File dest) {
        if (!dest.getParentFile().exists() && !dest.getParentFile().mkdirs()) {
            log.error("Unable do create directory " + dest.getParentFile());
            return;
        }
        try {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().forType(type).writeValue(dest, object);
        } catch (IOException e) {
            log.error("Unable to write object to file " + dest.getAbsolutePath(), e);
        }
    }

    public static <T> Optional<T> read(File source, Class<T> type) {
        try {
            return Optional.ofNullable(OBJECT_MAPPER.readerFor(type).readValue(source));
        } catch (IOException e) {
            log.error("Unable to load object from file " + source.getAbsolutePath(), e);
            return Optional.empty();
        }
    }
}
