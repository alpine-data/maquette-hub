package maquette.core.common.forms;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public class FormRow {

    boolean separatorAfter;

    List<FormControl> controls;

    public static FormRow apply(boolean separatorAfter) {
        return apply(separatorAfter, List.of());
    }

    public static FormRow apply() {
        return apply(false);
    }

    public FormRow withFormControl(FormControl control) {
        var controls = Lists.newArrayList(this.controls.iterator());
        controls.add(control);
        return apply(separatorAfter, List.copyOf(controls));
    }

}
