package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Base64;
import java.util.Map;

/**
 * These values can be retrieved from a running stack instance. This may contain endpoint URLs, tool specific connection
 * parameters, secrets, etc...
 * <p>
 * Note! Per convention all values retrieved from infrastructure provide should be Base64 encoded. Thus, the class
 * offers methods
 * to encode/ decode while reading/ writing data.
 * <p>
 * These parameters are usually required to connect or use the tools provided by a stack.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StackInstanceParameters {

    String entrypoint;

    String entrypointLabel;

    @JsonAnyGetter
    Map<String, String> parameters;

    @SuppressWarnings("unused")
    private StackInstanceParameters() {
        this(null, null, Maps.newHashMap());
    }

    /**
     * Creates a new instance. Values passed to this function should be base64 encoded.
     *
     * @param entrypointEncoded      The entrypoint URL of the stack (Base64 encoded).
     * @param entrypointLabelEncoded The label of the entrypoint for the stack (Base64 encoded).
     * @param parametersEncoded      Additional stack parameters (Base64 encoded).
     * @return A new instance.
     */
    public static StackInstanceParameters create(String entrypointEncoded, String entrypointLabelEncoded,
                                                 Map<String, String> parametersEncoded) {
        if (!org.apache.commons.codec.binary.Base64.isBase64(entrypointEncoded)) {
            throw new IllegalArgumentException("`entrypointEncoded` must be as Base64 string.");
        }

        if (!org.apache.commons.codec.binary.Base64.isBase64(entrypointLabelEncoded)) {
            throw new IllegalArgumentException("`entrypointLabelEncoded` must be as Base64 string.");
        }

        for (var key : parametersEncoded.keySet()) {
            if (!org.apache.commons.codec.binary.Base64.isBase64(parametersEncoded.get(key))) {
                throw new IllegalArgumentException(
                    String.format("`%s` must be as Base64 string.", parametersEncoded.get(key)));
            }
        }

        return new StackInstanceParameters(entrypointEncoded, entrypointLabelEncoded, parametersEncoded);
    }

    /**
     * Creates a new instance. Make sure that values passed to this create are already Base64 encoded!
     *
     * @param entrypointEncoded      The entrypoint URL of the stack (Base64 encoded)
     * @param entrypointLabelEncoded The label of the entrypoint for the stack (Base64 encoded).
     * @return A new instance.
     */
    public static StackInstanceParameters create(String entrypointEncoded, String entrypointLabelEncoded) {
        return create(entrypointEncoded, entrypointLabelEncoded, Maps.newHashMap());
    }

    /**
     * Creates a new instance and encodes all passed values using Base64.
     *
     * @param entrypoint      The entrypoint URL of the stack.
     * @param entrypointLabel A label for the entrypoint.
     * @param parameters      Additional, stack specific parameters.
     * @return A new instance.
     */
    public static StackInstanceParameters encodeAndCreate(String entrypoint, String entrypointLabel,
                                                          Map<String, String> parameters) {
        var entrypointEncoded = Base64
            .getEncoder()
            .encodeToString(entrypoint.getBytes());
        var entrypointLabelEncoded = entrypointLabel != null ? Base64
            .getEncoder()
            .encodeToString(entrypointLabel.getBytes()) : null;

        Map<String, String> parametersEncoded = Maps.newHashMap();

        for (var key : parameters.keySet()) {
            parametersEncoded.put(key, Base64
                .getEncoder()
                .encodeToString(parameters
                    .get(key)
                    .getBytes()));
        }

        return create(entrypointEncoded, entrypointLabelEncoded, parametersEncoded);
    }

    /**
     * Creates a new instance and encodes all passed values using Base64.
     *
     * @param entrypoint      The entrypoint URL of the stack.
     * @param entrypointLabel A label for the entrypoint.
     * @return A new instance.
     */
    public static StackInstanceParameters encodeAndCreate(String entrypoint, String entrypointLabel) {
        return encodeAndCreate(entrypoint, entrypointLabel, Maps.newHashMap());
    }

    /**
     * Returns the parameters of a stack instance. The values are Base64 encoded.
     *
     * @return The parameters.
     */
    public Map<String, String> getParameters() {
        return Map.copyOf(this.parameters);
    }

    public Map<String, String> getParametersDecoded() {
        var decoded = Maps.<String, String>newHashMap();

        for (var key : this.parameters.keySet()) {
            decoded.put(key, new String(Base64
                .getDecoder()
                .decode(this.parameters.get(key))));
        }

        return Map.copyOf(decoded);
    }

    /**
     * Adds parameter to the stack instance.
     * <p>
     * Only used while Jackson de-serialization.
     *
     * @param name  The name of the parameter.
     * @param value The value of the parameter (Base64 encoded).
     */
    @JsonAnySetter
    private void setParameterEncoded(String name, String value) {
        if (!org.apache.commons.codec.binary.Base64.isBase64(value)) {
            throw new IllegalArgumentException("`value` must be Base64 encoded.");
        }
        parameters.put(name, value);
    }

    /**
     * Encodes the value and creates a new instance with the additional parameter.
     *
     * @param key   The key of the new parameter.
     * @param value The value of the new parameter.
     * @return A new instance.
     */
    public StackInstanceParameters withParameter(String key, String value) {
        var valueEncoded = Base64
            .getEncoder()
            .encodeToString(value.getBytes());
        var parametersUpdated = Maps.<String, String>newHashMap();
        parametersUpdated.putAll(this.parameters);
        parametersUpdated.put(key, valueEncoded);

        return StackInstanceParameters.create(entrypoint, entrypointLabel, parametersUpdated);
    }

}
