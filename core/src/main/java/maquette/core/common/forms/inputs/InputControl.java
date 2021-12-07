package maquette.core.common.forms.inputs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "type")
@JsonSubTypes(
   {
      @JsonSubTypes.Type(value = ButtonGroup.class, name = "button-group"),
      @JsonSubTypes.Type(value = CheckboxGroup.class, name = "checkbox-group"),
      @JsonSubTypes.Type(value = Input.class, name = "input"),
      @JsonSubTypes.Type(value = InputNumber.class, name = "input-number"),
      @JsonSubTypes.Type(value = InputPicker.class, name = "input-picker"),
      @JsonSubTypes.Type(value = Input.class, name = "radio-group"),
   })
public interface InputControl {

   String getName();

   @JsonIgnore
   Object getDefaultValue();

}
