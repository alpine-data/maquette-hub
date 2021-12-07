package maquette.development.values.stacks;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;
import java.util.Map;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "stack")
@JsonSubTypes({
   @JsonSubTypes.Type(value = MlflowStackConfiguration.class, name = MlflowStack.STACK_NAME)
})
/**
 * Custom configuration types for defined stacks. See also {@link Stack}.
 */
public interface StackConfiguration {

   String getStackInstanceName();

   List<String> getResourceGroups();

   StackInstanceParameters getInstanceParameters(Map<String, String> parameters);

}
