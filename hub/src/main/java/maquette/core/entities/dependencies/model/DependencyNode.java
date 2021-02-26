package maquette.core.entities.dependencies.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "nodeType")
@JsonSubTypes({
   @JsonSubTypes.Type(value = ApplicationNode.class, name = "application"),
   @JsonSubTypes.Type(value = DataAssetNode.class, name = "data-asset"),
   @JsonSubTypes.Type(value = ModelNode.class, name = "model"),
   @JsonSubTypes.Type(value = ProjectNode.class, name = "project"),
   @JsonSubTypes.Type(value = UserNode.class, name = "user")
})
public interface DependencyNode {
}
