package maquette.core.values.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.core.entities.data.collections.CollectionProperties;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.entities.data.datasources.model.DataSourceProperties;
import maquette.core.entities.data.repositories.RepositoryProperties;
import maquette.core.entities.data.streams.StreamProperties;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.user.User;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "type")
@JsonSubTypes(
   {
      @JsonSubTypes.Type(value = CollectionProperties.class, name = "collection"),
      @JsonSubTypes.Type(value = DatasetProperties.class, name = "dataset"),
      @JsonSubTypes.Type(value = StreamProperties.class, name = "stream"),
      @JsonSubTypes.Type(value = DataSourceProperties.class, name = "datasource"),
   })
public interface DataAssetProperties<T> {

   UID getId();

   String getTitle();

   String getName();

   String getSummary();

   ActionMetadata getCreated();

   ActionMetadata getUpdated();

   DataVisibility getVisibility();

   DataClassification getClassification();

   PersonalInformation getPersonalInformation();

   T withUpdated(ActionMetadata updated);

   default T withUpdated(User by) {
      return withUpdated(ActionMetadata.apply(by));
   }

}
