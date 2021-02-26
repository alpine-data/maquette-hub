package maquette.core.services.dependencies.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "nodeType")
@JsonSubTypes({
   @JsonSubTypes.Type(value = ApplicationPropertiesNode.class, name = "application"),
   @JsonSubTypes.Type(value = DataAssetPropertiesNode.class, name = "data-asset"),
   @JsonSubTypes.Type(value = ModelPropertiesNode.class, name = "model"),
   @JsonSubTypes.Type(value = ProjectPropertiesNode.class, name = "project"),
   @JsonSubTypes.Type(value = UserPropertiesNode.class, name = "user")
})
public interface DependencyPropertiesNode {
}
