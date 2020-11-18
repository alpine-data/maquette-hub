package maquette.core.server.results;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.CommandResult;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvWriteOptions;
import tech.tablesaw.io.json.JsonWriteOptions;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;

@Value
@AllArgsConstructor(staticName = "apply")
@JsonSerialize(using = TableResult.Serializer.class)
public class TableResult<T> implements CommandResult {

    Table table;

    T data;

    public static TableResult<Object> apply(Table table) {
        return apply(table, null);
    }

    @Override
    public String toPlainText(RuntimeConfiguration runtime) {
        return table.printAll();
    }

    public Optional<String> toCSV(RuntimeConfiguration runtime) {
        var sw = new StringWriter();
        Operators.suppressExceptions(() -> table.write().usingOptions(CsvWriteOptions.builder(sw).build()));
        return Optional.of(sw.toString());
    }

    public static class Serializer extends StdSerializer<TableResult> {

        protected Serializer() {
            super(TableResult.class);
        }

        @Override
        public void serialize(TableResult value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeObject(value.data);
        }

    }
}
