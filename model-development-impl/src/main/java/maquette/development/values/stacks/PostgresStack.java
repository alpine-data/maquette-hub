package maquette.development.values.stacks;

import lombok.AllArgsConstructor;
import maquette.core.common.forms.Form;
import maquette.core.common.forms.FormControl;
import maquette.core.common.forms.FormRow;
import maquette.core.common.forms.inputs.Input;

import java.util.List;

@AllArgsConstructor(staticName = "apply")
public final class PostgresStack implements Stack<PostgresStackConfiguration> {

    public static final String STACK_NAME = "postgres";

    @Override
    public String getTitle() {
        return "PostgreSQL";
    }

    @Override
    public String getName() {
        return STACK_NAME;
    }

    @Override
    public String getSummary() {
        return "PostgreSQL database with integrated pgAdmin administration UI.";
    }

    @Override
    public List<String> getTags() {
        return List.of("sql", "analytics");
    }

    @Override
    public Class<PostgresStackConfiguration> getConfigurationType() {
        return PostgresStackConfiguration.class;
    }

    @Override
    public Form getConfigurationForm() {
        var dbUsername = FormControl.apply(
            "Database username",
            Input.apply("dbUsername"),
            "The username for the database. Leave empty for random value.");

        var dbPassword = FormControl.apply(
            "Database password",
            Input.apply("dbPassword"),
            "The password for the database. Leave empty for random value.");

        var adminMail = FormControl.apply(
            "pgAdmin E-Mail address",
            Input.apply("pgAdminMail", "dev@maquette.ai"));

        var adminPassword = FormControl.apply(
            "pgAdmin password",
            Input.apply("pgAdminPassword"),
            "Password for accessing pgAdmin. Leave empty for random value.");

        return Form
            .apply()
            .withRow(FormRow
                .apply()
                .withFormControl(dbUsername)
                .withFormControl(dbPassword))
            .withRow(FormRow
                .apply()
                .withFormControl(adminMail)
                .withFormControl(adminPassword));
    }

    @Override
    public Boolean isVolumeSupported() {
        return true;
    }
}
