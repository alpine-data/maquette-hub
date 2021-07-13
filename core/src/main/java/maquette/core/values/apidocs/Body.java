package maquette.core.values.apidocs;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "mode")
@JsonSubTypes(
   {
      @JsonSubTypes.Type(value = FormDataBody.class, name = "formdata"),
      @JsonSubTypes.Type(value = RawBody.class, name = "raw")
   })
public interface Body {

   Map<String, Object> getOptions();

}
