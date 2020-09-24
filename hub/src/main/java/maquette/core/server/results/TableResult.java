package maquette.core.server.results;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.CommandResult;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.json.JsonWriteOptions;

import java.io.IOException;
import java.io.StringWriter;

@Value
@AllArgsConstructor(staticName = "apply")
@JsonSerialize(using = TableResult.Serializer.class)
public class TableResult implements CommandResult {

    Table table;

    @Override
    public String toPlainText(RuntimeConfiguration runtime) {
        return table.printAll();
    }

    public static class Serializer extends StdSerializer<TableResult> {

        protected Serializer() {
            super(TableResult.class);
        }

        @Override
        public void serialize(TableResult value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            var sw = new StringWriter();
            value.getTable().write().usingOptions(JsonWriteOptions.builder(sw).asObjects(true).build());
            gen.writeRaw(sw.toString());
        }

    }
}
