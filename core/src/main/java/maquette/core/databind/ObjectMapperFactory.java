package maquette.core.databind;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ObjectMapperFactory {

    /**
     * Creates a new {@link ObjectMapper} instance.
     *
     * @param pretty Whether JSON output should be pretty printed.
     * @return The new instance.
     */
    ObjectMapper createJsonMapper(boolean pretty);

    /**
     * Creates a new {@link ObjectMapper} without pretty-printing.
     *
     * @return A new instance.
     */
    default ObjectMapper createJsonMapper() {
        return createJsonMapper(false);
    }

    /**
     * Creates a new {@link ObjectMapper} to parse and write YAML.
     *
     * @return A new instance.
     */
    ObjectMapper createYamlMapper();

}
