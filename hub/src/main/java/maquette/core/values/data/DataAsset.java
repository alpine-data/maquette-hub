package maquette.core.values.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.core.entities.data.datasets.model.Dataset;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Objects;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "type")
@JsonSubTypes(
   {
      @JsonSubTypes.Type(value = Dataset.class, name = "dataset")
   })
public interface DataAsset {

   UID getId();

   String getTitle();

   String getName();

   String getSummary();

   ActionMetadata getCreated();

   ActionMetadata getUpdated();

   List<GrantedAuthorization<DataAssetMemberRole>> getMembers();

   default boolean isMember(User user, DataAssetMemberRole role) {
      return getMembers()
         .stream()
         .anyMatch(granted -> granted.getAuthorization().authorizes(user) && (Objects.isNull(role) || granted.getRole().equals(role)));
   }

   default boolean isMember(User user) {
      return isMember(user, null);
   }

}
