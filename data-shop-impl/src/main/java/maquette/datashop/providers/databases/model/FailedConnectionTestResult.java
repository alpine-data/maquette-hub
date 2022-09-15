package maquette.datashop.providers.databases.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class FailedConnectionTestResult implements ConnectionTestResult {

    String message;

    @Override
    public String getResult() {
        return "failed";
    }
}
