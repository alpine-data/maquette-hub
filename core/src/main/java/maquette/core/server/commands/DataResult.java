package maquette.core.server.commands;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;

import java.io.IOException;

/**
 * Return any kind of structured data, usually some JSON object tree.
 *
 * @param <T> The actual type of the data.
 */
@Getter
@AllArgsConstructor
@JsonSerialize(using = DataResult.Serializer.class)
public class DataResult<T> implements CommandResult {

    T data;

    public static <T> DataResult<T> apply(T data) {
        return new DataResult<>(data);
    }

    @Override
    public String toPlainText(MaquetteRuntime runtime) {
        return Operators.suppressExceptions(() -> runtime
            .getObjectMapperFactory()
            .createJsonMapper(true)
            .writeValueAsString(data));
    }

    public static class Serializer extends StdSerializer<DataResult> {

        protected Serializer() {
            super(DataResult.class);
        }

        @Override
        public void serialize(DataResult value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObject(value.data);
        }

    }

}
