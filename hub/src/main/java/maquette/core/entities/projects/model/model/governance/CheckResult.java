package maquette.core.entities.projects.model.model.governance;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "type")
@JsonSubTypes(
   {
      @JsonSubTypes.Type(value = CheckOk.class, name = "ok"),
      @JsonSubTypes.Type(value = CheckWarning.class, name = "warning")
   })
public interface CheckResult {

}
