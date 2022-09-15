package maquette.core.server.commands;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.config.MaquetteConfiguration;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvWriteOptions;

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
    public String toPlainText(MaquetteRuntime runtime) {
        return TablePrinter.print(table);
    }

    public Optional<String> toCSV(MaquetteConfiguration runtime) {
        var sw = new StringWriter();
        Operators.suppressExceptions(() -> table
            .write()
            .usingOptions(CsvWriteOptions
                .builder(sw)
                .build()));
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
