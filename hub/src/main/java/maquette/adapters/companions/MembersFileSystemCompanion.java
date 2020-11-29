package maquette.adapters.companions;

import akka.Done;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.ports.HasMembers;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import org.apache.commons.compress.utils.Lists;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MembersFileSystemCompanion<T extends Enum<T>> implements HasMembers<T> {

   private final Path directory;

   private final ObjectMapper om;

   private final JavaType type;

   public static <T extends Enum<T>> MembersFileSystemCompanion<T> apply(Path directory, ObjectMapper om, Class<T> typeClass) {
      var type = om.getTypeFactory().constructParametricType(GrantedAuthorization.class, typeClass);
      type = om.getTypeFactory().constructCollectionLikeType(List.class, type);
      return new MembersFileSystemCompanion<>(directory, om, type);
   }

   @Override
   public CompletionStage<List<GrantedAuthorization<T>>> findAllMembers(UID parent) {
      var result = load(parent);
      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<List<GrantedAuthorization<T>>> findMembersByRole(UID parent, T role) {
      var result = load(parent)
         .stream()
         .filter(granted -> granted.getRole().equals(role))
         .collect(Collectors.toList());

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateMember(UID parent, GrantedAuthorization<T> member) {
      var removed = load(parent)
         .stream()
         .filter(granted -> !granted.getAuthorization().equals(member.getAuthorization()))
         .collect(Collectors.toList());

      var mutable = Lists.<GrantedAuthorization<T>>newArrayList();
      mutable.addAll(removed);
      mutable.add(member);
      save(parent, mutable);

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Done> removeMember(UID parent, Authorization member) {
      var removed = load(parent)
         .stream()
         .filter(granted -> !granted.getAuthorization().equals(member))
         .collect(Collectors.toList());

      save(parent, removed);

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   private Path getFile(UID parent) {
      return directory
         .resolve(parent.getValue())
         .resolve("members.json");
   }

   private List<GrantedAuthorization<T>> load(UID parent) {
      var file = getFile(parent);

      if (Files.exists(file)) {
         return Operators.suppressExceptions(() -> om.readValue(file.toFile(), type));
      } else {
         return List.of();
      }
   }

   private void save(UID parent, List<GrantedAuthorization<T>> granted) {
      Operators.suppressExceptions(() -> om.writeValue(getFile(parent).toFile(), granted));
   }

}
