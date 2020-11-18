package maquette.core.entities.sandboxes.model.stacks;

import lombok.Value;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Optional;

@Value
public class Stacks {

   private static Stacks INSTANCE = new Stacks();

   List<Stack<?>> stacks;

   private Stacks() {
      var stacks = Lists.<Stack<?>>newArrayList();
      stacks.add(PythonStack.apply());
      stacks.add(PostgreSqlStack.apply());

      this.stacks = List.copyOf(stacks);
   }

   public static Stacks apply() {
      return INSTANCE;
   }

   public Optional<Stack<?>> findStackByName(String name) {
      return stacks
         .stream()
         .filter(s -> s.getName().equals(name))
         .findFirst();
   }

   public Stack<?> getStackByName(String name) {
      return findStackByName(name).orElseThrow(); // TODO mw: Better exception
   }

   @SuppressWarnings("unchecked")
   public <T extends StackConfiguration> Stack<T> getStackByConfiguration(T config) {
      return stacks
         .stream()
         .filter(s -> s.getConfigurationType().isInstance(config))
         .map(s -> (Stack<T>) s)
         .findFirst()
         .orElseThrow(); // TODO mw: Better exception
   }

}
