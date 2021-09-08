package maquette.core.values.apidocs;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type")
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = TextField.class, name = "text"),
        @JsonSubTypes.Type(value = FileField.class, name = "file")
    })
public interface Field {

    String getKey();

}
