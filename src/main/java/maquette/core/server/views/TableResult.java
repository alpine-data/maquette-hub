package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.config.RuntimeConfiguration;
import maquette.core.server.CommandResult;
import tech.tablesaw.api.Table;

@Value
@AllArgsConstructor(staticName = "apply")
public class TableResult implements CommandResult {

    Table table;

    @Override
    public String toPlainText(RuntimeConfiguration runtime) {
        return table.printAll();
    }
}
