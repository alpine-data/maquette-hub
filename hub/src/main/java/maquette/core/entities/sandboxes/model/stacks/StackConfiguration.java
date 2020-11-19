package maquette.core.entities.sandboxes.model.stacks;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "stack")
@JsonSubTypes({
   @JsonSubTypes.Type(value = PythonStack.Configuration.class, name = PythonStack.STACK_NAME),
   @JsonSubTypes.Type(value = PostgreSqlStack.Configuration.class, name = PostgreSqlStack.STACK_NAME)
})
public interface StackConfiguration {

   String getStackName();

}
