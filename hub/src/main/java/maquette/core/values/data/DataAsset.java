package maquette.core.values.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.core.entities.data.collections.model.Collection;
import maquette.core.entities.data.datasets.model.Dataset;
import maquette.core.entities.data.datasources.model.DataSource;
import maquette.core.entities.data.streams.model.Stream;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.access.DataAccessRequest;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.core.values.user.User;

import java.util.List;
import java.util.Objects;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "type")
@JsonSubTypes(
   {
      @JsonSubTypes.Type(value = Collection.class, name = "collection"),
      @JsonSubTypes.Type(value = Dataset.class, name = "dataset"),
      @JsonSubTypes.Type(value = DataSource.class, name = "source"),
      @JsonSubTypes.Type(value = Stream.class, name = "stream")
   })
public interface DataAsset<T extends DataAsset<T>> {

   /**
    * The unique id of the data asset. Unique for the asset type.
    *
    * @return The id.
    */
   UID getId();

   /**
    * A human readable speaking title for the asset.
    *
    * @return The title
    */
   String getTitle();

   /**
    * The technical name for the data asset.
    *
    * @return The name.
    */
   String getName();

   /**
    * A short description of the data asset.
    *
    * @return A summary for it.
    */
   String getSummary();

   /**
    * See {@link DataVisibility}.
    *
    * @return The asset's visibility.
    */
   DataVisibility getVisibility();

   /**
    * See {@link DataClassification}.
    *
    * @return The asset's classification.
    */
   DataClassification getClassification();

   /**
    * See {@link PersonalInformation}.
    *
    * @return The asset's personal information classification.
    */
   PersonalInformation getPersonalInformation();

   /**
    * See {@link DataAssetState}.
    *
    * @return The current state of the data asset.
    */
   DataAssetState getState();

   /**
    * See {@link DataZone}.
    *
    * @return The asset's data zone.
    */
   DataZone getZone();

   /**
    * @return Metadata about data asset creation.
    */
   ActionMetadata getCreated();

   /**
    * @return Metadata about data asset updates.
    */
   ActionMetadata getUpdated();

   /**
    * Number of likes of the asset.
    *
    * @return The number.
    */
   int getLikes();

   /**
    * Whether the current user has liked the asset.
    *
    * @return The users like.
    */
   boolean isLiked();

   /**
    * The list of existing (for the user accessible) access requests.
    *
    * @return A list of {@link DataAccessRequest}
    */
   List<DataAccessRequest> getAccessRequests();

   /**
    * The list of members of the data asset.
    *
    * @return The list of active members.
    */
   List<GrantedAuthorization<DataAssetMemberRole>> getMembers();

   /**
    * Check whether a user has a member role of the data asset.
    *
    * @param user The user to check.
    * @param role The role the user should have.
    * @return True if the user is a member with the given role.
    */
   default boolean isMember(User user, DataAssetMemberRole role) {
      return getMembers()
         .stream()
         .anyMatch(granted -> granted.getAuthorization().authorizes(user) && (Objects.isNull(role) || granted.getRole().equals(role)));
   }

   /**
    * Check whether a user is a member with any role of the data asset.
    *
    * @param user The user to check.
    * @return True if the user a member of the data asset.
    */
   default boolean isMember(User user) {
      return isMember(user, null);
   }

   /**
    * Update/ Replace the existing access requests.
    *
    * @param accessRequests The list of existing access requests.
    * @return This instance of the data asset.
    */
   T withAccessRequests(List<DataAccessRequest> accessRequests);

}
