package maquette.core.values.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.core.entities.data.collections.CollectionProperties;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.entities.data.datasources.DataSourceProperties;
import maquette.core.entities.data.repositories.RepositoryProperties;
import maquette.core.entities.data.streams.StreamProperties;
import maquette.core.values.ActionMetadata;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "type")
@JsonSubTypes(
   {
      @JsonSubTypes.Type(value = CollectionProperties.class, name = "collection"),
      @JsonSubTypes.Type(value = DatasetProperties.class, name = "dataset"),
      @JsonSubTypes.Type(value = StreamProperties.class, name = "stream"),
      @JsonSubTypes.Type(value = DataSourceProperties.class, name = "datasource"),
      @JsonSubTypes.Type(value = RepositoryProperties.class, name = "repository"),
      @JsonSubTypes.Type(value = LinkedDataAsset.class, name = "linked")
   })
public interface DataAssetProperties {

   String getId();

   String getTitle();

   String getName();

   String getSummary();

   ActionMetadata getCreated();

   ActionMetadata getUpdated();

}
