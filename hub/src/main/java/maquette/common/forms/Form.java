package maquette.common.forms;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Form {

   String helpText;

   List<FormRow> fields;

   public static Form apply(String helpText, List<FormRow> fields) {
      return new Form(helpText, List.copyOf(fields));
   }

   public static Form apply(String helpText, FormRow ...fields) {
      return apply(helpText, Lists.newArrayList(Arrays.stream(fields).iterator()));
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
      fields.add(FormRow.apply(false).withFormControl(control));

      return apply(helpText, fields);
   }

}
