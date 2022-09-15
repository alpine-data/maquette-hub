package maquette.core.common.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Form {

    String helpText;

    List<FormRow> fields;

    public static Form apply(String helpText, List<FormRow> fields) {
        return new Form(helpText, List.copyOf(fields));
    }

    public static Form apply(String helpText, FormRow... fields) {
        return apply(helpText, Lists.newArrayList(Arrays
            .stream(fields)
            .iterator()));
    }

    public static Form apply() {
        return apply(null);
    }

    public Form withRow(FormRow row) {
        var fields = Lists.newArrayList(this.fields.iterator());
        fields.add(row);

        return apply(helpText, fields);
    }

    public Form withControl(FormControl control) {
        var fields = Lists.newArrayList(this.fields.iterator());
        fields.add(FormRow
            .apply(false)
            .withFormControl(control));

        return apply(helpText, fields);
    }

    @JsonProperty("defaults")
    public Map<String, Object> getDefaults() {
        Map<String, Object> defaults = Maps.newHashMap();

        fields.forEach(row -> row
            .getControls()
            .forEach(control -> {
                defaults.put(control
                    .getControl()
                    .getName(), control
                    .getControl()
                    .getDefaultValue());
            }));

        return Map.copyOf(defaults);
    }

}
